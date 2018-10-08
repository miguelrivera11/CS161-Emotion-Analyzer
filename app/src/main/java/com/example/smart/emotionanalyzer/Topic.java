package com.example.smart.emotionanalyzer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Topic implements Parcelable {
    String topic;
    String creator;
    String creatorID;
    String date;
    int happy;
    int sad;
    int angry;
    int neutral;
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
        this.angry = in.readInt();
        this.happy = in.readInt();
        this.sad = in.readInt();
        this.neutral = in.readInt();
        this.date = in.readString();
        this.category = in.readString();

    }

    public Topic() {
        if (comments == null) {
            comments = new ArrayList<>();
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
     * @param neutral
     * @param date
     * @param category
     */
    public Topic (String topic, String creator,String creatorID, int angry, int happy, int sad, int neutral, String date, String category) {
        this.topic = topic;
        this.creator = creator;
        this.creatorID = creatorID;
        this.angry = angry;
        this.happy = happy;
        this.sad = sad;
        this.neutral = neutral;
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

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(topic);
        parcel.writeString(creator);
        parcel.writeString(creatorID);
        parcel.writeInt(angry);
        parcel.writeInt(happy);
        parcel.writeInt(sad);
        parcel.writeInt(neutral);
        parcel.writeString(date);
        parcel.writeString(category);
        //parcel.writeList(comments);

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
}
