package com.example.smart.emotionanalyzer;

import android.content.Intent;
import android.graphics.Color;
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

public class TopicDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Topic a = getIntent().getExtras().getParcelable("topic");

        PieChart pieChart = (PieChart) findViewById(R.id.emotionChart);
        TextView topic = (TextView) findViewById(R.id.textViewTopic);
        TextView category = (TextView) findViewById(R.id.textViewCategory);
        TextView date = (TextView) findViewById(R.id.textViewDate);
        TextView author = (TextView) findViewById(R.id.textViewAuthor);

        topic.setMovementMethod(new ScrollingMovementMethod());
        topic.setText( a.getTopic());
        category.setText("Category: " +a.getCategory());
        date.setText("Created on: " + a.getDate());
        author.setText("By: " + a.getCreator());

        float total = a.getAngry() + a.getHappy() + a.getSad() + a.getNeutral();
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) a.getAngry() / total, "Angry"));
        entries.add(new PieEntry((float)a.getHappy() / total, "Happy"));
        entries.add(new PieEntry((float)a.getSad() / total, "Sad"));
        entries.add(new PieEntry((float)a.getNeutral() / total, "Neutral"));

        PieDataSet dataSet = new PieDataSet(entries,"" );

        ArrayList<String> labels = new ArrayList<>();
        labels.add("Angry");
        labels.add("Happy");
        labels.add("Sad");
        labels.add("Neutral");

        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        pieChart.setDrawHoleEnabled(true);
        //pieChart.setHoleRadius(5f);
        //pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setData(data);


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
