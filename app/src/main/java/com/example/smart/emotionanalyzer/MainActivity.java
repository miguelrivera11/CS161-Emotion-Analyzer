package com.example.smart.emotionanalyzer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;




import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    MaterialSearchView searchView;
    private ArrayList<String> topicsList = new ArrayList<>();
    boolean search = false;
    TopicDatabaseManager topicManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        search = false;
        super.onCreate(savedInstanceState);
        topicManager = new TopicDatabaseManager(this);

        setContentView(R.layout.activity_main);
        topicsList = new ArrayList<>();
        topicManager.getAllTopicNames(topicsList);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Material Search");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                //if closed searchview, listview will return default
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText!= null && !newText.isEmpty()) {
                    ArrayList<String> lstFound = new ArrayList<String>();
                    //get all topic string
                    for (String item : topicsList) {
                        if (item.contains(newText))
                            lstFound.add(item);
                    }
                    search = true;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("search", search);
                    bundle.putStringArrayList("topiclist", lstFound);
                    MainFeedFragment fragment = new MainFeedFragment();
                    fragment.setArguments(bundle);
                    loadMyFragment(fragment);
                }
                return true;
            }
        });

        String fragment = getIntent().getExtras().getString("fragment");

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
        getMenuInflater().inflate(R.menu.menu_item, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }
    public ArrayList<String> getTopicsList () {
        return topicsList;
    }
    public boolean getSearch() {
        return search;
    }
}
