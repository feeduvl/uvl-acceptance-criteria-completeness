package de.uhd.ifi.se.accompleteness.model;

public class Relationship {
    Topic left_topic;
    Topic right_topic;
    String relationship;

    public Relationship(Topic left_topic, Topic right_topic, String relationship) {
        this.left_topic = left_topic;
        this.right_topic = right_topic;
        this.relationship = relationship;
    }

    @Override
    public boolean equals(Object arg0) {
        if (!(arg0 instanceof Relationship)) {
            return false;
        }
        Relationship r = (Relationship) arg0;
        return r.left_topic.topic.equals(this.left_topic.topic) && r.right_topic.topic.equals(this.right_topic.topic);
    }
}
