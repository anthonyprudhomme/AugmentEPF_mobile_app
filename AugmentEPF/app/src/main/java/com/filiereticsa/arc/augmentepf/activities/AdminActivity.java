package com.filiereticsa.arc.augmentepf.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.filiereticsa.arc.augmentepf.R;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.admin_activity_title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onFloor1Click(View view) {
        Intent intent = new Intent(this, AdminEditActivity.class);
        intent.putExtra("floor",1);
        startActivity(intent);
    }

    public void onFloor2Click(View view) {
        Intent intent = new Intent(this, AdminEditActivity.class);
        intent.putExtra("floor",2);
        startActivity(intent);
    }

    public void onFloor3Click(View view) {
        Intent intent = new Intent(this, AdminEditActivity.class);
        intent.putExtra("floor",3);
        startActivity(intent);
    }

    public void onFloor4Click(View view) {
        Intent intent = new Intent(this, AdminEditActivity.class);
        intent.putExtra("floor",4);
        startActivity(intent);
    }
}
