package com.example.smart.emotionanalyzer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class AnalysisFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_analysis, null);

        TopicDetail activity = (TopicDetail) getActivity();
        Topic a = activity.getTopic();
        PieChart pieChart = (PieChart) view.findViewById(R.id.emotionChart);
        float total = a.getAngry() + a.getHappy() + a.getSad();
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) a.getAngry() / total * 100, "Angry"));
        entries.add(new PieEntry((float)a.getHappy() / total * 100, "Happy"));
        entries.add(new PieEntry((float)a.getSad() / total * 100, "Sad"));

        PieDataSet dataSet = new PieDataSet(entries,"" );

        ArrayList<String> labels = new ArrayList<>();
        labels.add("Angry");
        labels.add("Happy");
        labels.add("Sad");
        Description desc = new Description();
        desc.setText("Percentage of Comments");
        pieChart.setDescription(desc);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        pieChart.setDrawHoleEnabled(false);
        //pieChart.setHoleRadius(5f);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.getDescription().setEnabled(true);
        pieChart.setData(data);
        return view;
    }


}
