package com.filiereticsa.arc.augmentepf.activities;

import android.app.Application;
import android.content.Context;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.service.RangedBeacon;


/**
 * Created by ARC© Team for AugmentEPF project on 07/05/2017.
 */

public class AugmentEPFApplication extends Application {

    private static Context context;

    public static Context getAppContext() {
        return AugmentEPFApplication.context;
    }

    public void onCreate() {
        super.onCreate();
        AugmentEPFApplication.context = getApplicationContext();

        BeaconManager beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        RangedBeacon.setSampleExpirationMilliseconds(1000);
    }
}
