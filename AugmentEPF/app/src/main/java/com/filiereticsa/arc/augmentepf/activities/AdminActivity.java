package com.filiereticsa.arc.augmentepf.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
    private boolean editBeacon;
    private int current_floor;
    private ImageView iv;
    private TextView closest;
    private EditText xcoord, ycoord, name;
    private String itemName, itemXCoord, itemYCoord;
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.admin_activity_title);
        iv = (ImageView) findViewById(R.id.currentMap);
        RadioButton rb = (RadioButton) findViewById(R.id.beaconEdit);
        closest = (TextView) findViewById(R.id.closestText);
        name = (EditText) findViewById(R.id.item_name_Text);
        xcoord = (EditText) findViewById(R.id.xCoordText);
        ycoord = (EditText) findViewById(R.id.yCoordText);
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
        editBeacon = true;
    }

    public void onPOIClick(View view) {
        closest.setText("");
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
        if (name.getText().toString().matches("")) {
            Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show();
        } else save();
    }

    public void onRemoveClick(View view) {
        if (closest.getText().toString().matches("")) {
            Toast.makeText(this, "None selected", Toast.LENGTH_SHORT).show();
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
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("Cancel", dialogClickListener).show();
        }
    }

    private void save() {
        itemName = name.getText().toString();
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
            //Toast.makeText(this, name.getText() + " saved", Toast.LENGTH_SHORT).show();
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
        //Toast.makeText(this, name.getText() + " saved", Toast.LENGTH_SHORT).show();
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
            //Toast.makeText(this, closest.getText() + " removed", Toast.LENGTH_SHORT).show();
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
        //Toast.makeText(this, closest.getText() + " removed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestDone(String result, int requestId) {
        if (result.equals(ERROR)) {
            Toast.makeText(this, R.string.error_server, Toast.LENGTH_SHORT).show();
        }else
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
        }
    }
}
