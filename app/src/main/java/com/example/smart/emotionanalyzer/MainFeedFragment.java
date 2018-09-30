package com.example.smart.emotionanalyzer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;


public class MainFeedFragment extends Fragment {
    private DatabaseReference topicsRef;
    private ArrayList<Topic> topics;
    private ListView topicListView;
    boolean starting;

    @Override
    public void onStart() {
        super.onStart();
        starting = true;
        topicsRef.addValueEventListener(new ValueEventListener() {
            /*Look at the code below apparently this is always listening if the main activity is finished
            I noticed in addTopic we were getting a null exception and I linked it back to the context c being null
            here. Since the data changes, as soon as you tap add topic, this method gets called, but since you're
            on CreateTopic, this fragment doesn't exist and the activity doesnt exist. I fixed it by adding the
            starting boolean. Im not sure if there is a way to get this to stop listening. I tried
            AddListenerforSingleValueEvent but it sill had the same issue
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (starting) {
                    topics.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Topic topic = child.getValue(Topic.class);
                        topics.add(topic);
                    }
                    Context c = getActivity();

                    TopicList adapter = new TopicList(getActivity(), topics);
                    topicListView.setAdapter(adapter);
                    starting = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  view =  inflater.inflate(R.layout.fragment_main_feed, null);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        topicsRef = database.getReference("Topics");
        final Random r = new Random();
        topicListView = view.findViewById(R.id.list_of_topics);
        topics = new ArrayList<>();

        Button addTopic = view.findViewById(R.id.add_topic);
        addTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CHANGE SO IT GOES TO CREATE TOPIC ACTIVITY
                Topic test = new Topic("" + r.nextInt(), "miguel rivera", 5, 90, 0, 5, "9-20-18", "news");
                String id = topicsRef.push().getKey();
                topicsRef.child(id).setValue(test);
            }
        });


        return view;
    }
}
