package com.example.smart.emotionanalyzer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class TopicDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Topic a = getIntent().getExtras().getParcelable("topic");

        TextView topic = (TextView) findViewById(R.id.textViewTopic);
        TextView category = (TextView) findViewById(R.id.textViewCategory);
        TextView date = (TextView) findViewById(R.id.textViewDate);
        TextView author = (TextView) findViewById(R.id.textViewAuthor);

        topic.setMovementMethod(new ScrollingMovementMethod());
        topic.setText( a.getTopic());
        category.setText("Category: " +a.getCategory());
        date.setText("Created on: " + a.getDate());
        author.setText("By: " + a.getCreator());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("back", "selected");
                sendToMain();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void sendToMain() {
        Bundle bundle = new Bundle();
        bundle.putString("fragment", "MainFeedFragment");
        Intent intent = new Intent(TopicDetail.this, MainActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
