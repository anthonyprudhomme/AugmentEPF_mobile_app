package com.filiereticsa.arc.augmentepf.Localization;


import android.util.Pair;

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

    private int id;

    public int getId() {
        return id;
    }

    double heading = 0;
    private Pair<Integer, Integer> dimensions = new Pair<>(0, 0);
    private int floor = 0;

    int nbRow =0;
    int nbCol = 0;

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

    // Mapping between ids and map items
    private Map<Integer, MapItem> itemIdsToMapItems = new HashMap<>();

    public MapItem mapItemWithId(int id) {
        return itemIdsToMapItems.get(id);
    }

    // Map items accessor
    private ArrayList<MapItem> mapItems = null;

    public ArrayList<MapItem> getMapItems() {
        if(mapItems == null) {
            mapItems = new ArrayList<>(itemIdsToMapItems.values());
        }
        return mapItems;
    }

    // Image url and image
    String imagePath;

    // Path to image

    // MARK - Initializers

    public GABeaconMap(){
        this.id = 0;
        this.nbRow = 15;
        this.nbCol = 15;
        this.floor = 0;
        this.heading = 0;
        dimensions = new Pair<>(nbRow, nbCol);
        //this.imagePath = "http://pre14.deviantart.net/f230/th/pre/i/2015/175/4/2/modern_tabletop_rpg_map__office_1_by_woekan-d8yjyyw.jpg";
        this.mapItems = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                this.mapItems.add(new MapItem(i+(j*15), MapItem.MapItemType.Free,new Pair<>(i,j)));
            }
        }
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

    public Pair<Integer,Integer> getDebugMapDimensions(){
        return new Pair<>(nbRow,nbCol);
    }

    public String getImagePath() {
        return imagePath;
    }
}
