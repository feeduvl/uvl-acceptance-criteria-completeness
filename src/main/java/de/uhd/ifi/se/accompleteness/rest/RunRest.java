package de.uhd.ifi.se.accompleteness.rest;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.uhd.ifi.se.accompleteness.calculation.wordnet.WordnetCompletenessCalculator;
import de.uhd.ifi.se.accompleteness.extractor.ACExtractor;
import de.uhd.ifi.se.accompleteness.extractor.ExtractionParams;
import de.uhd.ifi.se.accompleteness.extractor.USExtractor;
import de.uhd.ifi.se.accompleteness.extractor.openie.OpenIEACExtractor;
import de.uhd.ifi.se.accompleteness.extractor.openie.OpenIEExtractionParams;
import de.uhd.ifi.se.accompleteness.extractor.openie.OpenIEUSExtractor;
import de.uhd.ifi.se.accompleteness.model.CompletenessCalcResult;
import de.uhd.ifi.se.accompleteness.model.CompletenessResponse;
import de.uhd.ifi.se.accompleteness.model.NLPResultSingle;
import de.uhd.ifi.se.accompleteness.model.Relationship;
import de.uhd.ifi.se.accompleteness.model.Topic;
import de.uhd.ifi.se.accompleteness.model.UserStory;
import de.uhd.ifi.se.accompleteness.model.UvlResponse;
import spark.Request;
import spark.Response;

public class RunRest {
        /** 
     * Creates a response for requests to the /run API and starts the
     * acceptance criteria generation process
     * 
     * @param req the HTTP request sent to the /run endpoint whose payload
     * contains a dataset of user stories and the debug parameter
     * @param res the HTTP response containing header and HTTP status code
     * information
     * @return an object used as payload for the HTTP response which contains
     * acceptance criteria, log messages and metrics in the form of a 
     * {@link UvlResponse} object on success, or an error message in the form
     * of a string if an exception is thrown and catched
     */
    public Object createResponse(Request req, Response res) {
        try {

            // Interpret the payload of the HTTP request as JSON and extract
            // the user stories (called documents) and parameters
            JsonObject jsonRequest = new Gson().fromJson(req.body(), JsonObject.class);
            JsonArray documents = jsonRequest.get("dataset").getAsJsonObject().get("documents").getAsJsonArray();
            JsonObject paramsJson = jsonRequest.get("params").getAsJsonObject();

            // Read the params for the user story information extraction
            ExtractionParams params = new OpenIEExtractionParams();
            params.setExtractionParamsFromJson(paramsJson);

            // Generate acceptance criteria and put them into the form required
            // by the API
            JsonArray response = addAcceptanceCriteriaToResponse(documents, params);

            //response.addMetric("count", documents.size());

            // Stop measuring the runtime
            // long finish = System.currentTimeMillis();
            // response.addMetric("runtime", finish - start);
            res.header("Content-Type", "application/json");
            return response;     
        } catch (Exception e) {
            res.status(500);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString();
            return "<h1>500 Internal Server Error</h1><code>" + sStackTrace.replaceAll("\\n", "<br>") + "</code>";
        }
    }

        /** 
     * Starts the acceptance criteria generation and creates a
     * {@link UvlResponse} which contains acceptance criteria, log messages and
     * metrics in the syntax required by the FeedUVL API.
     * 
     * @param documents a part of the HTTP request payload containing the user
     * stories
     * @param debug whether to include debug information in the HTTP response.
     * @return a object containing acceptance criteria, log
     * messages and metrics in the syntax required by the FeedUVL API.
     * 
     * @see UvlResponse
     * @see <a href="https://github.com/feeduvl/uvl-acceptance-criteria/blob/main/swagger.yaml">https://github.com/feeduvl/uvl-acceptance-criteria/blob/main/swagger.yaml</a>
     * for the API documentation
     */
    public static JsonArray addAcceptanceCriteriaToResponse(JsonArray documents, ExtractionParams extrParams) {
        JsonArray responseList = new JsonArray();
        int errors = 0;
        for (JsonElement document : documents) { // for every user story
            int userStoryNumber = document.getAsJsonObject().get("id").getAsInt();
            String inputText = document.getAsJsonObject().get("text").getAsString();
            String userStoryText = extractUserStoryString(inputText);
            String acceptanceText = extractAcceptanceCriteriaString(inputText);
            CompletenessResponse response = new CompletenessResponse(userStoryNumber);
            try {
                // Extract the user story from the string
                UserStory userStory = new UserStory(userStoryText);

                USExtractor usExtractor = new OpenIEUSExtractor();
                NLPResultSingle usNlpResult = usExtractor.extract(userStory, extrParams);
                for (Topic topic: usNlpResult.getTopics()) {
                    response.addUSTopic(topic);
                }

                for (Relationship relationship : usNlpResult.getRelationships()) {
                    response.addUSRelationship(relationship);
                }

                ACExtractor acExtractor = new OpenIEACExtractor();
                NLPResultSingle acNlpResult = acExtractor.extract(acceptanceText);

                for (Topic topic: acNlpResult.getTopics()) {
                    response.addACTopic(topic);
                }

                for (Relationship relationship : acNlpResult.getRelationships()) {
                    response.addACRelationship(relationship);
                }

                CompletenessCalcResult calcResult = new WordnetCompletenessCalculator().calculate_completeness(usNlpResult, acNlpResult);
                String metricName = calcResult.getMetrics().entrySet().iterator().next().getKey();
                double metricValue = calcResult.getMetrics().entrySet().iterator().next().getValue();
                response.addMetric(metricName, metricValue);
                responseList.add(response.toJson());
            } catch (Exception e) {
                JsonObject objectTest = new JsonObject();
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                objectTest.add(e.getMessage(), new JsonPrimitive(sw.toString()));
                responseList.add(objectTest);
                //response.addAcceptanceCriterion(new AcceptanceCriterion(e.getMessage(), AcceptanceCriterionType.ERROR), userStoryNumber);
            }
        }

        // Add the log message counts to the response
        JsonObject objectTest = new JsonObject();
        objectTest.add("errorCount", new JsonPrimitive(errors));
        responseList.add(objectTest);
        return responseList;
    }

    private static String extractUserStoryString (String inputString) {
        inputString = inputString.replace("\n", "");
        int start = inputString.indexOf("###");
        int end = inputString.indexOf("###", start + 1);
        return inputString.substring(start + 3, end);
    }

    private static String extractAcceptanceCriteriaString (String inputString) {
        inputString = inputString.replace("\n", "");
        int start = inputString.indexOf("+++");
        int end = inputString.indexOf("+++", start + 1);
        return inputString.substring(start + 3, end);
    }
}
