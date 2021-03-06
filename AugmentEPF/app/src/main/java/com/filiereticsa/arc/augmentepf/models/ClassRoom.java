package com.filiereticsa.arc.augmentepf.models;

import com.filiereticsa.arc.augmentepf.AppUtils;
import com.filiereticsa.arc.augmentepf.fragments.SearchFragment;
import com.filiereticsa.arc.augmentepf.managers.FileManager;
import com.filiereticsa.arc.augmentepf.managers.HTTP;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ARC© Team for AugmentEPF project on 07/05/2017.
 */

public class ClassRoom extends Place {

    public static final String NAME = "name";
    public static final String FLOOR = "floor";
    public static final String POS_X = "x";
    public static final String POS_Y = "y";
    public static final String AVAILABLE = "available";
    public static final String CLASSROOMS_JSON = "classrooms.json";
    public static final String CLASS_ROOMS = "classRooms";
    public static final String CONTENT_TYPE = "contentType";
    private static final String TAG = "Ici";
    public static final String FREE_ROOMS = "freeRooms";
    private static ArrayList<ClassRoom> classRooms;
    private static ArrayList<Place> availableClassroomList = new ArrayList<>();

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
        ClassRoom roomi1 = new ClassRoom("I1", new Position(45, 10, 2), true);
        classRooms.add(roomi1);
        ClassRoom roomi2 = new ClassRoom("I2", new Position(43, 10, 2), true);
        classRooms.add(roomi2);
        ClassRoom roomi3 = new ClassRoom("I3", new Position(32, 10, 2), true);
        classRooms.add(roomi3);
        ClassRoom roomi4 = new ClassRoom("I4", new Position(12, 10, 2), true);
        classRooms.add(roomi4);
        ClassRoom roomi5 = new ClassRoom("I5", new Position(8, 10, 2), true);
        classRooms.add(roomi5);
        ClassRoom roomi6 = new ClassRoom("I6", new Position(5, 10, 2), true);
        classRooms.add(roomi6);
    }

    private boolean isFree;

    public ClassRoom(String name, Position position, boolean isFree) {
        super(name, position);
        this.isFree = isFree;
    }

    public ClassRoom(JSONObject jsonObject) throws JSONException {
        super(jsonObject.getString(NAME), new Position(jsonObject.getInt(POS_X), jsonObject.getInt(POS_Y), jsonObject.getInt(FLOOR)));
        this.isFree = false;
    }

    public static ArrayList<Place> getAvailableClassroomList() {
        ArrayList<Place> classesAsPlaces = new ArrayList<>();
        for (int i = 0; i < classRooms.size(); i++) {
            classesAsPlaces.add(classRooms.get(i));
        }
        availableClassroomList = AppUtils.sortByClosest(classesAsPlaces);
        return availableClassroomList;
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
        HTTPRequestManager.doPostRequest(HTTP.GET_ROOMS_PHP, "",
                SearchFragment.httpRequestInterface, HTTPRequestManager.AVAILABLE_CLASSROOMS);
    }

    public static void askForClassRooms() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CONTENT_TYPE, "room");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HTTPRequestManager.doPostRequest(HTTP.GET_ELEMENT_PHP, jsonObject.toString(),
                SearchFragment.httpRequestInterface, HTTPRequestManager.CLASSROOMS);
    }

    public static void onAvailableRequestDone(String result) {
        if (result.equals(HTTP.ERROR)) {
            // if there is a connection timeout
        } else {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String state = jsonObject.getString(HTTP.STATE);
                if (state.equals(HTTP.TRUE)){
                    JSONArray availableClassJsonArray = jsonObject.getJSONArray(FREE_ROOMS);
                    if (availableClassroomList == null) {
                        availableClassroomList = new ArrayList<>();
                    }
                    availableClassroomList.clear();
                    for (int i = 0; i < availableClassJsonArray.length(); i++) {
                        ClassRoom classRoomAvailable = ClassRoom.getClassRoomCalled((String) availableClassJsonArray.get(i));
                        if (classRoomAvailable != null) {
                            availableClassroomList.add(classRoomAvailable);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void onClassRoomsRequestDone(String result) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            JSONArray classRoomsJsonArray = jsonObject.getJSONArray(HTTP.RESULT);
            if (classRooms == null) {
                classRooms = new ArrayList<>();
            }
            classRooms.clear();
            for (int i = 0; i < classRoomsJsonArray.length(); i++) {
                JSONObject currentClassroom = classRoomsJsonArray.getJSONObject(i);
                classRooms.add(new ClassRoom(currentClassroom));
            }
            ClassRoom.saveClassRoomsToFile();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ClassRoom getClassRoomCalled(String classRoomName) {
        for (int i = 0; i < classRooms.size(); i++) {
            ClassRoom currentClassRoom = classRooms.get(i);
            if (currentClassRoom.getName().equals(classRoomName)) {
                return currentClassRoom;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "ClassRoom{" +
                "isFree=" + isFree +
                " name=" + getName() +
                '}';
    }
}
