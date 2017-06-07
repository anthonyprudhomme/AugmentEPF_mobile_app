package com.filiereticsa.arc.augmentepf.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.filiereticsa.arc.augmentepf.R;

public class AdminActivity extends AppCompatActivity {
    private boolean editBeacon;
    private int current_floor;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editBeacon=true;
        current_floor=0;
        setContentView(R.layout.activity_admin);
        iv = (ImageView) findViewById(R.id.currentMap);
        iv.setImageResource(R.drawable.floor_admin);
        RadioButton rb = (RadioButton) findViewById(R.id.beaconEdit);
        rb.setChecked(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.admin_activity_title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //ImageView iv = (ImageView) findViewById(R.id.admin_imageView);
        switch(item.getItemId()){
            case android.R.id.home :
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
        TextView close = (TextView) findViewById(R.id.closestText);
        close.setText("");
        editBeacon=true;
    }

    public void onPOIClick(View view) {
        TextView close = (TextView) findViewById(R.id.closestText);
        close.setText("");
        editBeacon=false;
    }

    public void onGPSClick(View view) {
        EditText xcoord = (EditText) findViewById(R.id.xCoordText);
        EditText ycoord = (EditText) findViewById(R.id.yCoordText);
        TextView close = (TextView) findViewById(R.id.closestText);
        xcoord.setText("0.0");
        ycoord.setText("0.0");
        if (editBeacon){
            close.setText("B-747");
        }else
            close.setText("Cantina");
    }

    public void onSaveClick(View view) {
        EditText name = (EditText) findViewById(R.id.item_name_Text);

        if (name.getText().toString().matches("")){
            Toast.makeText(this,"Name required", Toast.LENGTH_SHORT).show();
        }else

        if (editBeacon){
            Toast.makeText(this,name.getText() + " saved", Toast.LENGTH_SHORT).show();
        }else Toast.makeText(this,name.getText() + " saved", Toast.LENGTH_SHORT).show();

    }

    public void onRemoveClick(View view) {
        final TextView close = (TextView) findViewById(R.id.closestText);

        if (close.getText().toString().matches("")){
            Toast.makeText(this,"None selected", Toast.LENGTH_SHORT).show();
        }else {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            remove(close);
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

    private void remove (TextView close){
        if (editBeacon){
            Toast.makeText(this, close.getText() + " removed", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this,close.getText() + " removed", Toast.LENGTH_SHORT).show();
    }

}
