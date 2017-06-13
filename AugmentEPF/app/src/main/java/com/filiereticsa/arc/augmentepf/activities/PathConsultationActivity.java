package com.filiereticsa.arc.augmentepf.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.adapters.HistoryListAdapter;
import com.filiereticsa.arc.augmentepf.interfaces.HTTPRequestInterface;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;
import com.filiereticsa.arc.augmentepf.models.ClassRoom;
import com.filiereticsa.arc.augmentepf.models.Path;

public class PathConsultationActivity extends AppCompatActivity implements HTTPRequestInterface {

    private static final String TAG = "Ici";
    private ListView listView;
    public static HTTPRequestInterface httpRequestInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        httpRequestInterface = this;
        setContentView(R.layout.activity_path_consultation);
        listView = (ListView) findViewById(R.id.path_history);

        HistoryListAdapter listAdapter = new HistoryListAdapter(getApplicationContext(), Path.getPaths());
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO Opening pop-up Dialog / Activity with informations for Planned path
            }
        });
        Path.askForPaths();
    }

    @Override
    public void onRequestDone(String result, int requestId) {
        Log.d(TAG, "onRequestDone: path history: "+result);
        switch (requestId) {
            case HTTPRequestManager.PATH_HISTORY:
                Path.onPathRequestDone(result);
                break;
        }
    }
}
