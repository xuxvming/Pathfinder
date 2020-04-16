package com.group12.activities;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import com.group12.pathfinder.PathFinderFactory;

public class PathSettingActivity extends AppCompatActivity {
    private RadioButton radioButton;
    private RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_setting);
        Button settingButton = findViewById(R.id.saveSettingButton);
        radioGroup = findViewById(R.id.PathRadioGroup);
        final PathFinderFactory factory = (PathFinderFactory) getIntent().getSerializableExtra(PathFinderFactory.class.getName());
        settingButton.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 //TODO: double check if the case number is correct
                                                 int radioId = radioGroup.getCheckedRadioButtonId();
                                                 if (radioId == -1) {
                                                     factory.setTravelChoice(1);
                                                 }
                                                 else {
                                                     radioButton = findViewById(radioId);
                                                     if (radioButton.getText().equals("Environment")) {
                                                         factory.setTravelChoice(2);
                                                     } else if (radioButton.getText().equals("Comfort")) {
                                                         factory.setTravelChoice(1);
                                                     } else {
                                                         factory.setTravelChoice(0);
                                                     }
                                                 }
                                                 Intent intent = new Intent(PathSettingActivity.this,SearchActivity.class);
                                                 intent.putExtra(PathFinderFactory.class.getName(),factory);
                                                 startActivity(intent);
                                             }
                                         }
        );
    }

    public void checkRadioButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
    }
}

