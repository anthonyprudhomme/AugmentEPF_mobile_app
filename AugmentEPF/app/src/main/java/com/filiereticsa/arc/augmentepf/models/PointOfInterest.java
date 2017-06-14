package com.filiereticsa.arc.augmentepf.models;

import com.filiereticsa.arc.augmentepf.AppUtils;
import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.activities.AugmentEPFApplication;
import com.filiereticsa.arc.augmentepf.fragments.SearchFragment;
import com.filiereticsa.arc.augmentepf.managers.FileManager;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ARC© Team for AugmentEPF project on 07/05/2017.
 */

public class PointOfInterest extends Place {

    public static final String FLOOR = "floor";
    public static final String POS_X = "x";
    public static final String POS_Y = "y";
    public static final String URL = "getElement.php";
    public static final String CONTENT_TYPE = "contentType";
    private static final String TAG = "Ici";
    private static final String POI_JSON = "poi.json";
    private static final String POINT_OF_INTEREST = "poi";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    public static final String RESULT = "result";
    private static ArrayList<PointOfInterest> pointOfInterests;
    private static ArrayList<Place> surroundingPoi = new ArrayList<>();

    static {
        pointOfInterests = new ArrayList<>();
        PointOfInterest poi1 = new PointOfInterest(AugmentEPFApplication.getAppContext().getString(R.string.man_restroom) + " " + 2,
                new Position(31, 6, 2),
                AugmentEPFApplication.getAppContext().getString(R.string.info_restroom_man));
        pointOfInterests.add(poi1);
        PointOfInterest poi2 = new PointOfInterest(AugmentEPFApplication.getAppContext().getString(R.string.woman_restroom) + " " + 2,
                new Position(19, 6, 2),
                AugmentEPFApplication.getAppContext().getString(R.string.info_restroom_woman));
        pointOfInterests.add(poi2);
        PointOfInterest poi3 = new PointOfInterest(AugmentEPFApplication.getAppContext().getString(R.string.man_restroom) + " " + 1,
                new Position(30, 6, 1),
                AugmentEPFApplication.getAppContext().getString(R.string.info_restroom_man));
        pointOfInterests.add(poi3);
        PointOfInterest poi4 = new PointOfInterest(AugmentEPFApplication.getAppContext().getString(R.string.woman_restroom) + " " + 1,
                new Position(19, 6, 1),
                AugmentEPFApplication.getAppContext().getString(R.string.info_restroom_woman));
        pointOfInterests.add(poi4);
    }

    private String information;

    public PointOfInterest(String name, Position position, String information) {
        super(name, position);
        this.information = information;
    }

    public PointOfInterest(JSONObject jsonObject) throws JSONException {
        super(jsonObject.getString(NAME), new Position(jsonObject.getInt(POS_X), jsonObject.getInt(POS_Y), jsonObject.getInt(FLOOR)));
        this.information = jsonObject.getString(DESCRIPTION);
    }

    public static ArrayList<Place> getSurroundingPoi() {
        ArrayList<Place> classesAsPlaces = new ArrayList<>();
        for (int i = 0; i < pointOfInterests.size(); i++) {
            classesAsPlaces.add(pointOfInterests.get(i));
        }
        surroundingPoi = AppUtils.sortByClosest(classesAsPlaces);
        return surroundingPoi;
    }

    public static ArrayList<PointOfInterest> getPointOfInterests() {
        return pointOfInterests;
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
                currentPoiJson.put(DESCRIPTION, currentPoi.getInformation());

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
            JSONArray poiJsonArray = jsonObject.getJSONArray(RESULT);
            if (pointOfInterests == null) {
                pointOfInterests = new ArrayList<>();
            }
            pointOfInterests.clear();
            for (int i = 0; i < poiJsonArray.length(); i++) {
                JSONObject currentPoi = poiJsonArray.getJSONObject(i);
                pointOfInterests.add(new PointOfInterest(currentPoi));
            }
            PointOfInterest.savePoiToFile();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void askForPointOfInterests() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CONTENT_TYPE, "poi");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                String information = currentPoiJsonObject.getString(DESCRIPTION);
                PointOfInterest pointOfInterest = new PointOfInterest(name, new Position(positionX, positionY, floor), information);
                pointOfInterests.add(pointOfInterest);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static PointOfInterest getPoiCalled(String poiName) {
        for (int i = 0; i < pointOfInterests.size(); i++) {
            PointOfInterest currentPoi = pointOfInterests.get(i);
            if (currentPoi.getName().equals(poiName)) {
                return currentPoi;
            }
        }
        return null;
    }

    public static String[] getPoisAsStrings() {
        String[] classroomsAsStrings = new String[pointOfInterests.size()];
        for (int i = 0; i < pointOfInterests.size(); i++) {
            classroomsAsStrings[i] = pointOfInterests.get(i).getName();
        }
        return classroomsAsStrings;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }
}
