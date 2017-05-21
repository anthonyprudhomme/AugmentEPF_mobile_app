package com.filiereticsa.arc.augmentepf.Localization;

import android.util.Log;
import android.util.Pair;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;

/**
 * Created by anthonyprudhomme on 04/08/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */
public class GABeacon {

    private final static double LIMIT_UNKNOWN_IMMEDIATE = 0;
    private final static double LIMIT_IMMEDIATE_NEAR = 0.5;
    private final static double LIMIT_NEAR_FAR = 3;
    private static final String TAG = "Ici";
    private static int proximityHistorySize = 1;
    private String name;
    public int xCoord;
    public int yCoord;
    private double accuracy = -1;
    private double oldAccuracy = Double.MAX_VALUE;
    private int amountOfSameAccuracy = 0;
    private final static int MAXIMUM_SAME_ACCURACY = 3;
    private String proximity = "Unknown";
    private String uuid;
    private int major;
    private int minor;
    private String color;
    private int mapId;
    private ArrayList<String> proximityHistory = new ArrayList<>();
    private GABeaconObserverLocalisation observerLocalisation = null;
    //private GABeaconObserver observer = null;
    private ArrayList<LocalizedBeaconStatus> keyString;
    public Pair<Integer, Integer> mapIndexPath;

    public static ArrayList<GABeacon> allBeacons;

    static {
        allBeacons = new ArrayList<>();

        //Lakanal Floor 2 i1 to i6
        allBeacons.add(new GABeacon("699EBC80-E1F3-11E3-9A0F-0CF3EE3BC012", 3, 42194, "BeaconName1", "White", 10, 8, 3,0));
        allBeacons.add(new GABeacon("699EBC80-E1F3-11E3-9A0F-0CF3EE3BC012", 3, 43216, "BeaconName2", "White", 10, 12, 3,0));
        allBeacons.add(new GABeacon("699EBC80-E1F3-11E3-9A0F-0CF3EE3BC012", 3, 43364, "BeaconName3", "White", 6, 19, 3,0));
        allBeacons.add(new GABeacon("699EBC80-E1F3-11E3-9A0F-0CF3EE3BC012", 1, 40935, "BeaconName4", "White", 3,25, 3,0));
        allBeacons.add(new GABeacon("699EBC80-E1F3-11E3-9A0F-0CF3EE3BC012", 3, 43348, "BeaconName5", "White", 6, 31, 3,0));
        allBeacons.add(new GABeacon("699EBC80-E1F3-11E3-9A0F-0CF3EE3BC012", 3, 44020, "BeaconName6", "White", 10, 31, 3,0));
        allBeacons.add(new GABeacon("699EBC80-E1F3-11E3-9A0F-0CF3EE3BC012", 1, 39757, "BeaconName7", "White", 10, 39, 3,0));
        allBeacons.add(new GABeacon("699EBC80-E1F3-11E3-9A0F-0CF3EE3BC012", 3, 42742, "BeaconName8", "White", 6, 13, 4,0));
        allBeacons.add(new GABeacon("699EBC80-E1F3-11E3-9A0F-0CF3EE3BC012", 3, 43184, "BeaconName8", "White", 4, 37, 3,0));

        //Lakanal Floor 1 1L to 6L
        //New beacons
        allBeacons.add(new GABeacon("699EBC80-E1F3-11E3-9A0F-0CF3EE3BC012", 3, 43232, "BeaconName8", "White", 10, 12, 3,1));
        allBeacons.add(new GABeacon("699EBC80-E1F3-11E3-9A0F-0CF3EE3BC012", 3, 43989, "BeaconName8", "White", 10, 18, 3,1));
        allBeacons.add(new GABeacon("699EBC80-E1F3-11E3-9A0F-0CF3EE3BC012", 3, 43060, "BeaconName8", "White", 7, 19, 3,1));
        allBeacons.add(new GABeacon("699EBC80-E1F3-11E3-9A0F-0CF3EE3BC012", 3, 43032, "BeaconName8", "White", 5, 25, 3,1));
        allBeacons.add(new GABeacon("699EBC80-E1F3-11E3-9A0F-0CF3EE3BC012", 3, 43104, "BeaconName8", "White", 7, 30, 3,1));
        allBeacons.add(new GABeacon("699EBC80-E1F3-11E3-9A0F-0CF3EE3BC012", 3, 43277, "BeaconName8", "White", 10, 31, 3,1));
        allBeacons.add(new GABeacon("699EBC80-E1F3-11E3-9A0F-0CF3EE3BC012", 3, 43045, "BeaconName8", "White", 10, 37, 3,1));

        //Unused beacon
        //allBeacons.add(new GABeacon("699EBC80-E1F3-11E3-9A0F-0CF3EE3BC012", 3, 44067, "BeaconName8", "White", 10, 12, 3,1));
    }

    public static int getProximityHistorySize() {
        return proximityHistorySize;
    }

    public int getGraniteId() {
        return graniteId;
    }

