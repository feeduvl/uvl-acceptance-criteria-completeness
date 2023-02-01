package de.uhd.ifi.se.accompleteness.calculation.naive;

import java.util.HashMap;
import java.util.Map;

import de.uhd.ifi.se.accompleteness.calculation.CalculationParams;
import de.uhd.ifi.se.accompleteness.calculation.CompletenessCalculator;
import de.uhd.ifi.se.accompleteness.model.CompletenessCalcResult;
import de.uhd.ifi.se.accompleteness.model.ExtractionResult;
import de.uhd.ifi.se.accompleteness.model.Topic;
import de.uhd.ifi.se.accompleteness.model.UserStory;

public class NaiveCompletenessCalculator implements CompletenessCalculator {

    @Override
    public CompletenessCalcResult calculate_completeness(ExtractionResult usResult, ExtractionResult acResult, CalculationParams params, UserStory userStory) {
        Map<Topic, Topic> matchedTopics = new HashMap<>();
        int topicsFound = 0;
        int topicsAll = 0;
        for (Topic topic: usResult.getTopics()) {
            boolean topicFound = false;
            for (Topic topic2 : acResult.getTopics()) {
                if (topic.equals(topic2)) {
                    topicFound = true;
                }
            }
            if (topicFound) {
                topicsFound++;
            } 
            topicsAll++;
        }
        double completeness = ((double)(topicsFound) / (double)(topicsAll));
        return new CompletenessCalcResult(completeness, usResult.getTopics(), acResult.getTopics(), matchedTopics, userStory);
    }
    
}
