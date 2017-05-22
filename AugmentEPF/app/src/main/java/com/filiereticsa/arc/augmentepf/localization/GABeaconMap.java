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
                this.floor = 0;
                this.heading = 210;
                this.imageResId = R.drawable.plan_lakanal_etage2;
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
                break;

            case 1:
                this.nbRow = 48;
                this.nbCol = 19;
                this.floor = 0;
                this.heading = 210;
                this.imageResId = R.drawable.plan_lakanal_etage1;
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

                //Escaliers droite
                for (int i = 31; i <= 36; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 5)));
                }
                for (int i = 6; i <= 7; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(36, i)));
                }

                //Escaliers gauche
                for (int i = 18; i >= 13; i--) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(i, 5)));
                }
                for (int i = 6; i <= 7; i++) {
                    this.mapItems.add(new MapItem(j++, MapItem.MapItemType.Free, new Pair<>(13, i)));
                }
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

    public Pair<Integer, Integer> getDebugMapDimensions() {
        return new Pair<>(nbRow, nbCol);
    }

    public String getImagePath() {
        return imagePath;
    }
}
