package com.filiereticsa.arc.augmentepf.models;

import com.filiereticsa.arc.augmentepf.activities.ConnectionActivity;
import com.filiereticsa.arc.augmentepf.activities.PathPlanningActivity;
import com.filiereticsa.arc.augmentepf.managers.HTTP;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ARC© Team for AugmentEPF project on 07/05/2017.
 */

public class PlannedPath extends Path {

    public static final String DESTINATION_TYPE = "destinationType";
    public static final String TYPE_DESTINATION = "typeDestination";
    public static final String NAME_DESTINATION = "nameDestination";
    public static final String WARNING_TYPE = "warningType";
    public static final String WARN_DATE = "warnDate";
    private AlarmType alarmType;
    private Date whenToAlarmUser;
    private static ArrayList<PlannedPath> plannedPaths;

    public PlannedPath(Position departure, Place closestDeparturePlace, Place arrival,
                       boolean mustTakeElevator, Date departureDate, Date arrivalDate,
                       AlarmType alarmType, Date whenToAlarmUser) {
        super(departure, closestDeparturePlace, arrival, mustTakeElevator, departureDate, arrivalDate);
        this.alarmType = alarmType;
        this.whenToAlarmUser = whenToAlarmUser;
    }

    public AlarmType getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(AlarmType alarmType) {
        this.alarmType = alarmType;
    }

    public Date getWhenToAlarmUser() {
        return whenToAlarmUser;
    }

    public void setWhenToAlarmUser(Date whenToAlarmUser) {
        this.whenToAlarmUser = whenToAlarmUser;
    }

    public static void addPlannedPath(PlannedPath plannedPath) {
        if (plannedPaths == null) {
            plannedPaths = new ArrayList<>();
        }
        plannedPaths.add(plannedPath);
    }

    public static void sendPlannedPathToServer(PlannedPath plannedPath) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(HTTP.ID_USER, ConnectionActivity.idUser);
            jsonObject.put(HTTP.TOKEN, ConnectionActivity.token);
            jsonObject.put(TYPE_DESTINATION, plannedPath.getArrival().getType());
            jsonObject.put(NAME_DESTINATION, plannedPath.getArrival().getName());
            jsonObject.put(WARNING_TYPE, plannedPath.alarmType.toString());
            String formattedDate = Path.simpleDateFormat.format(plannedPath.getWhenToAlarmUser());
            jsonObject.put(WARN_DATE, formattedDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HTTPRequestManager.doPostRequest(
                HTTP.SEND_TRIP_PHP,
                jsonObject.toString(),
                PathPlanningActivity.httpRequestInterface,
                HTTPRequestManager.PLANNED_PATH);
    }

    public static void getPlannedPathFromServer() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(HTTP.ID_USER, ConnectionActivity.idUser);
            jsonObject.put(HTTP.TOKEN, ConnectionActivity.token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HTTPRequestManager.doPostRequest(
                HTTP.GET_TRIP_PHP,
                jsonObject.toString(),
                PathPlanningActivity.httpRequestInterface,
                HTTPRequestManager.PLANNED_PATH_LIST);
    }
}
