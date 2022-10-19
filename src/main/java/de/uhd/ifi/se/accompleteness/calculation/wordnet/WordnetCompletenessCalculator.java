package de.uhd.ifi.se.accompleteness.calculation.wordnet;

import java.util.HashMap;
import java.util.Map;

import de.uhd.ifi.se.accompleteness.calculation.CompletenessCalculator;
import de.uhd.ifi.se.accompleteness.model.CompletenessCalcResult;
import de.uhd.ifi.se.accompleteness.model.NLPResultSingle;
import de.uhd.ifi.se.accompleteness.model.Topic;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.relationship.RelationshipFinder;
import net.sf.extjwnl.data.relationship.RelationshipList;
import net.sf.extjwnl.dictionary.Dictionary;

public class WordnetCompletenessCalculator implements CompletenessCalculator {

    int SIMILAR_THRESHHOLD = 5;

    @Override
    public CompletenessCalcResult calculate_completeness(NLPResultSingle usResult, NLPResultSingle acResult) throws JWNLException, CloneNotSupportedException, Exception {
        Dictionary dictionary = Dictionary.getDefaultResourceInstance();
        Map<String, Double> toReturn = new HashMap<>();
        Map<Topic, Topic> matchedTopics = new HashMap<>();
        int topicsFound = 0;
        int topicsAll = 0;
        for (Topic usTopic : usResult.getTopics()) {
            POS tag = usTopic.getPOSTag();
            if (tag != null) {
                IndexWord usWord = dictionary.lookupIndexWord(tag, usTopic.toString());
                topicsAll++;
                for (Topic acTopic : acResult.getTopics()) {
                    POS tag2 = acTopic.getPOSTag();
                    if (tag2 != null) {
                        IndexWord acWord = dictionary.lookupIndexWord(tag2, acTopic.toString());
                        if (usWord != null && acWord != null) {
                            RelationshipList relationships = RelationshipFinder.findRelationships(usWord.getSenses().get(0), acWord.getSenses().get(0), PointerType.HYPERNYM);
                            if (relationships.size() > 0 && relationships.getShallowest().getDepth() < SIMILAR_THRESHHOLD) {
                                topicsFound++;
                                matchedTopics.put(usTopic, acTopic);
                                continue;
                            }
                        }
                    }
                }
            }
        }
        toReturn.put("completeness", ((double)(topicsFound) / (double)(topicsAll)));
        return new CompletenessCalcResult(toReturn, matchedTopics);
    }
    
}
