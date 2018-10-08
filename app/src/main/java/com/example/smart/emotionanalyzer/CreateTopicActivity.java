package com.example.smart.emotionanalyzer;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
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
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateTopicActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private boolean added = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_topic);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
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
                DatabaseReference topicref = database.getReference().child("Topics");
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
                Date date = new Date();
                Topic topic = new Topic(postEditText.getText().toString(), user.getDisplayName(), user.getUid(), 20, 50, 30, 45, formatter.format(date), spinner.getSelectedItem().toString());
                Log.d("Write", "Writing to database");
                String id = topicref.push().getKey();
                topicref.child(id).setValue(topic);
                userRef = database.getReference("Users/" + user.getUid());
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Method gets called twice since we are reading and writing to the database. Using the added boolean it only gets called once
                        if (!added) {
                            User userObject = dataSnapshot.getValue(User.class);
                            userObject.addCreatedTopic(postEditText.getText().toString());
                            userRef.setValue(userObject);
                            added = true;
                            sendToMain();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


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

    public void sendToMain() {
        Bundle bundle = getIntent().getExtras();
        bundle.putString("fragment", "main");
        Intent intent = new Intent(CreateTopicActivity.this, MainActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
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
}
