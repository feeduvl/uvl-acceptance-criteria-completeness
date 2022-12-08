package de.uhd.ifi.se.accompleteness.model;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Generates the payload of the HTTP response, including acceptance criteria and
 * metrics, in the syntax required by the Feed UVL API.
 * 
 * @see <a href=
 *      "https://github.com/feeduvl/uvl-acceptance-criteria/blob/main/swagger.yaml">https://github.com/feeduvl/uvl-acceptance-criteria/blob/main/swagger.yaml</a>
 *      for the API documentation
 */
public class UvlResponse {

    public static JsonObject getJsonFromResults(List<CompletenessCalcResult> results) {
        JsonObject mainObject = new JsonObject();
        mainObject.add("doc_topic", null);
        mainObject.add("codes", null);
        JsonArray resultsArr = new JsonArray();
        double sum_completeness = 0;
        for (CompletenessCalcResult calcResult : results) {
            JsonObject singleObject = new JsonObject();
            singleObject.addProperty("id", calcResult.getUserStory().getId());
            singleObject.addProperty("user_story_text", calcResult.getUserStory().getUserStoryString());
            singleObject.addProperty("user_story_goal", calcResult.getUserStory().getGoal());
            singleObject.addProperty("acceptance_criteria_text", calcResult.getUserStory().getAcceptanceCriteria());

            String[] tokensInUserStory = calcResult.getUserStory().getUserStoryString().split(" ");
            JsonArray matchedTopics = new JsonArray();
            int pos = 0;
            for (String tokenString : tokensInUserStory) {
                matchedTopics.add(getMapping(tokenString, pos, calcResult.getMatchedTopics().entrySet(),
                        calcResult.getUsTopics()));
                pos += tokenString.length() + 1;
            }

            singleObject.add("mapping", matchedTopics);
            singleObject.addProperty("completeness", calcResult.getCompleteness());

            JsonArray usTopics = new JsonArray();
            for (Topic usTopic : calcResult.getUsTopics()) {
                usTopics.add(usTopic.toString());
            }
            singleObject.add("user_story_topics", usTopics);

            JsonArray acTopics = new JsonArray();
            for (Topic acTopic : calcResult.getAcTopics()) {
                acTopics.add(acTopic.toString());
            }
            singleObject.add("acceptance_criteria_topics", acTopics);

            resultsArr.add(singleObject);
            sum_completeness += calcResult.getCompleteness();
        }
        double avg_completeness = sum_completeness / results.size();

        JsonObject completenessResults = new JsonObject();
        completenessResults.add("completeness_results", resultsArr);
        mainObject.add("topics", completenessResults);

        JsonObject metrics = new JsonObject();
        metrics.addProperty("avg_completeness", avg_completeness);
        mainObject.add("metrics", metrics);

        return mainObject;
    }

    private static JsonObject getMapping(String tokenString, int pos, Set<Entry<Topic, Topic>> matchedTopics,
            List<Topic> userStoryTopics) {
        JsonObject mapping = new JsonObject();
        mapping.addProperty("text", tokenString);
        for (var entry : matchedTopics) {
            if (entry.getKey().getStartPosition() == pos) {
                mapping.addProperty("annotation", "complete");
                mapping.addProperty("mapping", entry.getKey().toString());
                mapping.addProperty("usTopicStart", entry.getKey().getStartPosition());
                mapping.addProperty("usTopicEnd", entry.getKey().getEndPosition());
                mapping.addProperty("acTopicStart", entry.getValue().getStartPosition());
                mapping.addProperty("acTopicEnd", entry.getValue().getEndPosition());
                return mapping;
            }
        }
        for (var topic : userStoryTopics) {
            if (topic.getStartPosition() == pos) {
                mapping.addProperty("annotation", "non-complete");
                return mapping;
            }
        }
        mapping.addProperty("annotation", "no-concept");
        return mapping;
    }
}