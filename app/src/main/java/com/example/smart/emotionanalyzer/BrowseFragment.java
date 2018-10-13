package com.example.smart.emotionanalyzer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;



public class BrowseFragment extends Fragment {

    private ActivityManager activityManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse, null);
        Button politics = view.findViewById(R.id.button_politics);
        Button construction = view.findViewById(R.id.button_constuction_category);
        Button entertainment = view.findViewById(R.id.button_TVMovies);
        Button ethics = view.findViewById(R.id.button_ethics);
        Button education = view.findViewById(R.id.button_education);
        Button business = view.findViewById(R.id.button_business);

        activityManager = new ActivityManager(getActivity());
        final Bundle bundle = getActivity().getIntent().getExtras();

        politics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("category", "politics");
                activityManager.changeActivty(TopicCateogryActivity.class, bundle);
            }
        });

        construction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("category", "construction");
                activityManager.changeActivty(TopicCateogryActivity.class, bundle);
            }
        });

        entertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("category", "TV/Movies");
                activityManager.changeActivty(TopicCateogryActivity.class, bundle);
            }
        });

        ethics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("category", "society and ethics");
                activityManager.changeActivty(TopicCateogryActivity.class, bundle);
            }
        });

        education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("category", "education");
                activityManager.changeActivty(TopicCateogryActivity.class, bundle);
            }
        });

        business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("category", "business");
                activityManager.changeActivty(TopicCateogryActivity.class, bundle);
            }
        });

        return view;
    }

}
