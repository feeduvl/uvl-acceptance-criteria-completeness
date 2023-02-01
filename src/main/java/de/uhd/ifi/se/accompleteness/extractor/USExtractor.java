package de.uhd.ifi.se.accompleteness.extractor;

import de.uhd.ifi.se.accompleteness.model.ExtractionResult;
import de.uhd.ifi.se.accompleteness.exception.TokenNotFoundException;
import de.uhd.ifi.se.accompleteness.model.UserStory;

/**
 * Generates acceptance criteria for a user story.
 */
public interface USExtractor {
    
    /**
     * Generates acceptance criteria for a user story.
     * 
     * @param userStory the user story acceptance criteria are generated for
     * @param debug whether to include debug information
     * @return a list of acceptance criteria
     * @throws TokenNotFoundException if an essential part of a user story such
     * as the verb or the subject could not be identified by the Stanford
     * CoreNLP tools.
     */
    public ExtractionResult extract(UserStory userStory, ExtractionParams params);

}
