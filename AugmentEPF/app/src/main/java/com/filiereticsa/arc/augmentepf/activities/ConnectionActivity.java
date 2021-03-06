package com.filiereticsa.arc.augmentepf.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.interfaces.HTTPRequestInterface;
import com.filiereticsa.arc.augmentepf.managers.FileManager;
import com.filiereticsa.arc.augmentepf.managers.HTTP;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;

import org.json.JSONException;
import org.json.JSONObject;

import static com.filiereticsa.arc.augmentepf.activities.CreateAccountActivity.CREDENTIALS_JSON;

public class ConnectionActivity extends AppCompatActivity implements HTTPRequestInterface {

    public static final String SUCCESS = "Success";
    public static final String ID = "id";
    public static final String GET_ATTRIBUTE = "getAttribute";
    public static final String GET_EMAIL = "getEmail";
    public static final String GET_TYPE = "getType";
    public static final String GET_ICAL = "getIcal";
    public static final String ATTRIBUTE = "attribute";
    public static final String TYPE = "type";
    public static final String ICAL = "ical";
    public static final String TYPE_USER = "type_user";
    private static final String NAME = "name";
    private static final String PASSWORD = "password";
    private static final String TAG = "Ici";
    public static final String AUTO_LOG = "autoLog";
    public static final String SAVE_CRED = "saveCred";
    public static int idUser;
    public static String token;
    private EditText login;
    private EditText password;
    private CheckBox saveCredential;
    private CheckBox autoLogIn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setUpCheckBoxes();
        fillFieldsOrNot();
    }

    private void fillFieldsOrNot() {
        if (sharedPreferences.getBoolean(SAVE_CRED, false)) {
            JSONObject credentials = loadCredentials();
            if (credentials != null) {
                try {
                    login.setText(credentials.getString(NAME));
                    password.setText(credentials.getString(PASSWORD));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setUpCheckBoxes() {
        saveCredential = (CheckBox) findViewById(R.id.saveCredCheckBox);
        autoLogIn = (CheckBox) findViewById(R.id.autoLogCheckBox);

        boolean saveCred = sharedPreferences.getBoolean(SAVE_CRED, false);
        boolean autoLog = sharedPreferences.getBoolean(AUTO_LOG, false);

        saveCredential.setChecked(saveCred);
        autoLogIn.setChecked(autoLog);

        saveCredential.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                prefEditor.putBoolean(SAVE_CRED, isChecked);
                prefEditor.apply();
            }
        });

        autoLogIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                prefEditor.putBoolean(AUTO_LOG, isChecked);
                prefEditor.apply();
                if (isChecked) {
                    saveCredential.setChecked(true);
                }
            }
        });
    }

    private JSONObject loadCredentials() {
        FileManager fileManager = new FileManager(null, CREDENTIALS_JSON);
        JSONObject jsonDataRead = null;
        try {
            jsonDataRead = new JSONObject(fileManager.readFile());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonDataRead;
    }

    private void saveCredentialsToFile() {
        FileManager fileManager = new FileManager(null, CREDENTIALS_JSON);
        JSONObject jsonToSave = new JSONObject();

        String loginValue = login.getText().toString();
        String passwordValue = password.getText().toString();
        try {
            jsonToSave.put(NAME, loginValue);
            // TODO hash the password or do something else to protect the password
            jsonToSave.put(PASSWORD, passwordValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fileManager.saveFile(jsonToSave.toString());
    }

    public void onValidateClick(View view) {
        //Try and connect user with id and password
        Pair<Boolean, String> checkedValues = checkValues();
        if (checkedValues.first) {
            if (HomePageActivity.isNetworkAvailable()) {
                connectToServer();
            } else {
                Toast.makeText(this, R.string.need_internet_account, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, checkedValues.second, Toast.LENGTH_SHORT).show();
        }
    }

    private Pair<Boolean, String> checkValues() {
        String loginValue = login.getText().toString();
        String passwordValue = password.getText().toString();
        if ("".equals(loginValue)) {
            return new Pair<>(false, getString(R.string.enter_login));
        }
        if ("".equals(passwordValue)) {
            return new Pair<>(false, getString(R.string.enter_password));
        }
        return new Pair<>(true, null);
    }

    private void connectToServer() {
        String loginValue = login.getText().toString();
        String passwordValue = password.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(NAME, loginValue);
            jsonObject.put(PASSWORD, passwordValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.d(TAG, "sendAccountCreationToServer: " + jsonObject.toString());
        HTTPRequestManager.doPostRequest(HTTP.CONNECTION_PHP, jsonObject.toString(),
                this, HTTPRequestManager.CONNECTION);
    }

    public void onCreateAccountClick(View view) {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestDone(String result, int requestId) {
        //Log.d(TAG, "onConnectionRequestDone: "+result);
        switch (requestId) {
            case HTTPRequestManager.CONNECTION:
                if (result.equals(HTTP.ERROR)) {
                    Toast.makeText(this, R.string.error_server, Toast.LENGTH_SHORT).show();
                }
                try {
                    // Put the result in a JSONObject to use it.
                    JSONObject jsonObject = new JSONObject(result);
                    // Show in the log the message given by the result:
                    // it will give error or success information
                    String success = jsonObject.getString(HTTP.MESSAGE);
                    if (success.equals(SUCCESS)) {
                        Toast.makeText(this, R.string.connected, Toast.LENGTH_SHORT).show();
                        if (sharedPreferences.getBoolean(SAVE_CRED, false)) {
                            saveCredentialsToFile();
                        }
                        HomePageActivity.isUserConnected = true;
                        idUser = jsonObject.getInt(HTTP.ID_USER);
                        token = jsonObject.getString(HTTP.TOKEN);
                        //Log.d(TAG, "onRequestDone: " + idUser + " " + token);
                        checkForNewAccountSettings();
                        Intent intent = new Intent(this, com.filiereticsa.arc.augmentepf.activities.HomePageActivity.class);
                        startActivity(intent);
                    } else {
                        // If request failed, shows the message from the server
                        String message = jsonObject.getString(HTTP.MESSAGE);
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        HomePageActivity.isUserConnected = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case HTTPRequestManager.GET_SETTINGS:
                if (result.equals(HTTP.ERROR)) {
                    Toast.makeText(this, R.string.error_server, Toast.LENGTH_SHORT).show();
                }
                try {
                    // Put the result in a JSONObject to use it.
                    JSONObject jsonObject = new JSONObject(result);
                    // Show in the log the message given by the result : it will give error or success information
                    String success = jsonObject.getString(HTTP.STATE);
                    if (success.equals(HTTP.TRUE)) {

                        // Get sharedPreferences
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

                        // Update user specific attributes in SharedPreferences
                        String attribute = jsonObject.getString(ATTRIBUTE);
                        String[] attributes = attribute.split("/");
                        boolean elevator = false;
                        boolean soundGuidance = false;
                        for (int i = 0; i < attributes.length; i++) {
                            if (attributes[i].equals("soundGuidance")) {
                                soundGuidance = true;
                            }
                            if (attributes[i].equals("elevator")) {
                                elevator = true;
                            }
                        }
                        String specificAttributeValue = "0";
                        if (elevator && soundGuidance) {
                            specificAttributeValue = "VA";
                        } else if (elevator) {
                            specificAttributeValue = "A";
                        } else if (soundGuidance) {
                            specificAttributeValue = "V";
                        }

                        prefEditor.putString("specific_attribute_user", specificAttributeValue);


                        // Update user type in SharedPreferences
                        String userType = jsonObject.getString(TYPE);
                        String userTypeValue;
                        switch (userType) {

                            case "Student":
                                userTypeValue = "S";
                                break;

                            case "Teacher":
                                userTypeValue = "T";
                                break;

                            case "Contributor":
                                userTypeValue = "C";
                                break;

                            case "Visitor":
                                userTypeValue = "V";
                                break;

                            case "Administrator":
                                userTypeValue = "A";
                                break;
                            default:
                                userTypeValue = "V";
                        }

                        prefEditor.putString(TYPE_USER, userTypeValue);

                        // Update user iCal link
                        String iCalLink = jsonObject.getString(ICAL);
                        prefEditor.putString(ICAL, iCalLink);

                        prefEditor.apply();

                    } else {
                        // If request failed, shows the message from the server
                        String message = jsonObject.getString(HTTP.MESSAGE);
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void checkForNewAccountSettings() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(HTTP.ID, idUser);
            jsonObject.put(HTTP.TOKEN, token);
            jsonObject.put(GET_ATTRIBUTE, HTTP.TRUE);
            jsonObject.put(GET_EMAIL, HTTP.TRUE);
            jsonObject.put(GET_TYPE, HTTP.TRUE);
            jsonObject.put(GET_ICAL, HTTP.TRUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HTTPRequestManager.doPostRequest(HTTP.SETTINGS_PHP, jsonObject.toString(),
                this, HTTPRequestManager.GET_SETTINGS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
