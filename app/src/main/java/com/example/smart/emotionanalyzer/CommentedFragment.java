package com.example.smart.emotionanalyzer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CommentedFragment extends Fragment {

    ArrayList<Topic> topics;
    ListView topicListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_commented, null);

        topics = new ArrayList<>();
        topicListView = view.findViewById(R.id.previously_commented_list);

        return view;
    }

    private void getPreviouslyCommented() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("Users/" + currentUser.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                getTopics(user.getCommentedTopics());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void getTopics(final ArrayList<String> previoulsyCommented) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference topicsRef = database.getReference("Topics");
        topicsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                topics.clear();
                int count = 0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Topic topic = child.getValue(Topic.class);
                    if (previoulsyCommented.contains(topic.getTopic())) {
                        topics.add(topic);
                        count += 1;
                    }
                    if (count >= previoulsyCommented.size()) break;
                }

                TopicList adapter = new TopicList(getActivity(), topics);
                topicListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
