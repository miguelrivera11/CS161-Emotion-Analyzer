package com.example.smart.emotionanalyzer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainFeedFragment extends Fragment {
    FirebaseDatabase database;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  view =  inflater.inflate(R.layout.fragment_main_feed, null);
        database = FirebaseDatabase.getInstance();
        final DatabaseReference topicRef = database.getReference("Topics");

        Button addTopic = view.findViewById(R.id.add_topic);
        addTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment comment = new Comment("This is my comment", "Miguel Rivera", "9-20-18");
                Comment reply = new Comment("This is my reply", "Albert Yu", "9-20-18");
                Comment comment2 = new Comment("This is another comment", "Miguel Rivera", "9-20-18");
                Comment reply2 = new Comment("This is another reply", "Albert Yu", "9-20-18");
                comment.addReply(reply);
                comment2.addReply(reply2);
                Topic test = new Topic("new Park", "miguel rivera", 5, 90, 0, 5, "9-20-18");
                test.addComment(comment);
                test.addComment(comment2);
                String id = topicRef.push().getKey();
                topicRef.child(id).setValue(test);
            }
        });


        return view;
    }

}
