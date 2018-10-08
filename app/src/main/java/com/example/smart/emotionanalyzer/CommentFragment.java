package com.example.smart.emotionanalyzer;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;
import android.support.annotation.NonNull;

import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.app.ProgressDialog;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CommentFragment extends Fragment {

    private DatabaseReference topicsRef;
    private DatabaseReference usersRef;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private ArrayList<Comment> comments;
    ExpandableListAdapter listAdapter;
    ExpandableListView expandableListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        topicsRef = database.getReference("Topics");
        usersRef = database.getReference("Users/" + user.getUid());
        comments = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_comment, null);
        expandableListView = (ExpandableListView) view.findViewById(R.id.lvExp);

        TopicDetail activity = (TopicDetail) getActivity();
        final Topic a = activity.getTopic();
        final EditText commentEditText = (EditText) view.findViewById(R.id.editTextComment);

        commentEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                // If the event is a key-down event on the "enter" button
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    final Comment comment = new Comment(commentEditText.getText().toString(), user.getDisplayName(),user.getUid(), a.getDate());
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User userObject = dataSnapshot.getValue(User.class);
                            userObject.addCreatedTopic(a.getTopic());
                            userObject.addCommentedTopic(a.getTopic());
                            usersRef.setValue(userObject);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    topicsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot topicSnapShot : dataSnapshot.getChildren()) {
                                Topic compare = topicSnapShot.getValue(Topic.class);
                                if (a.getTopic().equals(compare.getTopic())) {
                                    compare.addComment(comment);

                                    String id = topicSnapShot.getKey();
                                    topicsRef.child(id).setValue(compare);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                            //Comment comment = new Comment("TEST COMMENT", "Random Person", "MadeUpID", "9/29/18");
                    Toast.makeText(getActivity(), commentEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                    commentEditText.setText("");
                    return true;
                }
                return false;
            }
        });


        topicsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot topicSnapShot : dataSnapshot.getChildren()) {
                    Topic compare = topicSnapShot.getValue(Topic.class);
                    if (compare.getTopic().equals(a.getTopic())) {
                        for (Comment c : compare.getComments()) {
                            comments.add(c);
                        }
                    }
                }

                prepareListData();
                listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
                expandableListView.setAdapter(listAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;

    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

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

        // Adding parent data(
        /*listDataHeader.add("Top 250asdfaslkdfj;asdkjf;alsdkjf;laskdjf;laskjdf;lakjdf;laskjdf;laskjdf;lakjdf;lajsdkfl;jas;dlfjkas;ldfjka;lsdjkf;alsdf");
        listDataHeader.add("Now Showing");
        listDataHeader.add("Coming Soon..");

        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");

        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);*/
    }

}
