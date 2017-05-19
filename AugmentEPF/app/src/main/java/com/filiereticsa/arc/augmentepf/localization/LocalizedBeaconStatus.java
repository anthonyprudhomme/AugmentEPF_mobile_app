package com.filiereticsa.arc.augmentepf.localization;

import android.graphics.Point;
import android.util.Pair;

/**
 * Created by anthonyprudhomme on 11/10/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */

public class LocalizedBeaconStatus {
    String regionUUIDString;
    String proximity;
    double accuracy;

    Point coordinates;
    int mapId;
    Pair<Integer,Integer> mapIndexPath;

    String keyString;


    public LocalizedBeaconStatus(String uuidString, String proximity, double accuracy, Point coordinates, int mapId, Pair<Integer,Integer> mapIndexPath, String keyString) {
        if (keyString == null) {
            keyString = "";
        }
        this.regionUUIDString = uuidString;
        this.proximity = proximity;
        this.accuracy = accuracy;
        this.coordinates = coordinates;
        this.mapId = mapId;
        this.mapIndexPath = mapIndexPath;
        this.keyString = keyString;
    }

    public double description() {
        return this.accuracy;
    }

}
