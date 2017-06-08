package com.filiereticsa.arc.augmentepf.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.interfaces.HTTPRequestInterface;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;

import org.json.JSONException;
import org.json.JSONObject;

import static com.filiereticsa.arc.augmentepf.activities.HomePageActivity.ERROR;

public class SettingsActivity extends PreferenceActivity implements HTTPRequestInterface, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "Ici";
    private static final String MESSAGE = "message";
    private static final String SUCCESS = "true";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onRequestDone(String result, int requestId) {
        Log.d(TAG, "onRequestDone: " + result);
        switch (requestId) {
            case HTTPRequestManager.CONNECTION:
                if (result.equals(ERROR)) {
                    Toast.makeText(this, R.string.error_server, Toast.LENGTH_SHORT).show();
                }
                // Put the result in a JSONObject to use it.
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    // Show in the log the message given by the result : it will give error or success information
                    Log.d(TAG, "onRequestDone: " + jsonObject.getString(MESSAGE));
                    String success = jsonObject.getString(MESSAGE);
                    if (success.equals(SUCCESS)) {
                        Log.d(TAG, "onRequestDone: success : " + jsonObject.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged: before check");
        if (!sharedPreferences.getString("ical", "").equals("")) {
            Log.d(TAG, "onSharedPreferenceChanged: after check");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", ConnectionActivity.idUser);
                jsonObject.put("token", ConnectionActivity.token);
                jsonObject.put("ical", sharedPreferences.getString("ical", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onSharedPreferenceChanged: sending request");
            HTTPRequestManager.doPostRequest("addIcalLink.php", jsonObject.toString(), this, HTTPRequestManager.ICAL);
        }
    }
}
