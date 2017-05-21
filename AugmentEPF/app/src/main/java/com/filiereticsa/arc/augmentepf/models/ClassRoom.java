package com.filiereticsa.arc.augmentepf.models;

import java.util.ArrayList;

/**
 * Created by anthony on 07/05/2017.
 */

public class ClassRoom {

    private static ArrayList<ClassRoom> classRooms;
    private static ArrayList<ClassRoom> availableClassroomList;

    static {
        availableClassroomList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ClassRoom classRoom = new ClassRoom(
                    i + "L",
                    new Position(i, i * i, i),
                    i % 2 == 0
            );
            availableClassroomList.add(classRoom);
        }
    }

    private String nom;
    private Position position;
    private boolean isFree;

    public ClassRoom(String nom, Position position, boolean isFree) {
        this.nom = nom;
        this.position = position;
        this.isFree = isFree;
    }

    public static ArrayList<ClassRoom> getClassRooms() {
        return classRooms;
    }

    public static void setClassRooms(ArrayList<ClassRoom> classRooms) {
        ClassRoom.classRooms = classRooms;
    }

    public static ArrayList<ClassRoom> getAvailableClassroomList() {
        return availableClassroomList;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }
}
