package com.filiereticsa.arc.augmentepf.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.adapters.HistoryListAdapter;
import com.filiereticsa.arc.augmentepf.models.Path;
import com.filiereticsa.arc.augmentepf.models.Place;

import java.util.ArrayList;

public class PathConsultationActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_consultation);
        listView = (ListView) findViewById(R.id.path_history);

        HistoryListAdapter listAdapter =  new HistoryListAdapter(getApplicationContext(), Path.testPath);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO Opening pop-up Dialog / Activity with informations for Planned path
            }
        });

    }
}
