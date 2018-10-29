package com.example.smart.emotionanalyzer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;


public class MainFeedFragment extends Fragment {
    private ArrayList<Topic> topics;
    private ListView topicListView;
    boolean added = false;
    ArrayList<String> searchTopicString;
    ArrayList<Topic> searchTopic;
    private ActivityManager activityManager;
    private TopicDatabaseManager topicManager;
    private UserManager userManager;
    @Override
    public void onStart() {
        super.onStart();
        MainActivity activity = (MainActivity) getActivity();
        Bundle args = getArguments();
        if (args == null || !args.getBoolean("search")) {
           topicManager.getAllTopicsListView(topics, topicListView);
        }
        else {
            searchTopicString = getArguments().getStringArrayList("topiclist");
            topicManager.searchTopics(searchTopicString, searchTopic, topicListView);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  view =  inflater.inflate(R.layout.fragment_main_feed, null);
        topicManager = new TopicDatabaseManager(getActivity());
        activityManager = new ActivityManager(getActivity());
        userManager = new UserManager();
        topicListView = view.findViewById(R.id.list_of_topics);
        topics = new ArrayList<>();
        searchTopic = new ArrayList<>();
        topicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Topic t = (Topic) topics.get(i);
                Bundle bundle = getActivity().getIntent().getExtras();
                bundle.putParcelable("topic", t);
                bundle.putString("topicID", t.getTopicID());
                bundle.putString("fragment_detail", "Analysis");
                activityManager.changeActivty(TopicDetail.class, bundle);
            }
        });
        Button addTopic = view.findViewById(R.id.add_topic);
        addTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getActivity().getIntent().getExtras();
                bundle.putString("user", userManager.getName());
                activityManager.changeActivty(CreateTopicActivity.class, bundle);
            }
        });


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///THIS BUTTON AUTO ADDS A TOPIC TO DATABASE WITH COMMENTS AND REPLIES
        //IT IS FOR QUICK ADDING FOR THE SAKE OF TESTING
        //WE WILL REMOVE IT AFTER WE ARE DONE
        //DELETE FROM START TO END AND DELETE BUTTON FROM XML
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// START
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Button autoAdd = view.findViewById(R.id.auto_add);
        autoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userManager.getName();
                String creatorId = userManager.getUserID();
                Comment comment = new Comment("TEST COMMENT", "Random Person", "MadeUpID", "9/29/18", "Happy");
                Comment comment2 = new Comment("TEST COMMENT FROM CURRENT USER", name, creatorId, "9/29/18", "Happy");
                Comment reply = new Comment("TEST REPLY", name, creatorId, "9/29/18", "");
                comment.addReply(reply);
                Topic topic = new Topic("TEST TOPIC",name,creatorId, 32,32,32,4,"9/29/18", "Education");
                topic.addComment(comment);
                topic.addComment(comment2);
                topicManager.addTopic(topic);
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///END
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return view;
    }

}
