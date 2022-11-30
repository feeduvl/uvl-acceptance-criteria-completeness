package de.uhd.ifi.se.accompleteness.rest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.uhd.ifi.se.accompleteness.calculation.CalculationParams;
import de.uhd.ifi.se.accompleteness.calculation.wordnet.WordnetCalculationParams;
import de.uhd.ifi.se.accompleteness.calculation.wordnet.WordnetCompletenessCalculator;
import de.uhd.ifi.se.accompleteness.extractor.ACExtractor;
import de.uhd.ifi.se.accompleteness.extractor.ExtractionParams;
import de.uhd.ifi.se.accompleteness.extractor.USExtractor;
import de.uhd.ifi.se.accompleteness.extractor.openie.OpenIEACExtractor;
import de.uhd.ifi.se.accompleteness.extractor.openie.OpenIEExtractionParams;
import de.uhd.ifi.se.accompleteness.extractor.openie.OpenIEUSExtractor;
import de.uhd.ifi.se.accompleteness.model.CompletenessCalcResult;
import de.uhd.ifi.se.accompleteness.model.NLPResultSingle;
import de.uhd.ifi.se.accompleteness.model.UserStory;
import de.uhd.ifi.se.accompleteness.model.UvlResponse;
import spark.Request;
import spark.Response;

public class RunRest {

    private static final Logger LOG = LoggerFactory.getLogger(RunRest.class);

    /**
     * Creates a response for requests to the /run API and starts the
     * acceptance criteria generation process
     * 
     * @param req the HTTP request sent to the /run endpoint whose payload
     *            contains a dataset of user stories and the debug parameter
     * @param res the HTTP response containing header and HTTP status code
     *            information
     * @return an object used as payload for the HTTP response which contains
     *         acceptance criteria, log messages and metrics in the form of a
     *         {@link UvlResponse} object on success, or an error message in the
     *         form
     *         of a string if an exception is thrown and catched
     */
    public Object createResponse(Request req, Response res) {
        try {

            LOG.info("Received event: %s".formatted(req.body()));

            // Interpret the payload of the HTTP request as JSON and extract
            // the user stories (called documents) and parameters
            JsonObject jsonRequest = new Gson().fromJson(req.body(), JsonObject.class);
            JsonArray documents = jsonRequest.get("dataset").getAsJsonObject().get("documents").getAsJsonArray();
            JsonObject paramsJson = jsonRequest.get("params").getAsJsonObject();

            // Read the params for the user story information extraction
            ExtractionParams extractionParams = new OpenIEExtractionParams();
            extractionParams.setExtractionParamsFromJson(paramsJson);

            // Read the params for the user story completeness calculation
            CalculationParams calcParams = new WordnetCalculationParams();
            calcParams.setCalculationParamsFromJson(paramsJson);

            // Calculate the completeness
            JsonObject response = calculateCompleteness(documents, extractionParams, calcParams);

            res.header("Content-Type", "application/json");

            LOG.info("Returning response: %s".formatted(response.toString()));

            return response;
            
        } catch (Exception e) {
            res.status(500);
            LOG.error("Error during request handling: ", e);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString();

            return "<h1>500 Internal Server Error</h1><code>" + sStackTrace.replaceAll("\\n", "<br>") + "</code>";
        }
    }

    /**
     * Starts the acceptance criteria generation and creates a
     * {@link UvlResponse} which contains completeness, log messages and
     * metrics in the format required by the FeedUVL API.
     * 
     * @param documents  a part of the HTTP request payload containing the user
     *                   stories
     * @param extrParams params for user story extraction.
     * @param calcParams params for completeness calculation.
     * @return a object containing results in a Json format
     */
    public static JsonObject calculateCompleteness(JsonArray documents, ExtractionParams extrParams,
            CalculationParams calcParams) throws Exception {
        List<CompletenessCalcResult> results = new ArrayList<>();
        ACExtractor acExtractor = new OpenIEACExtractor();
        USExtractor usExtractor = new OpenIEUSExtractor();

        for (JsonElement document : documents) { // for every user story
            String inputText = document.getAsJsonObject().get("text").getAsString();
            String userStoryId = document.getAsJsonObject().get("id").getAsString();
            String userStoryText = extractUserStoryString(inputText);
            String acceptanceText = extractAcceptanceCriteriaString(inputText);

            // Extract the user story from the string
            UserStory userStory = new UserStory(userStoryText, userStoryId, acceptanceText);

            // Do User Story extraction
            NLPResultSingle usNlpResult = usExtractor.extract(userStory, extrParams);

            // Do Acceptance Criteria extraction
            NLPResultSingle acNlpResult = acExtractor.extract(acceptanceText);

            // Calculate completeness, generate mappings
            CompletenessCalcResult calcResult = new WordnetCompletenessCalculator()
                    .calculate_completeness(usNlpResult, acNlpResult, calcParams, userStory);
            results.add(calcResult);
        }

        return UvlResponse.getJsonFromResults(results);
    }

    private static String extractUserStoryString(String inputString) {
        inputString = inputString.replace("\n", "");
        int start = inputString.indexOf("###");
        int end = inputString.indexOf("###", start + 1);
        return inputString.substring(start + 3, end);
    }

    private static String extractAcceptanceCriteriaString(String inputString) {
        inputString = inputString.replace("\n", "");
        int start = inputString.indexOf("+++");
        int end = inputString.indexOf("+++", start + 1);
        if (end == -1) {
            end = start;
            int startUS = inputString.indexOf("###");
            int endUS = inputString.indexOf("###", startUS + 1);
            start = endUS + 3;
        }
        return inputString.substring(start + 3, end);
    }
}
