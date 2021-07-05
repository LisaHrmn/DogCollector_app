package com.example.dogcollector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class OverviewActivity extends AppCompatActivity {

    //variables
    private final static String LOG_TAG = "AAA";
    private int counter = 0;
    public final static String S_COUNTER = "counter";
    public final static int S_COUNTER_DEFAULT = 0;
    private MediaPlayer mp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate overview");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "onResume overview");
        super.onResume();
        restoreCounter();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    //back to main activity on click
    public void back(View v) {
        Log.d(LOG_TAG, "go back");
        Intent i = new Intent(OverviewActivity.this, MainActivity.class);
        startActivity(i);
    }

    //go to map on click
    public void openMap(View v) {
        Log.d(LOG_TAG, "to map");
        Intent i = new Intent(OverviewActivity.this, MapsActivity.class);
        startActivity(i);
    }

    //add one to counter
    public void count (View v){
        Log.d(LOG_TAG, "count");
        counter++;
        updateCounter();
        saveCounter();

        //sound on count
        if(SettingsActivity.isSoundOn(this)){
            mp = MediaPlayer.create(this, R.raw.bark);
            mp.start();
        }

        //api fact appears on count
        getRandomFact();
    }

    public void updateCounter(){
        TextView tv = findViewById(R.id.textView_counter);
        tv.setText(counter + "");
    }

    //save counter
    public void saveCounter(){
        Log.d(LOG_TAG, "save count");
        getPreferences(MODE_PRIVATE).edit().putInt(S_COUNTER, counter).commit();
    }

    public void restoreCounter(){
        counter = getPreferences(MODE_PRIVATE).getInt(S_COUNTER, S_COUNTER_DEFAULT);
        updateCounter();
    }

    //get and show json data from internet for facts
    private void getRandomFact() {
        //instantiate
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://lisakeers.github.io/api/facts.json";

        //request
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        showRandomFact(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "That didn't work!");
            }
        });

        //add the request to the RequestQueue
        queue.add(jsonRequest);

    }

   private void showRandomFact(JSONObject data) {
       TextView tv = findViewById(R.id.textView_facts);
        try {
            //get and format random fact
            JSONArray facts = (JSONArray)data.get("facts");
            Random generator = new Random();
            int randomIndex = generator.nextInt(facts.length());
            JSONObject item = (JSONObject)facts.get(randomIndex);
            String fact = (String)item.get("fact");
            Log.d(LOG_TAG, fact);

            //add fact to textview
            tv.setText(fact);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON exception");
        }
    }

}