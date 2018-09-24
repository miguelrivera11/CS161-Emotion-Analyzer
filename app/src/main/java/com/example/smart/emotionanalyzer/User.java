package com.example.smart.emotionanalyzer;

import java.util.ArrayList;

public class User {
    String name;
    ArrayList<String> createdTopics;
    ArrayList<String> commentedTopics;

    public User() {
        if (createdTopics == null) {
            createdTopics = new ArrayList<>();
        }
        if (commentedTopics == null) {
            commentedTopics = new ArrayList<>();
        }
    }

    public User (String name) {
        this.name = name;
        createdTopics = new ArrayList<>();
        commentedTopics = new ArrayList<>();
    }

    public ArrayList<String> getCommentedTopics() {
        return commentedTopics;
    }

    public ArrayList<String> getCreatedTopics() {
        return createdTopics;
    }

    public String getName() {
        return name;
    }

    public void addCreatedTopic(String topic) {
        createdTopics.add(topic);
    }

    public void addCommentedTopic(String topic) {
        commentedTopics.add(topic);
    }
}
