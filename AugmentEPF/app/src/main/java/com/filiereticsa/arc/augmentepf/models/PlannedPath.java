package com.filiereticsa.arc.augmentepf.models;

import com.filiereticsa.arc.augmentepf.activities.ConnectionActivity;
import com.filiereticsa.arc.augmentepf.activities.PathConsultationActivity;
import com.filiereticsa.arc.augmentepf.activities.PathPlanningActivity;
import com.filiereticsa.arc.augmentepf.managers.FileManager;
import com.filiereticsa.arc.augmentepf.managers.HTTP;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
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
    public static final String TRIP = "trip";
    public static final String PLANNED_PATH_JSON = "planned_path.json";
    public static final String NULL = "null";
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

    public PlannedPath(JSONObject jsonObject) {
        super(jsonObject);
        try {
            String alarmAsString = jsonObject.getString(WARNING_TYPE);
            this.alarmType = AlarmType.valueOf(alarmAsString.toUpperCase());
            String warnDateAsString = jsonObject.getString(WARN_DATE);
            this.whenToAlarmUser = simpleDateFormat.parse(warnDateAsString);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    public AlarmType getAlarmType() {
        return alarmType;
    }

    public Date getWhenToAlarmUser() {
        return whenToAlarmUser;
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
            jsonObject.put(DATE,plannedPath.getDepartureDate());
            jsonObject.put(TYPE_DESTINATION, plannedPath.getArrival().getType());
            jsonObject.put(NAME_DESTINATION, plannedPath.getArrival().getName());
            jsonObject.put(WARNING_TYPE, plannedPath.alarmType.toString());
            String formattedDate = Path.simpleDateFormat.format(plannedPath.getWhenToAlarmUser());
            jsonObject.put(WARN_DATE, formattedDate);
            jsonObject.put(MUST_TAKE_ELEVATOR,plannedPath.isMustTakeElevator());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HTTPRequestManager.doPostRequest(
                HTTP.SEND_TRIP_PHP,
                jsonObject.toString(),
                PathPlanningActivity.httpRequestInterface,
                HTTPRequestManager.PLANNED_PATH);
    }

    public static void askForPlannedPaths() {
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
                PathConsultationActivity.httpRequestInterface,
                HTTPRequestManager.PLANNED_PATH_LIST);
    }

    public static void onPlannedPathListRequestDone(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            loadPlannedPathsFromJson(jsonObject);
            savePlannedPathsToFile();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<PlannedPath> getPlannedPaths() {
        if (plannedPaths == null) {
            plannedPaths = new ArrayList<>();
        }
        return plannedPaths;
    }

    public static void loadPlannedPathsFromFile() {
        FileManager fileManager = new FileManager(null, PLANNED_PATH_JSON);
        String data = fileManager.readFile();
        try {
            loadPlannedPathsFromJson(new JSONObject(data));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void loadPlannedPathsFromJson(JSONObject jsonObject) {
        String trip;
        try {
            trip = jsonObject.getString(TRIP);
            if (!trip.equalsIgnoreCase(NULL)) {
                JSONArray jsonArray = jsonObject.getJSONArray(TRIP);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject currentPlannedPathJson = jsonArray.getJSONObject(i);
                    PlannedPath plannedPath = new PlannedPath(currentPlannedPathJson);
                    getPlannedPaths().add(plannedPath);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void savePlannedPathsToFile() {
        FileManager fileManager = new FileManager(null, PLANNED_PATH_JSON);
        fileManager.saveFile(getJsonFromPlannedPaths());
    }

    private static String getJsonFromPlannedPaths() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < plannedPaths.size(); i++) {
                PlannedPath currentPlannedPath = plannedPaths.get(i);
                JSONObject currentPlannedPathAsJson = currentPlannedPath.getJsonFromPath();
                currentPlannedPathAsJson.put(WARNING_TYPE, currentPlannedPath.alarmType.toString());
                String formattedDate = Path.simpleDateFormat.format(currentPlannedPath.getWhenToAlarmUser());
                currentPlannedPathAsJson.put(WARN_DATE, formattedDate);
                jsonArray.put(currentPlannedPathAsJson);
            }
            jsonObject.put(TRIP, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
