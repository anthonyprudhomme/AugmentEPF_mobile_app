package com.filiereticsa.arc.augmentepf.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.filiereticsa.arc.augmentepf.R;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
