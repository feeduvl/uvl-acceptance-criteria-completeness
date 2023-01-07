package de.uhd.ifi.se.accompleteness.model;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOG = LoggerFactory.getLogger(UvlResponse.class);

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
            for (int i = 0; i < tokensInUserStory.length; i++) {
                UvlResponse.MappingReturnObject mapReturn = getMapping(tokensInUserStory[i], pos,
                        calcResult.getMatchedTopics().entrySet(),
                        calcResult.getUsTopics(),
                        Arrays.asList(Arrays.copyOfRange(tokensInUserStory, i + 1, tokensInUserStory.length)));
                matchedTopics.add(mapReturn.getMapping());
                pos += tokensInUserStory[i].length() + 1;
                pos += mapReturn.getNextTopicIncluded();
                i += mapReturn.getNextWordsCount();
            }

            String[] tokensInAcceptanceCriteria = calcResult.getUserStory().getAcceptanceCriteria().split(" ");
            JsonArray matchedACTopics = new JsonArray();
            pos = 0;
            for (int i = 0; i < tokensInAcceptanceCriteria.length; i++) {
                UvlResponse.MappingReturnObject mapReturn = getMappingAC(tokensInAcceptanceCriteria[i], pos,
                        calcResult.getMatchedTopics().entrySet(),
                        calcResult.getAcTopics(), Arrays.asList(Arrays.copyOfRange(tokensInAcceptanceCriteria, i + 1,
                                tokensInAcceptanceCriteria.length)));
                matchedACTopics.add(mapReturn.getMapping());
                pos += tokensInAcceptanceCriteria[i].length() + 1;
                pos += mapReturn.getNextTopicIncluded();
                i += mapReturn.getNextWordsCount();
            }

            singleObject.add("mapping", matchedTopics);
            singleObject.add("acMapping", matchedACTopics);
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

    private static class MappingReturnObject {
        private JsonObject mapping;

        public JsonObject getMapping() {
            return mapping;
        }

        public void setMapping(JsonObject mapping) {
            this.mapping = mapping;
        }

        public int getNextTopicIncluded() {
            return nextWordsNum;
        }

        public void setNextTopicIncluded(int nextWordsNum) {
            this.nextWordsNum = nextWordsNum;
        }

        private int nextWordsNum;
        private int nextWordsCount;

        public int getNextWordsCount() {
            return nextWordsCount;
        }

        public void setNextWordsCount(int nextWordsCount) {
            this.nextWordsCount = nextWordsCount;
        }

        public MappingReturnObject(JsonObject mapping, int nextWordsNum, int nextWordsCount) {
            this.mapping = mapping;
            this.nextWordsNum = nextWordsNum;
            this.nextWordsCount = nextWordsCount;
        }
    }

    private static MappingReturnObject getMapping(String tokenString, int pos, Set<Entry<Topic, Topic>> matchedTopics,
            List<Topic> userStoryTopics, List<String> nextTokenStrings) {
        JsonObject mapping = new JsonObject();
        mapping.addProperty("text", tokenString);
        for (var entry : matchedTopics) {
            if (entry.getKey().getStartPosition() == pos) {
                int wordsLengthSum = tokenString.length();
                int numAdditionalWords = 0;
                while (wordsLengthSum < (entry.getKey().getEndPosition() - entry.getKey().getStartPosition())) {
                    String oldText = mapping.get("text").getAsString();
                    mapping.remove("text");
                    String newWord = nextTokenStrings.get(numAdditionalWords);
                    mapping.addProperty("text", oldText + " " + newWord);
                    numAdditionalWords++;
                    wordsLengthSum += newWord.length() + 1;
                }

                mapping.addProperty("annotation", "complete");
                mapping.addProperty("mapping", entry.getValue().toString());
                mapping.addProperty("token", entry.getKey().toString());
                mapping.addProperty("usTopicStart", entry.getKey().getStartPosition());
                mapping.addProperty("usTopicEnd", entry.getKey().getEndPosition());
                mapping.addProperty("acTopicStart", entry.getValue().getStartPosition());
                mapping.addProperty("acTopicEnd", entry.getValue().getEndPosition());
                return new UvlResponse.MappingReturnObject(mapping, wordsLengthSum, numAdditionalWords);
            }
        }
        for (var topic : userStoryTopics) {
            if (topic.getStartPosition() == pos) {
                mapping.addProperty("annotation", "non-complete");
                return new MappingReturnObject(mapping, 0, 0);
            }
        }
        mapping.addProperty("annotation", "no-concept");
        return new MappingReturnObject(mapping, 0, 0);
    }

    private static MappingReturnObject getMappingAC(String tokenString, int pos, Set<Entry<Topic, Topic>> matchedTopics,
            List<Topic> acTopics, List<String> nextTokenStrings) {
        JsonObject mapping = new JsonObject();
        mapping.addProperty("text", tokenString);
        for (var entry : matchedTopics) {
            if (entry.getValue().getStartPosition() == pos) {
                int wordsLengthSum = tokenString.length();
                int numAdditionalWords = 0;
                while (wordsLengthSum < (entry.getKey().getEndPosition() - entry.getKey().getStartPosition())) {
                    String oldText = mapping.get("text").getAsString();
                    mapping.remove("text");
                    String newWord = nextTokenStrings.get(numAdditionalWords);
                    mapping.addProperty("text", oldText + " " + newWord);
                    numAdditionalWords++;
                    wordsLengthSum += newWord.length() + 1;
                }

                mapping.addProperty("annotation", "complete");
                mapping.addProperty("mapping", entry.getKey().toString());
                mapping.addProperty("token", entry.getValue().toString());
                mapping.addProperty("usTopicStart", entry.getKey().getStartPosition());
                mapping.addProperty("usTopicEnd", entry.getKey().getEndPosition());
                mapping.addProperty("acTopicStart", entry.getValue().getStartPosition());
                mapping.addProperty("acTopicEnd", entry.getValue().getEndPosition());
                return new UvlResponse.MappingReturnObject(mapping, wordsLengthSum, numAdditionalWords);
            }
        }
        for (var topic : acTopics) {
            if (topic.getStartPosition() == pos) {
                mapping.addProperty("annotation", "non-complete");
                return new MappingReturnObject(mapping, 0, 0);
            }
        }
        mapping.addProperty("annotation", "no-concept");
        return new MappingReturnObject(mapping, 0, 0);
    }
}