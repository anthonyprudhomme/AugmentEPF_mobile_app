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

import org.json.JSONException;
import org.json.JSONObject;

public class AdminActivity extends AppCompatActivity implements HTTPRequestInterface{
    public static final String BEACONNAME = "beaconname";
    public static final String BEACONXCOORD = "beaconxcoord";
    public static final String BEACONYCOORD = "beaconycoord";
    private boolean editBeacon;
    private int current_floor;
    private ImageView iv;
    private TextView closest;
    private EditText xcoord, ycoord, name;
    final private String ADMIN_MODIFICATION_PHP = "administrationChanges.php";

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
        } else

            save();

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
        JSONObject jsonObject = new JSONObject();
        String itemName = name.getText().toString();
        String itemXCoord = xcoord.getText().toString();
        String itemYCoord = ycoord.getText().toString();

        if (editBeacon) {
            try {
                jsonObject.put(BEACONNAME, itemName);
                jsonObject.put(BEACONXCOORD, itemXCoord);
                jsonObject.put(BEACONYCOORD, itemYCoord);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HTTPRequestManager.doPostRequest(ADMIN_MODIFICATION_PHP, jsonObject.toString(),
                    this, HTTPRequestManager.BEACONS);

            Toast.makeText(this, name.getText() + " saved", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, name.getText() + " saved", Toast.LENGTH_SHORT).show();
    }

    private void remove() {
        if (editBeacon) {

            Toast.makeText(this, closest.getText() + " removed", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, closest.getText() + " removed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestDone(String result) {

    }


}
