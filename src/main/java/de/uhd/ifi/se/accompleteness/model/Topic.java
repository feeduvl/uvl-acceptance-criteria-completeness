package de.uhd.ifi.se.accompleteness.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.extjwnl.data.POS;

public class Topic  {
    String topic;
    String tag;
    int startPosition;
    int endPosition;

    public Topic(String topic) {
        this.topic = topic;
        this.tag = "";
    }

    public Topic(String topic, String tag, int startPosition, int endPosition) {
        this.topic = topic;
        this.tag = tag;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public POS getPOSTag() throws Exception {
        String newTag = this.tag.substring(0,1).toLowerCase();
        List<String> correctTags = new ArrayList<String>(Arrays.asList("a", "n", "v", "r"));
        if (correctTags.contains(newTag)) {
            POS pos = POS.getPOSForKey(newTag);
            return pos;
        }
        
        return null;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    @Override
    public String toString() {
        return topic;
    }

    @Override
    public boolean equals(Object arg0) {
        if (!(arg0 instanceof Topic)) {
            return false;
        }
        Topic t = (Topic) arg0;
        return t.topic.equals(this.topic);
    }
}