package com.filiereticsa.arc.augmentepf.localization;

import android.util.Pair;

import java.util.ArrayList;

/**
 * Created by anthonyprudhomme on 11/10/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */

public interface GAFrameworkUserTrackerObserver {

    void userMovedToMap(GABeaconMap map);

    void userMovedToIndexPath(Pair<Integer, Integer> indexPath, double heading, double currentHeading, String direction);

    void userMovedToIndexPath(Pair<Integer, Integer> indexPath, ArrayList<Pair<Integer, Integer>> candidates);

    void userChangedDirection(Pair<Integer, Integer> newDirection);

    void onPathChanged(Pair<ArrayList<Pair<Integer, Integer>>, Integer> path, FloorAccess.FloorAccessType floorAccessType);

    void onOrientationChange(double currentHeading);
}
