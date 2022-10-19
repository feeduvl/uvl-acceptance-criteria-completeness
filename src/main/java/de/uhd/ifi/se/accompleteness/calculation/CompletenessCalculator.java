package de.uhd.ifi.se.accompleteness.calculation;

import de.uhd.ifi.se.accompleteness.model.CompletenessCalcResult;
import de.uhd.ifi.se.accompleteness.model.NLPResultSingle;
import net.sf.extjwnl.JWNLException;

public interface CompletenessCalculator {
    public CompletenessCalcResult calculate_completeness(NLPResultSingle usResult, NLPResultSingle acResult) throws JWNLException, CloneNotSupportedException, Exception;
}
