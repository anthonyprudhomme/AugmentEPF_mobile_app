package com.filiereticsa.arc.augmentepf.localization;

import android.util.Pair;

import com.filiereticsa.arc.augmentepf.activities.HomePageActivity;
import com.filiereticsa.arc.augmentepf.managers.FileManager;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;

import org.altbeacon.beacon.Beacon;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by anthonyprudhomme on 04/08/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */
public class GABeacon {

    private static final String TAG = "Ici";
    private final static double LIMIT_UNKNOWN_IMMEDIATE = 0;
    private final static double LIMIT_IMMEDIATE_NEAR = 0.5;
    private final static double LIMIT_NEAR_FAR = 3;
    public static final String ID_USER = "idUser";
    public static final String TOKEN = "token";
    public static final String CONTENT_TYPE = "contentType";
    public static final String RESULT = "result";
    public static ArrayList<GABeacon> allBeacons;
    private static int proximityHistorySize = 3;

    public static final String URL = "getElementAdministration.php";
    private static final String BEACON_JSON = "beacons.json";
    private static final String BEACON = "beacon";
    private static final String UUID = "uuid";
    private static final String MAJOR = "major";
    private static final String MINOR = "minor";
    private static final String FLOOR = "floor";
    private static final String POS_X = "x";
    private static final String POS_Y = "y";
    private static final String MESSAGE = "message";
    private static final String VALIDATE = "validate";
    private static final String YES = "y";
    private static final String NO = "n";

    public static final String EMBCUUID = "699EBC80-E1F3-11E3-9A0F-0CF3EE3BC012";

    static {
        allBeacons = new ArrayList<>();

        //Lakanal Floor 2 i1 to i6
        allBeacons.add(new GABeacon(EMBCUUID, 3, 42194, 8, 10, 2));
        allBeacons.add(new GABeacon(EMBCUUID, 3, 43216, 12, 10, 2));
        allBeacons.add(new GABeacon(EMBCUUID, 3, 43364, 19, 6, 2));
        allBeacons.add(new GABeacon(EMBCUUID, 1, 40935, 25, 3, 2));
        allBeacons.add(new GABeacon(EMBCUUID, 3, 43348, 31, 6, 2));
        allBeacons.add(new GABeacon(EMBCUUID, 3, 44067, 31, 10, 2));
        allBeacons.add(new GABeacon(EMBCUUID, 1, 39757, 39, 10, 2));
        allBeacons.add(new GABeacon(EMBCUUID, 3, 42742, 13, 6, 2));
        allBeacons.add(new GABeacon(EMBCUUID, 3, 43184, 37, 4, 2));

        //Lakanal Floor 1 1L to 6L
        allBeacons.add(new GABeacon(EMBCUUID, 3, 43232, 12, 10, 1));
        allBeacons.add(new GABeacon(EMBCUUID, 3, 43989, 18, 10, 1));
        allBeacons.add(new GABeacon(EMBCUUID, 3, 43060, 19, 7, 1));
        allBeacons.add(new GABeacon(EMBCUUID, 3, 43032, 25, 5, 1));
        allBeacons.add(new GABeacon(EMBCUUID, 3, 43104, 30, 7, 1));
        allBeacons.add(new GABeacon(EMBCUUID, 3, 43277, 30, 10, 1));
        allBeacons.add(new GABeacon(EMBCUUID, 3, 43045, 37, 10, 1));

        //Out of battery
        //allBeacons.add(new GABeacon(UUID, 3, 44020, "BeaconName6", "White", 10, 31, 3, 2));
    }

    public int xCoord;
    public int yCoord;
    public Pair<Integer, Integer> mapIndexPath;
    private String name;
    private double accuracy = -1;
    private String proximity = "Unknown";
    private String uuid;
    private int major;
    private int minor;
    private String color;
    private int mapId;
    private ArrayList<String> proximityHistory = new ArrayList<>();

    GABeacon(String uuidBeacon, int majorBeacon, int minorBeacon, int xCoord, int yCoord, int mapId) {
        this.uuid = uuidBeacon;
        this.major = majorBeacon;
        this.minor = minorBeacon;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.setIndexPath(xCoord, yCoord);
        this.setMapId(mapId);
    }

