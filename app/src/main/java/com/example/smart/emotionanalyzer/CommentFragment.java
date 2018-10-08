package com.example.smart.emotionanalyzer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ExpandableListView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {

                int actualI = comments.size() - i - 1;

                Toast.makeText(getContext(), comments.get(actualI).getComment(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
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
                            Boolean same = false;
                            User userObject = dataSnapshot.getValue(User.class);
                            for (int i = 0; i < userObject.getCommentedTopics().size(); i++) {
                                if (!userObject.getCommentedTopics().get(i).equals(a.getTopic()))
                                    continue;
                                same = true;
                            }
                            if (!same) {
                                userObject.addCommentedTopic(a.getTopic());
                                usersRef.setValue(userObject);
                            }


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
                    Toast.makeText(getActivity(), commentEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                    comments = new ArrayList<>();
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
    }
}
