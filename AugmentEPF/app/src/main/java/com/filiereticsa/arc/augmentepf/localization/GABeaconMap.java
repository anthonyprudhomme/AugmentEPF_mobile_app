package com.filiereticsa.arc.augmentepf.localization;


import android.util.Log;
import android.util.Pair;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.activities.HomePageActivity;
import com.filiereticsa.arc.augmentepf.fragments.SearchFragment;
import com.filiereticsa.arc.augmentepf.managers.FileManager;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anthonyprudhomme on 11/10/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */
public class GABeaconMap {

    private static final String TAG = "Ici";
    public static final String FLOOR_ACCESS_TYPE = "floorAccessType";
    public static final String FLOOR_ACCESS_X_POS = "floorAccessXPos";
    public static final String FLOOR_ACCESS_Y_POS = "floorAccessYPos";
    public static final String FLOOR_POSSIBILITY = "floorPossibility";
    public static final String FLOOR_ACCESS_POSSIBILITIES = "floorAccessPossibilities";
    public static HashMap<Integer, GABeaconMap> maps;
    public static ArrayList<GABeaconMap> mapsArrayList;
    double heading = 0;
    // Image url and image
    String imagePath;
    private int id;
    private Pair<Integer, Integer> dimensions = new Pair<>(0, 0);
    private int floor = 0;
    private int nbRow = 0;
    private int nbCol = 0;
    private int imageResId;
    // Mapping between ids and map items
    private Map<Integer, MapItem> itemIdsToMapItems = new HashMap<>();
    // Map items accessor
    private ArrayList<MapItem> mapItems = null;
    private ArrayList<FloorAccess> floorAccesses;

    private static final String MAP_JSON = "maps.json";
    private static final String URL = "getMaps.php";
    private static final String MAP = "map";
    private static final String HEADING = "heading";
    private static final String ID = "id";
    private static final String FLOOR = "floor";
    private static final String ROW = "row";
    private static final String COL = "col";
    private static final String IMAGE_ID = "imageId";
    private static final String MAP_ITEMS = "maptItems";
    private static final String FLOOR_ACCESSES = "floorAccesses";
    private static final String VALIDATE = "validate";
    private static final String YES = "y";
    private static final String NO = "n";
    private static final String MESSAGE = "message";

    private static final String ITEM_ID = "item_id";
    private static final String ITEM_TYPE = "type";
    private static final String POS_X = "posX";
    private static final String POS_Y = "pos_y";

    static {
        if (maps == null) {
            maps = new HashMap<>();
        }
        int id = 2;
        int nbRow = 20;
        int nbCol = 50;
        int floor = 2;
        int heading = 210;
        int imageResId = R.drawable.plan_epf_etage2;
        int j = 0;
        ArrayList<MapItem> mapItems = new ArrayList<>();

        // Couloir i5/6 jusqu'aux toilettes
        for (int i = 5; i <= 18; i++) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 10)));
        }

        //Montée du couloir avec escaliers sur la gauche
        for (int i = 10; i >= 3; i--) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(19, i)));
        }

        //Passage avec l'ascenseur
        for (int i = 20; i <= 30; i++) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 3)));
        }

        //Descente avec escaliers à droite
        for (int i = 4; i <= 10; i++) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(31, i)));
        }
        //Couloir vers les salles i1/i2
        for (int i = 32; i <= 45; i++) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 10)));
        }

        //Escaliers droite
        for (int i = 31; i <= 37; i++) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 4)));
        }
        for (int i = 5; i <= 7; i++) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(37, i)));
        }

        //Escaliers gauche
        for (int i = 18; i >= 13; i--) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 4)));
        }
        for (int i = 5; i <= 7; i++) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(13, i)));
        }

        // This variable contains all the points where you can switch floors
        ArrayList<FloorAccess> floorAccesses = new ArrayList<>();

        // Stairs on the left of the map
        ArrayList<Integer> nextFloors21 = new ArrayList<>();
        nextFloors21.add(1);
        FloorAccess floorAccess1 = new FloorAccess(new Pair<>(13, 7), FloorAccess.FloorAccessType.STAIRS, nextFloors21);
        floorAccesses.add(floorAccess1);

        // Stairs on the right of the map
        ArrayList<Integer> nextFloors22 = new ArrayList<>();
        nextFloors22.add(1);
        FloorAccess floorAccess2 = new FloorAccess(new Pair<>(37, 7), FloorAccess.FloorAccessType.STAIRS, nextFloors22);
        floorAccesses.add(floorAccess2);

        // Elevator
        ArrayList<Integer> nextFloors23 = new ArrayList<>();
        nextFloors23.add(1);
        nextFloors23.add(0);
        FloorAccess floorAccess3 = new FloorAccess(new Pair<>(25, 3), FloorAccess.FloorAccessType.ELEVATOR, nextFloors23);
        floorAccesses.add(floorAccess3);
        GABeaconMap gaBeaconMap = new GABeaconMap(heading, id, floor, nbRow, nbCol,
                imageResId, mapItems, floorAccesses);
        maps.put(id, gaBeaconMap);
