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
        classRooms = new ArrayList<>();
        ClassRoom room3L = new ClassRoom("Real 3L", new Position(31,10,1),true);
        availableClassroomList.add(room3L);
        classRooms.add(room3L);
        ClassRoom roomi3 = new ClassRoom("Real i3", new Position(32,10,2),true);
        availableClassroomList.add(roomi3);
        classRooms.add(roomi3);
        for (int i=0; i < 20; i++){
            ClassRoom classRoom = new ClassRoom(
                    i +"L",
                    new Position(i,i+i,i/2),
                    i%2 ==0
            );
            classRooms.add(classRoom);
            availableClassroomList.add(classRoom);
        }
    }

    public static String[] getClassroomsAsStrings(){
        String[] classroomsAsStrings = new String[classRooms.size()];
        for (int i = 0; i < classRooms.size(); i++) {
            classroomsAsStrings[i]= classRooms.get(i).getName();
        }
        return classroomsAsStrings;
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
