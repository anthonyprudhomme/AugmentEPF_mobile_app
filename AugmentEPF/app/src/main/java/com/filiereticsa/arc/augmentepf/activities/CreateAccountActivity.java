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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.interfaces.HTTPRequestInterface;
import com.filiereticsa.arc.augmentepf.managers.FileManager;
import com.filiereticsa.arc.augmentepf.managers.HTTP;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateAccountActivity extends AppCompatActivity implements HTTPRequestInterface {

    public static final String SPECIFIC_ATTRIBUTES = "specificAttributes";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String SOUND_GUIDANCE = "soundGuidance";
    public static final String ELEVATOR = "elevator";
    public static final String CREDENTIALS_JSON = "credentials.json";
    public static final String NONE = "none";
    // TODO change this code
    private static final String ADMIN_CODE = "admin";
    private static final String TAG = "Ici";
    private EditText login;
    private EditText email;
    private EditText password;
    private EditText passwordConfirmation;
    private Spinner userType;
    private Spinner specificAttribute;
    private EditText adminCode;
    private LinearLayout adminLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        login = (EditText) findViewById(R.id.login);
        email = (EditText) findViewById(R.id.mail);
        password = (EditText) findViewById(R.id.password);
        passwordConfirmation = (EditText) findViewById(R.id.password_confirmation);
        userType = (Spinner) findViewById(R.id.user_type);
        specificAttribute = (Spinner) findViewById(R.id.specific_attribute);
    }

    public void onCreateClick(View view) {
        //Register user in database, connect and go back to OptionsFragment
        Pair<Boolean, String> checkedValues = checkValues();
        if (checkedValues.first) {
            if (HomePageActivity.isNetworkAvailable()) {
                sendAccountCreationToServer();
            } else {
                Toast.makeText(this, R.string.need_internet_account, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, checkedValues.second, Toast.LENGTH_SHORT).show();
        }
    }

    private Pair<Boolean, String> checkValues() {
        String loginValue = login.getText().toString();
        String emailValue = email.getText().toString();
        String passwordValue = password.getText().toString();
        String passwordConfirmationValue = passwordConfirmation.getText().toString();
        if ("".equals(loginValue)) {
            return new Pair<>(false, getString(R.string.enter_login));
        }
        if ("".equals(emailValue)) {
            return new Pair<>(false, getString(R.string.enter_email));
        }
        if ("".equals(passwordValue)) {
            return new Pair<>(false, getString(R.string.enter_password));
        }
        if ("".equals(passwordConfirmationValue)) {
            return new Pair<>(false, getString(R.string.enter_password_confirmation));
        }
        return new Pair<>(true, null);
    }

    private void sendAccountCreationToServer() {
        if (!userType.getSelectedItem().toString().equals("Admin") || adminCode.getText().toString().equals(ADMIN_CODE)) {

            String loginValue = login.getText().toString();
            String emailValue = email.getText().toString();
            String passwordValue = password.getText().toString();
            String passwordConfirmationValue = passwordConfirmation.getText().toString();
            if (passwordValue.equals(passwordConfirmationValue)) {
                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put(NAME, loginValue);
                    switch (userType.getSelectedItemPosition()) {
                        case 0:
                            jsonObject.put(TYPE, "Student");
                            break;

                        case 1:
                            jsonObject.put(TYPE, "Visitor");
                            break;

                        case 2:
                            jsonObject.put(TYPE, "Teacher");
                            break;

                        case 3:
                            jsonObject.put(TYPE, "Contributor");
                            break;
                    }

                    jsonObject.put(PASSWORD, passwordValue);
                    jsonObject.put(EMAIL, emailValue);

                    JSONArray jsonArray = new JSONArray();
                    switch (specificAttribute.getSelectedItemPosition()) {
                        case 0:
                            jsonArray.put(NONE);
                            break;

                        case 1:
                            jsonArray.put(SOUND_GUIDANCE);
                            break;

                        case 2:
                            jsonArray.put(ELEVATOR);
                            break;

                        case 3:
                            jsonArray.put(ELEVATOR);
                            jsonArray.put(SOUND_GUIDANCE);
                            break;
                    }

                    jsonObject.put(SPECIFIC_ATTRIBUTES, jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HTTPRequestManager.doPostRequest(HTTP.ACCOUNT_CREATION_PHP, jsonObject.toString(),
                        this, HTTPRequestManager.ACCOUNT_CREATION);
            } else {
                Toast.makeText(this, R.string.password_password_confirmation_no_match, Toast.LENGTH_SHORT).show();
                passwordConfirmation.setText("");
                password.setText("");
            }
        }
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

    @Override
    public void onRequestDone(String result, int requestId) {
        switch (requestId) {
            case HTTPRequestManager.ACCOUNT_CREATION:
                if (result.equals(HTTP.ERROR)) {
                    Toast.makeText(this, R.string.error_server, Toast.LENGTH_SHORT).show();
                }
                try {
                    // Put the result in a JSONObject to use it.
                    JSONObject jsonObject = new JSONObject(result);
                    // Show in the log the message given by the result : it will give error or success information
                    String success = jsonObject.getString(HTTP.VALIDATE);
                    if (success.equals(HTTP.YES)) {
                        saveCredentialsToFile();
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                        String userTypeValue = "S";
                        switch (userType.getSelectedItemPosition()) {
                            case 0:
                                userTypeValue = "S";
                                break;

                            case 1:
                                userTypeValue = "V";
                                break;

                            case 2:
                                userTypeValue = "T";
                                break;

                            case 3:
                                userTypeValue = "C";
                                break;

                            case 4:
                                userTypeValue = "A";
                                break;
                        }
                        prefEditor.putString("type_user", userTypeValue);
                        String specificAttributeValue = "0";
                        switch (specificAttribute.getSelectedItemPosition()) {
                            case 0:
                                specificAttributeValue = "0";
                                break;

                            case 1:
                                specificAttributeValue = "V";
                                break;

                            case 2:
                                specificAttributeValue = "A";
                                break;

                            case 3:
                                specificAttributeValue = "VA";
                                break;
                        }
                        prefEditor.putString("specific_attribute_user", specificAttributeValue);
                        prefEditor.apply();
                        Toast.makeText(this, R.string.account_created, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, com.filiereticsa.arc.augmentepf.activities.HomePageActivity.class);
                        startActivity(intent);
                        HomePageActivity.isUserConnected = true;
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
        }

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