//---------------------------------------------------------------------------------------------------
        id = 1;
        nbRow = 19;
        nbCol = 48;
        floor = 1;
        heading = 210;
        imageResId = R.drawable.plan_epf_etage1;
        mapItems = new ArrayList<>();

        // Couloir 5L/6L jusqu'aux toilettes
        j = 0;
        for (int i = 11; i <= 19; i++) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 10)));
        }

        //Montée du couloir avec escaliers sur la gauche
        for (int i = 9; i >= 5; i--) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(19, i)));
        }

        //Passage avec l'ascenseur
        for (int i = 20; i <= 30; i++) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 5)));
        }

        //Descente avec escaliers à droite
        for (int i = 6; i <= 10; i++) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(30, i)));
        }
        //Couloir vers les salles 1L/2L
        for (int i = 31; i <= 38; i++) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 10)));
        }

        //Escaliers droite descente
        for (int i = 30; i <= 36; i++) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 5)));
        }
        for (int i = 6; i <= 7; i++) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(36, i)));
        }

        //Escaliers droite montée
        for (int i = 15; i <= 18; i++) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 8)));
        }

        //Escaliers gauche descente
        for (int i = 18; i >= 13; i--) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 5)));
        }
        for (int i = 6; i <= 7; i++) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(13, i)));
        }

        //Escaliers gauche montée
        for (int i = 31; i <= 35; i++) {
            mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 8)));
        }

        // This variable contains all the points where you can switch floors
        floorAccesses = new ArrayList<>();

        // Stairs on the left of the map
        ArrayList<Integer> nextFloors11 = new ArrayList<>();
        nextFloors11.add(2);
        FloorAccess floorAccess11 = new FloorAccess(new Pair<>(13, 7), FloorAccess.FloorAccessType.STAIRS, nextFloors11);
        floorAccesses.add(floorAccess11);

        // Stairs on the right of the map
        ArrayList<Integer> nextFloors12 = new ArrayList<>();
        nextFloors12.add(2);
        FloorAccess floorAccess21 = new FloorAccess(new Pair<>(36, 7), FloorAccess.FloorAccessType.STAIRS, nextFloors12);
        floorAccesses.add(floorAccess21);

        // Stairs on the left of the map
        ArrayList<Integer> nextFloors14 = new ArrayList<>();
        nextFloors14.add(0);
        FloorAccess floorAccess14 = new FloorAccess(new Pair<>(15, 8), FloorAccess.FloorAccessType.STAIRS, nextFloors14);
        floorAccesses.add(floorAccess14);

        // Stairs on the right of the map
        ArrayList<Integer> nextFloors15 = new ArrayList<>();
        nextFloors15.add(0);
        FloorAccess floorAccess25 = new FloorAccess(new Pair<>(35, 8), FloorAccess.FloorAccessType.STAIRS, nextFloors15);
        floorAccesses.add(floorAccess25);

        // Elevator
        ArrayList<Integer> nextFloors13 = new ArrayList<>();
        nextFloors13.add(2);
        nextFloors13.add(0);
        FloorAccess floorAccess31 = new FloorAccess(new Pair<>(25, 5), FloorAccess.FloorAccessType.ELEVATOR, nextFloors13);
        floorAccesses.add(floorAccess31);

        gaBeaconMap = new GABeaconMap(heading, id, floor, nbRow, nbCol,
                imageResId, mapItems, floorAccesses);
        maps.put(id, gaBeaconMap);

        mapsArrayList = new ArrayList<>(maps.values());
    }

    public GABeaconMap(double heading, int id,
                       int floor, int nbRow, int nbCol, int imageResId,
                       ArrayList<MapItem> mapItems, ArrayList<FloorAccess> floorAccesses) {
        this.heading = heading;
        this.id = id;
        this.floor = floor;
        this.nbRow = nbRow;
        this.nbCol = nbCol;
        this.dimensions = new Pair<>(nbRow, nbCol);
        this.imageResId = imageResId;
        this.mapItems = mapItems;
        this.floorAccesses = floorAccesses;
    }

