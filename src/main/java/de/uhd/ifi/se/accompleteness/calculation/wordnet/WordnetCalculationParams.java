package de.uhd.ifi.se.accompleteness.calculation.wordnet;

import com.google.gson.JsonObject;

import de.uhd.ifi.se.accompleteness.calculation.CalculationParams;

public class WordnetCalculationParams implements CalculationParams {

    private int wordnetDistanceThreshold;

    public int getWordnetDistanceThreshold() {
        return wordnetDistanceThreshold;
    }

    @Override
    public void setCalculationParamsFromJson(JsonObject params) {

        this.wordnetDistanceThreshold = params.get("wordnetDistanceThreshold").getAsInt();
    }

}
