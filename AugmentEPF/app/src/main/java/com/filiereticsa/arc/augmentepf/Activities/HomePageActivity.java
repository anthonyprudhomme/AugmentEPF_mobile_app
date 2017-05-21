package com.filiereticsa.arc.augmentepf.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.filiereticsa.arc.augmentepf.Localization.BeaconDetector;
import com.filiereticsa.arc.augmentepf.Localization.BeaconDetectorInterface;
import com.filiereticsa.arc.augmentepf.Localization.GAFrameworkUserTracker;
import com.filiereticsa.arc.augmentepf.Localization.LocalizationFragment;
import com.filiereticsa.arc.augmentepf.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;

import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity implements BeaconDetectorInterface {

    private static final String TAG = "Ici";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    public static BeaconDetectorInterface beaconObserver;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

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
    }

    /* Create a menu to go to settings activity */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Display the menu
        getMenuInflater().inflate(R.menu.home, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /* Item selected in the menu above */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Initialize the intention
        Intent intent = new Intent();

        // Which item is selected?
        switch (item.getItemId()) {
            // The user want to plan a path
            case R.id.action_plan:
                intent = new Intent(this,PathPlanningActivity.class);
                break;
            // The user want to see settings
            case R.id.action_settings:
                intent = new Intent(this,SettingsActivity.class);
                break;
            default:
                break;
        }

        // Start the activity of the item selected
        startActivity(intent);

        return super.onOptionsItemSelected(item);
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
    public void rangedBeacons(ArrayList<Beacon> beacons) {
        //Log.d(TAG,"Ranging beacons " + beacons.size());
        for (int i = 0; i < beacons.size(); i++) {
            //Log.d(TAG,""+beacons.get(i).getDistance());
        }
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

    /* I don't know why, but there are errors in this function */
    /*@Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
            Fragment currentFragment = getSupportFragmentManager().getFragments().get(i);
            if (currentFragment instanceof LocalizationFragment) {
                if (((LocalizationFragment) currentFragment).isGestureEnabled()) {
                    LocalizationFragment currentLocalizationFragment = (LocalizationFragment) currentFragment;
                    currentLocalizationFragment.getGestureDetector().onTouchEvent(event);
                    currentLocalizationFragment.setPointerCount(event.getPointerCount());
                    return currentLocalizationFragment.getmScaleDetector().onTouchEvent(event);
                }
            }
        }
        return false;
    }*/

}
