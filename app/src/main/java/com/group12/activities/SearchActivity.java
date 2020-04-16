package com.group12.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.group12.pathfinder.AbstractDirectionsObject;
import com.group12.pathfinder.AbstractPathFinder;
import com.group12.pathfinder.PathFinderFactory;
import com.group12.utils.RequestMaker;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchActivity.class);
    private MaterialSearchBar searchBar;
    private FloatingActionButton settingButton, realtimeButton;
    private Bundle b;
    private String travelParam = "0";
    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        b = getIntent().getExtras();
        searchBar = findViewById(R.id.search_bar);
        searchBar.setOnSearchActionListener(this);
        settingButton = findViewById(R.id.setting_button);
        realtimeButton = findViewById(R.id.realtime_button);
        if(b != null) travelParam = b.getString("travelChoice");
        settingButton.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 Intent intent = new Intent(SearchActivity.this,PathSettingActivity.class);
                                                 startActivity(intent);
                                             }
                                         }
        );
        realtimeButton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  Intent intent = new Intent(SearchActivity.this,RealtimeDataActivity.class);
                                                  startActivity(intent);
                                              }
                                          }
        );
    }


    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        PathFinderFactory factory = (PathFinderFactory) getIntent().getSerializableExtra(PathFinderFactory.class.getName());
        String destination = text.toString();
        factory.setDestination(travelParam.concat(destination));
        AbstractDirectionsObject response = searchForDirection(factory);
        Intent intent = new Intent(SearchActivity.this,MapsActivity.class);
        intent.putExtra("Response",response);
        LOGGER.info("Switching context ..");
        startActivity(intent);
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    private synchronized AbstractDirectionsObject searchForDirection(PathFinderFactory factory){
        AbstractPathFinder pathFinder = factory.getPathFinder();
        RequestMaker requestMaker = new RequestMaker();
        return pathFinder.makeRequest(requestMaker);
        //TODO:handle P2P data transfer here
    }
}