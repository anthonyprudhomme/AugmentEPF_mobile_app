package com.filiereticsa.arc.augmentepf.activities;

import android.preference.PreferenceActivity;
import android.os.Bundle;

import com.filiereticsa.arc.augmentepf.R;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}
