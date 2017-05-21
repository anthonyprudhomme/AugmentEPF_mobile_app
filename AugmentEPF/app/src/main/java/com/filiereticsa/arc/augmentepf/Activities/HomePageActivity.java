package com.filiereticsa.arc.augmentepf.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SlidingDrawer;

import com.filiereticsa.arc.augmentepf.Localization.BeaconDetector;
import com.filiereticsa.arc.augmentepf.Localization.BeaconDetectorInterface;
import com.filiereticsa.arc.augmentepf.Localization.GABeacon;
import com.filiereticsa.arc.augmentepf.Localization.GAFrameworkUserTracker;
import com.filiereticsa.arc.augmentepf.Localization.LocalizationFragment;
import com.filiereticsa.arc.augmentepf.Managers.HTTPRequestManager;
import com.filiereticsa.arc.augmentepf.R;

import org.altbeacon.beacon.BeaconManager;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomePageActivity extends AppCompatActivity implements BeaconDetectorInterface {

    private static final String TAG = "Ici";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    public static BeaconDetectorInterface beaconObserver;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        beaconObserver = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.app_need_location);
                builder.setMessage(R.string.app_need_location_message);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }

                });
                builder.show();
            }
        }
        GAFrameworkUserTracker.sharedTracker().startTrackingUser();
        //GAFrameworkUserTracker.sharedTracker().setTarget(new Pair<>(31, 4));
        new HttpAsyncTask().execute("http://192.168.206.106/AugmentEPF/php/getNextLesson.php");
        final SlidingDrawer leftSlidingDrawer = (SlidingDrawer) findViewById(R.id.leftSlidingDrawer); // initiate the SlidingDrawer
        final SlidingDrawer rightSlidingDrawer = (SlidingDrawer) findViewById(R.id.rightSlidingDrawer); // initiate the SlidingDrawer


        leftSlidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                rightSlidingDrawer.close();
                rightSlidingDrawer.setVisibility(View.GONE);
            }
        });
        // implement setOnDrawerCloseListener event
        leftSlidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                rightSlidingDrawer.setVisibility(View.VISIBLE);
            }
        });


        rightSlidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                leftSlidingDrawer.close();
                leftSlidingDrawer.setVisibility(View.GONE);
            }
        });
        // implement setOnDrawerCloseListener event
        rightSlidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                leftSlidingDrawer.setVisibility(View.VISIBLE);
            }
        });
    }

    /* Create a menu to go to settings activity */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Display the menu
        getMenuInflater().inflate(R.menu.home, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BeaconDetector.sharedBeaconDetector().bindBeaconManager();
        BeaconDetector.sharedBeaconDetector().setActivity(this);
        if (beaconManager.isBound(BeaconDetector.sharedBeaconDetector().getBeaconConsumer()))
            beaconManager.setBackgroundMode(false);
    }

    @Override
    public void rangedBeacons(ArrayList<GABeacon> beacons) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BeaconDetector.sharedBeaconDetector.stopMonitoring();
        beaconManager.unbind(BeaconDetector.sharedBeaconDetector.getBeaconConsumer());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(BeaconDetector.sharedBeaconDetector.getBeaconConsumer()))
            beaconManager.setBackgroundMode(true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
            Fragment currentFragment = getSupportFragmentManager().getFragments().get(i);
            if (currentFragment instanceof LocalizationFragment) {
                if (((LocalizationFragment) currentFragment).isGestureEnabled()) {
                    LocalizationFragment currentLocalizationFragment = (LocalizationFragment) currentFragment;
                    currentLocalizationFragment.getGestureDetector().onTouchEvent(event);
                    currentLocalizationFragment.setPointerCount(event.getPointerCount());
                    return currentLocalizationFragment.getScaleDetector().onTouchEvent(event);
                }
            }
        }
        return false;
    }

    public String doGetRequest(String url) throws IOException {
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        Response response = client.newCall(request).execute();
//        return response.body().string();
        HTTPRequestManager httpRequestManager =
                new HTTPRequestManager("http://192.168.206.106/AugmentEPF/php/", "getNextLesson.php", "Send=144");
        return httpRequestManager.doPostHTTPRequest();
    }

    public static final MediaType JSON
            = MediaType.parse("application/x-www-form-urlencoded");

    public String doPostHttpRequest(String url, String query) throws IOException {
        RequestBody body = RequestBody.create(JSON, query);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String valueReturned = "";
            //Log.d(TAG, "doInBackground: before request");
            try {
                valueReturned = doPostHttpRequest(url, "Send=144");
                Log.d(TAG, "doInBackground: " + valueReturned);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return valueReturned;
        }

        @Override
        protected void onPostExecute(String returnedValue) {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public void onGoClick(View view) {
    }

    public void onAdminClick(View view) {
    }

    public void onConnectClick(View view) {
    }

    public void onPlannedClick(View view) {
        Intent intent = new Intent(this, PathPlanningActivity.class);
        startActivity(intent);
    }

    public void onHistoryClick(View view) {
        Intent intent = new Intent(this, PathConsultationActivity.class);
        startActivity(intent);
    }

    public void onCalendarClick(View view) {
    }

    public void onSettingsClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onContactClick(View view) {
    }

}
