package com.example.smart.emotionanalyzer;

import android.app.Activity;
import android.content.Context;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TopicDatabaseManager {
    DatabaseReference topicsRef;
    Activity context;

    public TopicDatabaseManager(Activity context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        topicsRef = database.getReference("Topics");
        this.context = context;
    }

    public void getTopicsListAllTopics(final ArrayList<Topic> topics, final ListView listView) {
        topicsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                topics.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Topic topic = child.getValue(Topic.class);
                    topics.add(topic);
                }
                TopicList adapter = new TopicList(context, topics);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getTopicsListByCategory(final String category, final ArrayList<Topic> topics, final ListView listView) {
        topicsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                topics.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Topic topic = child.getValue(Topic.class);
                    if (topic.getCategory().toLowerCase().equals(category.toLowerCase())) {
                        topics.add(topic);
                    }
                }
                TopicList adapter = new TopicList(context, topics);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getPreviouslyCommentedTopics(final ArrayList<Topic> topics, final ListView listView) {
        UserManager userManager = new UserManager();
        DatabaseReference userRef = userManager.getUserRef();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                getTopicsbyIDs(user.getCommentedTopics(), topics, listView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getCreatedTopics(final ArrayList<Topic> topics, final ListView listView) {
        UserManager userManager = new UserManager();
        DatabaseReference userRef = userManager.getUserRef();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                getTopicsbyIDs(user.getCreatedTopics(), topics, listView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getTopicsbyIDs(final ArrayList<String> topicIDs, final ArrayList<Topic> topics, final ListView listView) {
        for(String topicID: topicIDs) {
            DatabaseReference topicRef = topicsRef.child(topicID);
            topicRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    topics.add(dataSnapshot.getValue(Topic.class));
                    TopicList adapter = new TopicList(context, topics);
                    listView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void addTopic(Topic topic) {
        String topicID = topicsRef.push().getKey();
        UserManager userManager = new UserManager();
        userManager.addCreatedTopic(topicID);
        topicsRef.child(topicID).setValue(topic);
    }

    public void updateDatabaseforNameChange(final String name) {
        final UserManager userManager = new UserManager();
        DatabaseReference userRef = userManager.getUserRef();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                updateTopicsAndComments(user.getCreatedTopics(), user.getCommentedTopics(), name, userManager.getUserID());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateTopicsAndComments(ArrayList<String> createdTopicIDs, final ArrayList<String> commentedTopicIDs, final String name, final String uid) {
        final ArrayList<String> commentedButNotCreated = new ArrayList<>();
        final ArrayList<String> commentedAndCreated = new ArrayList<>();
        for (String topicID: commentedTopicIDs) {
            if (createdTopicIDs.contains(topicID)) {
                commentedAndCreated.add(topicID);
            }
            else {
                commentedButNotCreated.add(topicID);
            }
        }
        updateCreatedAndCommented(createdTopicIDs, commentedAndCreated, name, uid);
        updateCommentedButNotCreated(commentedButNotCreated, name, uid);
    }

    private void updateCreatedAndCommented(final ArrayList<String> createdTopicIDs, final ArrayList<String> commentedAndCreated,final String name, final String uid) {
        for (final String topicID: createdTopicIDs){
            final DatabaseReference topicRef = topicsRef.child(topicID);
            topicRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Topic topic = dataSnapshot.getValue(Topic.class);
                    topic.setCreator(name);
                    if (commentedAndCreated.contains(topicID)){
                        for (Comment comment: topic.getComments()) {
                            if (comment.getCreatorID().equals(uid)){
                                comment.setCreator(name);
                            }
                            for (Comment reply: comment.getReplies()) {
                                if (reply.getCreatorID().equals(uid)) {
                                    reply.setCreator(name);
                                }
                            }
                        }
                    }
                    topicRef.setValue(topic);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void updateCommentedButNotCreated(ArrayList<String> commentedButNotCreated,final String name, final String uid) {
        for (final String topicID: commentedButNotCreated){
            final DatabaseReference topicRef = topicsRef.child(topicID);
            topicRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Topic topic = dataSnapshot.getValue(Topic.class);
                    for (Comment comment: topic.getComments()) {
                        if (comment.getCreatorID().equals(uid)) {
                            comment.setCreator(name);
                            for (Comment reply: comment.getReplies()) {
                                if (reply.getCreatorID().equals(uid)) {
                                    reply.setCreator(name);
                                }
                            }
                        }
                    }
                    topicRef.setValue(topic);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


}
