package de.uhd.ifi.se.accompleteness.model;

import java.util.List;

public class ExtractionResult {
    List<Relationship> relationships;
    List<Topic> topics;

    public ExtractionResult(List<Relationship> relationships, List<Topic> topics) {
        this.relationships = relationships;
        this.topics = topics;
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public List<Topic> getTopics() {
        return topics;
    }
}
