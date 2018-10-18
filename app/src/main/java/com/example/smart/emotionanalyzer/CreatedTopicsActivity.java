package com.example.smart.emotionanalyzer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class CreatedTopicsActivity extends AppCompatActivity {

    private ArrayList<Topic> topics;
    private ListView topicListView;
    private TopicDatabaseManager topicManager;
    private  ActivityManager activityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_topics);
        activityManager = new ActivityManager(this);
        topicManager = new TopicDatabaseManager(this);
        topics = new ArrayList<>();
        topicListView = findViewById(R.id.created_topics_list);
        topicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Topic t = (Topic) topics.get(position);
                Bundle bundle = getIntent().getExtras();
                bundle.putParcelable("topic", t);
                bundle.putString("topicID", t.getTopicID());
                bundle.putString("fragment_detail", "Analysis");
                activityManager.changeActivty(TopicDetail.class, bundle);
            }
        });
        topicManager.getCreatedTopics(topics, topicListView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bundle = getIntent().getExtras();
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("back", "selected");
                activityManager.changeActivty(MainActivity.class, bundle);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
