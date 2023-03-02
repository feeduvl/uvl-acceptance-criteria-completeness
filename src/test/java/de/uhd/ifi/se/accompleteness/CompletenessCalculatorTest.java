package de.uhd.ifi.se.accompleteness;

import org.junit.Test;

import com.google.gson.JsonObject;

import de.uhd.ifi.se.accompleteness.calculation.CalculationParams;
import de.uhd.ifi.se.accompleteness.calculation.CompletenessCalculator;
import de.uhd.ifi.se.accompleteness.calculation.wordnet.WordnetCalculationParams;
import de.uhd.ifi.se.accompleteness.calculation.wordnet.WordnetCompletenessCalculator;
import de.uhd.ifi.se.accompleteness.model.CompletenessCalcResult;
import de.uhd.ifi.se.accompleteness.model.ExtractionResult;
import de.uhd.ifi.se.accompleteness.model.Relationship;
import de.uhd.ifi.se.accompleteness.model.Topic;
import de.uhd.ifi.se.accompleteness.model.UserStory;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

public class CompletenessCalculatorTest {
    
    @Test
    public void testCalculateCompletenessFull() throws Exception {
        List<Topic> usTopics = new ArrayList<>();
        List<Relationship> usRelationships = new ArrayList<>();
        usTopics.add(new Topic("mouse", "NOUN", 4, 9));

        List<Topic> acTopics = new ArrayList<>();
        List<Relationship> acRelationships = new ArrayList<>();
        acTopics.add(new Topic("mouse", "NOUN", 4, 9));

        // Initialize test input data
        ExtractionResult usResult = new ExtractionResult(usRelationships, usTopics);
        ExtractionResult acResult = new ExtractionResult(acRelationships, acTopics);
        CalculationParams params = new WordnetCalculationParams();
        JsonObject paramsJson = new JsonObject();
        paramsJson.addProperty("wordnetDistanceThreshold", 3);
        params.setCalculationParamsFromJson(paramsJson);
        UserStory userStory = new UserStory("As a person I want to have a mouse so that I can have a mouse.", "TEST-1", "The mouse");
        
        
        // Initialize the completeness calculator object to be tested
        CompletenessCalculator calculator = new WordnetCompletenessCalculator();
        
        // Call the calculate_completeness() method and get the actual result
        CompletenessCalcResult actualResult = calculator.calculate_completeness(usResult, acResult, params, userStory);
        
        // Assert that the actual result is not null
        assertEquals(1, actualResult.getCompleteness(), .01);
    }

    @Test
    public void testCalculateCompletenessZero() throws Exception {
        List<Topic> usTopics = new ArrayList<>();
        List<Relationship> usRelationships = new ArrayList<>();
        usTopics.add(new Topic("mouse", "NOUN", 4, 9));

        List<Topic> acTopics = new ArrayList<>();
        List<Relationship> acRelationships = new ArrayList<>();
        acTopics.add(new Topic("house", "NOUN", 4, 9));

        // Initialize test input data
        ExtractionResult usResult = new ExtractionResult(usRelationships, usTopics);
        ExtractionResult acResult = new ExtractionResult(acRelationships, acTopics);
        CalculationParams params = new WordnetCalculationParams();
        JsonObject paramsJson = new JsonObject();
        paramsJson.addProperty("wordnetDistanceThreshold", 3);
        params.setCalculationParamsFromJson(paramsJson);
        UserStory userStory = new UserStory("As a person I want to have a mouse so that I can have a mouse.", "TEST-1", "The house");
        
        
        // Initialize the completeness calculator object to be tested
        CompletenessCalculator calculator = new WordnetCompletenessCalculator();
        
        // Call the calculate_completeness() method and get the actual result
        CompletenessCalcResult actualResult = calculator.calculate_completeness(usResult, acResult, params, userStory);
        
        // Assert that the actual result is not null
        assertEquals(0, actualResult.getCompleteness(), .01);
    }

    @Test
    public void testCalculateCompletenessHalf() throws Exception {
        List<Topic> usTopics = new ArrayList<>();
        List<Relationship> usRelationships = new ArrayList<>();
        usTopics.add(new Topic("mouse", "NOUN", 4, 9));
        usTopics.add(new Topic("worker", "NOUN", 4, 9));

        List<Topic> acTopics = new ArrayList<>();
        List<Relationship> acRelationships = new ArrayList<>();
        acTopics.add(new Topic("house", "NOUN", 4, 9));
        acTopics.add(new Topic("worker", "NOUN", 4, 9));

        // Initialize test input data
        ExtractionResult usResult = new ExtractionResult(usRelationships, usTopics);
        ExtractionResult acResult = new ExtractionResult(acRelationships, acTopics);
        CalculationParams params = new WordnetCalculationParams();
        JsonObject paramsJson = new JsonObject();
        paramsJson.addProperty("wordnetDistanceThreshold", 3);
        params.setCalculationParamsFromJson(paramsJson);
        UserStory userStory = new UserStory("As a person I want to have a mouse so that I can have a mouse.", "TEST-1", "The house");
        
        
        // Initialize the completeness calculator object to be tested
        CompletenessCalculator calculator = new WordnetCompletenessCalculator();
        
        // Call the calculate_completeness() method and get the actual result
        CompletenessCalcResult actualResult = calculator.calculate_completeness(usResult, acResult, params, userStory);
        
        // Assert that the actual result is not null
        assertEquals(.5, actualResult.getCompleteness(), .01);
    }
}