    GABeacon(JSONObject jsonObject) {
        try {
            this.uuid = jsonObject.getString(UUID);
            this.major = jsonObject.getInt(MAJOR);
            this.minor = jsonObject.getInt(MINOR);
            this.xCoord = jsonObject.getInt(POS_X);
            this.yCoord = jsonObject.getInt(POS_Y);
            this.setIndexPath(xCoord, yCoord);
            this.setMapId(jsonObject.getInt(FLOOR));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getMapId() {
        return mapId;
    }

    void setMapId(int mapId) {
        this.mapId = mapId;
    }

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

    public Pair<Integer, Integer> getMapIndexPath() {
        return mapIndexPath;
    }

    public void setIndexPath(int beaconRow, int beaconColumn) {
        this.mapIndexPath = new Pair<>(beaconRow, beaconColumn);
    }

    /// Get to return the UUID/major/minor
    public String getKeyString() {
        return uuid.toUpperCase() + "/" + major + "/" + minor;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public static int getProximityHistorySize() {
        return proximityHistorySize;
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
        this.accuracy = accuracy;
        setProximity(accuracy);
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

    public static JSONObject getJsonFromBeacons() {
        JSONObject beaconAsJsonObject = new JSONObject();
        JSONArray beaconAsJsonArray = new JSONArray();
        for (int i = 0; i < allBeacons.size(); i++) {
            GABeacon currentBeacon = allBeacons.get(i);
            JSONObject currentBeaconJson = new JSONObject();
            try {
                currentBeaconJson.put(UUID, currentBeacon.getUuid());
                currentBeaconJson.put(FLOOR, currentBeacon.getMapId());
                currentBeaconJson.put(POS_X, currentBeacon.getMapIndexPath().first);
                currentBeaconJson.put(POS_Y, currentBeacon.getMapIndexPath().second);
                currentBeaconJson.put(MINOR, currentBeacon.getMinor());
                currentBeaconJson.put(MAJOR, currentBeacon.getMajor());

                beaconAsJsonArray.put(currentBeaconJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            beaconAsJsonObject.put(BEACON, beaconAsJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return beaconAsJsonObject;
    }

    public static void onBeaconRequestDone(String result) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            JSONArray beaconJsonArray = jsonObject.getJSONArray(RESULT);
            if (allBeacons == null) {
                allBeacons = new ArrayList<>();
            }
            allBeacons.clear();
            for (int i = 0; i < beaconJsonArray.length(); i++) {
                JSONObject currentBeaconJson = beaconJsonArray.getJSONObject(i);
                int major = currentBeaconJson.getInt(MAJOR);
                int minor = currentBeaconJson.getInt(MINOR);
                int posX = currentBeaconJson.getInt(POS_X);
                int posY = currentBeaconJson.getInt(POS_Y);
                int floor = currentBeaconJson.getInt(FLOOR);
                allBeacons.add(
                        new GABeacon(EMBCUUID,major,minor,posX,posY,floor));
            }
            GABeacon.saveBeaconsToFile();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void askForBeacons() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CONTENT_TYPE, "beacon");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HTTPRequestManager.doPostRequest(URL, jsonObject.toString(),
                HomePageActivity.httpRequestInterface, HTTPRequestManager.BEACONS);
    }

    public static void saveBeaconsToFile() {
        FileManager fileManager = new FileManager(null, BEACON_JSON);
        fileManager.saveFile(getJsonFromBeacons().toString());
    }

    public static void loadBeaconsFromFile() {
        FileManager fileManager = new FileManager(null, BEACON_JSON);
        String data = fileManager.readFile();
        if (data != null && !data.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                GABeacon.loadBeaconsFromJson(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static void loadBeaconsFromJson(JSONObject beaconAsJson) {
        allBeacons = new ArrayList<>();
        JSONArray beaconAsJsonArray;
        try {
            beaconAsJsonArray = beaconAsJson.getJSONArray(BEACON);
            for (int i = 0; i < beaconAsJsonArray.length(); i++) {
                JSONObject currentBeaconJsonObject = beaconAsJsonArray.getJSONObject(i);
                GABeacon gaBeacon = new GABeacon(currentBeaconJsonObject);
                allBeacons.add(gaBeacon);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}


