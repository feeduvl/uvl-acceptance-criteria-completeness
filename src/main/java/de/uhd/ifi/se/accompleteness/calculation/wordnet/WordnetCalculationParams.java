package de.uhd.ifi.se.accompleteness.calculation.wordnet;

import com.google.gson.JsonObject;

import de.uhd.ifi.se.accompleteness.calculation.CalculationParams;

public class WordnetCalculationParams implements CalculationParams {

    private double wordnetAlpha;

    private int wordnetDistanceThreshold;

    public int getWordnetDistanceThreshold() {
        return wordnetDistanceThreshold;
    }

    public double getWordnetAlpha() {
        return wordnetAlpha;
    }

    @Override
    public void setCalculationParamsFromJson(JsonObject params) {

        this.wordnetAlpha = params.get("wordnetAlpha").getAsDouble();
        this.wordnetDistanceThreshold = params.get("wordnetDistanceThreshold").getAsInt();
    }

}
