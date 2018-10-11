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

public class TopicDatabaseManager {
    DatabaseReference topicsRef;
    Activity context;

    public TopicDatabaseManager(Activity context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        topicsRef = database.getReference("Topics");
        this.context = context;
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
}
