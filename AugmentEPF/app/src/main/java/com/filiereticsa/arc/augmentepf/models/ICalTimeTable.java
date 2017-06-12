package com.filiereticsa.arc.augmentepf.models;

import com.filiereticsa.arc.augmentepf.managers.FileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by ARC© Team for AugmentEPF project on 07/05/2017.
 */

public class ICalTimeTable {

    public static final String DATE = "date";
    public static final String TAB = "tab";
    public static final String TIME_TABLE = "timeTable";
    public static ICalTimeTable iCalInstance;
    private HashMap<String, ArrayList<Class>> classes;
    private Class nextClass = null;
    private JSONObject timeTableAsJson;

    public ICalTimeTable(HashMap<String, ArrayList<Class>> classes) {
        this.classes = classes;
    }

    public ICalTimeTable(JSONObject jsonObject) {
        timeTableAsJson = jsonObject;
        classes = new HashMap<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(TAB);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonForDate = jsonArray.getJSONObject(i);
                String day = jsonForDate.getString(DATE);
                int index = 0;
                ArrayList<Class> currentDay = new ArrayList<>();
                while (jsonForDate.has((String.valueOf(index)))) {
                    JSONObject jsonClass = jsonForDate.getJSONObject(String.valueOf(index));
                    currentDay.add(new Class(jsonClass));
                    index++;
                }
                classes.put(day, currentDay);
            }
            iCalInstance = this;
            saveTimeTableToFile(timeTableAsJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void saveTimeTableToFile(JSONObject timeTableAsJson) {
        FileManager fileManager = new FileManager(null, TIME_TABLE);
        fileManager.saveFile(timeTableAsJson.toString());
    }

    public static void loadTimeTableFromFile() {
        FileManager fileManager = new FileManager(null, TIME_TABLE);
        String data = fileManager.readFile();
        try {
            JSONObject jsonObject = new JSONObject(data);
            new ICalTimeTable(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public HashMap<String, ArrayList<Class>> getClasses() {
        return classes;
    }

    public void setClasses(HashMap<String, ArrayList<Class>> classes) {
        this.classes = classes;
    }

//    public Class getNextClass() {
//        if (nextClass != null) {
//            return nextClass;
//        } else {
//            nextClass = null;
//            for (int i = 0; i < classes.size(); i++) {
//                Class currentClass = classes.get(i);
//                if ((nextClass == null
//                        && currentClass.getStartDate().getTime() > System.currentTimeMillis())
//                        || (nextClass != null
//                        && currentClass.getStartDate().getTime() < nextClass.getStartDate().getTime())) {
//
//                    nextClass = currentClass;
//                }
//            }
//        }
//        return nextClass;
//    }

    public void setNextClass(Class nextClass) {
        this.nextClass = nextClass;
    }

    public Class getNextCourse() {
        Calendar calendar = Calendar.getInstance();
        String dayString = getKeyFromCalendar(calendar);
        if (classes.containsKey(dayString)) {
            ArrayList<Class> classesForCurrentDay = classes.get(dayString);
            for (int i = 0; i < classesForCurrentDay.size(); i++) {
                Class currentClass = classesForCurrentDay.get(i);
                if (calendar.getTime().before(currentClass.getStartDate())) {
                    return currentClass;
                }
            }
        }
        return null;
    }

    // method that returns a string to get the current day of classes from the HashMap classes
    public String getKeyFromCalendar(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.valueOf(year) + String.valueOf(day) + String.valueOf(month);
    }
}
