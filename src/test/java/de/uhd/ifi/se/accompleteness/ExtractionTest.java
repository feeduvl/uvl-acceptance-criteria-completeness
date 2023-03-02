package de.uhd.ifi.se.accompleteness;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.gson.JsonObject;

import de.uhd.ifi.se.accompleteness.extractor.ACExtractor;
import de.uhd.ifi.se.accompleteness.extractor.USExtractor;
import de.uhd.ifi.se.accompleteness.extractor.openie.OpenIEACExtractor;
import de.uhd.ifi.se.accompleteness.extractor.openie.OpenIEExtractionParams;
import de.uhd.ifi.se.accompleteness.extractor.openie.OpenIEUSExtractor;
import de.uhd.ifi.se.accompleteness.model.ExtractionResult;
import de.uhd.ifi.se.accompleteness.model.Topic;
import de.uhd.ifi.se.accompleteness.model.UserStory;

public class ExtractionTest {
    
    @Test
    public void testExtractionSimpleConceptUS () throws Exception {
        USExtractor extractor = new OpenIEUSExtractor();

        UserStory userStory = new UserStory("As a person I want to have a mouse so that I can have a mouse.", "TEST-1", "The mouse");
        
        JsonObject paramsJson = new JsonObject();
        paramsJson.addProperty("debug", false);
        paramsJson.addProperty("filterUSTopicsExcludeList", false);
        paramsJson.addProperty("filterUSTopicsSimilarity", false);
        paramsJson.addProperty("filterUSTopicsSimilarityThreshold", .5);
        paramsJson.addProperty("filterUSTopicsCompositions", false);
        paramsJson.addProperty("filterUSTopicsCompositionsMinLength", 3);
        OpenIEExtractionParams params = new OpenIEExtractionParams();
        params.setExtractionParamsFromJson(paramsJson);

        ExtractionResult result = extractor.extract(userStory, params);

        assertEquals(true, result.getTopics().contains(new Topic("mouse")));
    }

    @Test
    public void testExtractionMultipleConceptsUS () throws Exception {
        USExtractor extractor = new OpenIEUSExtractor();

        UserStory userStory = new UserStory("As a developer I want to be able to see if my acceptance criteria are complete and my user stories are good so that I can judge the quality of my requirements.", "TEST-1", "I can see the overall completeness of my acceptance criteria.");
        
        JsonObject paramsJson = new JsonObject();
        paramsJson.addProperty("debug", false);
        paramsJson.addProperty("filterUSTopicsExcludeList", false);
        paramsJson.addProperty("filterUSTopicsSimilarity", false);
        paramsJson.addProperty("filterUSTopicsSimilarityThreshold", .5);
        paramsJson.addProperty("filterUSTopicsCompositions", false);
        paramsJson.addProperty("filterUSTopicsCompositionsMinLength", 3);
        OpenIEExtractionParams params = new OpenIEExtractionParams();
        params.setExtractionParamsFromJson(paramsJson);

        ExtractionResult result = extractor.extract(userStory, params);

        assertEquals(true, result.getTopics().contains(new Topic("my user story")) && result.getTopics().contains(new Topic("my acceptance criterion")));
    }

    @Test
    public void testExtractionSimpleConceptAC () throws Exception {
        ACExtractor extractor = new OpenIEACExtractor();

        UserStory userStory = new UserStory("As a developer I want to be able to see if my acceptance criteria are complete so that I can judge the quality of my requirements.", "TEST-1", "I can see the overall completeness of my acceptance criteria.");

        ExtractionResult result = extractor.extract(userStory.getAcceptanceCriteria());

        assertEquals(true, result.getTopics().contains(new Topic("completeness")));
    }
}
