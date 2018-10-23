package com.example.smart.emotionanalyzer;

import android.app.Activity;
import android.content.Context;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TopicDatabaseManager {
    DatabaseReference topicsRef;
    Activity context;

    public TopicDatabaseManager(Activity context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        topicsRef = database.getReference("Topics");
        this.context = context;
    }

    public void searchTopics(final ArrayList<String> searchTopicString, final ArrayList<Topic> results, final ListView listView) {
        topicsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    Topic topic = child.getValue(Topic.class);
                    topic.setTopicID(child.getKey().toString());
                    for (String s : searchTopicString) {
                        if (topic.getTopic().contains(s)) {
                            if (!results.contains(topic)) {
                                results.add(topic);
                            }
                        }
                    }
                }
                TopicList adapter = new TopicList(context, results);
                listView.setAdapter(adapter);
                //results.clear();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getAllTopicNames(final ArrayList<String> topicNames){
        topicsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                topicNames.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Topic topic = child.getValue(Topic.class);
                    topicNames.add(topic.getTopic());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getAllTopicsListView(final ArrayList<Topic> topics, final ListView listView) {
        topicsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                topics.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Topic topic = child.getValue(Topic.class);
                    topic.setTopicID(child.getKey().toString());
                    topics.add(0, topic);
                }
                if (listView != null) {
                    TopicList adapter = new TopicList(context, topics);
                    listView.setAdapter(adapter);
                }
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
                    topic.setTopicID(child.getKey().toString());
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
        for(final String topicID: topicIDs) {
            DatabaseReference topicRef = topicsRef.child(topicID);
            topicRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Topic topic = dataSnapshot.getValue(Topic.class);
                    topics.add(topic);
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
        topic.setTopicID(topicID);
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

    public void deleteTopic(String topicID) {
        DatabaseReference topicRef = topicsRef.child(topicID);
        topicRef.removeValue();
        UserManager userManager = new UserManager();
        userManager.deleteCreatedTopic(topicID);
    }

    public void addCommentToTopic(Topic topic, Comment comment) {
        topic.addComment(comment);
        topicsRef.child(topic.getTopicID()).setValue(topic);
    }

    public void getComments(String topicID, final ArrayList<Comment> comments, final ExpandableListView expandableListView,
    final List<String> listDataHeader, final HashMap<String, List<String>> listDataChild) {
        DatabaseReference topicRef = topicsRef.child(topicID);
        topicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments.clear();
                Topic topic = dataSnapshot.getValue(Topic.class);
                for (Comment c : topic.getComments()) {
                    comments.add(c);
                }

                prepareListData(comments, listDataHeader, listDataChild);
                ExpandableListAdapter listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
                expandableListView.setAdapter(listAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void prepareListData( ArrayList<Comment> comments, List<String> listDataHeader, HashMap<String, List<String>> listDataChild) {
        listDataHeader.clear();
        listDataChild.clear();

        int counter = 0;

        for (int i = comments.size() - 1; i >= 0; i--) {
            String message = comments.get(i).getComment();
            List<String> reply = new ArrayList<>();
            for (int j = comments.get(i).getReplies().size() - 1; j >= 0; j--) {
                String replyMessage = comments.get(i).getReplies().get(j).getComment();
                reply.add(replyMessage);
            }

            listDataHeader.add(message);
            listDataChild.put(listDataHeader.get(counter), reply);
            counter++;
        }
    }
}
