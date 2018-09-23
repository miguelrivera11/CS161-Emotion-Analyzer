package com.example.smart.emotionanalyzer;

import java.util.ArrayList;

public class Comment {
    String comment;
    String creator;
    String date;
    ArrayList<Comment> replies;

    public Comment(String comment, String creator, String date) {
        this.comment = comment;
        this.creator = creator;
        this.date = date;
        replies = new ArrayList<>();
    }

    public String getCreator() {
        return creator;
    }

    public ArrayList<Comment> getReplies() {
        return replies;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public void addReply(Comment reply) {
        replies.add(reply);
    }
}
