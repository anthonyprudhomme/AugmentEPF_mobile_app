package com.filiereticsa.arc.augmentepf.models;

import android.util.Log;
import android.util.Pair;

import com.filiereticsa.arc.augmentepf.fragments.SearchFragment;
import com.filiereticsa.arc.augmentepf.localization.GABeaconMapHelper;
import com.filiereticsa.arc.augmentepf.localization.GAFrameworkUserTracker;
import com.filiereticsa.arc.augmentepf.managers.FileManager;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ARC© Team for AugmentEPF project on 07/05/2017.
 */

public class ClassRoom extends Place {

    private static final String TAG = "Ici";
    public static final String NAME = "name";
    public static final String FLOOR = "floor";
    public static final String POS_X = "posX";
    public static final String POS_Y = "posY";
    public static final String CURRENT_TIME = "currentTime";
    public static final String AVAILABLE = "available";
    public static final String OK = "ok";
    public static final String PROBLEM = "problem";
    public static final String MESSAGE = "message";
    public static final String CLASSROOMS_JSON = "classrooms.json";
    public static final String CLASS_ROOMS = "classRooms";
    public static final String GET_AVAILABLE_CLASS_ROOMS_PHP = "getAvailableClassRooms.php";
    public static final String GET_CLASS_ROOMS_PHP = "getClassRooms.php";

    private static ArrayList<ClassRoom> classRooms;

    private boolean isFree;
    private static ArrayList<Place> availableClassroomList = new ArrayList<>();

    public ClassRoom(String name, Position position, boolean isFree) {
        super(name, position);
        this.isFree = isFree;
    }

    public ClassRoom(JSONObject jsonObject) throws JSONException {
        super(jsonObject.getString(NAME), new Position(jsonObject.getInt(POS_X), jsonObject.getInt(POS_Y), jsonObject.getInt(FLOOR)));
        this.isFree = false;
    }

