package com.example.smart.emotionanalyzer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Comment implements Parcelable{
    String comment;
    String creator;
    String creatorID;
    String date;
    ArrayList<Comment> replies;
    boolean isReply;

    /**
     * Use when reconstructing User object from parcel
     * This will be used only by the 'CREATOR'
     * @param in a parcel to read this object
     */
    public Comment(Parcel in) {
        this.comment = in.readString();
        this.creator = in.readString();
        this.creatorID = in.readString();
        this.date = in.readString();
        this.replies = in.readArrayList(Comment.class.getClassLoader());
        this.isReply = (in.readInt() == 0)? false: true;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(comment);
        parcel.writeString(creator);
        parcel.writeString(creatorID);
        parcel.writeString(date);
        parcel.writeList(replies);
        int n = (isReply)? 1:0;
        parcel.writeInt(n);
    }

    public Comment() {
        if (replies == null) {
            replies = new ArrayList<>();
        }
    }

    public Comment(String comment, String creator, String creatorID, String date) {
        this.comment = comment;
        this.creator = creator;
        this.creatorID = creatorID;
        this.date = date;
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
    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {

        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public int hashCode() {
        return (comment + creatorID).hashCode();
    }
}
