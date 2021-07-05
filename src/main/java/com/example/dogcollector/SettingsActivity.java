package com.example.dogcollector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.util.prefs.PreferenceChangeEvent;

public class SettingsActivity extends PreferenceActivity {

    public final static String OPT_SOUND = "key_sound";
    public final static boolean OPT_SOUND_DEFAULT = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    //sound setting
    public static boolean isSoundOn(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_SOUND, OPT_SOUND_DEFAULT);
    }
}