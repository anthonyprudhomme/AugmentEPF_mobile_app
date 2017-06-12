package com.filiereticsa.arc.augmentepf.localization;

import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ARC© Team for AugmentEPF project on 25/05/2017.
 */

public class FloorAccess {

    public static final String FLOOR_ACCESS_TYPE = "floorAccessType";
    public static final String FLOOR_ACCESS_X_POS = "floorAccessXPos";
    public static final String FLOOR_ACCESS_Y_POS = "floorAccessYPos";
    public static final String FLOOR_POSSIBILITY = "floorPossibility";
    public static final String FLOOR_ACCESS_POSSIBILITIES = "floorAccessPossibilities";
    private Pair<Integer, Integer> position;
    private FloorAccessType floorAccessType;
    private ArrayList<Integer> floorsPossibilities;

    public FloorAccess(Pair<Integer, Integer> position, FloorAccessType floorAccessType, ArrayList<Integer> floorsPossibilities) {
        this.position = position;
        this.floorAccessType = floorAccessType;
        this.floorsPossibilities = floorsPossibilities;
    }

    public FloorAccess(JSONObject currentFloorAccess) {
        try {
            int xPos = currentFloorAccess.getInt(FLOOR_ACCESS_X_POS);
            int yPos = currentFloorAccess.getInt(FLOOR_ACCESS_Y_POS);
            this.position = new Pair<>(xPos, yPos);

            this.floorAccessType = FloorAccessType.valueOf(currentFloorAccess.getString(FLOOR_ACCESS_TYPE));

            JSONArray floorPossibilitiesJsonArray = currentFloorAccess.getJSONArray(FLOOR_ACCESS_POSSIBILITIES);
            this.floorsPossibilities = new ArrayList<>();
            for (int i = 0; i < floorPossibilitiesJsonArray.length(); i++) {
                this.floorsPossibilities.add(floorPossibilitiesJsonArray.getJSONObject(i).getInt(FLOOR_POSSIBILITY));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Pair<Integer, Integer> getPosition() {
        return position;
    }

    public FloorAccessType getFloorAccessType() {
        return floorAccessType;
    }

    public ArrayList<Integer> getFloorsPossibilities() {
        return floorsPossibilities;
    }

    public enum FloorAccessType {
        STAIRS,
        ELEVATOR
    }

}
