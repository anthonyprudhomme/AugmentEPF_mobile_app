package com.filiereticsa.arc.augmentepf.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SlidingDrawer;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.localization.BeaconDetector;
import com.filiereticsa.arc.augmentepf.localization.BeaconDetectorInterface;
import com.filiereticsa.arc.augmentepf.localization.GAFrameworkUserTracker;
import com.filiereticsa.arc.augmentepf.localization.LocalizationFragment;

import org.altbeacon.beacon.BeaconManager;

public class HomePageActivity extends AppCompatActivity {
    private static final String TAG = "Ici";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    public static BeaconDetectorInterface beaconObserver;
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        askForPermission();
        setUpSlidingDrawers();
        GAFrameworkUserTracker.sharedTracker().startTrackingUser();
    }

    private void askForPermission() {
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
    }

    public void setUpSlidingDrawers() {
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

    @Override
    protected void onResume() {
        super.onResume();
        BeaconDetector.sharedBeaconDetector().bindBeaconManager();
        BeaconDetector.sharedBeaconDetector().setActivity(this);
        if (beaconManager.isBound(BeaconDetector.sharedBeaconDetector().getBeaconConsumer()))
            beaconManager.setBackgroundMode(false);
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

    public void onGoClick(View view) {
    }

    public void onAdminClick(View view) {
    }

    public void onConnectClick(View view) {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
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
