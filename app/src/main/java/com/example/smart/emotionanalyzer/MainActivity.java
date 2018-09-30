package com.example.smart.emotionanalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();
        String fragment = getIntent().getExtras().getString("fragment");
        mAuth = FirebaseAuth.getInstance();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        if(fragment.equals("browse")) {
            navigation.setSelectedItemId(R.id.navigation_Browse);
            loadMyFragment(new BrowseFragment());
        }
        else if (fragment.equals("account")) {
            navigation.setSelectedItemId(R.id.navigation_Account);
            loadMyFragment(new AccountFragment());
        }
        else if (fragment.equals("commented")) {
            navigation.setSelectedItemId(R.id.navigation_Commented);
            loadMyFragment(new CommentedFragment());
        }
        else {
            navigation.setSelectedItemId(R.id.navigation_Home);
            loadMyFragment(new MainFeedFragment());
        }

    }

    private boolean loadMyFragment(Fragment fragment) {
        if (fragment == null) {
            return false;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        return true;
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        int id = item.getItemId();
        if (id == R.id.navigation_Home) {
            fragment = new MainFeedFragment();
        }
        else if (id == R.id.navigation_Browse){
            fragment = new BrowseFragment();
        }
        else if(id == R.id.navigation_Commented) {
            fragment = new CommentedFragment();
        }
        else if(id == R.id.navigation_Account) {
            fragment = new AccountFragment();
        }
        return loadMyFragment(fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.item1:
                Bundle bundle = getIntent().getExtras();
                Toast.makeText(getApplicationContext(), "Item selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, CreateTopicActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
