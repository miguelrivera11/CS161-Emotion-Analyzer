package com.example.smart.emotionanalyzer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateTopic extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_topic);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<String> categories = new ArrayList<String>();
        categories.add("Education");
        categories.add("Entertainment/TV");
        categories.add("Politics");
        categories.add("Society");
        categories.add("Ethics");
        categories.add("Construction");
        categories.add("Business");
        categories.add("Sports");

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Button post = (Button) findViewById(R.id.PostButton);
        final EditText postEditText = (EditText) findViewById(R.id.editTextPost);
        Button delete = (Button) findViewById(R.id.DeleteButton);


        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categories);
        spinner.setAdapter(categoriesAdapter);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                System.out.println(formatter.format(date));
                //TODO: get actual user and analyze number of comments for each emotion
                Topic topic = new Topic(postEditText.getText().toString(), "test", 0, 0, 0, 0,formatter.format(date), spinner.getSelectedItem().toString());

                //TODO: write to database under topic id and return to main screen and wire up delete post
            }
        });
    }
}
