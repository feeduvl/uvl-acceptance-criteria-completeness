package de.uhd.ifi.se.accompleteness.extractor.openie;

import de.uhd.ifi.se.accompleteness.extractor.ExtractionParams;
import de.uhd.ifi.se.accompleteness.extractor.USExtractor;
import de.uhd.ifi.se.accompleteness.extractor.openie.util.StringSimilarity;
import de.uhd.ifi.se.accompleteness.model.UserStory;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import de.uhd.ifi.se.accompleteness.model.ExtractionResult;
import de.uhd.ifi.se.accompleteness.model.Relationship;
import de.uhd.ifi.se.accompleteness.model.Topic;

public class OpenIEUSExtractor implements USExtractor {

    private static final List<String> exclude_tokens = Arrays.asList("I");

    StanfordCoreNLP pipeline;

    public OpenIEUSExtractor() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");
        pipeline = new StanfordCoreNLP(props);
    }

    public ExtractionResult extract(UserStory userStory, ExtractionParams params) {
        OpenIEExtractionParams paramsOpenIE = (OpenIEExtractionParams) params;
        String userStoryString = userStory.getGoal();

        List<Topic> topics = new ArrayList<Topic>();
        List<Relationship> relationships = new ArrayList<>();

        // Set up the NLP pipeline and annotate the preprocessed user story
        // string
        Annotation document = new Annotation(userStoryString);
        pipeline.annotate(document);

        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            // Get the OpenIE triples for the sentence
            Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
            
            for (RelationTriple triple : triples) {
                System.out.println(triple.confidence);
                if (triple.confidence > 0.5) { // confidence will always be 1.0
                    Topic subjectTopic = new Topic(triple.subjectLemmaGloss(), triple.subject.get(0).tag(),
                            userStory.getGoalStartPosition() + triple.subject.get(0).beginPosition(),
                            userStory.getGoalStartPosition() + triple.subject.get(triple.subject.size() - 1).endPosition());
                    Topic objectTopic = new Topic(triple.objectLemmaGloss(), triple.object.get(0).tag(),
                            userStory.getGoalStartPosition() + triple.object.get(0).beginPosition(),
                            userStory.getGoalStartPosition() + triple.object.get(triple.object.size() - 1).endPosition());
                    Relationship relationship = new Relationship(subjectTopic, objectTopic,
                            triple.relationLemmaGloss());
                    if (!(topics.contains(subjectTopic))) {
                        if (paramsOpenIE.isFilterUSTopicsSimilarity()) {
                            if (check_sim_threshold(topics, subjectTopic, paramsOpenIE)) {
                                topics.add(subjectTopic);
                            }
                        } else {
                            topics.add(subjectTopic);
                        }
                    }
                    if (!(topics.contains(objectTopic))) {
                        if (paramsOpenIE.isFilterUSTopicsSimilarity()) {
                            if (check_sim_threshold(topics, objectTopic, paramsOpenIE)) {
                                topics.add(objectTopic);
                            }
                        } else {
                            topics.add(objectTopic);
                        }
                    }
                    if (!(relationships.contains(relationship))) {
                        relationships.add(relationship);
                    }
                }
            }
        }
        if (paramsOpenIE.isFilterUSTopicsExcludeList()) {
            topics = filter_topics(topics);
        }
        if (paramsOpenIE.isFilterUSTopicsCompositions()) {
            topics = filterCompositeTopics(topics, paramsOpenIE);
        }
        return new ExtractionResult(relationships, topics);
    }

    private boolean check_sim_threshold(List<Topic> topics, Topic topic, OpenIEExtractionParams params) {

        for (Topic topic1Topic : topics) {
            double similarity = StringSimilarity.similarity(topic1Topic.toString(), topic.toString());
            if (similarity > params.getFilterUSTopicsSimilarityThreshold()) {
                return false;
            }
        }
        return true;
    }

    private List<Topic> filterCompositeTopics(List<Topic> topics, OpenIEExtractionParams params) {
        List<Topic> resultTopics = new ArrayList<>();
        for (Topic topic : topics) {
            String topicString = topic.toString();
            String[] topicTokens = topicString.split(" ");
            if (topicTokens.length < params.getFilterUSTopicsCompositionsMinLength())
                resultTopics.add(topic);
            int topicsAlreadyFound = 0;
            for (String topicToken : topicTokens) {
                for (Topic topic1 : topics) {
                    if (topic1.toString().equals(topicToken))
                        topicsAlreadyFound++;
                }
            }
            if (topicsAlreadyFound != topicTokens.length)
                resultTopics.add(topic);
        }
        return resultTopics;
    }

    private List<Topic> filter_topics(List<Topic> topics) {
        List<Topic> resultTopics = new ArrayList<>();
        for (Topic topic : topics) {
            if (!exclude_tokens.contains(topic.toString())) {
                resultTopics.add(topic);
            }
        }
        return resultTopics;
    }
}
