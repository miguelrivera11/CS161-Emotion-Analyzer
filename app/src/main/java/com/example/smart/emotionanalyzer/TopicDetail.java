package com.example.smart.emotionanalyzer;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class TopicDetail extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Topic a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String fragment = getIntent().getExtras().getString("fragment_detail");
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.detail_navigation);
        a = getIntent().getExtras().getParcelable("topic");
        navigation.setOnNavigationItemSelectedListener(this);
        if(fragment.equals("Comment")) {
            navigation.setSelectedItemId(R.id.navigation_Comment);
            loadMyFragment(new CommentFragment());
        }
        else if (fragment.equals("Analysis")) {
            navigation.setSelectedItemId(R.id.navigation_Analysis);
            loadMyFragment(new AnalysisFragment());
        }

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

    private boolean loadMyFragment(Fragment fragment) {
        if (fragment == null) {
            return false;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        int id = item.getItemId();
        if (id == R.id.navigation_Analysis) {
            fragment = new AnalysisFragment();
        }
        else if (id == R.id.navigation_Comment){
            fragment = new CommentFragment();
        }
        return loadMyFragment(fragment);
    }

    public Topic getTopic() {
        return a;
    }
}

