package com.example.smart.emotionanalyzer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class TopicDetail extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Topic a;
    private String message;

    private DatabaseReference repliesRef;
    private DatabaseReference topicsRef;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private UserManager userManager;
    private ActivityManager activityManager;
    private TopicDatabaseManager topicManager;
    private ReportManager reportManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();
        userManager = new UserManager();
        activityManager = new ActivityManager(this);
        database = FirebaseDatabase.getInstance();
        topicManager = new TopicDatabaseManager(this);

        setContentView(R.layout.activity_topic_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String fragment = getIntent().getExtras().getString("fragment_detail");
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.detail_navigation);
        Bundle bundle = getIntent().getExtras();
        a = bundle.getParcelable("topic");
        a.setTopicID(bundle.getString("topicID"));
        reportManager = new ReportManager();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.topic_detail_menu, menu);
        if (!userManager.getUserID().equals(a.getCreatorID())) {
            menu.removeItem(R.id.topic_detail_delete);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bundle = getIntent().getExtras();
        switch (item.getItemId()) {
            case R.id.topic_detail_report_duplicate:
                reportManager.addReport(a.getTopicID(), userManager.getUserID(), Report.DUPLICATE);
                return true;
            case R.id.topic_detail_report_spam:
                reportManager.addReport(a.getTopicID(), userManager.getUserID(), Report.SPAM);
                return true;
            case R.id.topic_detail_report_inappropiate:
                reportManager.addReport(a.getTopicID(), userManager.getUserID(), Report.INAPPROPIATE);
                return true;
            case R.id.topic_detail_delete:
                topicManager.deleteTopic(bundle.getString("topicID"));
                activityManager.changeActivityWithDelay(MainActivity.class, bundle, 3000);
                return true;
            case android.R.id.home:
                Log.d("back", "selected");
                String fragment = bundle.getString("fragment");
                if (fragment.equals("browse")) {
                    activityManager.changeActivty(MainActivity.class, bundle);
                }
                else if (fragment.equals("account")) {
                    activityManager.changeActivty(MainActivity.class, bundle);
                }
                else {
                    activityManager.changeActivty(MainActivity.class, bundle);
                }
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
    public String getMessage() { return message; };

    //Handle Reply in comment
    public void onClick(View v) {
        final View parentRow = (View) v.getParent();
        final ListView listView = (ListView) parentRow.getParent();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reply");

        final EditText input = new EditText(this);

        input.setSingleLine(false);
        input.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        builder.setView(input);

        builder.setPositiveButton("Reply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                message = input.getText().toString();
                //Write Replies to database

                topicsRef = database.getReference("Topics");
                //TODO: Only rewrite replies instead of entire topic
                topicsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot topicSnapShot : dataSnapshot.getChildren()) {
                            Topic compare = topicSnapShot.getValue(Topic.class);
                            if (compare.getTopic().equals(a.getTopic())) {
                                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                                Date date = new Date();

                                int position =compare.getComments().size() - listView.getPositionForView(parentRow) - 1;
                                compare.getComments().get(position).addReply(new Comment(message, user.getDisplayName(), user.getUid(), formatter.format(date).toString(), ""));
                                String id = topicSnapShot.getKey();
                                topicsRef.child(id).setValue(compare);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}

