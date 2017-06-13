package com.filiereticsa.arc.augmentepf.models;

import java.util.ArrayList;

/**
 * Created by ARC© Team for AugmentEPF project on 22/05/2017.
 */

public class Place {
    private String name;
    private Position position;
    private static ArrayList<Place> allPlaces;
    private String type;

    public Place(String name, Position position) {
        this.name = name;
        this.position = position;
    }

    public static Place getPlaceFromName(String placeName) {
        ClassRoom classRoom = ClassRoom.getClassRoomCalled(placeName);
        if (classRoom != null){
            return classRoom;
        }else{
            PointOfInterest pointOfInterest = PointOfInterest.getPoiCalled(placeName);
            if (pointOfInterest != null){
                return pointOfInterest;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public static ArrayList<Place> getAllPlaces() {
        if (allPlaces == null) {
            allPlaces = new ArrayList<>();
            allPlaces.addAll(ClassRoom.getClassRooms());
            allPlaces.addAll(PointOfInterest.getPointOfInterests());
        }
        return allPlaces;
    }

    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", position=" + position.toString() +
                '}';
    }

    public String getType() {
        if (this.type!= null){
            return this.type;
        }
        ClassRoom classroom = ClassRoom.getClassRoomCalled(getName());
        if (classroom!= null){
            this.type = "Room";
            return this.type;
        }else{
            PointOfInterest pointOfInterest = PointOfInterest.getPoiCalled(getName());
            if (pointOfInterest != null){
                this.type = "PointOfInterest";
                return this.type;
            }
        }
        return "";
    }
}
