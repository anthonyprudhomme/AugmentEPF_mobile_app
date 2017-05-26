package com.filiereticsa.arc.augmentepf.models;

import android.util.Log;
import android.util.Pair;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.activities.AugmentEPFApplication;
import com.filiereticsa.arc.augmentepf.fragments.SearchFragment;
import com.filiereticsa.arc.augmentepf.localization.GABeaconMapHelper;
import com.filiereticsa.arc.augmentepf.localization.GAFrameworkUserTracker;
import com.filiereticsa.arc.augmentepf.managers.FileManager;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ARC© Team for AugmentEPF project on 07/05/2017.
 */

public class PointOfInterest extends Place {

    private static final String TAG = "Ici";
    private static final String POI_JSON = "poi.json";
    private static final String POINT_OF_INTEREST = "pointOfInterest";
    private static final String NAME = "name";
    public static final String FLOOR = "floor";
    public static final String POS_X = "posX";
    public static final String POS_Y = "posY";
    private static final String INFORMATION = "information";
    public static final String OK = "ok";
    public static final String PROBLEM = "problem";
    public static final String MESSAGE = "message";
    public static final String URL = "getPOI.php";
    private static ArrayList<PointOfInterest> pointOfInterests;
    private String information;
    private static ArrayList<Place> surroundingPoi = new ArrayList<>();

    public PointOfInterest(String name, Position position, String information) {
        super(name, position);
        this.information = information;
    }

    public PointOfInterest(JSONObject jsonObject) throws JSONException {
        super(jsonObject.getString(NAME), new Position(jsonObject.getInt(POS_X), jsonObject.getInt(POS_Y), jsonObject.getInt(FLOOR)));
        this.information = jsonObject.getString(INFORMATION);
    }

    static {
        pointOfInterests = new ArrayList<>();
        PointOfInterest poi1 = new PointOfInterest(AugmentEPFApplication.getAppContext().getString(R.string.man_restroom)+ " "+2,
                new Position(31, 6, 2),
                AugmentEPFApplication.getAppContext().getString(R.string.info_restroom_man));
        pointOfInterests.add(poi1);
        PointOfInterest poi2 = new PointOfInterest(AugmentEPFApplication.getAppContext().getString(R.string.woman_restroom)+ " "+2,
                new Position(19, 6, 2),
                AugmentEPFApplication.getAppContext().getString(R.string.info_restroom_woman));
        pointOfInterests.add(poi2);
        PointOfInterest poi3 = new PointOfInterest(AugmentEPFApplication.getAppContext().getString(R.string.man_restroom)+" "+1,
                new Position(30, 6, 1),
                AugmentEPFApplication.getAppContext().getString(R.string.info_restroom_man));
        pointOfInterests.add(poi3);
        PointOfInterest poi4 = new PointOfInterest(AugmentEPFApplication.getAppContext().getString(R.string.woman_restroom)+" "+1,
                new Position(19, 6, 1),
                AugmentEPFApplication.getAppContext().getString(R.string.info_restroom_woman));
        pointOfInterests.add(poi4);
    }

