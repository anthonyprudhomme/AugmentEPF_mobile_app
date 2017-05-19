package com.filiereticsa.arc.augmentepf.localization;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by anthonyprudhomme on 11/10/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */
public class MapItem {

    private int id = 0;
    MapItemType type = MapItemType.Free;
    String name;
    String imageUrl;
    Bitmap image;

    // Coordinates
    Pair<Integer, Integer> coordinates = new Pair<>(0, 0);

    public MapItem(int id, MapItemType type, Pair<Integer, Integer> coordinates) {
        this.id = id;
        this.type = type;
        this.coordinates = coordinates;
    }

    public MapItem(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        try {
            this.id = jsonObject.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String typeString = null;
        try {
            typeString = jsonObject.getString("map_item_type");
            this.type = MapItemType.getTypeFromString(typeString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            String name = jsonObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Coordinates
        int row = 0;
        int column = 0;
        try {
            row = jsonObject.getInt("row");
            column = jsonObject.getInt("column");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.coordinates = new Pair<>(row,column);
    }

    public Integer getId() {
        return id;
    }

    enum MapItemType {
        Unknown("MapItemType.Unknown"),
        Busy("MapItemType.Busy"),
        Free("MapItemType.Free"),
        Beacon("MapItemType.Beacon"),
        Intersection("MapItemType.Intersection"),
        Table("MapItemType.Table");

        String type;
        static ArrayList<MapItemType> allTypes = new ArrayList<>();

        MapItemType(String itemType) {
            this.type = itemType;
        }

        public static MapItemType getTypeFromString(String itemType) {
            initAllTypes();
            switch (itemType) {
                case "MapItemType.Unknown":
                    return Unknown;
                case "MapItemType.Busy":
                    return Busy;
                case "MapItemType.Free":
                    return Free;
                case "MapItemType.Beacon":
                    return Beacon;
                case "MapItemType.Intersection":
                    return Intersection;
                case "MapItemType.Table":
                    return Table;
                default:
                    return null;
            }
        }

        private static void initAllTypes() {
            if (allTypes.size() == 0) {
                allTypes.add(Unknown);
                allTypes.add(Busy);
                allTypes.add(Free);
                allTypes.add(Beacon);
                allTypes.add(Intersection);
                allTypes.add(Table);


            }
        }

    }


    // MARK: - Public functions

    // Returns view for item
    private ImageView view;
    public ImageView getView(Context context) {
        if (view == null) {
            switch (type) {
                case Beacon:
                    break;
                case Free:
                    LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);

                    imageParams.gravity = Gravity.CENTER;
                    view = new ImageView(context);
                    view.setLayoutParams(imageParams);
                    break;
                default:
                    break;
            }
        }
        return view;
    }

}
