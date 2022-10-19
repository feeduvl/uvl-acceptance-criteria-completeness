package de.uhd.ifi.se.accompleteness.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Stores the payload of the HTTP response, including acceptance criteria and
 * metrics, in the syntax required by the Feed UVL API.
 * 
 * @see <a href=
 *      "https://github.com/feeduvl/uvl-acceptance-criteria/blob/main/swagger.yaml">https://github.com/feeduvl/uvl-acceptance-criteria/blob/main/swagger.yaml</a>
 *      for the API documentation
 */
public class CompletenessResponse {

    /**
     * A JSON object storing the user story topics.
     */
    JsonArray usTopics;

    /**
     * A JSON object storing the relationships.
     */
    JsonObject usRelationships;

    /**
     * A JSON object storing the acceptance criteria.
     */
    JsonArray acTopics;

    /**
     * A JSON object storing the relationships.
     */
    JsonObject acRelationships;

    /**
     * A JSON object storing the computed metrics.
     */
    JsonObject metrics;

    /**
     * An integer uniquely identifying the user story.
     */
    JsonPrimitive userStoryNumber;

    /**
     * The constructor of the {@link UvlResponse} initializing the member
     * variables with empty JSON objects.
     * @param userStoryNumber
     */
    public CompletenessResponse(int userStoryNumber) {
        metrics = new JsonObject();
        acTopics = new JsonArray();
        acRelationships = new JsonObject();
        usTopics = new JsonArray();
        usRelationships = new JsonObject();
        this.userStoryNumber = new JsonPrimitive(userStoryNumber);
    };

    /**
     * Creates a JSON object from all member variables with the names required
     * by the Feed UVL API.
     * 
     * @return a JSON object containing all member variables
     * 
     * @see <a href=
     *      "https://github.com/feeduvl/uvl-acceptance-criteria/blob/main/swagger.yaml">https://github.com/feeduvl/uvl-acceptance-criteria/blob/main/swagger.yaml</a>
     *      for the API documentation
     */
    public JsonObject toJson() {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("metrics", this.metrics);
        jsonResponse.add("us_topics", this.usTopics);
        jsonResponse.add("us_relationships", this.usRelationships);
        jsonResponse.add("ac_topics", this.acTopics);
        jsonResponse.add("ac_relationships", this.acRelationships);
        jsonResponse.add("number", this.userStoryNumber);
        return jsonResponse;
    }

    /**
     * Creates a string containing the {@link UvlResponse} in JSON format as
     * created by the {@code toJson()} method.
     * 
     * @return a string containing the {@link UvlResponse} in JSON format
     */
    public String toString() {
        return this.toJson().toString();
    }

    /**
     * Adds a metric to the metric object
     * 
     * @param string the name of the metric
     * @param value  the metric value
     */
    public void addMetric(String string, double value) {
        metrics.addProperty(string, value);
    }

    /**
     * Adds a relationship to the response.
     * 
     * @param relationship the relationship
     */
    public void addUSRelationship(Relationship relationship) {
        JsonArray acceptanceCriterionAsArray = new JsonArray();
        acceptanceCriterionAsArray.add(relationship.left_topic.topic);
        acceptanceCriterionAsArray.add(relationship.right_topic.topic);
        this.usRelationships.add(relationship.relationship, acceptanceCriterionAsArray);
    }

    /**
     * Adds an acceptance criterion to the response and associates it with a
     * user story.
     * 
     * @param acceptanceCriterion the acceptance criterion to be stored
     * @param usNumber            the ID of the corresponding user story
     */
    public void addUSTopic(Topic topic) {

        // Add the acceptance criterion to the acceptance criteria object
        this.usTopics.add(topic.toString());
    }

        /**
     * Adds a relationship to the response.
     * 
     * @param relationship the relationship
     */
    public void addACRelationship(Relationship relationship) {
        JsonArray acceptanceCriterionAsArray = new JsonArray();
        acceptanceCriterionAsArray.add(relationship.left_topic.topic);
        acceptanceCriterionAsArray.add(relationship.right_topic.topic);
        this.acRelationships.add(relationship.relationship, acceptanceCriterionAsArray);
    }

    /**
     * Adds an acceptance criterion to the response and associates it with a
     * user story.
     * 
     * @param acceptanceCriterion the acceptance criterion to be stored
     * @param usNumber            the ID of the corresponding user story
     */
    public void addACTopic(Topic topic) {

        // Add the acceptance criterion to the acceptance criteria object
        this.acTopics.add(topic.toString());
    }
}
