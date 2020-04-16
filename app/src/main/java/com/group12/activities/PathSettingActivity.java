package com.group12.activities;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class PathSettingActivity extends AppCompatActivity {
    private Button settingButton;
    private RadioButton radioButton;
    private RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_setting);
        settingButton = findViewById(R.id.saveSettingButton);
        radioGroup = findViewById(R.id.PathRadioGroup);
        settingButton.setOnClickListener(new View.OnClickListener() {
                                             @SuppressLint("SetTextI18n")
                                             @Override
                                             public void onClick(View view) {
                                                 Intent intent = new Intent(PathSettingActivity.this,SearchActivity.class);
                                                 Bundle b = new Bundle();
                                                 int radioId = radioGroup.getCheckedRadioButtonId();
                                                 if (radioId == -1) {
                                                     b.putString("travelChoice", "0");
                                                 }
                                                 else {
                                                     radioButton = findViewById(radioId);
                                                     if (radioButton.getText().equals("Environment")) {
                                                         b.putString("travelChoice", "2");
                                                     } else if (radioButton.getText().equals("Comfort")) {
                                                         b.putString("travelChoice", "1");
                                                     } else b.putString("travelChoice", "0");
                                                     intent.putExtras(b);
                                                     startActivity(intent);
                                                 }
                                             }
                                         }
        );
    }

    public void checkRadioButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
    }
}

