package com.example.smart.emotionanalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_cateogry);
        String category = getIntent().getExtras().getString("category");
        topics = new ArrayList<>();
        topicListView = findViewById(R.id.list_of_topics_by_category);
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
        getTopics(category);
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToBrowse();
            }
        });
    }

    private void getTopics(final String category) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference topicsRef = database.getReference("Topics");
        topicsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                topics.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Topic topic = child.getValue(Topic.class);
                    if (topic.getCategory().toLowerCase().equals(category.toLowerCase())) {
                        topics.add(topic);
                    }
                }

                TopicList adapter = new TopicList(TopicCateogryActivity.this, topics);
                topicListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendToBrowse() {
        Bundle bundle = getIntent().getExtras();
        bundle.putString("fragment", "browse");
        Intent intent = new Intent(TopicCateogryActivity.this, MainActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

}
