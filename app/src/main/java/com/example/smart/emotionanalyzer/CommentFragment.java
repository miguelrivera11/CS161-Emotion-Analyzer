package com.example.smart.emotionanalyzer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class CommentFragment extends Fragment {

    private ArrayList<Comment> comments;
    ExpandableListAdapter listAdapter;
    ExpandableListView expandableListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    private UserManager userManager;
    private TopicDatabaseManager topicDatabaseManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        userManager = new UserManager();
        topicDatabaseManager = new TopicDatabaseManager(getActivity());
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
                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    Date date = new Date();
                    final Comment comment = new Comment(commentEditText.getText().toString(), userManager.getName(),userManager.getUserID(), formatter.format(date).toString());
                    userManager.addCommentedTopic(a.getTopicID());
                    //TODO: Only rewrite comments instead of entire topic
                    topicDatabaseManager.addCommentToTopic(a, comment);
                    //Toast.makeText(getActivity(), commentEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                    comments = new ArrayList<>();
                    commentEditText.setText("");
                    return true;
                }
                return false;
            }
        });

        topicDatabaseManager.getComments(a.getTopicID(), comments, expandableListView, listDataHeader, listDataChild);
        return view;

    }


}
