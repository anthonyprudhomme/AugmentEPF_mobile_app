package com.filiereticsa.arc.augmentepf.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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


        // The button in settings for password
        Preference changeButton = findPreference(getString(R.string.changeEmailButton));
        // If the user click on this button
        changeButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Create the dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                // Get the layout inflater
                LayoutInflater inflater = SettingsActivity.this.getLayoutInflater();
                View rootView = inflater.inflate(R.layout.dialog_change_email, null);

                // Get the text in all fields
                final EditText newEmailAddress = (EditText) rootView.findViewById(R.id.newEmail);
                final EditText confirmEmailAddress = (EditText) rootView.findViewById(R.id.confirmEmail);
                final EditText password = (EditText) rootView.findViewById(R.id.pwForMail);

                // Set the style of the dialog box
                builder.setView(rootView)
                        // Set the title of the dialog box
                        .setTitle(R.string.titleMailButton)
                        // If the user click on "OK"
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Transform text in fields in String
                                String newEmail = newEmailAddress.getText().toString();
                                String confirmEmail = confirmEmailAddress.getText().toString();
                                String pw = password.getText().toString();

                                checkChangeEmail(newEmail, confirmEmail, pw);
                            }
                        })
                        // If the user cancel the dialog
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Display a toast (Action cancelled)
                                Toast.makeText(SettingsActivity.this, R.string.emailToastCancelled,
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        // Display the dialog box
                        .show();

                return true;
            }
        });

        // The button in settings for password
        Preference pwButton = findPreference(getString(R.string.resetPwButton));
        // If the user click on this button
        pwButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Create the dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                // Get the layout inflater
                LayoutInflater inflater = SettingsActivity.this.getLayoutInflater();
                View rootView = inflater.inflate(R.layout.dialog_reset_password, null);

                // Get the text in all fields
                final EditText oldPassword = (EditText) rootView.findViewById(R.id.oldPw);
                final EditText newPassword = (EditText) rootView.findViewById(R.id.newPw);
                final EditText confirmPassword = (EditText) rootView.findViewById(R.id.confirmPw);

                // Set the style of the dialog box
                builder.setView(rootView)
                        // Set the title of the dialog box
                        .setTitle(R.string.titleResetPwButton)
                        // If the user click on "OK"
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Transform text in fields in String
                                String oldPw = oldPassword.getText().toString();
                                String newPw = newPassword.getText().toString();
                                String confPw = confirmPassword.getText().toString();

                                checkResetPw(oldPw, newPw, confPw);
                            }
                        })
                        // If the user cancel the dialog
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Display a toast (Action cancelled)
                                Toast.makeText(SettingsActivity.this, R.string.pwToastCancelled,
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        // Display the dialog box
                        .show();

                return true;
            }
        });
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onRequestDone(String result, int requestId) {
        switch (requestId) {
            case HTTPRequestManager.ICAL:
                if (result.equals(ERROR)) {
                    Toast.makeText(this, R.string.error_server, Toast.LENGTH_SHORT).show();
                }
                // Put the result in a JSONObject to use it.
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    // Show in the log the message given by the result : it will give error or success information
                    String success = jsonObject.getString(MESSAGE);
                    if (success.equals(SUCCESS)) {
                        // TODO do something when success ?
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!sharedPreferences.getString("ical", "").equals("")) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", ConnectionActivity.idUser);
                jsonObject.put("token", ConnectionActivity.token);
                jsonObject.put("ical", sharedPreferences.getString("ical", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HTTPRequestManager.doPostRequest("addIcalLink.php", jsonObject.toString(), this, HTTPRequestManager.ICAL);
        }

    }

    public void checkChangeEmail(String newEmail, String confirmEmail, String password) {
        //Log.d(TAG, "new: " + newEmail + " | confirm: " + confirmEmail + " | pw: " + password);
        boolean emailOk = false;
        boolean pwOk = true;

        // Compare email addresses
        if ("".equals(newEmail)) {
            emailOk = false;
        } else if (confirmEmail.equals(newEmail)) {
            emailOk = true;
        }

        // Compare password with the DB
        // TODO Check the DB to have the good password

        // Display the result of change
        if (emailOk && pwOk) {
            Toast.makeText(this, R.string.emailChangeOk, Toast.LENGTH_SHORT).show();
        } else if (!emailOk) {
            Toast.makeText(this, R.string.emailChangeProblemEmail, Toast.LENGTH_SHORT).show();
        } else if (!pwOk) {
            Toast.makeText(this, R.string.emailChangeProblemPw, Toast.LENGTH_SHORT).show();
        }
    }

    public void checkResetPw(String oldPW, String newPw, String confirmPw) {
        //Log.d(TAG, "old: " + oldPW + " | new: " + newPw + " | conf: " + confirmPw);
        boolean oldPwOk = true;
        boolean newPwOk = false;

        // Compare old password with the DB
        // TODO Check the DB to have the good old password

        // Compare new passwords together
        if ("".equals(newPw)) {
            newPwOk = false;
        } else if (confirmPw.equals(newPw)) { // If they are identical
            newPwOk = true;
        }

        // Display the result of change
        if (oldPwOk && newPwOk) {
            Toast.makeText(this, R.string.pwChangeOk, Toast.LENGTH_SHORT).show();
        } else if (oldPwOk == false) {
            Toast.makeText(this, R.string.pwChangeProblemOld, Toast.LENGTH_SHORT).show();
        } else if (newPwOk == false) {
            Toast.makeText(this, R.string.pwChangeProblemNew, Toast.LENGTH_SHORT).show();
        }
    }
}
