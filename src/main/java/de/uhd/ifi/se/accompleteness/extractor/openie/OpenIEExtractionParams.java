package de.uhd.ifi.se.accompleteness.extractor.openie;

import com.google.gson.JsonObject;

import de.uhd.ifi.se.accompleteness.extractor.ExtractionParams;

public class OpenIEExtractionParams implements ExtractionParams {
    private boolean debug;

    private boolean filterUSTopicsExcludeList;

    private boolean filterUSTopicsSimilarity;
    private double filterUSTopicsSimilarityThreshold;

    private boolean filterUSTopicsCompositions;
    private int filterUSTopicsCompositionsMinLength;

    private double OpenIEConfidence;

    @Override
    public void setExtractionParamsFromJson(JsonObject params) {
        this.debug = params.get("debug").getAsBoolean();
        
        this.filterUSTopicsExcludeList = params.get("filterUSTopicsExcludeList").getAsBoolean();

        this.filterUSTopicsSimilarity = params.get("filterUSTopicsSimilarity").getAsBoolean();
        this.filterUSTopicsSimilarityThreshold = params.get("filterUSTopicsSimilarityThreshold").getAsDouble();
        
        this.filterUSTopicsCompositions = params.get("filterUSTopicsCompositions").getAsBoolean();
        this.filterUSTopicsCompositionsMinLength = params.get("filterUSTopicsCompositionsMinLength").getAsInt();
        
        this.OpenIEConfidence = params.get("OpenIEConfidence").getAsDouble();
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isFilterUSTopicsExcludeList() {
        return filterUSTopicsExcludeList;
    }

    public boolean isFilterUSTopicsSimilarity() {
        return filterUSTopicsSimilarity;
    }

    public double getFilterUSTopicsSimilarityThreshold() {
        return filterUSTopicsSimilarityThreshold;
    }

    public boolean isFilterUSTopicsCompositions() {
        return filterUSTopicsCompositions;
    }

    public int getFilterUSTopicsCompositionsMinLength() {
        return filterUSTopicsCompositionsMinLength;
    }

    public double getOpenIEConfidence() {
        return OpenIEConfidence;
    }

}
