package com.filiereticsa.arc.augmentepf.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.filiereticsa.arc.augmentepf.R;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setLogo(R.drawable.logo_appli);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        Intent intent;
        switch (itemID){
            case android.R.id.home  :
                finish();
                return true;

            default: break;
        }
        return super.onOptionsItemSelected(item);
    }
}
