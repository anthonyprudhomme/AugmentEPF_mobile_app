package com.filiereticsa.arc.augmentepf.localization;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.filiereticsa.arc.augmentepf.activities.AugmentEPFApplication;
import com.filiereticsa.arc.augmentepf.activities.HomePageActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anthony on 09/05/2017.
 */

public class BeaconDetector implements BeaconConsumer {

    private static final String TAG = "Ici";
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 4;
    public static BeaconDetector sharedBeaconDetector;
    private Activity activity;
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(activity);
    private ArrayList<Beacon> wholeBeaconList = new ArrayList<>();
    private Map<Beacon, Integer> consecutiveUnknownMap = new HashMap<>();

    public static BeaconDetector sharedBeaconDetector() {
        if (sharedBeaconDetector == null) {
            sharedBeaconDetector = new BeaconDetector();
            sharedBeaconDetector.getBeaconManager().getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        }
        return sharedBeaconDetector;
    }

    public BeaconManager getBeaconManager() {
        return beaconManager;
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
                Log.d(TAG, "Determining state " + state);
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
        ArrayList<GABeacon> beaconsFound = new ArrayList<>();
        ArrayList<Beacon> beaconArrayList = new ArrayList<>(beacons);
        ArrayList<Beacon> lostBeacons = new ArrayList<>();
        // Get the list of the lost beacons
        for (int i = 0; i < wholeBeaconList.size(); i++) {
            if (!beaconArrayList.contains(wholeBeaconList.get(i))) {
                lostBeacons.add(wholeBeaconList.get(i));
                if (consecutiveUnknownMap.containsKey(wholeBeaconList.get(i))) {
                    consecutiveUnknownMap.put(wholeBeaconList.get(i), consecutiveUnknownMap.get(wholeBeaconList.get(i)) + 1);
                } else {
                    consecutiveUnknownMap.put(wholeBeaconList.get(i), 1);
                }
            }
        }
        // Add the new beacons to the wholeBeaconList and update the distance of old ones
        for (int i = 0; i < beaconArrayList.size(); i++) {
            int count = 0;
            Beacon currentBeacon = beaconArrayList.get(i);
            if (consecutiveUnknownMap.containsKey(currentBeacon)) {
                consecutiveUnknownMap.put(currentBeacon, 0);
            }
            if (wholeBeaconList.size() == 0) {
                wholeBeaconList.add(currentBeacon);
            }
            for (int j = 0; j < wholeBeaconList.size(); j++) {
                if ((!currentBeacon.getId2().equals(wholeBeaconList.get(j).getId2())) || (!currentBeacon.getId3().equals(wholeBeaconList.get(j).getId3()))) {
                    //Count how many beacons are different from the one we are looking at
                    count++;
                } else {
                    //Remove then add to refresh distance
                    wholeBeaconList.remove(count);
                    wholeBeaconList.add(count, currentBeacon);
                }
                //If we never found a beacon corresponding to the current one then we didn't have it in the list so let's add it

            }
            //Happens if the current beacon was different from every other beacons in the list
            if (count == wholeBeaconList.size()) {
                wholeBeaconList.add(currentBeacon);
            }
        }
        for (int i = 0; i < wholeBeaconList.size(); i++) {

            Beacon beacon = wholeBeaconList.get(i);
            GABeacon currentBeacon = GABeacon.findBeacon(beacon);
            double distance;
            if (!lostBeacons.contains(beacon)) {
                distance = beacon.getDistance();
            } else {
                distance = 10;
                Log.d(TAG, "myDidRangeBeaconsInRegion: lost a beacon");
                if (consecutiveUnknownMap.get(beacon) == GABeacon.getProximityHistorySize()) {
                    wholeBeaconList.remove(beacon);
                }
            }
            if (currentBeacon != null) {
                currentBeacon.setDistance(distance);
                beaconsFound.add(currentBeacon);
            }
        }
        HomePageActivity.beaconObserver.rangedBeacons(beaconsFound);
        GAFrameworkUserTracker.sharedTracker().rangedBeacons(beaconsFound);

    }

    private void myDidExitRegion(Region region) {
        Log.d(TAG, "Exited region");
        // Stop ranging the beacons
        try {
            beaconManager.stopRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void myDidEnterRegion(Region region) {
        Log.d(TAG, "Entered region");
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
