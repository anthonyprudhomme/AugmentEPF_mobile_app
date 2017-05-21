package com.filiereticsa.arc.augmentepf.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;

import com.filiereticsa.arc.augmentepf.fragments.SettingsFragment;
import com.filiereticsa.arc.augmentepf.localization.BeaconDetector;
import com.filiereticsa.arc.augmentepf.localization.BeaconDetectorInterface;
import com.filiereticsa.arc.augmentepf.localization.GAFrameworkUserTracker;
import com.filiereticsa.arc.augmentepf.localization.LocalizationFragment;
import com.filiereticsa.arc.augmentepf.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;


import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity implements BeaconDetectorInterface {
    private static final String TAG = "Ici";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    public static BeaconDetectorInterface beaconObserver;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private FragmentManager fragmentManager;

    private ImageButton findButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);
        beaconObserver = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Android M Permission check
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

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void onSettingsClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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
                    return currentLocalizationFragment.getmScaleDetector().onTouchEvent(event);
                }
            }
        }
        return false;
    }
}
