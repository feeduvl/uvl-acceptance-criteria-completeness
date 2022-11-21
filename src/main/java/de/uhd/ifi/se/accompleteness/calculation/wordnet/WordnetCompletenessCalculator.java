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
        int wordsTotal = usWordsNonWordnet.size();
        int wordsFound = 0;
        for (var usWordString : usWordsNonWordnet.entrySet()) {
            for (var acWordString : acWordsNonWordnet.entrySet()) {
                if (usWordString.equals(acWordString)) {
                    wordsFound++;
                    matchedTopics.put(usWordString.getKey(), acWordString.getKey());
                }
            }
        }
        wordsTotal += usWordsWordnet.size();
        for (var usSynset : usWordsWordnet.entrySet()) {
            for (var acSynset : acWordsWordnet.entrySet()) {
                RelationshipList relationships = RelationshipFinder.findRelationships(
                        usSynset.getValue(), acSynset.getValue(), PointerType.HYPERNYM);
                if (relationships.size() > 0
                        && relationships.getShallowest().getDepth() < SIMILAR_THRESHHOLD) {
                            wordsFound++;
                    matchedTopics.put(usSynset.getKey(), acSynset.getKey());
                    break;
                }
            }
        }
        double resultCompleteness = ((double) (wordsFound) / (double) (wordsTotal));
        System.out.println("wordsFound" + wordsFound);
        System.out.println("wordsTotal" + wordsTotal);
        System.out.println("resultCompleteness" + resultCompleteness);
        if (Double.isNaN(resultCompleteness)) {
            resultCompleteness = 0;
        }
        toReturn.put("completeness",  resultCompleteness);
        return new CompletenessCalcResult(resultCompleteness, usResult.getTopics(), acResult.getTopics(), matchedTopics, userStory);
    }

}
