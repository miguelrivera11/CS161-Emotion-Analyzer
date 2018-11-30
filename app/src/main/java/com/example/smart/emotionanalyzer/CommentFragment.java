package com.example.smart.emotionanalyzer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
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
    Spinner filterSpinner;
    private UserManager userManager;
    private TopicDatabaseManager topicDatabaseManager;
    private EmotionClassifier classifier;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        try {
            classifier = new EmotionClassifier(getActivity());
        }
        catch (IOException e) {
            classifier = null;
        }
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        userManager = new UserManager();
        topicDatabaseManager = new TopicDatabaseManager(getActivity());
        comments = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_comment, null);
        expandableListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        filterSpinner = (Spinner) view.findViewById(R.id.filterEmotionSpinner);

        TopicDetail activity = (TopicDetail) getActivity();
        final Topic a = activity.getTopic();
        final EditText commentEditText = (EditText) view.findViewById(R.id.editTextComment);

        List<String> emotions = new ArrayList<>();
        emotions.add("All");
        emotions.add("Angry");
        emotions.add("Happy");
        emotions.add("Sad");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, emotions);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(dataAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                switch(selected) {
                    case "All" :
                        topicDatabaseManager.getComments(a.getTopicID(), comments, expandableListView, listDataHeader, listDataChild);
                        break;
                    case "Happy" :
                        topicDatabaseManager.getHappyComments(a.getTopicID(), comments, expandableListView, listDataHeader, listDataChild);
                        break;
                    case "Sad" :
                        topicDatabaseManager.getSadComments(a.getTopicID(), comments, expandableListView, listDataHeader, listDataChild);
                        break;
                    case "Angry" :
                        topicDatabaseManager.getAngryComments(a.getTopicID(), comments, expandableListView, listDataHeader, listDataChild);
                        break;
                    default:
                        topicDatabaseManager.getComments(a.getTopicID(), comments, expandableListView, listDataHeader, listDataChild);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /*filterSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                switch(selected) {
                    case "All" :
                        topicDatabaseManager.getComments(a.getTopicID(), comments, expandableListView, listDataHeader, listDataChild);
                        break;
                    case "Happy" :
                        topicDatabaseManager.getHappyComments(a.getTopicID(), comments, expandableListView, listDataHeader, listDataChild);
                        break;
                    case "Neutral" :
                        topicDatabaseManager.getNeutralComments(a.getTopicID(), comments, expandableListView, listDataHeader, listDataChild);
                        break;
                    case "Sad" :
                        topicDatabaseManager.getSadComments(a.getTopicID(), comments, expandableListView, listDataHeader, listDataChild);
                        break;
                    case "Angry" :
                        topicDatabaseManager.getAngryComments(a.getTopicID(), comments, expandableListView, listDataHeader, listDataChild);
                        break;
                    default:
                        topicDatabaseManager.getComments(a.getTopicID(), comments, expandableListView, listDataHeader, listDataChild);
                        break;

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/



        commentEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                // If the event is a key-down event on the "enter" button
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    Date date = new Date();

                    try {
                        int emotionDetected = classifier.predict(commentEditText.getText().toString());
                        String emotion = "";
                        if (emotionDetected == EmotionClassifier.HAPPY) {
                            a.setHappy(a.getHappy() + 1);
                            emotion = "Happy";
                        }
                        if (emotionDetected == EmotionClassifier.ANGRY) {
                            a.setAngry(a.getAngry() + 1);
                            emotion = "Angry";
                        }
                        if (emotionDetected == EmotionClassifier.SAD) {
                            a.setSad(a.getSad() + 1);
                            emotion = "Sad";
                        }
                        final Comment comment = new Comment(commentEditText.getText().toString(), userManager.getName(),userManager.getUserID(), formatter.format(date).toString(), emotion);
                        comment.isReply = false;
                        userManager.addCommentedTopic(a.getTopicID());
                        topicDatabaseManager.addCommentToTopic(a, comment);

                    } catch(NullPointerException e) {
                        final Comment comment = new Comment(commentEditText.getText().toString(), userManager.getName(),userManager.getUserID(), formatter.format(date).toString(),"");
                        userManager.addCommentedTopic(a.getTopicID());
                        topicDatabaseManager.addCommentToTopic(a, comment);
                    }

                    comments = new ArrayList<>();
                    commentEditText.setText("");
                    return true;
                }
                return false;
            }
        });


        return view;

    }

}

