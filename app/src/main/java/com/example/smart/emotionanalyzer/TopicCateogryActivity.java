package com.example.smart.emotionanalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.ImageButton;

import java.util.ArrayList;

public class TopicCateogryActivity extends AppCompatActivity {

    private ArrayList<Topic> topics;
    private ListView topicListView;
    private TopicDatabaseManager topicManager;
    private  ActivityManager activityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_cateogry);
        activityManager = new ActivityManager(this);
        String category = getIntent().getExtras().getString("category");
        topics = new ArrayList<>();
        topicListView = findViewById(R.id.list_of_topics_by_category);
        topicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Topic t = (Topic) topics.get(i);
                Bundle bundle = getIntent().getExtras();
                bundle.putParcelable("topic", t);
                bundle.putString("topicID", t.getTopicID());
                bundle.putString("fragment_detail", "Analysis");
                activityManager.changeActivty(TopicDetail.class, bundle);;
            }
        });
        TextView title = findViewById(R.id.text_category);
        title.setText(category.toUpperCase());
        if (category.equals("construction")) {
            title.setBackgroundColor(getResources().getColor(R.color.color_construction, null));
        }
        else if (category.equals("business")) {
            title.setBackgroundColor(getResources().getColor(R.color.color_business, null));
        }
        else if (category.equals("education")) {
            title.setBackgroundColor(getResources().getColor(R.color.color_education, null));
        }
        else if (category.equals("TV/Movies")) {
            title.setBackgroundColor(getResources().getColor(R.color.color_entertainment, null));
        }
        else if (category.equals("society and ethics")) {
            title.setBackgroundColor(getResources().getColor(R.color.color_ethics, null));
        }
        else if (category.equals("politics")) {
            title.setBackgroundColor(getResources().getColor(R.color.color_politics, null));
        }
        topicManager = new TopicDatabaseManager(this);
        topicManager.getTopicsListByCategory(category, topics, topicListView);
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();
                bundle.putString("fragment", "browse");
                activityManager.changeActivty(MainActivity.class, bundle);
            }
        });
    }

}
