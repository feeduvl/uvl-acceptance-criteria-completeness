package de.uhd.ifi.se.accompleteness.model;

import java.util.List;

public class NLPResult {
    List<Relationship> USRelationships;
    List<Topic> USTopics;

    List<Relationship> ACRelationships;
    List<Topic> ACTopics;

    public NLPResult(List<Relationship> USRelationships, List<Topic> USTopics, List<Relationship> ACRelationships, List<Topic> ACTopics) {
        this.USRelationships = USRelationships;
        this.USTopics = USTopics;
        this.ACRelationships = ACRelationships;
        this.ACTopics = ACTopics;
    }

    public NLPResult(NLPResultSingle USResult, NLPResultSingle ACResult) {
        this.USRelationships = USResult.relationships;
        this.USTopics = USResult.topics;
        this.ACRelationships = ACResult.relationships;
        this.ACTopics = ACResult.topics;
    }

    public List<Relationship> getUSRelationships() {
        return USRelationships;
    }

    public List<Topic> getUSTopics() {
        return USTopics;
    }

    public List<Relationship> getACRelationships() {
        return ACRelationships;
    }

    public List<Topic> getACTopics() {
        return ACTopics;
    }
}
