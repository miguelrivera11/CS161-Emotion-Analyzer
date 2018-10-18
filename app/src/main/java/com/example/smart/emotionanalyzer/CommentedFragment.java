package com.example.smart.emotionanalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import java.util.ArrayList;


public class CommentedFragment extends Fragment {

    ArrayList<Topic> topics;
    ListView topicListView;
    private TopicDatabaseManager topicManager;
    private ActivityManager activityManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_commented, null);
        topicManager = new TopicDatabaseManager(getActivity());
        activityManager = new ActivityManager(getActivity());

        topics = new ArrayList<>();
        topicListView = view.findViewById(R.id.previously_commented_list);
        topicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Topic t = (Topic) topics.get(i);
                Bundle bundle = getActivity().getIntent().getExtras();
                bundle.putParcelable("topic", t);
                bundle.putString("topicID", t.getTopicID());
                bundle.putString("fragment_detail", "Analysis");
                activityManager.changeActivty(TopicDetail.class, bundle);;
            }
        });
        topicManager.getPreviouslyCommentedTopics(topics, topicListView);

        return view;
    }
}