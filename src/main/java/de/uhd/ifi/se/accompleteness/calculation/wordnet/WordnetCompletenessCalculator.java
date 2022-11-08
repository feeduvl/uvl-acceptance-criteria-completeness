package de.uhd.ifi.se.accompleteness.calculation.wordnet;

import java.util.HashMap;
import java.util.Map;

import de.uhd.ifi.se.accompleteness.calculation.CalculationParams;
import de.uhd.ifi.se.accompleteness.calculation.CompletenessCalculator;
import de.uhd.ifi.se.accompleteness.model.CompletenessCalcResult;
import de.uhd.ifi.se.accompleteness.model.NLPResultSingle;
import de.uhd.ifi.se.accompleteness.model.Topic;
import de.uhd.ifi.se.accompleteness.model.UserStory;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.relationship.RelationshipFinder;
import net.sf.extjwnl.data.relationship.RelationshipList;
import net.sf.extjwnl.dictionary.Dictionary;

public class WordnetCompletenessCalculator implements CompletenessCalculator {

    @Override
    public CompletenessCalcResult calculate_completeness(NLPResultSingle usResult, NLPResultSingle acResult, CalculationParams params, UserStory userStory)
            throws JWNLException, CloneNotSupportedException, Exception {
        WordnetCalculationParams calcParams = (WordnetCalculationParams) params;
        double WORDNET_ALPHA = calcParams.getWordnetAlpha();
        int SIMILAR_THRESHHOLD = calcParams.getWordnetDistanceThreshold();
        Dictionary dictionary = Dictionary.getDefaultResourceInstance();
        Map<String, Double> toReturn = new HashMap<>();
        Map<Topic, Topic> matchedTopics = new HashMap<>();
        Map<Topic, Synset> usWordsWordnet = new HashMap<>();
        Map<Topic, Synset> acWordsWordnet = new HashMap<>();
        Map<Topic, String> usWordsNonWordnet = new HashMap<>();
        Map<Topic, String> acWordsNonWordnet = new HashMap<>();

        for (Topic topic : usResult.getTopics()) {
            POS tag = topic.getPOSTag();
            String topicString = topic.toString();
            for (String singleWord : topicString.split(" ")) {
                if (tag != null) {
                    IndexWord word = dictionary.lookupIndexWord(tag, singleWord);
                    if (word != null) {
                        Synset synset = word.getSenses().get(0);
                        usWordsWordnet.put(topic, synset);
                        continue;
                    }
                }
                usWordsNonWordnet.put(topic, singleWord);
            }
        }
        for (Topic topic : acResult.getTopics()) {
            POS tag = topic.getPOSTag();
            String topicString = topic.toString();
            for (String singleWord : topicString.split(" ")) {
                if (tag != null) {
                    IndexWord word = dictionary.lookupIndexWord(tag, singleWord);
                    if (word != null) {
                        Synset synset = word.getSenses().get(0);
                        acWordsWordnet.put(topic, synset);
                        continue;
                    }
                }
                acWordsNonWordnet.put(topic, singleWord);
            }
        }
        int nonWordNetWordsTotal = usWordsNonWordnet.size();
        int nonWordNetWordsFound = 0;
        for (var usWordString : usWordsNonWordnet.entrySet()) {
            for (var acWordString : acWordsNonWordnet.entrySet()) {
                if (usWordString.equals(acWordString)) {
                    nonWordNetWordsFound++;
                    matchedTopics.put(usWordString.getKey(), acWordString.getKey());
                }
            }
        }
        int wordNetWordsTotal = usWordsWordnet.size();
        int wordNetWordsFound = 0;
        for (var usSynset : usWordsWordnet.entrySet()) {
            for (var acSynset : acWordsWordnet.entrySet()) {
                RelationshipList relationships = RelationshipFinder.findRelationships(
                        usSynset.getValue(), acSynset.getValue(), PointerType.HYPERNYM);
                if (relationships.size() > 0
                        && relationships.getShallowest().getDepth() < SIMILAR_THRESHHOLD) {
                    wordNetWordsFound++;
                    matchedTopics.put(usSynset.getKey(), acSynset.getKey());
                    continue;
                }
            }
        }
        double wordnetResultCompleteness = WORDNET_ALPHA * ((double) (wordNetWordsFound) / (double) (wordNetWordsTotal));
        double nonWordnetResultCompleteness = (1 - WORDNET_ALPHA) * ((double) (nonWordNetWordsFound) / (double) (nonWordNetWordsTotal));
        if (Double.isNaN(nonWordnetResultCompleteness)) {
            nonWordnetResultCompleteness = 0;
        }
        if (Double.isNaN(wordnetResultCompleteness)) {
            wordnetResultCompleteness = 0;
        }
        double completeness = wordnetResultCompleteness + nonWordnetResultCompleteness;
        toReturn.put("completeness",  completeness);
        return new CompletenessCalcResult(completeness, usResult.getTopics(), acResult.getTopics(), matchedTopics, userStory);
    }

}
