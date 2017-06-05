package com.filiereticsa.arc.augmentepf.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.filiereticsa.arc.augmentepf.R;

public class AdminEditActivity extends AppCompatActivity {

    private boolean PoITool;
    private int floor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        floor = getIntent().getIntExtra("floor",0);
        switch (floor){
            case 1:
                getSupportActionBar().setTitle("2nd floor");
                break;
            case 2:
                getSupportActionBar().setTitle("1st floor");
                break;
            case 3:
                getSupportActionBar().setTitle("Ground floor");
                break;
            case 4:
                getSupportActionBar().setTitle("Park floor");
                break;
            default:
                getSupportActionBar().setTitle("Unknown floor");
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBeaconClick(View view) {
        PoITool=false;
    }

    public void onPOIClick(View view) {
        PoITool=true;
    }
}
