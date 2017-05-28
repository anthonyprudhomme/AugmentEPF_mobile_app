package com.filiereticsa.arc.augmentepf.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.interfaces.HTTPRequestInterface;
import com.filiereticsa.arc.augmentepf.managers.FileManager;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;

import org.json.JSONException;
import org.json.JSONObject;

import static com.filiereticsa.arc.augmentepf.activities.CreateAccountActivity.CREDENTIALS_JSON;
import static com.filiereticsa.arc.augmentepf.activities.CreateAccountActivity.NAME;

public class ConnectionActivity extends AppCompatActivity implements HTTPRequestInterface {

    public static final String CONNECTION_PHP = "connection.php";
    private static final String NAME = "name";
    private static final String PASSWORD = "password";
    private static final String TAG = "Ici";
    private static final String ERROR = "Error";
    private static final String MESSAGE = "message";
    private static final String VALIDATE = "validate";
    private static final String YES = "y";
    private EditText login;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        JSONObject credentials = loadCredentials();
        if (credentials != null){
            try {
                login.setText(credentials.getString(NAME));
                password.setText(credentials.getString(PASSWORD));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject loadCredentials(){
        FileManager fileManager = new FileManager(null, CREDENTIALS_JSON);
        JSONObject jsonDataRead = null;
        try {
            jsonDataRead = new JSONObject(fileManager.readFile());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonDataRead;
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
        Log.d(TAG, "sendAccountCreationToServer: " + jsonObject.toString());
        HTTPRequestManager.doPostRequest(CONNECTION_PHP, jsonObject.toString(),
                this, HTTPRequestManager.CONNECTION);
    }

    public void onCreateAccountClick(View view) {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestDone(String result, int requestId) {
        switch (requestId) {
            case HTTPRequestManager.CONNECTION:
                if (result.equals(ERROR)) {
                    Toast.makeText(this, R.string.error_server, Toast.LENGTH_SHORT).show();
                }
                try {
                    // Put the result in a JSONObject to use it.
                    JSONObject jsonObject = new JSONObject(result);
                    // Show in the log the message given by the result : it will give error or success information
                    Log.d(TAG, "onRequestDone: " + jsonObject.getString(MESSAGE));
                    String success = jsonObject.getString(VALIDATE);
                    if (success.equals(YES)){
                        Toast.makeText(this, R.string.connected, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, com.filiereticsa.arc.augmentepf.activities.HomePageActivity.class);
                        startActivity(intent);
                    }else{
                        // If request failed, shows the message from the server
                        String message = jsonObject.getString(MESSAGE);
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
