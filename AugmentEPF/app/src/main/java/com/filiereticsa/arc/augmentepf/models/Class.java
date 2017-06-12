package com.filiereticsa.arc.augmentepf.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ARC© Team for AugmentEPF project on 07/05/2017.
 */

public class Class {

    public static final String CATEGORY = "category";
    public static final String ROOM = "room";
    public static final String DATE_END = "dateEnd";
    public static final String DATE_START = "dateStart";
    private static final String TAG = "Ici";
    private String name;
    private Date startDate;
    private Date endDate;
    private ClassRoom classRoom;

    public Class(String name, Date startDate, Date endDate, ClassRoom classRoom) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.classRoom = classRoom;
    }

    public Class(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString(CATEGORY);
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.FRANCE);

            // get startDate from string
            String startDateString = jsonObject.getString(DATE_START);
            try {
                calendar.setTime(sdf.parse(startDateString));
                Log.d(TAG, "Class: " + calendar.getTime().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.startDate = calendar.getTime();

            // get endDate from string
            String endDateString = jsonObject.getString(DATE_END);
            try {
                calendar.setTime(sdf.parse(endDateString));
                Log.d(TAG, "Class: " + calendar.getTime().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.endDate = calendar.getTime();

            String classRoomName = jsonObject.getString(ROOM);
            this.classRoom = ClassRoom.getClassRoomCalled(classRoomName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ClassRoom getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(ClassRoom classRoom) {
        this.classRoom = classRoom;
    }
}
