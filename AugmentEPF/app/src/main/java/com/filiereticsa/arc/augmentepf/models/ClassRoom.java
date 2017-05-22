package com.filiereticsa.arc.augmentepf.models;

import java.util.ArrayList;

/**
 * Created by anthony on 07/05/2017.
 */

public class ClassRoom extends Place {

    private static ArrayList<ClassRoom> classRooms;

    private boolean isFree;
    private static ArrayList<Place> availableClassroomList = new ArrayList<>();

    public ClassRoom(String name, Position position, boolean isFree) {
        super(name, position);
        this.isFree = isFree;
    }


    public static ArrayList<Place> getAvailableClassroomList() {
        return availableClassroomList;
    }

    static{
        availableClassroomList = new ArrayList<>();
        for (int i=0; i < 20; i++){
            ClassRoom classRoom = new ClassRoom(
                    i +"L",
                    new Position(i,i+i,i/2),
                    i%2 ==0
            );
            availableClassroomList.add(classRoom);
        }
    }

    public static ArrayList<ClassRoom> getClassRooms() {
        return classRooms;
    }

    public static void setClassRooms(ArrayList<ClassRoom> classRooms) {
        ClassRoom.classRooms = classRooms;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }
}
