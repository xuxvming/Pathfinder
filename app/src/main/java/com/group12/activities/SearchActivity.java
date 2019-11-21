package com.group12.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.group12.pathfinder.PathFinderFactory;
import com.mancj.materialsearchbar.MaterialSearchBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchActivity.class);
    private MaterialSearchBar searchBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchBar = findViewById(R.id.search_bar);
        searchBar.setOnSearchActionListener(this);
    }


    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
       PathFinderFactory factory = (PathFinderFactory) getIntent().getSerializableExtra("PathFinderFactory");
//       if (factory.getOrigin().isEmpty()){
//           throw new
//       }
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    private String searchForDirecttion(PathFinderFactory factory){
        return null;
    }
}
