package com.example.smart.emotionanalyzer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TopicDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);


        Topic a = getIntent().getExtras().getParcelable("topic");

        TextView topic = (TextView) findViewById(R.id.textViewTopic);
        TextView category = (TextView) findViewById(R.id.textViewCategory);
        TextView date = (TextView) findViewById(R.id.textViewDate);
        TextView author = (TextView) findViewById(R.id.textViewAuthor);

        topic.setText( a.getTopic());
        category.setText("Category: " +a.getCategory());
        date.setText("Created on: " + a.getDate());
        author.setText("By: " + a.getCreator());
    }
}
