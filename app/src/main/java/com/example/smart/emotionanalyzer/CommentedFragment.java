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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_commented, null);
        topicManager = new TopicDatabaseManager(getActivity());

        topics = new ArrayList<>();
        topicListView = view.findViewById(R.id.previously_commented_list);
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
        topicManager.getPreviouslyCommentedTopics(topics, topicListView);

        return view;
    }
}