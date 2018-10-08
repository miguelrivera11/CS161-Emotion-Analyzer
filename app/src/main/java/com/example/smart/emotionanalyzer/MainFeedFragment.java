package com.example.smart.emotionanalyzer;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseAuth mAuth;
    //DELETE
    boolean added = false;

    @Override
    public void onStart() {
        super.onStart();
        starting = true;
        topicsRef.addValueEventListener(new ValueEventListener() {
            /*Look at the code below apparently this is always listening even if the main activity is finished
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
                        topics.add(0, topic);
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
        topicListView = view.findViewById(R.id.list_of_topics);
        topics = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        topicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Topic t = (Topic) topics.get(i);
                Intent intent = new Intent(getActivity(), TopicDetail.class);
                intent.putExtra("fragment_detail", "Analysis");
                intent.putExtra("topic", t);
                startActivity(intent);
            }
        });
        Button addTopic = view.findViewById(R.id.add_topic);
        addTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateTopicActivity.class);
                intent.putExtra("user", mAuth.getCurrentUser().getDisplayName());

                startActivity(intent);
            }
        });


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///THIS BUTTON AUTO ADDS A TOPIC TO DATABASE WITH COMMENTS AND REPLIES
        //IT IS FOR QUICK ADDING FOR THE SAKE OF TESTING
        //WE WILL REMOVE IT AFTER WE ARE DONE
        //DELETE FROM START TO END AND DELETE BUTTON
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// START
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Button autoAdd = view.findViewById(R.id.auto_add);
        autoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                String name = user.getDisplayName();
                String creatorId = user.getUid();
                Comment comment = new Comment("TEST COMMENT", "Random Person", "MadeUpID", "9/29/18");
                Comment comment2 = new Comment("TEST COMMENT FROM CURRENT USER", name, creatorId, "9/29/18");
                Comment reply = new Comment("TEST REPLY", name, creatorId, "9/29/18");
                comment.addReply(reply);
                Topic topic = new Topic("TEST TOPIC",name,creatorId, 32,32,32,32,"9/29/18", "Education");
                topic.addComment(comment);
                topic.addComment(comment2);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference topicsRef = database.getReference("Topics");
                String id = topicsRef.push().getKey();
                topicsRef.child(id).setValue(topic);
                final DatabaseReference userRef = database.getReference("Users/" + user.getUid());
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Method gets called twice since we are reading and writing to the database. Using the added boolean it only gets called once
                        if (!added) {
                            User userObject = dataSnapshot.getValue(User.class);
                            userObject.addCreatedTopic("TEST TOPIC");
                            userObject.addCommentedTopic("TEST TOPIC");
                            userRef.setValue(userObject);
                            added = true;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///END
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return view;
    }

}
