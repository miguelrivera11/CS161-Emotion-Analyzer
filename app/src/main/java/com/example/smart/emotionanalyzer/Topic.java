package com.example.smart.emotionanalyzer;

import java.util.ArrayList;

public class Topic {
    String topic;
    String creator;
    String date;
    int happy;
    int sad;
    int angry;
    int neutral;
    ArrayList<Comment> comments;
    String category;

    public Topic() {
        if (comments == null) {
            comments = new ArrayList<>();
        }
    }

    public Topic (String topic, String creator, int angry, int happy, int sad, int neutral, String date, String category) {
        this.topic = topic;
        this.creator = creator;
        this.comments = comments;
        this.angry = angry;
        this.neutral = neutral;
        this.happy = happy;
        this.date = date;
        this.category = category;
        comments = new ArrayList<>();
    }

    public String getCreator() {
        return creator;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public String getTopic() {
        return topic;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public String getDate() {
        return date;
    }

    public int getHappy() {
        return happy;
    }

    public int getAngry() {
        return angry;
    }

    public int getNeutral() {
        return neutral;
    }

    public int getSad() {
        return sad;
    }

    public String getCategory() {
        return category;
    }
}
