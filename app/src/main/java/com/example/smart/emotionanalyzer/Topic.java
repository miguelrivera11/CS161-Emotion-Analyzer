package com.example.smart.emotionanalyzer;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Topic implements Parcelable {
    String topic;
    String creator;
    String creatorID;
    String topicID;
    String date;
    int happy;
    int sad;
    int angry;
    ArrayList<Comment> comments;
    String category;

    /**
     * Use when reconstructing User object from parcel
     * This will be used only by the 'CREATOR'
     * @param in a parcel to read this object
     */
    public Topic(Parcel in) {
        this.topic = in.readString();
        this.creator = in.readString();
        this.creatorID = in.readString();
        this.topicID = in.readString();
        this.angry = in.readInt();
        this.happy = in.readInt();
        this.sad = in.readInt();
        this.date = in.readString();
        this.category = in.readString();
        this.comments = in.readArrayList(Comment.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(topic);
        parcel.writeString(creator);
        parcel.writeString(creatorID);
        parcel.writeString(topicID);
        parcel.writeInt(angry);
        parcel.writeInt(happy);
        parcel.writeInt(sad);
        parcel.writeString(date);
        parcel.writeString(category);
        parcel.writeList(comments);
    }

    public Topic() {
        if (comments == null) {
            comments = new ArrayList<>();
            topicID = "";
        }
    }


    /**
     *
     * @param topic
     * @param creator
     * @param creatorID
     * @param angry
     * @param happy
     * @param sad
     * @param date
     * @param category
     */
    public Topic (String topic, String creator,String creatorID, int angry, int happy, int sad, String date, String category) {
        this.topic = topic;
        this.creator = creator;
        this.creatorID = creatorID;
        this.angry = angry;
        this.happy = happy;
        this.sad = sad;
        this.date = date;
        this.category = category;
        topicID = "";
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

    public int getSad() {
        return sad;
    }


    public void setHappy(int happy) {
        this.happy = happy;
    }

    public void setSad(int sad) {
        this.sad = sad;
    }

    public void setAngry(int angry) {
        this.angry = angry;
    }
    public String getCategory() {
        return category;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getTopicID() {
        return topicID;
    }

    public void setTopicID(String topicID) {
        this.topicID = topicID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * This field is needed for Android to be able to
     * create new objects, individually or as arrays
     *
     * If you don’t do that, Android framework will through exception
     * Parcelable protocol requires a Parcelable.Creator object called CREATOR
     */
    public static final Parcelable.Creator<Topic> CREATOR = new Parcelable.Creator<Topic>() {

        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };

    @Override
    public int hashCode() {
        return (this.getTopic()).hashCode();
    }

    public void removeReply(Comment replyToRemove) {
        String parentComment = replyToRemove.parentComment;
        String parentCommentCreatorId = replyToRemove.parentCommentCreatorID;
        for (Comment comment: comments) {
            if (comment.comment.equals(parentComment) && comment.creatorID.equals(parentCommentCreatorId)) {
                for (int i = 0; i < comment.replies.size(); i++) {
                    Comment reply = comment.replies.get(i);
                    if (reply.equals(replyToRemove)) {
                        comment.replies.remove(i);
                    }
                }
            }
        }
    }

    public void removeComment(Comment commentToRemove) {
        for (int i = 0; i < comments.size(); i++) {
            Comment comment = comments.get(i);
            if (comment.equals(commentToRemove)) {
                String emotion = commentToRemove.getEmotion();
                if (emotion.equals("Happy")) {
                    happy -= 1;
                }
                else if (emotion.equals("Sad")) {
                    sad -= 1;
                }
                else if (emotion.equals("Angry")) {
                    angry -= 1;
                }
                comments.remove(i);
            }
        }
    }
}
