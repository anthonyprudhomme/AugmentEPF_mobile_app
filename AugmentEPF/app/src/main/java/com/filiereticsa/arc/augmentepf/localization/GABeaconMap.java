package com.filiereticsa.arc.augmentepf.localization;


import android.util.Pair;

import com.filiereticsa.arc.augmentepf.R;

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

    public static HashMap<Integer,GABeaconMap> maps;

    public GABeaconMap(int id) {
        if(maps == null){
            maps = new HashMap<>();
        }
        this.id = id;
        int j = 0;
        switch (id) {
            case 0:
                this.nbRow = 20;
                this.nbCol = 50;
                this.floor = 2;
                this.heading = 210;
                this.imageResId = R.drawable.plan_epf_etage2;
                dimensions = new Pair<>(nbRow, nbCol);
                this.mapItems = new ArrayList<>();

                // Couloir i5/6 jusqu'aux toilettes
                for (int i = 5; i <= 18; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 10)));
                }

                //Montée du couloir avec escaliers sur la gauche
                for (int i = 10; i >= 3; i--) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(19, i)));
                }

                //Passage avec l'ascenseur
                for (int i = 20; i <= 31; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 3)));
                }

                //Descente avec escaliers à droite
                for (int i = 4; i <= 10; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(31, i)));
                }
                //Couloir vers les salles i1/i2
                for (int i = 32; i <= 44; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 10)));
                }

                //Escaliers droite
                for (int i = 31; i <= 37; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 4)));
                }
                for (int i = 5; i <= 7; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(37, i)));
                }

                //Escaliers gauche
                for (int i = 18; i >= 13; i--) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 4)));
                }
                for (int i = 5; i <= 7; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(13, i)));
                }

                // This variable contains all the points where you can switch floors
                this.floorAccesses = new ArrayList<>();

                // Stairs on the left of the map
                ArrayList<Integer> nextFloors21 = new ArrayList<>();
                nextFloors21.add(1);
                FloorAccess floorAccess1 = new FloorAccess(new Pair<>(13,7), FloorAccess.FloorAccessType.STAIRS,nextFloors21);
                this.floorAccesses.add(floorAccess1);

                // Stairs on the right of the map
                ArrayList<Integer> nextFloors22 = new ArrayList<>();
                nextFloors22.add(1);
                FloorAccess floorAccess2 = new FloorAccess(new Pair<>(37,7), FloorAccess.FloorAccessType.STAIRS,nextFloors22);
                this.floorAccesses.add(floorAccess2);

                // Elevator
                ArrayList<Integer> nextFloors23 = new ArrayList<>();
                nextFloors23.add(1);
                nextFloors23.add(0);
                FloorAccess floorAccess3 = new FloorAccess(new Pair<>(25,4), FloorAccess.FloorAccessType.ELEVATOR,nextFloors23);
                this.floorAccesses.add(floorAccess3);
                break;

            case 1:
                this.nbRow = 19;
                this.nbCol = 48;
                this.floor = 1;
                this.heading = 210;
                this.imageResId = R.drawable.plan_epf_etage1;
                dimensions = new Pair<>(nbRow, nbCol);
                this.mapItems = new ArrayList<>();

                // Couloir 5L/6L jusqu'aux toilettes
                j = 0;
                for (int i = 12; i <= 19; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 10)));
                }

                //Montée du couloir avec escaliers sur la gauche
                for (int i = 9; i >= 5; i--) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(19, i)));
                }

                //Passage avec l'ascenseur
                for (int i = 20; i <= 30; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 5)));
                }

                //Descente avec escaliers à droite
                for (int i = 6; i <= 10; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(30, i)));
                }
                //Couloir vers les salles 1L/2L
                for (int i = 31; i <= 37; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 10)));
                }

                //Escaliers droite descente
                for (int i = 30; i <= 36; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 5)));
                }
                for (int i = 6; i <= 7; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(36, i)));
                }

                //Escaliers droite montée
                for (int i = 15; i <= 18; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 8)));
                }

                //Escaliers gauche descente
                for (int i = 18; i >= 13; i--) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 5)));
                }
                for (int i = 6; i <= 7; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(13, i)));
                }

                //Escaliers gauche montée
                for (int i = 31; i <= 35; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 8)));
                }

                // This variable contains all the points where you can switch floors
                this.floorAccesses = new ArrayList<>();

                // Stairs on the left of the map
                ArrayList<Integer> nextFloors11 = new ArrayList<>();
                nextFloors11.add(2);
                FloorAccess floorAccess11 = new FloorAccess(new Pair<>(13,7), FloorAccess.FloorAccessType.STAIRS,nextFloors11);
                this.floorAccesses.add(floorAccess11);

                // Stairs on the right of the map
                ArrayList<Integer> nextFloors12 = new ArrayList<>();
                nextFloors12.add(2);
                FloorAccess floorAccess21 = new FloorAccess(new Pair<>(36,7), FloorAccess.FloorAccessType.STAIRS,nextFloors12);
                this.floorAccesses.add(floorAccess21);

                // Stairs on the left of the map
                ArrayList<Integer> nextFloors14 = new ArrayList<>();
                nextFloors14.add(0);
                FloorAccess floorAccess14 = new FloorAccess(new Pair<>(15,8), FloorAccess.FloorAccessType.STAIRS,nextFloors14);
                this.floorAccesses.add(floorAccess14);

                // Stairs on the right of the map
                ArrayList<Integer> nextFloors15 = new ArrayList<>();
                nextFloors15.add(0);
                FloorAccess floorAccess25 = new FloorAccess(new Pair<>(35,8), FloorAccess.FloorAccessType.STAIRS,nextFloors15);
                this.floorAccesses.add(floorAccess25);

                // Elevator
                ArrayList<Integer> nextFloors13 = new ArrayList<>();
                nextFloors13.add(2);
                nextFloors13.add(0);
                FloorAccess floorAccess31 = new FloorAccess(new Pair<>(25,5), FloorAccess.FloorAccessType.ELEVATOR,nextFloors13);
                this.floorAccesses.add(floorAccess31);
                break;
        }
        maps.put(this.id,this);
    }

    // JSON from server
    public GABeaconMap(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        int rows = 0;
        int columns = 0;
        try {
            this.id = jsonObject.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            rows = jsonObject.getInt("rows");
            nbRow = rows;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            columns = jsonObject.getInt("columns");
            nbCol = columns;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            this.floor = jsonObject.getInt("floor");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            this.heading = jsonObject.getInt("heading");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dimensions = new Pair<>(rows, columns);
        // Image path
        JSONObject image;
        try {
            image = jsonObject.getJSONObject("image");
            JSONObject picture = image.getJSONObject("picture");
            this.imagePath = picture.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            this.id = jsonObject.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Map Items
        JSONArray jsonItems;
        try {
            jsonItems = jsonObject.getJSONArray("config_map_items");
            for (int i = 0; i < jsonItems.length(); i++) {
                JSONObject currentJsonObject = jsonItems.getJSONObject(i);
                MapItem mapItem = new MapItem(currentJsonObject);
                this.itemIdsToMapItems.put(mapItem.getId(), mapItem);
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
}