    public void setGraniteId(int graniteId) {
        this.graniteId = graniteId;
    }

    private int graniteId=-1;

    GABeacon(String uuidBeacon, int majorBeacon, int minorBeacon, String beaconName, String color, int xCoord, int yCoord, int threshold
    ,int mapId) {
        this.uuid = uuidBeacon;
        this.major = majorBeacon;
        this.minor = minorBeacon;
        this.name = beaconName;
        this.color = color;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        proximityHistorySize = threshold;
        this.setIndexPath(yCoord,xCoord);
        this.setMapId(mapId);
    }

    private String getProximityFromDistance(double distance) {
        if (distance < LIMIT_UNKNOWN_IMMEDIATE) {
            return "Unknown";
        } else if (distance < LIMIT_IMMEDIATE_NEAR) {
            return "Immediate";
        } else if (distance < LIMIT_NEAR_FAR) {
            return "Near";
        } else {
            return "Far";
        }
    }

    public void setDistance(double accuracy) {
//        Log.d(TAG, "setDistance: "+this.oldAccuracy +" "+ accuracy);
//        if(this.oldAccuracy == accuracy && this.oldAccuracy != Double.MAX_VALUE){
//            Log.d(TAG, "setDistance: same value");
//            amountOfSameAccuracy++;
//            if (amountOfSameAccuracy == MAXIMUM_SAME_ACCURACY){
//                //Log.d(TAG, "setDistance: "+this.getMinor()+" "+ this.getAccuracy());
//                accuracy = Double.MAX_VALUE;
//                amountOfSameAccuracy = 0;
//            }
//        }else{
//            amountOfSameAccuracy = 0;
//        }
        this.accuracy = accuracy;
        setProximity(accuracy);
//        this.oldAccuracy = accuracy;
    }

    private boolean hasConsecutiveProximity(String proximity, int number) {
        if (proximityHistory.size() < number) {
            return false;
        }
        int consecutiveProximityNr = 0;
        for (int i = 0; i < proximityHistory.size(); i++) {
            if (proximityHistory.get(i).equals(proximity)) {
                consecutiveProximityNr += 1;
                if (consecutiveProximityNr >= number) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getProximity() {
        return proximity;
    }

    private void setProximity(double accuracy) {
        String lastProximity = this.proximity;
        this.proximity = getProximityFromDistance(accuracy);
        /**
         *  Removed first value of the array ProximityHistory if the size is higher than _proximityHistorySize
         */
        if (proximityHistory.size() >= proximityHistorySize) {
            proximityHistory.remove(0);
        }
        proximityHistory.add(proximity);
        if (proximity.equals("Unknown") && (!hasConsecutiveProximity("Unknown", proximityHistorySize))) {
            proximity = lastProximity;
        }
    }

    public int getMapId() {
        return mapId;
    }

    void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public void setProximityHistorySize() {
        if (proximityHistorySize < proximityHistory.size()) {
            // Remove unnecessary oldest data
            for (int i = proximityHistorySize; i < proximityHistory.size(); i++) {
                proximityHistory.remove(i);
            }
        }
    }

    public GABeaconObserverLocalisation getObserverLocalisation() {
        return this.observerLocalisation;
    }

    public void setObserverLocalisation(GABeaconObserverLocalisation observerLocalisation) {
        this.observerLocalisation = observerLocalisation;
    }

//    public GABeaconObserver getObserver() {
//        return this.observer;
//    }
//
//    void setObserver(GABeaconObserver observer) {
//        this.observer = observer;
//    }

    public String getUuid() {
        return this.uuid;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Pair<Integer,Integer> getMapIndexPath() {
        return mapIndexPath;
    }

    public void setIndexPath(int beaconRow, int beaconColumn) {
        this.mapIndexPath = new Pair<>(beaconRow,beaconColumn);
    }

//    interface GABeaconObserver {
//        void beacon(GABeacon beacon, GATagRule.GABeaconEvent event);
//    }

    interface GABeaconObserverLocalisation {
        void updateLocalisation();

    }

    /// Get to return the UUID/major/minor
    public String getKeyString() {
        return uuid.toUpperCase() + "/" + major + "/" + minor;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public static GABeacon findBeacon(Beacon altBeacon) {
        for (int i = 0; i < allBeacons.size(); i++) {
            GABeacon beacon = allBeacons.get(i);
            if (altBeacon.getId2().toInt() == -1) {

                if (beacon.getUuid().equalsIgnoreCase(altBeacon.getId1().toString())
                        && beacon.getMinor() == altBeacon.getId3().toInt()) {
                    beacon.setDistance(altBeacon.getDistance());
                    return beacon;
                } else {
                    return null;
                }
            }
            if (beacon.getUuid().equalsIgnoreCase(altBeacon.getId1().toString())
                    && beacon.getMinor() == altBeacon.getId3().toInt()
                    && beacon.getMajor() == altBeacon.getId2().toInt()) {
                beacon.setDistance(altBeacon.getDistance());
                return beacon;
            }
        }
        return null;
    }

}


