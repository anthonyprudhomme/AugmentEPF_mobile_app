package com.filiereticsa.arc.augmentepf.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.interfaces.HTTPRequestInterface;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdminActivity extends AppCompatActivity implements HTTPRequestInterface {
    public static final String CONTENT_TYPE = "contentType";
    public static final String CHANGE_TYPE = "changeType";
    public static final String CONTENT_INFORMATION = "contentInformation";
    public static final String ADMIN_MODIFICATION_PHP = "administrationChanges.php";
    public static final String ERROR = "Error";
    public static final String STATE = "state";
    public static final String TRUE = "true";
    public static final String MESSAGE = "message";
    public static final String GET_ELEMENT_PHP = "getElement.php";
    public static final String RESULT = "result";

    private boolean editBeacon;
    private int current_floor;
    private ImageView iv;
    private TextView closest, separator;
    private EditText xcoord, ycoord, poi_name, beacon_major, beacon_minor;
    private String itemName, itemXCoord, itemYCoord;
    private LinearLayout nameLayout;
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    JSONObject [] jsonObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.admin_activity_title);

        iv = (ImageView) findViewById(R.id.currentMap);
        RadioButton rb = (RadioButton) findViewById(R.id.beaconEdit);
        closest = (TextView) findViewById(R.id.closestText);
        xcoord = (EditText) findViewById(R.id.xCoordText);
        ycoord = (EditText) findViewById(R.id.yCoordText);
        nameLayout = (LinearLayout) findViewById(R.id.name_receiver);
        separator = new TextView(this);
        poi_name = new EditText(this);
        poi_name.setImeOptions(EditorInfo.IME_ACTION_DONE);
        poi_name.setSingleLine();
        beacon_major = new EditText(this);
        beacon_minor = new EditText(this);
        poi_name.setSingleLine();
        beacon_major.setSingleLine();
        beacon_minor.setSingleLine();
        separator.setText("/");
        nameLayout.addView(beacon_major);
        nameLayout.addView(separator);
        nameLayout.addView(beacon_minor);
        iv.setImageResource(R.drawable.floor_admin);
        rb.setChecked(true);
        editBeacon = true;
        current_floor = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_second_floor:
                iv.setImageResource(R.drawable.floor2_admin);
                current_floor = 2;
                break;
            case R.id.action_first_floor:
                iv.setImageResource(R.drawable.floor1_admin);
                current_floor = 1;
                break;
            case R.id.action_ground_floor:
                iv.setImageResource(R.drawable.floor_admin);
                current_floor = 0;
                break;
            case R.id.action_lower_floor:
                iv.setImageResource(R.drawable.floor_admin);
                current_floor = -1;
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBeaconClick(View view) {
        closest.setText("");
        nameLayout.removeView(poi_name);
        nameLayout.addView(beacon_major);
        nameLayout.addView(separator);
        nameLayout.addView(beacon_minor);
        editBeacon = true;
    }

    public void onPOIClick(View view) {
        closest.setText("");
        nameLayout.removeView(beacon_major);
        nameLayout.removeView(separator);
        nameLayout.removeView(beacon_minor);
        nameLayout.addView(poi_name);
        editBeacon = false;
    }

    public void onGPSClick(View view) {
        xcoord.setText("0.0");
        ycoord.setText("0.0");
        if (editBeacon) {
            closest.setText("B-747");
        } else
            closest.setText("Cantina");
    }

    public void onSaveClick(View view) {
        if (editBeacon){
            if (beacon_major.getText().toString().matches("") || beacon_minor.getText().toString().matches("")) {
                Toast.makeText(this, R.string.admin_name_missing, Toast.LENGTH_SHORT).show();
            } else checkForUpdate();
        }else
        if (poi_name.getText().toString().matches("")) {
            Toast.makeText(this, R.string.admin_name_missing, Toast.LENGTH_SHORT).show();
        } else checkForUpdate();
    }

    public void onRemoveClick(View view) {
        if (closest.getText().toString().matches("")) {
            Toast.makeText(this, R.string.admin_target_missing, Toast.LENGTH_SHORT).show();
        } else {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            remove();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.confirm).setPositiveButton(R.string.yes, dialogClickListener)
                    .setNegativeButton(R.string.cancel, dialogClickListener).show();
        }
    }

    private void checkForUpdate(){
        if (editBeacon) {
            try {
                jsonObject.put(CONTENT_TYPE, "beacon");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HTTPRequestManager.doPostRequest(GET_ELEMENT_PHP, jsonObject.toString(), this, HTTPRequestManager.ELEMENT);
        } else
            try {
                jsonObject.put(CONTENT_TYPE, "poi");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        HTTPRequestManager.doPostRequest(GET_ELEMENT_PHP, jsonObject.toString(), this, HTTPRequestManager.ELEMENT);
    }

    private void update() {
        if (editBeacon){
            itemName = beacon_major.getText().toString()+"/"+beacon_minor.getText().toString();
        }else {
            itemName = poi_name.getText().toString();
        }
        itemXCoord = xcoord.getText().toString();
        itemYCoord = ycoord.getText().toString();

        if (editBeacon) {
            try {
                jsonObject.put(CONTENT_TYPE, "beacon");
                jsonObject.put(CHANGE_TYPE, "update");
                jsonArray.put(itemXCoord + "/" + itemYCoord + "/" + current_floor);
                jsonArray.put(itemName);
                jsonObject.put(CONTENT_INFORMATION, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HTTPRequestManager.doPostRequest(ADMIN_MODIFICATION_PHP, jsonObject.toString(), this, HTTPRequestManager.BEACONS);
        } else
            try {
                jsonObject.put(CONTENT_TYPE, "poi");
                jsonObject.put(CHANGE_TYPE, "update");
                jsonArray.put(itemXCoord + "/" + itemYCoord + "/" + current_floor);
                jsonArray.put(itemName);
                jsonObject.put(CONTENT_INFORMATION, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        HTTPRequestManager.doPostRequest(ADMIN_MODIFICATION_PHP, jsonObject.toString(), this, HTTPRequestManager.POI);
    }

    private void save() {
        if (editBeacon){
            itemName = beacon_major.getText().toString()+"/"+beacon_minor.getText().toString();
        }else {
            itemName = poi_name.getText().toString();
        }
        itemXCoord = xcoord.getText().toString();
        itemYCoord = ycoord.getText().toString();

        if (editBeacon) {
            try {
                jsonObject.put(CONTENT_TYPE, "beacon");
                jsonObject.put(CHANGE_TYPE, "add");
                jsonArray.put(itemXCoord + "/" + itemYCoord + "/" + current_floor);
                jsonArray.put(itemName);
                jsonObject.put(CONTENT_INFORMATION, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HTTPRequestManager.doPostRequest(ADMIN_MODIFICATION_PHP, jsonObject.toString(), this, HTTPRequestManager.BEACONS);
        } else
            try {
                jsonObject.put(CONTENT_TYPE, "poi");
                jsonObject.put(CHANGE_TYPE, "add");
                jsonArray.put(itemXCoord + "/" + itemYCoord + "/" + current_floor);
                jsonArray.put(itemName);
                jsonObject.put(CONTENT_INFORMATION, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        HTTPRequestManager.doPostRequest(ADMIN_MODIFICATION_PHP, jsonObject.toString(), this, HTTPRequestManager.POI);
    }

    private void remove() {
        itemName = closest.getText().toString();
        itemXCoord = xcoord.getText().toString();
        itemYCoord = ycoord.getText().toString();
        if (editBeacon) {
            try {
                jsonObject.put(CONTENT_TYPE, "beacon");
                jsonObject.put(CHANGE_TYPE, "remove");
                jsonArray.put("");
                jsonArray.put(itemName);
                jsonObject.put(CONTENT_INFORMATION, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HTTPRequestManager.doPostRequest(ADMIN_MODIFICATION_PHP, jsonObject.toString(), this, HTTPRequestManager.BEACONS);
        } else
            try {
                jsonObject.put(CONTENT_TYPE, "poi");
                jsonObject.put(CHANGE_TYPE, "remove");
                jsonArray.put("");
                jsonArray.put(itemName);
                jsonObject.put(CONTENT_INFORMATION, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        HTTPRequestManager.doPostRequest(ADMIN_MODIFICATION_PHP, jsonObject.toString(), this, HTTPRequestManager.POI);
    }

    @Override
    public void onRequestDone(String result, int requestId) {
        if (result.equals(ERROR)) {
            Toast.makeText(this, R.string.error_server, Toast.LENGTH_SHORT).show();
        } else
            switch (requestId) {
                case HTTPRequestManager.BEACONS:
                    try {
                        // Put the result in a JSONObject to use it.
                        JSONObject jsonObject = new JSONObject(result);
                        String success = jsonObject.getString(STATE);
                        String message = jsonObject.getString(MESSAGE);
                        if (success.equals(TRUE)) {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            // If request failed, shows the message from the server
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HTTPRequestManager.POI:
                    try {
                        // Put the result in a JSONObject to use it.
                        JSONObject jsonObject = new JSONObject(result);
                        String success = jsonObject.getString(STATE);
                        String message = jsonObject.getString(MESSAGE);
                        if (success.equals(TRUE)) {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            // If request failed, shows the message from the server
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HTTPRequestManager.ELEMENT:
                    getResult(result);
                    break;
            }

    }

    private void getResult (String result){
        String targetName;
        if (editBeacon){
            targetName = beacon_major.getText().toString()+"/"+beacon_minor.getText().toString();
        }else {
            targetName = poi_name.getText().toString();
        }
        JSONArray resultArray;
        try {
            // Put the result in a JSONObject to use it.
            JSONObject jsonObject = new JSONObject(result);
            String success = jsonObject.getString(STATE);
            String message = jsonObject.getString(MESSAGE);
            if (success.equals(TRUE)) {
                resultArray = jsonObject.getJSONArray(RESULT);
                jsonObjects = new JSONObject[resultArray.length()];
                boolean existing = false;

                int i = 0;
                while (i<resultArray.length() || !existing){
                    jsonObjects[i]=resultArray.getJSONObject(i);
                    if (jsonObjects[i].getString("name").equalsIgnoreCase(targetName)){
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        update();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(R.string.admin_update).setPositiveButton(R.string.yes, dialogClickListener)
                                .setNegativeButton(R.string.cancel, dialogClickListener).show();
                        existing = true;
                    }
                    i++;
                }
                if (!existing) save();
            } else {
                // If request failed, shows the message from the server
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