//    public GABeaconMap(int id) {
//        if (maps == null) {
//            maps = new HashMap<>();
//        }
//        this.id = id;
//        int j = 0;
//        switch (id) {
//            case 2:
//
//                break;
//
//            case 1:
//
//                break;
//        }
//        maps.put(this.id, this);
//    }

    // JSON from server
    public GABeaconMap(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        int rows = 0;
        int columns = 0;
        try {
            this.id = jsonObject.getInt(ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            rows = jsonObject.getInt(ROW);
            nbRow = rows;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            columns = jsonObject.getInt(COL);
            nbCol = columns;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            this.floor = jsonObject.getInt(FLOOR);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            this.heading = jsonObject.getInt(HEADING);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dimensions = new Pair<>(rows, columns);
        try {
            this.imageResId = jsonObject.getInt(IMAGE_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Map Items
        JSONArray jsonItems;
        try {
            jsonItems = jsonObject.getJSONArray(MAP_ITEMS);
            this.mapItems = new ArrayList<>();
            for (int i = 0; i < jsonItems.length(); i++) {
                JSONObject currentJsonObject = jsonItems.getJSONObject(i);
                MapItem mapItem = new MapItem(currentJsonObject);
                this.itemIdsToMapItems.put(mapItem.getId(), mapItem);
                this.mapItems.add(mapItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Floor Accesses
        JSONArray jsonFloorAccesses;
        try {
            jsonFloorAccesses = jsonObject.getJSONArray(FLOOR_ACCESSES);
            for (int i = 0; i < jsonFloorAccesses.length(); i++) {
                JSONObject currentFloorAccess = jsonFloorAccesses.getJSONObject(i);
                FloorAccess floorAccess = new FloorAccess(currentFloorAccess);
                this.floorAccesses = new ArrayList<>();
                this.floorAccesses.add(floorAccess);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // Map Items Dictionary representation:
    //  - key  : index path with item coordinates (x,y)
    //  - value: a MapItem object
    public ArrayList<MapItem> mapItemsAtIndexPath(Pair<Integer, Integer> indexPath) {
        ArrayList<MapItem> listToReturn = new ArrayList<>();
        for (int i = 0; i < getMapItems().size(); i++) { //O(n x (n + 1))
            MapItem mapItem = getMapItems().get(i); //O(1)
            if (mapItem.coordinates.first.equals(indexPath.first) && mapItem.coordinates.second.equals(indexPath.second)) {
                listToReturn.add(mapItem); //O(n)
            }
        }
        return listToReturn;
    }

    // Path to image

    // MARK - Initializers

    public MapItem mapItemWithId(int id) {
        return itemIdsToMapItems.get(id);
    }

    public ArrayList<MapItem> getMapItems() {
        if (mapItems == null) {
            mapItems = new ArrayList<>(itemIdsToMapItems.values());
        }
        return mapItems;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getId() {
        return id;
    }

    public Pair<Integer, Integer> getMapDimensions() {
        return new Pair<>(nbRow, nbCol);
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getFloor() {
        return floor;
    }

    public ArrayList<FloorAccess> getFloorAccesses() {
        return floorAccesses;
    }

    public static JSONObject getJsonFromMaps() {
        JSONObject mapsAsJsonObject = new JSONObject();
        JSONArray mapsAsJsonArray = new JSONArray();
        for (int i = 0; i < mapsArrayList.size(); i++) {
            GABeaconMap currentMap = mapsArrayList.get(i);
            JSONObject currentMapsJson = new JSONObject();
            try {
                currentMapsJson.put(ID, currentMap.getId());
                currentMapsJson.put(FLOOR, currentMap.getFloor());
                currentMapsJson.put(ROW, currentMap.getNbRow());
                currentMapsJson.put(COL, currentMap.getNbCol());
                currentMapsJson.put(HEADING, currentMap.getHeading());
                JSONArray mapItems = new JSONArray();
                for (int j = 0; j < currentMap.mapItems.size(); j++) {
                    MapItem mapItem = currentMap.mapItems.get(j);
                    JSONObject mapItemJson = new JSONObject();
                    mapItemJson.put(ITEM_ID, mapItem.getId());
                    mapItemJson.put(ITEM_TYPE, mapItem.type.toString());
                    mapItemJson.put(POS_X, mapItem.coordinates.first);
                    mapItemJson.put(POS_Y, mapItem.coordinates.second);

                    mapItems.put(mapItemJson);
                }
                currentMapsJson.put(MAP_ITEMS, mapItems);
                currentMapsJson.put(IMAGE_ID, currentMap.getImageResId());

                JSONArray floorAccessesJsonArray = new JSONArray();
                for (int j = 0; j < currentMap.floorAccesses.size(); j++) {
                    FloorAccess floorAccess = currentMap.floorAccesses.get(j);
                    JSONObject floorAccessJson = new JSONObject();
                    floorAccessJson.put(FLOOR_ACCESS_TYPE, floorAccess.getFloorAccessType());
                    floorAccessJson.put(FLOOR_ACCESS_X_POS, floorAccess.getPosition().first);
                    floorAccessJson.put(FLOOR_ACCESS_Y_POS, floorAccess.getPosition().second);

                    JSONArray floorPossibilities = new JSONArray();
                    for (int k = 0; k < floorAccess.getFloorsPossibilities().size(); k++) {
                        int floor = floorAccess.getFloorsPossibilities().get(k);
                        JSONObject floorPossibilityJson = new JSONObject();
                        floorPossibilityJson.put(FLOOR_POSSIBILITY,floor);
                        floorPossibilities.put(floorPossibilityJson);
                    }
                    floorAccessJson.put(FLOOR_ACCESS_POSSIBILITIES,floorPossibilities);

                    floorAccessesJsonArray.put(floorAccessJson);
                }
                currentMapsJson.put(FLOOR_ACCESSES, floorAccessesJsonArray);

                mapsAsJsonArray.put(currentMapsJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            mapsAsJsonObject.put(MAP, mapsAsJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mapsAsJsonObject;
    }

    public static void onMapsRequestDone(String result) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            String validate = jsonObject.getString(VALIDATE);
            String message = jsonObject.getString(MESSAGE);
            switch (validate) {
                case YES:
                    JSONArray mapsJsonArray = jsonObject.getJSONArray(MAP);
                    if (mapsArrayList == null) {
                        mapsArrayList = new ArrayList<>();
                    }
                    if (maps == null) {
                        maps = new HashMap<>();
                    }
                    maps.clear();
                    mapsArrayList.clear();
                    for (int i = 0; i < mapsJsonArray.length(); i++) {
                        GABeaconMap gabeaconMap = new GABeaconMap(mapsJsonArray.getJSONObject(i));
                        mapsArrayList.add(gabeaconMap);
                        maps.put(gabeaconMap.getId(), gabeaconMap);
                    }
                    GABeaconMap.saveMapsToFile();
                    break;

                case NO:

                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void askForMaps() {
        JSONObject jsonObject = new JSONObject();
        // TODO rename this according to Guilhem's name
        HTTPRequestManager.doPostRequest(URL, jsonObject.toString(),
                HomePageActivity.httpRequestInterface, HTTPRequestManager.MAPS);
    }

    public static void saveMapsToFile() {
        FileManager fileManager = new FileManager(null, MAP_JSON);
        fileManager.saveFile(getJsonFromMaps().toString());
    }

    public static void loadMapsFromFile() {
        FileManager fileManager = new FileManager(null, MAP_JSON);
        String data = fileManager.readFile();
        if (data != null && !data.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                GABeaconMap.loadMapsFromJson(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static void loadMapsFromJson(JSONObject mapAsJson) {
        mapsArrayList = new ArrayList<>();
        maps = new HashMap<>();
        JSONArray mapAsJsonArray;
        try {
            mapAsJsonArray = mapAsJson.getJSONArray(MAP);
            for (int i = 0; i < mapAsJsonArray.length(); i++) {

                JSONObject currentMapJsonObject = mapAsJsonArray.getJSONObject(i);
                Log.d(TAG, "loadMapsFromJson: "+currentMapJsonObject.toString());
                GABeaconMap map = new GABeaconMap(currentMapJsonObject);
                mapsArrayList.add(map);
                maps.put(map.getId(), map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getNbRow() {
        return nbRow;
    }

    public int getNbCol() {
        return nbCol;
    }

    public double getHeading() {
        return heading;
    }
}
