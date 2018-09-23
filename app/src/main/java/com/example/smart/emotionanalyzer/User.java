package com.example.smart.emotionanalyzer;

import java.util.ArrayList;

public class User {
    String name;
    ArrayList<String> createdTopics;
    ArrayList<String> commentedTopics;

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
}
