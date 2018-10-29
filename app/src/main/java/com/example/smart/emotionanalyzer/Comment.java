package com.example.smart.emotionanalyzer;

import java.util.ArrayList;

public class Comment {
    String comment;
    String creator;
    String creatorID;
    String date;
    String emotion;
    ArrayList<Comment> replies;
    boolean isReply;

    public Comment() {
        if (replies == null) {
            replies = new ArrayList<>();
        }
    }

    public Comment(String comment, String creator, String creatorID, String date, String emotion) {
        this.comment = comment;
        this.creator = creator;
        this.creatorID = creatorID;
        this.date = date;
        this.emotion = emotion;
        isReply = false;
        replies = new ArrayList<>();
    }

    public String getCreator() {
        return creator;
    }

    public String getCreatorID() {
        return creatorID;
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

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void addReply(Comment reply) {
        replies.add(reply);
    }

    public String getEmotion() {
        return this.emotion;
    }
}
