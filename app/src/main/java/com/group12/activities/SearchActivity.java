package com.group12.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.group12.pathfinder.AbstractDirectionsObject;
import com.group12.pathfinder.AbstractPathFinder;
import com.group12.pathfinder.PathFinderFactory;
import com.group12.utils.PermissionChecker;
import com.group12.utils.RequestMaker;
import com.mancj.materialsearchbar.MaterialSearchBar;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SearchActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchActivity.class);
    private MaterialSearchBar searchBar;
    private File graph_file;
    private static final int MY_PERMISSIONS_REQUEST_READ = 97;
    private static final int MY_PERMISSIONS_REQUEST_WRITE = 98;


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
        searchBar = findViewById(R.id.search_bar);
        searchBar.setOnSearchActionListener(this);
    }


    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
       PathFinderFactory factory = (PathFinderFactory) getIntent().getSerializableExtra(PathFinderFactory.class.getName());
       //TODO: getCoordinates from destination String
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
        factory.setMode("P2P");
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