    public static ArrayList<Place> getSurroundingPoi() {
        if (surroundingPoi == null) {
            surroundingPoi = new ArrayList<>();
        } else {
            surroundingPoi.clear();
        }
        for (int i = 0; i < pointOfInterests.size(); i++) {
            surroundingPoi.add(pointOfInterests.get(i));
        }
        ArrayList<Pair<Place, Integer>> poiWithDistances = new ArrayList<>();
        if(GAFrameworkUserTracker.sharedTracker() != null
                && GAFrameworkUserTracker.sharedTracker().getCurrentUserLocation() != null) {
            Pair<Integer, Integer> currentPosition = GAFrameworkUserTracker.sharedTracker().getCurrentUserLocation().indexPath;

            if (currentPosition != null) {
                GABeaconMapHelper mapHelper = GAFrameworkUserTracker.sharedTracker().getMapHelper();
                for (int i = 0; i < surroundingPoi.size(); i++) {
                    Place currentPoi = surroundingPoi.get(i);
                    if (currentPoi.getPosition().getFloor() == mapHelper.getMapFloor()) {
                        surroundingPoi.remove(currentPoi);
                        poiWithDistances.add(
                                new Pair<>(
                                        currentPoi,
                                        mapHelper.pathFrom(currentPosition,
                                                new Pair<>(
                                                        currentPoi.getPosition().getPositionX(),
                                                        currentPoi.getPosition().getPositionY())
                                        ).second));
                    }
                }
                Collections.sort(poiWithDistances, new Comparator<Pair<Place, Integer>>() {
                    public int compare(Pair<Place, Integer> o1, Pair<Place, Integer> o2) {
                        if (o1.second < o2.second) {
                            return -1;
                        } else {
                            if (o1.second.equals(o2.second)) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    }
                });
                for (int i = poiWithDistances.size() - 1; i >= 0; i--) {
                    surroundingPoi.add(0, poiWithDistances.get(i).first);
                    Log.d(TAG, "surroundPOI: " + poiWithDistances.get(i).second);
                }
            }
        }
        return surroundingPoi;
    }

    public static ArrayList<PointOfInterest> getPointOfInterests() {
        return pointOfInterests;
    }

    public static void setPointOfInterests(ArrayList<PointOfInterest> pointOfInterests) {
        PointOfInterest.pointOfInterests = pointOfInterests;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public static JSONObject getJsonFromPOIs() {
        JSONObject poiAsJsonObject = new JSONObject();
        JSONArray poiAsJsonArray = new JSONArray();
        for (int i = 0; i < pointOfInterests.size(); i++) {
            PointOfInterest currentPoi = pointOfInterests.get(i);
            JSONObject currentPoiJson = new JSONObject();
            try {
                currentPoiJson.put(NAME, currentPoi.getName());
                currentPoiJson.put(FLOOR, currentPoi.getPosition().getFloor());
                currentPoiJson.put(POS_X, currentPoi.getPosition().getPositionX());
                currentPoiJson.put(POS_Y, currentPoi.getPosition().getPositionY());
                currentPoiJson.put(INFORMATION,currentPoi.getInformation());

                poiAsJsonArray.put(currentPoiJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            poiAsJsonObject.put(POINT_OF_INTEREST, poiAsJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return poiAsJsonObject;
    }

    public static void onPoiRequestDone(String result) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            String message = jsonObject.getString(MESSAGE);
            switch (message) {
                case OK:
                    JSONArray poiJsonArray = jsonObject.getJSONArray(POINT_OF_INTEREST);
                    if (pointOfInterests == null) {
                        pointOfInterests = new ArrayList<>();
                    }
                    pointOfInterests.clear();
                    for (int i = 0; i < poiJsonArray.length(); i++) {
                        pointOfInterests.add(
                                new PointOfInterest(poiJsonArray.getJSONObject(i)));
                    }
                    PointOfInterest.savePoiToFile();
                    break;

                case PROBLEM:

                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void askForPointOfInterests() {
        JSONObject jsonObject = new JSONObject();
        // TODO rename this according to Guilhem's name
        HTTPRequestManager.doPostRequest(URL, jsonObject.toString(),
                SearchFragment.httpRequestInterface, HTTPRequestManager.POI);
    }

    public static void savePoiToFile() {
        FileManager fileManager = new FileManager(null, POI_JSON);
        fileManager.saveFile(getJsonFromPOIs().toString());
    }

    public static void loadPOIFromFile() {
        FileManager fileManager = new FileManager(null, POI_JSON);
        String data = fileManager.readFile();
        if (data != null && !data.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                PointOfInterest.loadPOIFromJson(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static void loadPOIFromJson(JSONObject poiAsJson) {
        pointOfInterests = new ArrayList<>();
        JSONArray poiAsJsonArray;
        try {
            poiAsJsonArray = poiAsJson.getJSONArray(POINT_OF_INTEREST);
            for (int i = 0; i < poiAsJsonArray.length(); i++) {
                JSONObject currentPoiJsonObject = poiAsJsonArray.getJSONObject(i);
                String name = currentPoiJsonObject.getString(NAME);
                int floor = currentPoiJsonObject.getInt(FLOOR);
                int positionX = currentPoiJsonObject.getInt(POS_X);
                int positionY = currentPoiJsonObject.getInt(POS_Y);
                String information = currentPoiJsonObject.getString(INFORMATION);
                PointOfInterest pointOfInterest = new PointOfInterest(name, new Position(positionX, positionY, floor),information);
                pointOfInterests.add(pointOfInterest);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
