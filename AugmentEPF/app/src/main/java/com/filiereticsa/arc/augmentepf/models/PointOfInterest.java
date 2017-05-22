package com.filiereticsa.arc.augmentepf.models;

import java.util.ArrayList;

/**
 * Created by anthony on 07/05/2017.
 */

public class PointOfInterest extends Place {

    private static ArrayList<PointOfInterest> pointOfInterests;
    private String information;
    private static ArrayList<Place> surroundingPoi = new ArrayList<>();

    public PointOfInterest(String name, Position position, String information) {
        super(name, position);
        this.information = information;
    }


    public static ArrayList<Place> getSurroundingPoi() {
        return surroundingPoi;
    }

    static {
        surroundingPoi = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            PointOfInterest poi = new PointOfInterest(
                    "Point of interest " +i,
                    new Position(i, i + i, i / 2),
                    "Information"
            );
            surroundingPoi.add(poi);
        }
    }

    public static ArrayList<PointOfInterest> getPointOfInterests() {return pointOfInterests;}

    public static void setPointOfInterests(ArrayList<PointOfInterest> pointOfInterests) {
        PointOfInterest.pointOfInterests = pointOfInterests;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }
}