    public static ArrayList<Place> getAvailableClassroomList() {
        if (availableClassroomList == null) {
            availableClassroomList = new ArrayList<>();
        } else {
            availableClassroomList.clear();
        }
        for (int i = 0; i < classRooms.size(); i++) {
            ClassRoom currentClassRoom = classRooms.get(i);
            if (currentClassRoom.isFree()) {
                availableClassroomList.add(currentClassRoom);
            }
        }
        ArrayList<Pair<Place, Integer>> classWithDistances = new ArrayList<>();
        if (GAFrameworkUserTracker.sharedTracker() != null
                && GAFrameworkUserTracker.sharedTracker().getCurrentUserLocation() != null) {
            Pair<Integer, Integer> currentPosition = GAFrameworkUserTracker.sharedTracker().getCurrentUserLocation().indexPath;

            if (currentPosition != null) {
                GABeaconMapHelper mapHelper = GAFrameworkUserTracker.sharedTracker().getMapHelper();
                for (int i = 0; i < availableClassroomList.size(); i++) {
                    Place currentClassRoom = availableClassroomList.get(i);
                    if (currentClassRoom.getPosition().getFloor() == mapHelper.getMapFloor()) {
                        availableClassroomList.remove(currentClassRoom);
                        classWithDistances.add(
                                new Pair<>(
                                        currentClassRoom,
                                        mapHelper.pathFrom(currentPosition,
                                                new Pair<>(
                                                        currentClassRoom.getPosition().getPositionX(),
                                                        currentClassRoom.getPosition().getPositionY())
                                        ).second));
                    }
                }
                Collections.sort(classWithDistances, new Comparator<Pair<Place, Integer>>() {
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
                for (int i = classWithDistances.size() - 1; i >= 0; i--) {
                    availableClassroomList.add(0, classWithDistances.get(i).first);
                    Log.d(TAG, "getAvailableClassroomList: " + classWithDistances.get(i).second);
                }
            }
        }
        return availableClassroomList;
    }

    static {
        classRooms = new ArrayList<>();
        // Floor 1
        ClassRoom room1L = new ClassRoom("1L", new Position(38, 10, 1), true);
        classRooms.add(room1L);
        ClassRoom room2L = new ClassRoom("2L", new Position(37, 10, 1), true);
        classRooms.add(room2L);
        ClassRoom room3L = new ClassRoom("3L", new Position(31, 10, 1), true);
        classRooms.add(room3L);
        ClassRoom room4L = new ClassRoom("4L", new Position(18, 10, 1), true);
        classRooms.add(room4L);
        ClassRoom room5L = new ClassRoom("5L", new Position(12, 10, 1), true);
        classRooms.add(room5L);
        ClassRoom room6L = new ClassRoom("6L", new Position(11, 10, 1), true);
        classRooms.add(room6L);
        ClassRoom room7L = new ClassRoom("7L", new Position(19, 9, 1), true);
        classRooms.add(room7L);

        // Floor 2
        ClassRoom roomi1 = new ClassRoom("i1", new Position(45, 10, 2), true);
        classRooms.add(roomi1);
        ClassRoom roomi2 = new ClassRoom("i2", new Position(43, 10, 2), true);
        classRooms.add(roomi2);
        ClassRoom roomi3 = new ClassRoom("i3", new Position(32, 10, 2), true);
        classRooms.add(roomi3);
        ClassRoom roomi4 = new ClassRoom("i4", new Position(12, 10, 2), true);
        classRooms.add(roomi4);
        ClassRoom roomi5 = new ClassRoom("i5", new Position(8, 10, 2), true);
        classRooms.add(roomi5);
        ClassRoom roomi6 = new ClassRoom("i6", new Position(5, 10, 2), true);
        classRooms.add(roomi6);
    }

    public static String[] getClassroomsAsStrings() {
        String[] classroomsAsStrings = new String[classRooms.size()];
        for (int i = 0; i < classRooms.size(); i++) {
            classroomsAsStrings[i] = classRooms.get(i).getName();
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

    public static JSONObject getJsonFromClassRooms() {
        JSONObject classRoomsAsJsonObject = new JSONObject();
        JSONArray classRoomsAsJsonArray = new JSONArray();
        for (int i = 0; i < classRooms.size(); i++) {
            ClassRoom currentClass = classRooms.get(i);
            JSONObject currentClassJson = new JSONObject();
            try {
                currentClassJson.put(NAME, currentClass.getName());
                currentClassJson.put(FLOOR, currentClass.getPosition().getFloor());
                currentClassJson.put(POS_X, currentClass.getPosition().getPositionX());
                currentClassJson.put(POS_Y, currentClass.getPosition().getPositionY());

                classRoomsAsJsonArray.put(currentClassJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            classRoomsAsJsonObject.put(CLASS_ROOMS, classRoomsAsJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return classRoomsAsJsonObject;
    }

    public static void saveClassRoomsToFile() {
        FileManager fileManager = new FileManager(null, CLASSROOMS_JSON);
        fileManager.saveFile(getJsonFromClassRooms().toString());
    }

    public static void loadClassRoomsFromJson(JSONObject classRoomsAsJson) {
        classRooms = new ArrayList<>();
        JSONArray classRoomsAsJsonArray;
        try {
            classRoomsAsJsonArray = classRoomsAsJson.getJSONArray(CLASS_ROOMS);
            for (int i = 0; i < classRoomsAsJsonArray.length(); i++) {
                JSONObject currentClassRoomJsonObject = classRoomsAsJsonArray.getJSONObject(i);
                String name = currentClassRoomJsonObject.getString(NAME);
                int floor = currentClassRoomJsonObject.getInt(FLOOR);
                int positionX = currentClassRoomJsonObject.getInt(POS_X);
                int positionY = currentClassRoomJsonObject.getInt(POS_Y);
                ClassRoom classRoom = new ClassRoom(name, new Position(positionX, positionY, floor), true);
                classRooms.add(classRoom);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void loadClassRoomsFromFile() {
        FileManager fileManager = new FileManager(null, CLASSROOMS_JSON);
        String data = fileManager.readFile();
        if (data != null && !data.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                ClassRoom.loadClassRoomsFromJson(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void askForAvailableClassRooms() {
        JSONObject jsonObject = new JSONObject();
        try {
            long currentTime = System.currentTimeMillis();
            jsonObject.put(CURRENT_TIME, currentTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // TODO rename this according to Guilhem's name
        HTTPRequestManager.doPostRequest(GET_AVAILABLE_CLASS_ROOMS_PHP, jsonObject.toString(),
                SearchFragment.httpRequestInterface, HTTPRequestManager.AVAILABLE_CLASSROOMS);
    }

    public static void askForClassRooms() {
        JSONObject jsonObject = new JSONObject();
        // TODO rename this according to Guilhem's name
        HTTPRequestManager.doPostRequest(GET_CLASS_ROOMS_PHP, jsonObject.toString(),
                SearchFragment.httpRequestInterface, HTTPRequestManager.CLASSROOMS);
    }

    public static void onAvailableRequestDone(String result) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            String message = jsonObject.getString(MESSAGE);
            switch (message) {
                case OK:
                    JSONArray availableClassJsonArray = jsonObject.getJSONArray(AVAILABLE);
                    if (availableClassroomList == null) {
                        availableClassroomList = new ArrayList<>();
                    }
                    availableClassroomList.clear();
                    for (int i = 0; i < availableClassJsonArray.length(); i++) {
                        availableClassroomList.add(
                                new ClassRoom(availableClassJsonArray.getJSONObject(i)));
                    }
                    break;


                case PROBLEM:

                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void onClassRoomsRequestDone(String result) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            String message = jsonObject.getString(MESSAGE);
            switch (message) {
                case OK:
                    JSONArray classRoomsJsonArray = jsonObject.getJSONArray(CLASS_ROOMS);
                    if (classRooms == null) {
                        classRooms = new ArrayList<>();
                    }
                    classRooms.clear();
                    for (int i = 0; i < classRoomsJsonArray.length(); i++) {
                        classRooms.add(
                                new ClassRoom(classRoomsJsonArray.getJSONObject(i)));
                    }
                    ClassRoom.saveClassRoomsToFile();
                    break;


                case PROBLEM:

                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
