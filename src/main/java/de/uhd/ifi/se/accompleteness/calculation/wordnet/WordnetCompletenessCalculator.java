package de.uhd.ifi.se.accompleteness.calculation.wordnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uhd.ifi.se.accompleteness.calculation.CompletenessCalculator;
import de.uhd.ifi.se.accompleteness.model.CompletenessCalcResult;
import de.uhd.ifi.se.accompleteness.model.NLPResultSingle;
import de.uhd.ifi.se.accompleteness.model.Topic;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.relationship.RelationshipFinder;
import net.sf.extjwnl.data.relationship.RelationshipList;
import net.sf.extjwnl.dictionary.Dictionary;

public class WordnetCompletenessCalculator implements CompletenessCalculator {

    int SIMILAR_THRESHHOLD = 5;
    double WORDNET_ALPHA = .8;

    @Override
    public CompletenessCalcResult calculate_completeness(NLPResultSingle usResult, NLPResultSingle acResult)
            throws JWNLException, CloneNotSupportedException, Exception {
        Dictionary dictionary = Dictionary.getDefaultResourceInstance();
        Map<String, Double> toReturn = new HashMap<>();
        Map<String, String> matchedTopics = new HashMap<>();
        List<Synset> usWordsWordnet = new ArrayList<>();
        List<Synset> acWordsWordnet = new ArrayList<>();
        List<String> usWordsNonWordnet = new ArrayList<>();
        List<String> acWordsNonWordnet = new ArrayList<>();
        for (Topic topic : usResult.getTopics()) {
            POS tag = topic.getPOSTag();
            String topicString = topic.toString();
            for (String singleWord : topicString.split(" ")) {
                if (tag != null) {
                    IndexWord word = dictionary.lookupIndexWord(tag, singleWord);
                    if (word != null) {
                        Synset synset = word.getSenses().get(0);
                        usWordsWordnet.add(synset);
                        continue;
                    }
                }
                usWordsNonWordnet.add(singleWord);
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
                        acWordsWordnet.add(synset);
                        continue;
                    }
                }
                acWordsNonWordnet.add(singleWord);
            }
        }
        int nonWordNetWordsTotal = usWordsNonWordnet.size();
        int nonWordNetWordsFound = 0;
        for (String usWordString : usWordsNonWordnet) {
            for (String acWordString : acWordsNonWordnet) {
                if (usWordString.equals(acWordString)) {
                    nonWordNetWordsFound++;
                }
            }
        }
        int wordNetWordsTotal = usWordsWordnet.size();
        int wordNetWordsFound = 0;
        for (Synset usSynset : usWordsWordnet) {
            for (Synset acSynset : acWordsWordnet) {
                RelationshipList relationships = RelationshipFinder.findRelationships(
                        usSynset, acSynset, PointerType.HYPERNYM);
                if (relationships.size() > 0
                        && relationships.getShallowest().getDepth() < SIMILAR_THRESHHOLD) {
                    wordNetWordsFound++;
                    matchedTopics.put(usSynset.toString(), acSynset.toString());
                    continue;
                }
            }
        }
        toReturn.put("completeness", (WORDNET_ALPHA * (double) (wordNetWordsFound) / (double) (wordNetWordsTotal))
                + (1 - WORDNET_ALPHA) * (double) (nonWordNetWordsFound) / (double) (nonWordNetWordsTotal));
        return new CompletenessCalcResult(toReturn, matchedTopics);
    }

}
