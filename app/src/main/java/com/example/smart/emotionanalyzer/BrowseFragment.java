package com.example.smart.emotionanalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class BrowseFragment extends Fragment {

    private ArrayList<Topic> topicList = new ArrayList<>();

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

        politics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToTopicCategory("politics");
            }
        });

        construction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToTopicCategory("construction");
            }
        });

        entertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToTopicCategory("TV/Movies");
            }
        });

        ethics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToTopicCategory("society and ethics");
            }
        });

        education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToTopicCategory("education");
            }
        });

        business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToTopicCategory("business");
            }
        });

        return view;
    }


    private void sendToTopicCategory(String category) {
        Bundle bundle = getActivity().getIntent().getExtras();
        bundle.putString("category", category);
        Intent topicCategoryIntent = new Intent(getActivity(), TopicCateogryActivity.class);
        topicCategoryIntent.putExtras(bundle);
        startActivity(topicCategoryIntent);
        getActivity().finish();
    }

}
