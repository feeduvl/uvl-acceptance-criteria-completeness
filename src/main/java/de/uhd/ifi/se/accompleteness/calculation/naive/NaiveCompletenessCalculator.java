package de.uhd.ifi.se.accompleteness.calculation.naive;

import java.util.HashMap;
import java.util.Map;

import de.uhd.ifi.se.accompleteness.calculation.CompletenessCalculator;
import de.uhd.ifi.se.accompleteness.model.NLPResultSingle;
import de.uhd.ifi.se.accompleteness.model.Relationship;
import de.uhd.ifi.se.accompleteness.model.Topic;

public class NaiveCompletenessCalculator implements CompletenessCalculator {

    @Override
    public Map<String, Double> calculate_completeness(NLPResultSingle usResult, NLPResultSingle acResult) {
        Map<String, Double> toReturn = new HashMap<>();
        int topicsFound = 0;
        int topicsAll = 0;
        int relationshipsFound = 0;
        int relationshipsAll = 0;
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
        for (Relationship relationship: usResult.getRelationships()) {
            boolean relationshipFound = false;
            for (Relationship relationship2: acResult.getRelationships()) {
                if (relationship == relationship2) {
                    relationshipFound = true;
                }
            }
            if (relationshipFound) {
                relationshipsFound++;
            }
            relationshipsAll++;
        }
        toReturn.put("completeness", ((double)(topicsFound) / (double)(topicsAll)));
        return toReturn;
    }
    
}
