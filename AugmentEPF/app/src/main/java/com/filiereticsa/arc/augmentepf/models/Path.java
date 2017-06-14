package com.filiereticsa.arc.augmentepf.models;

import android.util.Log;

import com.filiereticsa.arc.augmentepf.AppUtils;
import com.filiereticsa.arc.augmentepf.activities.ConnectionActivity;
import com.filiereticsa.arc.augmentepf.activities.HomePageActivity;
import com.filiereticsa.arc.augmentepf.activities.PathConsultationActivity;
import com.filiereticsa.arc.augmentepf.localization.UserIndoorLocationCandidate;
import com.filiereticsa.arc.augmentepf.managers.FileManager;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.filiereticsa.arc.augmentepf.models.ClassRoom.TRUE;

/**
 * Created by ARC© Team for AugmentEPF project on 07/05/2017.
 */

public class Path {

    public static final String THE_DATE = "theDate";
    public static final String DESTINATION_NAME = "destinationName";
    public static final String DESTINATION_TYPE = "destinationType";
    public static final String X_BEGIN = "xBegin";
    public static final String Y_BEGIN = "yBegin";
    public static final String FLOOR_BEGIN = "floorBegin";
    public static final String RECORD_LIST = "recordList";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String MUST_TAKE_ELEVATOR = "mustTakeElevator";
    public static final String CLOSEST_DEPARTURE_PLACE = "closestDeparturePlace";
    public static final String PATHS_JSON = "paths.json";
    public static final String ID_USER = "idUser";
    public static final String TOKEN = "token";
    public static final String GET_RECORD_PHP = "getRecord.php";
    public static final String SUCCESS = "success";
    public static final String ERROR = "Error";
    public static final String RECORD_TRIP_PHP = "recordTrip.php";
    private static final String TAG = "Ici (Path)";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
    private Position departure;
    private Place closestDeparturePlace;
    private Place arrival;
    private ArrayList<Position> path;
    private boolean mustTakeElevator;
    private Date departureDate;
    private Date arrivalDate;
    private static ArrayList<Path> paths;
    public static ArrayList<Path> testPath;
    private JSONObject pathAsJson;


