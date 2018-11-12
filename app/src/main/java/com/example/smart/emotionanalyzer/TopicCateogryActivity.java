package com.example.smart.emotionanalyzer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
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
import android.widget.Toast;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_action_action_search);
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
                activityManager.changeActivty(TopicDetail.class, bundle);
            }
        });
        SpannableString s = new SpannableString(category.toUpperCase());
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, category.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
        if (category.equals("construction")) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fef15f")));

        }
        else if (category.equals("business")) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#47a7cd")));
        }
        else if (category.equals("education")) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f43971")));
        }
        else if (category.equals("TV/Movies")) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#e683c7")));
        }
        else if (category.equals("society and ethics")) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80de86")));
        }
        else if (category.equals("politics")) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#cda747")));
        }
        topicManager = new TopicDatabaseManager(this);
        topicManager.getTopicsListByCategory(category, topics, topicListView);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bundle = getIntent().getExtras();
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("back", "selected");
                String fragment = bundle.getString("fragment");
                if (fragment.equals("browse")) {
                    activityManager.changeActivty(MainActivity.class, bundle);
                } else if (fragment.equals("account")) {
                    activityManager.changeActivty(MainActivity.class, bundle);
                } else {
                    activityManager.changeActivty(MainActivity.class, bundle);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

