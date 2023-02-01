package de.uhd.ifi.se.accompleteness.calculation;

import de.uhd.ifi.se.accompleteness.model.CompletenessCalcResult;
import de.uhd.ifi.se.accompleteness.model.ExtractionResult;
import de.uhd.ifi.se.accompleteness.model.UserStory;
import net.sf.extjwnl.JWNLException;

public interface CompletenessCalculator {
    public CompletenessCalcResult calculate_completeness(ExtractionResult usResult, ExtractionResult acResult, CalculationParams params, UserStory userStory) throws JWNLException, CloneNotSupportedException, Exception;
}
