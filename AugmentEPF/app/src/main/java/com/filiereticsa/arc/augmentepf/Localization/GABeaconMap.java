package com.filiereticsa.arc.augmentepf.Localization;


import android.util.Log;
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

    private static final String TAG = "Ici";
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
        this.nbRow = 20;
        this.nbCol = 50;
        this.floor = 0;
        this.heading = 188;
        dimensions = new Pair<>(nbRow, nbCol);
        //this.imagePath = "http://pre14.deviantart.net/f230/th/pre/i/2015/175/4/2/modern_tabletop_rpg_map__office_1_by_woekan-d8yjyyw.jpg";
        this.mapItems = new ArrayList<>();
//        for (int i = 0; i < nbRow; i++) {
//            for (int j = 0; j < nbCol; j++) {
//                this.mapItems.add(new MapItem(i+(j*nbRow), MapItem.MapItemType.Free,new Pair<>(i,j)));
//            }
//        }

        // Couloir i5/6 jusqu'aux toilettes
        int j = 0;
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(5,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(5,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(6,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(6,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(7,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(7,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(8,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(8,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(9,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(9,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(10,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(10,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(11,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(11,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(12,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(12,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(13,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(13,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(14,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(14,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(15,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(15,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(16,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(16,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(17,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(17,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(18,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(18,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(19,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(19,11)));

        //Montée du couloir avec escaliers sur la gauche
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(19,9)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(18,9)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(19,8)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(18,8)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(19,7)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(18,7)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(19,6)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(18,6)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(19,5)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(18,5)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(19,4)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(18,4)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(19,3)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(18,3)));


        //Passage avec l'ascenseur
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(20,4)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(20,3)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(21,4)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(21,3)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(22,4)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(22,3)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(23,4)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(23,3)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(24,4)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(24,3)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(25,4)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(25,3)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(26,4)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(26,3)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(27,4)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(27,3)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(28,4)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(28,3)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(29,4)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(29,3)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(30,4)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(30,3)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(31,4)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(31,3)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(32,4)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(32,3)));

        //Descente avec escaliers à droite
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(30,5)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(31,5)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(32,5)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(30,6)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(31,6)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(32,6)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(30,7)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(31,7)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(32,7)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(30,8)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(31,8)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(32,8)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(30,9)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(31,9)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(32,9)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(30,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(31,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(32,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(30,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(31,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(32,11)));

        //Couloir vers les salles i1/i2
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(33,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(33,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(34,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(34,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(35,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(35,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(36,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(36,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(37,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(37,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(38,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(38,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(39,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(39,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(40,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(40,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(41,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(41,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(42,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(42,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(43,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(43,11)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(44,10)));
        this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free,new Pair<>(44,11)));


//        Beacons
//        this.mapItems.add(new MapItem(0, MapItem.MapItemType.Beacon,new Pair<>(8,10)));
//        this.mapItems.add(new MapItem(0, MapItem.MapItemType.Beacon,new Pair<>(12,11)));
//        this.mapItems.add(new MapItem(0, MapItem.MapItemType.Beacon,new Pair<>(19,6)));
//        this.mapItems.add(new MapItem(0, MapItem.MapItemType.Beacon,new Pair<>(25,4)));
//        this.mapItems.add(new MapItem(0, MapItem.MapItemType.Beacon,new Pair<>(30,6)));
//        this.mapItems.add(new MapItem(0, MapItem.MapItemType.Beacon,new Pair<>(30,11)));
//        this.mapItems.add(new MapItem(0, MapItem.MapItemType.Beacon,new Pair<>(39,10)));


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
