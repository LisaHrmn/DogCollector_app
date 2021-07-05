package com.example.dogcollector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity{

    //variables
    public final static String LOG_TAG = "AAA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate main");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "onResume main");
        Log.d(LOG_TAG, "sound:" + SettingsActivity.isSoundOn(this));
        super.onResume();
    }

    //go to overview activity onclick
    public void start(View v) {
        Log.d(LOG_TAG, "start");
        Intent i = new Intent(MainActivity.this, OverviewActivity.class);
        startActivity(i);
    }

    //go to settings activity onclick
    public void openSettings(View v){
        Log.d(LOG_TAG, "open settings");
        Intent i = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(i);
    }

}