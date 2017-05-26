package com.filiereticsa.arc.augmentepf.models;

import android.util.Log;
import android.util.Pair;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.activities.AugmentEPFApplication;
import com.filiereticsa.arc.augmentepf.localization.GABeaconMapHelper;
import com.filiereticsa.arc.augmentepf.localization.GAFrameworkUserTracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by anthony on 07/05/2017.
 */

public class PointOfInterest extends Place {

    private static final String TAG = "Ici";
    private static ArrayList<PointOfInterest> pointOfInterests;
    private String information;
    private static ArrayList<Place> surroundingPoi = new ArrayList<>();

    public PointOfInterest(String name, Position position, String information) {
        super(name, position);
        this.information = information;
    }

    static {
        pointOfInterests = new ArrayList<>();
        PointOfInterest poi1 = new PointOfInterest(AugmentEPFApplication.getAppContext().getString(R.string.man_restroom)+ " "+2,
                new Position(31, 6, 2),
                AugmentEPFApplication.getAppContext().getString(R.string.info_restroom_man));
        pointOfInterests.add(poi1);
        PointOfInterest poi2 = new PointOfInterest(AugmentEPFApplication.getAppContext().getString(R.string.woman_restroom)+ " "+2,
                new Position(19, 6, 2),
                AugmentEPFApplication.getAppContext().getString(R.string.info_restroom_woman));
        pointOfInterests.add(poi2);
        PointOfInterest poi3 = new PointOfInterest(AugmentEPFApplication.getAppContext().getString(R.string.man_restroom)+" "+1,
                new Position(30, 6, 1),
                AugmentEPFApplication.getAppContext().getString(R.string.info_restroom_man));
        pointOfInterests.add(poi3);
        PointOfInterest poi4 = new PointOfInterest(AugmentEPFApplication.getAppContext().getString(R.string.woman_restroom)+" "+1,
                new Position(19, 6, 1),
                AugmentEPFApplication.getAppContext().getString(R.string.info_restroom_woman));
        pointOfInterests.add(poi4);
    }

    public static ArrayList<Place> getSurroundingPoi() {
        if (surroundingPoi == null) {
            surroundingPoi = new ArrayList<>();
        } else {
            surroundingPoi.clear();
        }
        for (int i = 0; i < pointOfInterests.size(); i++) {
            surroundingPoi.add(pointOfInterests.get(i));
        }
        ArrayList<Pair<Place, Integer>> poiWithDistances = new ArrayList<>();
        if(GAFrameworkUserTracker.sharedTracker() != null
                && GAFrameworkUserTracker.sharedTracker().getCurrentUserLocation() != null) {
            Pair<Integer, Integer> currentPosition = GAFrameworkUserTracker.sharedTracker().getCurrentUserLocation().indexPath;

            if (currentPosition != null) {
                GABeaconMapHelper mapHelper = GAFrameworkUserTracker.sharedTracker().getMapHelper();
                for (int i = 0; i < surroundingPoi.size(); i++) {
                    Place currentClassRoom = surroundingPoi.get(i);
                    if (currentClassRoom.getPosition().getFloor() == mapHelper.getMapFloor()) {
                        surroundingPoi.remove(currentClassRoom);
                        poiWithDistances.add(
                                new Pair<>(
                                        currentClassRoom,
                                        mapHelper.pathFrom(currentPosition,
                                                new Pair<>(
                                                        currentClassRoom.getPosition().getPositionX(),
                                                        currentClassRoom.getPosition().getPositionY())
                                        ).second));
                    }
                }
                Collections.sort(poiWithDistances, new Comparator<Pair<Place, Integer>>() {
                    public int compare(Pair<Place, Integer> o1, Pair<Place, Integer> o2) {
                        if (o1.second < o2.second) {
                            return -1;
                        } else {
                            if (o1.second.equals(o2.second)) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    }
                });
                for (int i = poiWithDistances.size() - 1; i >= 0; i--) {
                    surroundingPoi.add(0, poiWithDistances.get(i).first);
                    Log.d(TAG, "surroundPOI: " + poiWithDistances.get(i).second);
                }
            }
        }
        return surroundingPoi;
    }

    public static ArrayList<PointOfInterest> getPointOfInterests() {
        return pointOfInterests;
    }

    public static void setPointOfInterests(ArrayList<PointOfInterest> pointOfInterests) {
        PointOfInterest.pointOfInterests = pointOfInterests;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public static void onPoiRequestDone(String result) {

    }
}
