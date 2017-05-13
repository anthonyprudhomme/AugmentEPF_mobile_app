package com.filiereticsa.arc.augmentepf.Localization;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.filiereticsa.arc.augmentepf.Activities.AugmentEPFApplication;
import com.filiereticsa.arc.augmentepf.Activities.HomePageActivity;

import org.altbeacon.beacon.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by anthony on 09/05/2017.
 */

public class BeaconDetector implements BeaconConsumer {

    private static final String TAG = "Ici";
    private Activity activity;
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(activity);
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 4;
    public static BeaconDetector sharedBeaconDetector;

    public BeaconManager getBeaconManager() {
        return beaconManager;
    }

    public static BeaconDetector sharedBeaconDetector(){
        if (sharedBeaconDetector == null){
            sharedBeaconDetector = new BeaconDetector();
            sharedBeaconDetector.getBeaconManager().getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        }
        return sharedBeaconDetector;
    }

    public void setActivity(FragmentActivity appActivity) {
        activity = appActivity;
    }

    private void checkBluetoothAndStart() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBlueTooth, REQUEST_CODE_ENABLE_BLUETOOTH);
            }
        }
        try {
            Region region = new Region("myRangingUniqueId", null, null, null);
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void bindBeaconManager() {
        beaconManager.bind(this);
    }

    public void stopMonitoring() {
        for (Region reg : beaconManager.getMonitoredRegions()) {
            try {
                beaconManager.stopMonitoringBeaconsInRegion(reg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBeaconServiceConnect() {
        stopMonitoring();
        checkBluetoothAndStart();
        beaconManager.getRangingNotifiers().clear();
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                myDidRangeBeaconsInRegion(beacons);
            }
        });
        beaconManager.getMonitoringNotifiers().clear();
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                myDidEnterRegion(region);
            }

            @Override
            public void didExitRegion(Region region) {
                myDidExitRegion(region);
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.d(TAG,"Determining state " + state);
                switch (state) {
                    case INSIDE:
                        try {
                            beaconManager.startRangingBeaconsInRegion(region);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    case OUTSIDE:
                        try {
                            beaconManager.stopRangingBeaconsInRegion(region);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }

    @Override
    public Context getApplicationContext() {
        return AugmentEPFApplication.getAppContext().getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection connection) {
        AugmentEPFApplication.getAppContext().unbindService(connection);
    }


    @Override
    public boolean bindService(Intent intent, ServiceConnection connection, int mode) {
        return AugmentEPFApplication.getAppContext().bindService(intent, connection, mode);
    }

    private void myDidRangeBeaconsInRegion(Collection<Beacon> beacons) {
        HomePageActivity.beaconObserver.rangedBeacons(new ArrayList<>(beacons));
        GAFrameworkUserTracker.sharedTracker().rangedBeacons(new ArrayList<>(beacons));

    }

    private void myDidExitRegion(Region region) {
        Log.d(TAG,"Exited region");
        // Stop ranging the beacons
        try {
            beaconManager.stopRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void myDidEnterRegion(Region region) {
        Log.d(TAG,"Entered region");
        // Start ranging the beacons
        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public BeaconConsumer getBeaconConsumer() {
        return this;
    }
}
