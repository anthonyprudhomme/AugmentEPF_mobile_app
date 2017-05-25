package com.filiereticsa.arc.augmentepf.localization;

import android.util.Pair;

import java.util.ArrayList;

/**
 * Created by anthony on 25/05/2017.
 */

public class FloorAccess {

    private Pair<Integer, Integer> position;
    private FloorAccessType floorAccessType;
    private ArrayList<Integer> floorsPossibilities;

    public FloorAccess(Pair<Integer, Integer> position, FloorAccessType floorAccessType, ArrayList<Integer> floorsPossibilities) {
        this.position = position;
        this.floorAccessType = floorAccessType;
        this.floorsPossibilities = floorsPossibilities;
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

    enum FloorAccessType {
        STAIRS,
        ELEVATOR
    }

}
