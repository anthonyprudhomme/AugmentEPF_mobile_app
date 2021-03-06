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

    private static final String TAG = "Ici";
    private static final String ITEM_ID = "item_id";
    private static final String ITEM_TYPE = "type";
    private static final String POS_X = "posX";
    private static final String POS_Y = "pos_y";
    // Coordinates
    public Pair<Integer, Integer> coordinates = new Pair<>(0, 0);
    MapItemType type = MapItemType.Free;
    String name;
    String imageUrl;
    Bitmap image;
    private int id = 0;
    // Returns view for item
    private ImageView view;

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
            this.id = jsonObject.getInt(ITEM_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String typeString;
        try {
            typeString = jsonObject.getString(ITEM_TYPE);
            this.type = MapItemType.valueOf(typeString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Coordinates
        int posX = 0;
        int posY = 0;
        try {
            posX = jsonObject.getInt(POS_X);
            posY = jsonObject.getInt(POS_Y);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.d(TAG, "MapItem: "+posX+" "+posY);
        this.coordinates = new Pair<>(posX, posY);
    }

    public Integer getId() {
        return id;
    }


    // MARK: - Public functions

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

    enum MapItemType {
        Unknown("MapItemType.Unknown"),
        Busy("MapItemType.Busy"),
        Free("MapItemType.Free"),
        Beacon("MapItemType.Beacon"),
        Intersection("MapItemType.Intersection"),
        Table("MapItemType.Table");

        static ArrayList<MapItemType> allTypes = new ArrayList<>();
        String type;

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

}