    static {
        testPath = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            Position pos = new Position(0, 0, 0);
            Position departureTest = new Position(0, 0, 0);
            Place closestDeparturePlaceTest = new Place("Closes" + i, pos);
            Place arrivalTest = new Place("ArrName" + i, pos);
            ArrayList<Position> pathTest = new ArrayList<>();
            Date DepDateTest = new Date(System.currentTimeMillis());
            Date ArrDateTest = new Date(System.currentTimeMillis());

            Path path = new Path(
                    departureTest,
                    closestDeparturePlaceTest,
                    arrivalTest,
                    false,
                    DepDateTest,
                    ArrDateTest
            );

            testPath.add(path);
        }
    }

    public Path(Position departure, Place closestDeparturePlace, Place arrival,
                boolean mustTakeElevator, Date departureDate,
                Date arrivalDate) {
        this.departure = departure;
        this.closestDeparturePlace = closestDeparturePlace;
        this.arrival = arrival;
        this.mustTakeElevator = mustTakeElevator;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
    }

    public Path(Position departure, Place closestDeparturePlace, Place arrival,
                boolean mustTakeElevator, Date departureDate) {
        this.departure = departure;
        this.closestDeparturePlace = closestDeparturePlace;
        this.arrival = arrival;
        this.mustTakeElevator = mustTakeElevator;
        this.departureDate = departureDate;
    }

    public Path(JSONObject jsonObject) {
        this.pathAsJson = jsonObject;
        try {
            int xBegin = jsonObject.getInt(X_BEGIN);
            int yBegin = jsonObject.getInt(Y_BEGIN);
            int floor = jsonObject.getInt(FLOOR_BEGIN);
            this.departure = new Position(xBegin, yBegin, floor);

//            String closestDeparturePlaceName = jsonObject.getString(CLOSEST_DEPARTURE_PLACE);
//            this.closestDeparturePlace = Place.getPlaceFromName(closestDeparturePlaceName);

            String arrivalPlaceName = jsonObject.getString(DESTINATION_NAME);
            this.arrival = Place.getPlaceFromName(arrivalPlaceName);

            String departureDate = jsonObject.getString(THE_DATE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(simpleDateFormat.parse(departureDate));
            this.departureDate = calendar.getTime();

            this.mustTakeElevator = jsonObject.getBoolean(MUST_TAKE_ELEVATOR);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    public Position getDeparture() {
        return departure;
    }

    public void setDeparture(Position departure) {
        this.departure = departure;
    }

    public Place getArrival() {
        return arrival;
    }

    public void setArrival(Place arrival) {
        this.arrival = arrival;
    }

    public ArrayList<Position> getPath() {
        return path;
    }

    public void setPath(ArrayList<Position> path) {
        this.path = path;
    }

    public boolean isMustTakeElevator() {
        return mustTakeElevator;
    }

    public void setMustTakeElevator(boolean mustTakeElevator) {
        this.mustTakeElevator = mustTakeElevator;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public static ArrayList<Path> getPaths() {
        if (paths == null) {
            paths = new ArrayList<>();
        }
        return paths;
    }

    private JSONObject getJsonFromPath() {
        JSONObject pathAsJson = new JSONObject();
        Date departureDate = this.getDepartureDate();
        String departureDateString = simpleDateFormat.format(departureDate);
        try {
            pathAsJson.put(THE_DATE, departureDateString);
            pathAsJson.put(CLOSEST_DEPARTURE_PLACE, this.closestDeparturePlace.getName());
            pathAsJson.put(DESTINATION_NAME, this.getArrival().getName());
            pathAsJson.put(MUST_TAKE_ELEVATOR, this.isMustTakeElevator());
            pathAsJson.put(X_BEGIN, this.departure.getPositionX());
            pathAsJson.put(Y_BEGIN, this.departure.getPositionX());
            pathAsJson.put(FLOOR_BEGIN, this.departure.getFloor());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.pathAsJson = pathAsJson;
        return pathAsJson;
    }

    public static JSONObject getJsonFromPaths() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < paths.size(); i++) {
                Path path = paths.get(i);
                JSONObject currentPath = path.getJsonFromPath();
                jsonArray.put(currentPath);
            }
            jsonObject.put(RECORD_LIST, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static void loadPathsFromJson(JSONObject jsonObject) {
        Log.d(TAG, "loadPathsFromJson: "+jsonObject.toString());
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(RECORD_LIST);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject currentPathJson = jsonArray.getJSONObject(i);
                Path path = new Path(currentPathJson);
                getPaths().add(path);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void loadPathsFromFile() {
        FileManager fileManager = new FileManager(null, PATHS_JSON);
        try {
            loadPathsFromJson(new JSONObject(fileManager.readFile()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void savePaths() {
        FileManager fileManager = new FileManager(null, PATHS_JSON);
        Log.d(TAG, "savePaths: "+getJsonFromPaths().toString());
        fileManager.saveFile(getJsonFromPaths().toString());
    }

    private void sendPathToServer() {
        JSONObject jsonObject = this.getJsonFromPath();
        try {
            jsonObject.put(DESTINATION_TYPE, this.getArrival().getType());
            jsonObject.put(ID_USER, ConnectionActivity.idUser);
            jsonObject.put(TOKEN, ConnectionActivity.token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "sendPathToServer: "+jsonObject.toString());
        HTTPRequestManager.doPostRequest(RECORD_TRIP_PHP, jsonObject.toString(), HomePageActivity.httpRequestInterface, HTTPRequestManager.PATH);
    }

    public static void askForPaths() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(ID_USER, ConnectionActivity.idUser);
            jsonObject.put(TOKEN, ConnectionActivity.token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HTTPRequestManager.doPostRequest(GET_RECORD_PHP, jsonObject.toString(), PathConsultationActivity.httpRequestInterface, HTTPRequestManager.PATH_HISTORY);
    }

    public static void onPathRequestDone(String result) {
        Log.d(TAG, "onPathRequestDone: " + result);
        if (result.equals(ERROR)) {
            loadPathsFromFile();
        } else {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                String state = jsonObject.getString(SUCCESS);
                if (state.equals(TRUE)) {
                    loadPathsFromJson(new JSONObject(result));
                } else {
                    loadPathsFromFile();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createNewPath(
            UserIndoorLocationCandidate departure,
            Place arrival,
            SpecificAttribute specificAttribute) {
        if (departure != null) {
            Log.d(TAG, "createNewPath() called with: departure = [" + departure.indexPath + "], arrival = [" + arrival.toString() + "], specificAttribute = [" + specificAttribute.toString() + "]");
            ArrayList<Place> sortedPlaces = AppUtils.sortByClosest(Place.getAllPlaces());
            Place closestPlace;
            if (sortedPlaces != null && sortedPlaces.size() > 0) {
                closestPlace = sortedPlaces.get(0);
                Path path = new Path(
                        new Position(
                                departure.indexPath.first,
                                departure.indexPath.second,
                                closestPlace.getPosition().getFloor()),
                        closestPlace,
                        arrival,
                        AppUtils.mustTakeElevator(specificAttribute),
                        Calendar.getInstance().getTime()
                );
                getPaths().add(path);
                savePaths();
                path.sendPathToServer();
            }
        }
    }


    public Place getClosestDeparturePlace() {
        return closestDeparturePlace;
    }
}
