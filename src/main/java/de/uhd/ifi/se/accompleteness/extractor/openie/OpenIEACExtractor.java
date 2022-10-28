package de.uhd.ifi.se.accompleteness.extractor.openie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import de.uhd.ifi.se.accompleteness.extractor.ACExtractor;
import de.uhd.ifi.se.accompleteness.model.NLPResultSingle;
import de.uhd.ifi.se.accompleteness.model.Relationship;
import de.uhd.ifi.se.accompleteness.model.Topic;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class OpenIEACExtractor implements ACExtractor {

    @Override
    public NLPResultSingle extract(String acceptanceCriterion, boolean debug) {

        List<Topic> topics = new ArrayList<Topic>();
        List<Relationship> relationships = new ArrayList<>();

        // Set up the NLP pipeline and annotate the preprocessed user story
        // string
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");
        // props.setProperty("ssplit.isOneSentence", "true");
        // props.setProperty("regexner.mapping",
        // "src/main/java/de/uhd/ifi/se/acgen/generator/gherkin/regexner/ui-mapping.txt");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(acceptanceCriterion);
        pipeline.annotate(document);
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            // Get the OpenIE triples for the sentence
            Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
            // Print the triples
            for (RelationTriple triple : triples) {
                Topic subjectTopic = new Topic(triple.subjectLemmaGloss(), triple.subject.get(0).tag());
                Topic objectTopic = new Topic(triple.objectLemmaGloss(), triple.object.get(0).tag());
                Relationship relationship = new Relationship(subjectTopic, objectTopic,
                        triple.relationLemmaGloss());
                if (!(topics.contains(subjectTopic))) {
                    topics.add(subjectTopic);
                }
                if (!(topics.contains(objectTopic))) {
                    topics.add(objectTopic);
                }
                if (!(relationships.contains(relationship))) {
                    relationships.add(relationship);
                }
            }
        }

        return new NLPResultSingle(relationships, topics);
    }

}
