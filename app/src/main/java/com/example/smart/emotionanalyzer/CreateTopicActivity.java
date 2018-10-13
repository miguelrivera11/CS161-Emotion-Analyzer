package com.example.smart.emotionanalyzer;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateTopicActivity extends AppCompatActivity {

    private UserManager userManager;
    private TopicDatabaseManager topicManager;
    private ActivityManager activityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_topic);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        topicManager = new TopicDatabaseManager(this);
        userManager = new UserManager();
        activityManager = new ActivityManager(this);
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            String valueReceived = bundle.getString("name");
        }
        List<String> categories = new ArrayList<String>();
        categories.add("Education");
        categories.add("Entertainment/TV");
        categories.add("Politics");
        categories.add("Society and Ethics");
        categories.add("Construction");
        categories.add("Business");
        categories.add("Sports");

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        final Button post = (Button) findViewById(R.id.PostButton);
        final EditText postEditText = (EditText) findViewById(R.id.editTextPost);
        final Button delete = (Button) findViewById(R.id.DeleteButton);
        post.setEnabled(false);
        delete.setEnabled(false);

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categories);
        spinner.setAdapter(categoriesAdapter);

        postEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() ==0) {
                    post.setEnabled(false);
                    delete.setEnabled(false);
                } else {
                    post.setEnabled(true);
                    delete.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
                Date date = new Date();
                Topic topic = new Topic(postEditText.getText().toString(), userManager.getName(), userManager.getUserID(), 20, 50, 30, 45, formatter.format(date), spinner.getSelectedItem().toString());
                Log.d("Write", "Writing to database");
                topicManager.addTopic(topic);
                Bundle bundle = getIntent().getExtras();
                bundle.putString("fragment", "main");
                activityManager.changeActivty(MainActivity.class, bundle);

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(CreateTopicActivity.this)
                        .setTitle("Discard Post?")
                        .setMessage("Are you sure you wish to discard your post?")
                        .setPositiveButton("Continue editing", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("CreateTopic", "Positive");
                            }
                        })
                        .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("CreateTopic", "Negative");
                                postEditText.setText("");
                                spinner.setSelection(0);
                            }
                        })
                        .show();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("back", "selected");
                Bundle bundle = getIntent().getExtras();
                bundle.putString("fragment", "main");
                activityManager.changeActivty(MainActivity.class, bundle);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
