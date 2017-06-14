package com.filiereticsa.arc.augmentepf.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.adapters.HistoryListAdapter;
import com.filiereticsa.arc.augmentepf.interfaces.HTTPRequestInterface;
import com.filiereticsa.arc.augmentepf.managers.HTTP;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;
import com.filiereticsa.arc.augmentepf.models.Path;

import org.json.JSONException;
import org.json.JSONObject;

public class PathConsultationActivity extends AppCompatActivity implements HTTPRequestInterface {

    private static final String TAG = "Ici";
    public static final String SUCCESS = "success";
    public static final String TRUE = "true";
    private ListView listView;
    public static HTTPRequestInterface httpRequestInterface;
    private HistoryListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        httpRequestInterface = this;
        setContentView(R.layout.activity_path_consultation);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        listAdapter = new HistoryListAdapter(getApplicationContext(), Path.getPaths());

        listView = (ListView) findViewById(R.id.path_history);
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
        switch (requestId) {
            case HTTPRequestManager.PATH_HISTORY:
                if (!result.equals(HTTP.ERROR)) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(result);

                        String state = jsonObject.getString(SUCCESS);
                        if (state.equals(TRUE)) {

                            Path.onPathRequestDone(result);
                            //listAdapter.clear();
                            //listAdapter.addAll(Path.getPaths());
                            listAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
