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

    Map<String, String> matchedTopics;

    public Map<String, String> getMatchedTopics() {
        return matchedTopics;
    }

    public void setMatchedTopics(Map<String, String> matchedTopics) {
        this.matchedTopics = matchedTopics;
    }

    public CompletenessCalcResult(Map<String, Double> metrics, Map<String, String> matchedTopics) {
        this.metrics = metrics;
        this.matchedTopics = matchedTopics;
    }
}
