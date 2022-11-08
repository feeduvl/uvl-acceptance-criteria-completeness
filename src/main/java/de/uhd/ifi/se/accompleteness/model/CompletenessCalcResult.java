package de.uhd.ifi.se.accompleteness.model;

import java.util.List;
import java.util.Map;

public class CompletenessCalcResult {

    double completeness;
    List<Topic> usTopics;
    List<Topic> acTopics;
    Map<Topic, Topic> matchedTopics;
    UserStory userStory;

    public CompletenessCalcResult(double completeness, List<Topic> usTopics, List<Topic> acTopics, Map<Topic, Topic> matchedTopics, UserStory userStory) {
        this.matchedTopics = matchedTopics;
        this.completeness = completeness;
        this.usTopics = usTopics;
        this.acTopics = acTopics;
        this.userStory = userStory;
    }

    public double getCompleteness() {
        return completeness;
    }

    public void setCompleteness(double completeness) {
        this.completeness = completeness;
    }

    public List<Topic> getUsTopics() {
        return usTopics;
    }

    public void setUsTopics(List<Topic> usTopics) {
        this.usTopics = usTopics;
    }

    public List<Topic> getAcTopics() {
        return acTopics;
    }

    public void setAcTopics(List<Topic> acTopics) {
        this.acTopics = acTopics;
    }

    public UserStory getUserStory() {
        return userStory;
    }

    public void setUserStory(UserStory userStory) {
        this.userStory = userStory;
    }

    public Map<Topic, Topic> getMatchedTopics() {
        return matchedTopics;
    }

    public void setMatchedTopics(Map<Topic, Topic> matchedTopics) {
        this.matchedTopics = matchedTopics;
    }
}
