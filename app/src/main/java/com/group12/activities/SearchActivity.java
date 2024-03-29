package com.group12.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.group12.pathfinder.AbstractDirectionsObject;
import com.group12.pathfinder.AbstractPathFinder;
import com.group12.pathfinder.PathFinderFactory;
import com.group12.utils.PermissionChecker;
import com.group12.utils.RequestMaker;
import com.mancj.materialsearchbar.MaterialSearchBar;
import org.apache.commons.io.FileUtils;
import org.osmdroid.util.GeoPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SearchActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchActivity.class);
    private PathFinderFactory factory;
    private File graph_file;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        graph_file = new File(getFilesDir().getPath(),"graph_new.json");
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        try {
            writeStreamToFile(getAssets().open("graph.json"),graph_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_search);

        MaterialSearchBar searchBar = findViewById(R.id.search_bar);
        searchBar.setOnSearchActionListener(this);

        FloatingActionButton settingButton = findViewById(R.id.setting_button);
        FloatingActionButton realtimeButton = findViewById(R.id.realtime_button);
        factory = (PathFinderFactory) getIntent().getSerializableExtra(PathFinderFactory.class.getName());
        searchBar.setText(factory.getSearchText());
        settingButton.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 Intent intent = new Intent(SearchActivity.this,PathSettingActivity.class);
                                                 intent.putExtra(PathFinderFactory.class.getName(),factory);
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
        String[] coords = text.toString().split(",");
        double latDouble = Double.parseDouble(coords[0]);
        double lonDouble = Double.parseDouble(coords[1]);

        GeoPoint destination = new GeoPoint(latDouble, lonDouble);
        factory.setDestinationLatLng(destination);

        factory.setGraph_location(graph_file.getAbsolutePath());
        AbstractDirectionsObject response = searchForDirection(factory);
        Intent intent = new Intent(SearchActivity.this,OSMMapsActivity.class);
        intent.putExtra("Response",response);
        LOGGER.info("Switching context ..");
        startActivity(intent);
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    private synchronized AbstractDirectionsObject searchForDirection(PathFinderFactory factory){
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (!activeNetwork.isConnected()){
            factory.setSource("Local");
        }
        AbstractPathFinder pathFinder = factory.getPathFinder();
        RequestMaker requestMaker = new RequestMaker();
        return pathFinder.makeRequest(requestMaker);
    }

    void writeStreamToFile(InputStream input, File file) {
        if (file.exists()){
            return;
        }
        if (!PermissionChecker.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        try {
            FileUtils.copyInputStreamToFile(input,file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
