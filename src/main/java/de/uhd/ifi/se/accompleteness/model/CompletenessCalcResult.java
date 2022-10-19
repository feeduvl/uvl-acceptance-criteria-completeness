package de.uhd.ifi.se.accompleteness.model;

import java.util.Map;

public class CompletenessCalcResult {

    Map<String, Double> metrics;

    public Map<String, Double> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Double> metrics) {
        this.metrics = metrics;
    }

    Map<Topic, Topic> matchedTopics;

    public Map<Topic, Topic> getMatchedTopics() {
        return matchedTopics;
    }

    public void setMatchedTopics(Map<Topic, Topic> matchedTopics) {
        this.matchedTopics = matchedTopics;
    }

    public CompletenessCalcResult(Map<String, Double> metrics, Map<Topic, Topic> matchedTopics) {
        this.metrics = metrics;
        this.matchedTopics = matchedTopics;
    }
}
