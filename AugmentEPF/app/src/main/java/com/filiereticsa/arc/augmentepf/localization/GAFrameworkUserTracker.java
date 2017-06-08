package com.filiereticsa.arc.augmentepf.localization;


import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;

import com.filiereticsa.arc.augmentepf.models.SpecificAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anthonyprudhomme on 11/10/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */

public class GAFrameworkUserTracker implements BeaconDetectorInterface, SensorEventListener, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final double distanceThresholdBetweenUserAndClosestBeacon = 5;
    private static final String TAG = "Ici";
    private static GAFrameworkUserTracker sharedTracker = null;
    private final long PERIOD_BETWEEN_TWO_ACCELEROMETER_VALUE = 400;
    private final long PERIOD_BETWEEN_TWO_GYROSCOPE_VALUE = 300;
    private final long TIME_WHILE_GYRO_IS_ON = 3000;
    private final int MINIMUM_PERIOD_BETWEEN_TWO_GYRO_VALUES = 20;
    // MARK: - Some settings (constant for now) for candidates update
    protected double stepsAccThreshold = 0.09 * 5;
    private String direction = "";
    private long alarmForAccelerometer = 0;
    private long alarmForGyroscope = 0;

    private Pair<Integer, Integer> target;
    private Integer floorTarget = null;
    private SpecificAttribute currentSpecificAttribute = SpecificAttribute.NONE;

    private double stepsPerMapItem = 1.5;
    private double beaconForceDistanceThreshold = 2;
    private int beaconForceDistanceMapItemMax = 2;  // In map items
    private double kGyroThreshold = 40;
    private long gyroAlarm = 0;
    private int numberOfHeadingsAccumulated = 10;
    private double offsetAccepted = 0.1;
    private boolean gyroIsOn = false;
    // Sensors
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor linearAccelerometer;
    private Sensor magnetometer;
    private Sensor gyroscope;
    private boolean indoorTrackingEnabled = false;


    // MARK: - Variables
    private boolean outdoorTrackingEnabled = false;
    // Beacon map
    private GABeaconMap currentMap = null;
    private int currentMapId;
    private GABeaconMapHelper mapHelper = new GABeaconMapHelper();
    private int userLocationHistorySize = 3;
    private ArrayList<UserIndoorLocationCandidate> userLocationHistory = new ArrayList<>();
    private UserIndoorLocationCandidate currentUserLocation;
    private Map<Pair<Integer, Integer>, UserIndoorLocationCandidate> userLocationCandidatesDict = new HashMap<>();
    private ArrayList<Pair<Integer, Integer>> sortedCandidateKeys = new ArrayList<>();
    // Beacon localization
    private GABeaconLocalizer beaconLocalizer = initLocalizer();
    private ArrayList<GABeacon> closestBeacons = new ArrayList<>();
    // Accelerometer and gyroscope
    private int directionHistorySize = 3;
    private ArrayList<Pair<Integer, Integer>> directionHistory = new ArrayList<>();
    private Pair<Integer, Integer> currentDirection;
    private ArrayList<Pair<Integer, Integer>> directionCandidates = new ArrayList<>();
    private double currentHeading = 0;
    private ArrayList<Pair<Double, Integer>> headings = null;
    private boolean headingGyroCanStart = false;
    private double averageHeading = 0;
    private double currentYGyro = 0;
    private double currentZGyro = 0;
    private double headingGyro = 0;
    /* Motion:
     * Steps? distance per step?
     * Distance?
    */
    private double steps = 0;
    // Observers
    private ArrayList<GAFrameworkUserTrackerObserver> observers = new ArrayList<>();
    private float[] linearAccelerationValues;
    private float[] accelerationValues;
    private float[] geomagneticValues;
    private float[] gyroscopeValues;
    private int x = 0, y = 1, z = 2;
    private long lastTimeStamp = 0;

    private SharedPreferences sharedPreferences;

    public GAFrameworkUserTracker(FragmentActivity activity) {
        if (sharedTracker == null) {
            sensorManager = (SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
            sharedTracker = this;
        }
    }

    public static GAFrameworkUserTracker sharedTracker() {
        return sharedTracker;
    }

    public void setCurrentMap(GABeaconMap currentMap) {
        this.currentMap = currentMap;
    }

    public void setCurrentMapId(int newMapId) {
        if (currentMap == null || currentMap.getId() != newMapId) {
            // TODO Uncomment this and put the real path to the map
//            String mapPath = "PATH_TO_MAP";
//            FileManager fileManager = new FileManager(null, mapPath);
//            String data = fileManager.readFile();
//            if (data != null && data.length() != 0) {
//                try {
//                    JSONObject jsonObject = new JSONObject(data);
//                    //currentMap = new GABeaconMap(jsonObject);
            if (GABeaconMap.maps != null && GABeaconMap.maps.containsKey(newMapId)) {
                currentMap = GABeaconMap.maps.get(newMapId);
            }
//            else {
//                currentMap = new GABeaconMap(newMapId);
//            }
            currentUserLocation = null;
            userLocationHistory.clear();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
            this.startIndoorTracking();
            steps = 0;
            for (int i = 0; i < observers.size(); i++) {
                observers.get(i).userMovedToMap(this.currentMap);
            }
            this.mapHelper.setBeaconMap(this.currentMap);
            this.currentMapId = newMapId;
        }
    }

    public void setUserLocationHistorySize(int newHistorySize) {
        if (userLocationHistorySize < userLocationHistory.size()) {
            for (int i = 0; i < userLocationHistory.size() - userLocationHistorySize; i++) {
                userLocationHistory.remove(0);
            }
        }
    }

    public UserIndoorLocationCandidate getCurrentUserLocation() {
        if (userLocationHistory.size() != 0) {
            return userLocationHistory.get(userLocationHistory.size() - 1);
        } else {
            return null;
        }
    }

    public void setCurrentUserLocation(UserIndoorLocationCandidate newUserLocation) {

        if (newUserLocation != null) {
            userLocationHistory.add(newUserLocation);
            if (userLocationHistory.size() > this.userLocationHistorySize) {
                userLocationHistory.remove(0);
            }
            for (int i = 0; i < observers.size(); i++) {
                observers.get(i).userMovedToIndexPath(newUserLocation.indexPath, headingGyro, currentHeading, direction);
            }
            this.currentUserLocation = newUserLocation;
            this.definePathTo(currentUserLocation.indexPath, target, floorTarget);
        }
    }

    public ArrayList<Pair<Integer, Integer>> getSortedCandidatesKeys() {
        sortedCandidateKeys = new ArrayList<>(userLocationCandidatesDict.keySet());
        Collections.sort(sortedCandidateKeys, new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> integerIntegerPair1, Pair<Integer, Integer> integerIntegerPair2) {
                if (userLocationCandidatesDict.get(integerIntegerPair1).weight
                        > userLocationCandidatesDict.get(integerIntegerPair2).weight) {
                    return -1;
                } else {
                    if (userLocationCandidatesDict.get(integerIntegerPair1).weight
                            < userLocationCandidatesDict.get(integerIntegerPair2).weight) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        });
        return sortedCandidateKeys;
    }

    private GABeaconLocalizer initLocalizer() {
        if (beaconLocalizer == null) {
            GABeaconLocalizer localizer = new GABeaconLocalizer();
            localizer.setSortUsingLastAccuracy(true);
            localizer.setPhoneCoordinateCalcMethod(GABeaconLocalizer.CoordinatesCalcMethod.NEAREST);
            beaconLocalizer = localizer;
        }
        return beaconLocalizer;
    }

    public void setCurrentDirection(Pair<Integer, Integer> newDirection) {
        if (currentDirection != null) {
            if (directionHistory.size() == 0 || currentDirection != directionHistory.get(directionHistory.size() - 1)) {
                directionHistory.add(currentDirection);
                if (directionHistory.size() > directionHistorySize) {
                    directionHistory.remove(0);
                }
                for (int i = 0; i < observers.size(); i++) {
                    observers.get(i).userChangedDirection(currentDirection);
                }
            }
        }
        currentDirection = newDirection;
    }

    public void setCurrentHeading(double newHeading) {
        if (headings == null) {
            headings = new ArrayList<>();
        } else {
            if (headings.size() > numberOfHeadingsAccumulated) {
                headings.remove(0);
                if (!headingGyroCanStart) {
                    headingGyroCanStart = true;
                    headingGyro = -currentHeading;
                }
            }
            if (gyroIsOn) {
                if (System.currentTimeMillis() < gyroAlarm) {
                    headings.add(new Pair<>(newHeading * Math.PI / 180, 10));
                } else {
                    gyroIsOn = false;
                }
            } else {
                headings.add(new Pair<>(newHeading * Math.PI / 180, 1));
            }
        }
        averageHeading = calcHeadingAverage(headings);
        if (averageHeading < 0) {
            averageHeading += 360;
        }
        this.currentHeading = averageHeading;
        // TODO uncomment this
        //this.directionCandidates = this.directionsForHeading(this.currentHeading-currentMap.heading);
    }

    public void setCurrentGyro(double newYGyro, double newZGyro) {
        if (Math.abs(newYGyro) > Math.abs(kGyroThreshold) && Math.abs(currentYGyro) <= kGyroThreshold) {
            updateDirection();
            gyroIsOn = true;
            gyroAlarm = System.currentTimeMillis() + TIME_WHILE_GYRO_IS_ON;
        }
        this.currentYGyro = newYGyro;
        if (Math.abs(newZGyro) > Math.abs(kGyroThreshold) && Math.abs(currentZGyro) <= kGyroThreshold) {
            updateDirection();
            gyroIsOn = true;
            gyroAlarm = System.currentTimeMillis() + TIME_WHILE_GYRO_IS_ON;
        }
        this.currentZGyro = newZGyro;
    }

    // Return average of heading values including their weight
    //The formula is the following: AVG = arcTan(sum_of_sin(heading)/sum_of_cos(heading))
    private double calcHeadingAverage(ArrayList<Pair<Double, Integer>> headings) {
        double sinHeadingSum = 0;
        double cosHeadingSum = 0;
        double offset = 0;
        for (int i = 0; i < headings.size(); i++) {
            if (i != 0) {
                offset += Math.abs(headings.get(i).first - headings.get(i - 1).first);
            }
            sinHeadingSum = sinHeadingSum + (Math.sin(headings.get(i).first) * headings.get(i).second);
            cosHeadingSum = cosHeadingSum + (Math.cos(headings.get(i).first) * headings.get(i).second);
        }
        if (offset * 180 / Math.PI < offsetAccepted && offset != 0) {
            this.headingGyro = -currentHeading;
        }
        if (cosHeadingSum == 0) {
            if (sinHeadingSum > 0) {
                return 0;
            }
            return 180;
        }
        return Math.atan2(sinHeadingSum, cosHeadingSum) * 180 / Math.PI;
    }

    public void registerObserver(Object observer) {
        if (!observers.contains(observer)) {
            observers.add((GAFrameworkUserTrackerObserver) observer);
        }
    }

    // MARK: - Functions
    public void startTrackingUser() {
        // Default: outdoor tracking unless a map is set
        this.startOutdoorTracking();


    }

    public void stopTrackingUser() {
        Log.d("Ici", "Stop tracking");
        this.stopOutdoorTracking();
        this.stopIndoorTracking();
    }

    // MARK: - Outdoor Tracking
    private void startOutdoorTracking() {
        if (!this.outdoorTrackingEnabled) {
            //GABeaconManager.sharedGABeaconManager().setLocalizationDelegate(this);
            this.outdoorTrackingEnabled = true;
        }
    }

    private void stopOutdoorTracking() {
        if (this.outdoorTrackingEnabled) {
            this.outdoorTrackingEnabled = false;
        }
    }

    // MARK: - Indoor Tracking
    private void startIndoorTracking() {
        if (!this.indoorTrackingEnabled) {
            Log.d(TAG, "enabling indoorTracking");
            this.beaconLocalizer.setHistorySize(1);
            sensorManager.registerListener(this, linearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
            this.indoorTrackingEnabled = true;
        }
    }

    private void stopIndoorTracking() {
        if (this.indoorTrackingEnabled) {
            sensorManager.unregisterListener(this);
            this.indoorTrackingEnabled = false;
            Log.d(TAG, "indoor tracking disabled");
        }
    }

    private void updateUserLocationWithMotion(double accelerationNorm) {
        if (accelerationNorm != -1) {

            if (accelerationNorm > stepsAccThreshold || accelerationNorm < -stepsAccThreshold) {
                //Log.d(TAG, "updateUserLocationWithMotion");
                steps += 1;
                if (steps >= stepsPerMapItem) {
                    // Empty candidates
                    this.userLocationCandidatesDict.clear();
                    ArrayList<Pair<Integer, Integer>> k;
                    // Look for neighbours
                    if (getCurrentUserLocation() != null) {
                        ArrayList<Pair<Integer, Integer>> neighbours = this.mapHelper.neighboursIndexPaths(getCurrentUserLocation().indexPath);
                        this.updateLocationCandidatesWithNeighbours(neighbours);
                        k = getSortedCandidatesKeys();
                    }

                    // Evaluate candidates using heading
                    this.updateLocationCandidatesWithHeading();
                    k = getSortedCandidatesKeys();
                    // Evaluate candidates using beacons
                    this.updateLocationCandidatesWithBeacons(this.closestBeacons);
                    // Final evaluation: return candidate with best score
                    k = getSortedCandidatesKeys();
                    if (k != null) {
                        if (k.size() > 0) {
                            setCurrentUserLocation(this.userLocationCandidatesDict.get(k.get(0)));
                            for (int i = 0; i < observers.size(); i++) {
                                observers.get(i).userMovedToIndexPath(userLocationCandidatesDict.get(k.get(0)).indexPath, getSortedCandidatesKeys());
                            }
                        }
                    }
                    steps -= stepsPerMapItem;
                }
            }
        }
    }

    private void updateLocationCandidatesWithNeighbours(ArrayList<Pair<Integer, Integer>> neighbourIndexPaths) {
        for (int i = 0; i < neighbourIndexPaths.size(); i++) {
            Pair<Integer, Integer> ip = neighbourIndexPaths.get(i);
            if (this.userLocationCandidatesDict.get(ip) == null) {
                this.userLocationCandidatesDict.put(ip, new UserIndoorLocationCandidate(ip, 0));
            }
        }
    }

    // Return the 3 most probable directions for the provided heading
//    private ArrayList<Pair<Integer, Integer>> directionsForHeading(double heading) {
//        ArrayList<Pair<Integer, Integer>> sortedDirections = new ArrayList<>();
//        int sectorNumber = (int) heading / 45;
//        switch (sectorNumber) {
//            case 0:
//                if (heading < 22.5) {
//                    sortedDirections.add(new Pair<>(0, -1));
//                    sortedDirections.add(new Pair<>(1, -1));
//                    sortedDirections.add(new Pair<>(-1, -1));
//                } else {
//                    sortedDirections.add(new Pair<>(1, -1));
//                    sortedDirections.add(new Pair<>(0, -1));
//                    sortedDirections.add(new Pair<>(1, 0));
//                }
//                break;
//
//            case 1:
//                if (heading < 67.5) {
//                    sortedDirections.add(new Pair<>(1, -1));
//                    sortedDirections.add(new Pair<>(1, 0));
//                    sortedDirections.add(new Pair<>(0, -1));
//                } else {
//                    sortedDirections.add(new Pair<>(1, 0));
//                    sortedDirections.add(new Pair<>(1, -1));
//                    sortedDirections.add(new Pair<>(1, 1));
//                }
//                break;
//
//            case 2:
//                if (heading < 112.5) {
//                    sortedDirections.add(new Pair<>(1, 0));
//                    sortedDirections.add(new Pair<>(1, 1));
//                    sortedDirections.add(new Pair<>(1, -1));
//                } else {
//                    sortedDirections.add(new Pair<>(1, 1));
//                    sortedDirections.add(new Pair<>(1, 0));
//                    sortedDirections.add(new Pair<>(0, 1));
//                }
//                break;
//
//            case 3:
//                if (heading < 157.5) {
//                    sortedDirections.add(new Pair<>(1, 1));
//                    sortedDirections.add(new Pair<>(0, 1));
//                    sortedDirections.add(new Pair<>(1, 0));
//                } else {
//                    sortedDirections.add(new Pair<>(0, 1));
//                    sortedDirections.add(new Pair<>(1, 1));
//                    sortedDirections.add(new Pair<>(-1, 1));
//                }
//                break;
//
//            case 4:
//                if (heading < 202.5) {
//                    sortedDirections.add(new Pair<>(0, 1));
//                    sortedDirections.add(new Pair<>(-1, 1));
//                    sortedDirections.add(new Pair<>(1, 1));
//                } else {
//                    sortedDirections.add(new Pair<>(-1, 1));
//                    sortedDirections.add(new Pair<>(0, 1));
//                    sortedDirections.add(new Pair<>(-1, 0));
//                }
//                break;
//
//            case 5:
//                if (heading < 247.5) {
//                    sortedDirections.add(new Pair<>(-1, 1));
//                    sortedDirections.add(new Pair<>(-1, 0));
//                    sortedDirections.add(new Pair<>(0, 1));
//                } else {
//                    sortedDirections.add(new Pair<>(-1, 0));
//                    sortedDirections.add(new Pair<>(-1, 1));
//                    sortedDirections.add(new Pair<>(-1, -1));
//                }
//                break;
//
//            case 6:
//                if (heading < 292.5) {
//                    sortedDirections.add(new Pair<>(-1, 0));
//                    sortedDirections.add(new Pair<>(-1, -1));
//                    sortedDirections.add(new Pair<>(-1, 1));
//                } else {
//                    sortedDirections.add(new Pair<>(-1, -1));
//                    sortedDirections.add(new Pair<>(-1, 0));
//                    sortedDirections.add(new Pair<>(0, -1));
//                }
//                break;
//
//            case 7:
//                if (heading < 337.5) {
//                    sortedDirections.add(new Pair<>(-1, -1));
//                    sortedDirections.add(new Pair<>(0, -1));
//                    sortedDirections.add(new Pair<>(-1, 0));
//                } else {
//                    sortedDirections.add(new Pair<>(0, -1));
//                    sortedDirections.add(new Pair<>(-1, -1));
//                    sortedDirections.add(new Pair<>(1, -1));
//                }
//                break;
//
//            default:
//                // Illegal
//                sortedDirections = new ArrayList<>();
//        }
//
//        return sortedDirections;
//    }

    private void updateLocationCandidatesWithBeacons(ArrayList<GABeacon> closestBeacons) {
        if (closestBeacons != null && closestBeacons.size() != 0) {
            GABeacon nearestBeacon = closestBeacons.get(0);
            if (nearestBeacon != null) {
//                Log.e("nearest beacon: " + nearestBeacon.getMajor() + " " + nearestBeacon.getMinor());
                //TODO uncomment this next line
                this.updateCandidatesWithNearBeacon(nearestBeacon);
                //this.updateBestCandidatesWithClosestBeacon(nearestBeacon);
                //TODO uncomment this next line
                this.updateCandidatesWithRealDistanceAndClosestBeacon(nearestBeacon);
            }
//            this.updateCandidatesWith2ClosestBeacons(closestBeacons);
        }
    }

    // Closest beacons: near filter
    // If distance from the closest beacon is below the provided threshold (beaconForceDistanceThreshold):
    // if the best candidate's distance to the closest beacon in map items is above a given threshold (beaconForceDistanceMapItemMax),
    // then a the first map item between the best candidate and the beacon that is near the beacon enough
    // is added to the list of candidates.
    //
    private void updateCandidatesWithNearBeacon(GABeacon nearestBeacon) {
        if (nearestBeacon.getAccuracy() < this.beaconForceDistanceThreshold) {
            Pair<Integer, Integer> beaconIP = nearestBeacon.getMapIndexPath();
            if (beaconIP != null) {
                Pair<Integer, Integer> key = null;
                if (getSortedCandidatesKeys() != null && getSortedCandidatesKeys().size() != 0) {
                    key = getSortedCandidatesKeys().get(0);
                }
                if (key != null) {
                    UserIndoorLocationCandidate candidate = this.userLocationCandidatesDict.get(key);
                    if (candidate != null) {
                        Pair<Integer, Integer> nearestBeaconCandidateIP = this.mapHelper.indexPathAtDistance(this.beaconForceDistanceMapItemMax,
                                beaconIP, candidate.indexPath);
                        this.userLocationCandidatesDict.put(nearestBeaconCandidateIP, new UserIndoorLocationCandidate(
                                nearestBeaconCandidateIP, candidate.weight + 1));
                    }
                }
            }
        }
    }

    private void updateCandidatesWithRealDistanceAndClosestBeacon(GABeacon nearestBeacon) {
        if (nearestBeacon.getAccuracy() < this.beaconForceDistanceThreshold) {
            Pair<Integer, Integer> beaconIP = nearestBeacon.getMapIndexPath();
            if (beaconIP != null) {
                Pair<Integer, Integer> key;
                if (currentUserLocation != null && currentUserLocation.indexPath != null) {
//                    Log.d(TAG, "updateCandidatesWithRealDistanceAndClosestBeacon: "+beaconIP.first+" "+ beaconIP.second
//                    + " "+ currentUserLocation.indexPath.first+" "+ currentUserLocation.indexPath.second);
                    int distanceBetweenUserAndClosestBeacon = this.mapHelper.pathFrom(beaconIP, currentUserLocation.indexPath).second;
                    if (distanceBetweenUserAndClosestBeacon > distanceThresholdBetweenUserAndClosestBeacon) {
                        if (getSortedCandidatesKeys() != null && getSortedCandidatesKeys().size() != 0) {
                            key = getSortedCandidatesKeys().get(0);
                            UserIndoorLocationCandidate candidate = this.userLocationCandidatesDict.get(key);
                            this.userLocationCandidatesDict.put(beaconIP, new UserIndoorLocationCandidate(
                                    beaconIP, candidate.weight + 1));

                        }
                    }
                }
            }
        }
    }

    // Nearest beacon: closest of the 2 best candidates.
    // We'll compute the distance between the nearest beacon and the 2 current best candidates.
    // If our second best candidate is closer to the nearest beacon, we'll make it the best.
    private void updateBestCandidatesWithClosestBeacon(GABeacon closestBeacon) {
        Pair<Integer, Integer> beaconIP = closestBeacon.mapIndexPath;
        ArrayList<Pair<Integer, Integer>> sortedKeys = getSortedCandidatesKeys();
        if (beaconIP != null && sortedKeys != null && sortedKeys.size() >= 2) {

            Pair<Integer, Integer> firstIndexPath = sortedKeys.get(0);
            Pair<Integer, Integer> secondIndexPath = sortedKeys.get(1);
            int distance1 = this.mapHelper.pathFrom(beaconIP, firstIndexPath).second;
            if (distance1 != -1) {
                int distance2 = this.mapHelper.pathFrom(beaconIP, secondIndexPath).second;
                if (distance2 != -1) {
                    if (distance2 < distance1) {
                        // Let's switch the best candidate and the second one
                        double w1 = this.userLocationCandidatesDict.get(firstIndexPath).weight;
                        this.userLocationCandidatesDict.get(firstIndexPath).weight = this.userLocationCandidatesDict.get(secondIndexPath).weight;
                        this.userLocationCandidatesDict.get(secondIndexPath).weight = w1;
                    }
                }
            }
        }
    }

    // Update using the 2 closest beacons:
    private void updateCandidatesWith2ClosestBeacons(ArrayList<GABeacon> closestBeacons) {
        Pair<Integer, Integer> candidateIndexPath = null;
        if (getSortedCandidatesKeys() != null && getSortedCandidatesKeys().size() != 0) {
            candidateIndexPath = getSortedCandidatesKeys().get(0);
        }
        if (candidateIndexPath == null) {
            return;
        }
        Pair<Integer, Integer> indexPathOfBeacon1 = null;
        Pair<Integer, Integer> indexPathOfBeacon2 = null;
        if (closestBeacons != null && closestBeacons.size() > 1) {
            indexPathOfBeacon1 = closestBeacons.get(0).getMapIndexPath();
            indexPathOfBeacon2 = closestBeacons.get(1).getMapIndexPath();
        }
        if (indexPathOfBeacon1 == null || indexPathOfBeacon2 == null) {
            return;
        }
        int distanceFromBeacon1 = this.mapHelper.pathFrom(candidateIndexPath, indexPathOfBeacon1).second;
        int distanceFromBeacon2 = this.mapHelper.pathFrom(candidateIndexPath, indexPathOfBeacon2).second;
        if (distanceFromBeacon1 == -1 || distanceFromBeacon2 == -1) {
            return;
        }

        if (distanceFromBeacon2 < distanceFromBeacon1) {
            // Let's use the next item towards d2 at a distance at most equal to d1
            // Check that candidate is actually between b1 and b2
            Pair<Integer, Integer> newCandidateIP = this.mapHelper.indexPathAtDistance((distanceFromBeacon1 + distanceFromBeacon2) / 2,
                    indexPathOfBeacon2, candidateIndexPath);
            if (newCandidateIP != null) {
                this.userLocationCandidatesDict.put(newCandidateIP, new UserIndoorLocationCandidate(
                        candidateIndexPath, this.userLocationCandidatesDict.get(candidateIndexPath).weight + 1));
            }
        }
    }

    // Use heading to update candidates
    private void updateLocationCandidatesWithHeading() {
        if (getCurrentUserLocation() == null) {
            return;
        }
        double correctedHeading = -(this.headingGyro - this.currentMap.heading);
        if (correctedHeading > 360) {
            correctedHeading -= 360;
        }
        if (correctedHeading < 0) {
            correctedHeading += 360;
        }
        // Sorted according to their proximity to current heading
        ArrayList<Pair<Integer, Integer>> sortedDirections = this.directionsForHeading(correctedHeading);
        ArrayList<Pair<Integer, Integer>> sortedDirectionsToRemove = this.getOppositeDirections(sortedDirections);
        Pair<Integer, Integer> currentIp = this.currentUserLocation.indexPath;
        // For each candidate:
        Map<Pair<Integer, Integer>, UserIndoorLocationCandidate> candidateDictCopy = new HashMap<>();
        candidateDictCopy.putAll(this.userLocationCandidatesDict);
        for (Map.Entry<Pair<Integer, Integer>, UserIndoorLocationCandidate> entry : candidateDictCopy.entrySet()) {
            UserIndoorLocationCandidate candidate = candidateDictCopy.get(entry.getKey());
            // Compute candidate direction
            Pair<Integer, Integer> direction = new Pair<>(candidate.indexPath.first - currentIp.first, candidate.indexPath.second - currentIp.second);
            // If sorted directions doesn't contain direction, remove candidate
            for (int i = 0; i < sortedDirectionsToRemove.size(); i++) {
                if ((sortedDirectionsToRemove.get(i).first.equals(direction.first) && sortedDirectionsToRemove.get(i).second.equals(direction.second))) {
                    userLocationCandidatesDict.remove(entry.getKey());
                }
            }
        }
        if (sortedDirections.size() > 0) {
            for (int i = 0; i < sortedDirections.size(); i++) {
                Pair<Integer, Integer> direction = sortedDirections.get(i);
                Pair<Integer, Integer> indexPath = new Pair<>(
                        getCurrentUserLocation().indexPath.first + direction.first
                        , getCurrentUserLocation().indexPath.second + direction.second);
                if (this.userLocationCandidatesDict.containsKey(indexPath)) {
                    this.userLocationCandidatesDict.get(indexPath).weight += (double) (sortedDirections.size() - i);
                }
            }
        }
    }

    private ArrayList<Pair<Integer, Integer>> getOppositeDirections(ArrayList<Pair<Integer, Integer>> sortedDirections) {
        ArrayList<Pair<Integer, Integer>> oppositeDirections = new ArrayList<>();
        for (int i = 0; i < sortedDirections.size(); i++) {
            Pair<Integer, Integer> currentDirection = sortedDirections.get(i);
            oppositeDirections.add(new Pair<>(-currentDirection.first, -currentDirection.second));
        }
        return oppositeDirections;
    }

    private ArrayList<Pair<Integer, Integer>> directionsForHeading(double heading) {
        ArrayList<Pair<Integer, Integer>> sortedDirections = new ArrayList<>();
        // North
        if (315 < heading || heading < 45) {
            sortedDirections.add(new Pair<>(0, -1));
            if (heading > 0 && heading < 315) {
                sortedDirections.add(new Pair<>(1, 0));
            } else {
                sortedDirections.add(new Pair<>(-1, 0));
            }
            direction = "North";
        }
        // East
        if (45 < heading && heading < 135) {
            sortedDirections.add(new Pair<>(1, 0));
            if (heading > 90) {
                sortedDirections.add(new Pair<>(0, 1));
            } else {
                sortedDirections.add(new Pair<>(0, -1));
            }
            direction = "East";
        }
        // South
        if (135 < heading && heading < 225) {
            sortedDirections.add(new Pair<>(0, 1));
            if (heading > 180) {
                sortedDirections.add(new Pair<>(-1, 0));
            } else {
                sortedDirections.add(new Pair<>(1, 0));
            }
            direction = "South";
        }
        // West
        if (225 < heading && heading < 315) {
            sortedDirections.add(new Pair<>(-1, 0));
            if (heading > 270) {
                sortedDirections.add(new Pair<>(0, -1));
            } else {
                sortedDirections.add(new Pair<>(0, 1));
            }
            direction = "West";
        }
        return sortedDirections;
    }

    // Update direction using the current candidates
    private void updateDirection() {
        if (directionCandidates != null && directionCandidates.size() != 0) {
            setCurrentDirection(this.directionCandidates.get(this.directionCandidates.size() - 1));
        }
    }

    @Override
    public void rangedBeacons(ArrayList<GABeacon> beacons) {

        ArrayList<GABeacon> localizationBeacons = new ArrayList<>();
        for (int i = 0; i < beacons.size(); i++) {
            GABeacon beacon = beacons.get(i);
            if (beacon != null) {
                if (beacon.mapIndexPath != null) {
                    localizationBeacons.add(beacon);
                }
            }
        }
        this.beaconLocalizer.addRangedBeacons(localizationBeacons);

        // Let's retrieve the 3 closest ones
        this.closestBeacons = this.beaconLocalizer.nearestBeacons(3);
        // If we don't have any map id, let's set it using the closest beacon
        if (closestBeacons != null && closestBeacons.size() != 0) {
            GABeacon closestBeacon = this.closestBeacons.get(0);
            if (closestBeacon != null) {
                setCurrentMapId(closestBeacon.getMapId());
            }
            // If we don't have any location, set it to the closest beacon
            if (userLocationHistory.size() == 0 && closestBeacons.get(0).mapIndexPath != null) {
                setCurrentUserLocation(new UserIndoorLocationCandidate(closestBeacons.get(0).mapIndexPath, 0));
            }
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            if (System.currentTimeMillis() > alarmForGyroscope) {
                gyroscopeValues = event.values;
                setCurrentGyro(gyroscopeValues[y] * 180 / Math.PI, gyroscopeValues[z] * 180 / Math.PI);
                alarmForGyroscope = System.currentTimeMillis() + PERIOD_BETWEEN_TWO_GYROSCOPE_VALUE;
            }
            headingGyro = headingGyro + currentZGyro * MINIMUM_PERIOD_BETWEEN_TWO_GYRO_VALUES / 1000;
        }
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            if (System.currentTimeMillis() > alarmForAccelerometer) {
                if (currentMap != null) {
                    this.updateUserLocationWithMotion(event.values[2]);
                }
                alarmForAccelerometer = System.currentTimeMillis() + PERIOD_BETWEEN_TWO_ACCELEROMETER_VALUE;
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerationValues = event.values;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geomagneticValues = event.values;
        if (accelerationValues != null && geomagneticValues != null) {

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, accelerationValues, geomagneticValues);
            if (success) {
                float orientation[] = new float[3];
                orientation = SensorManager.getOrientation(R, orientation);
                double heading = (orientation[0] * 180 / Math.PI);
                if (heading < 0) {
                    heading += 360;
                }
                setCurrentHeading(heading);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void definePathTo(Pair<Integer, Integer> currentPosition, Pair<Integer, Integer> target, Integer floorTarget) {
        // If the target is on the same floor
        if (floorTarget != null && currentMap.getFloor() == floorTarget) {
            Pair<ArrayList<Pair<Integer, Integer>>, Integer> path = this.mapHelper.pathFrom(currentPosition, target);
            if (path != null && path.first != null) {
                path.first.add(target);
                for (int i = 0; i < observers.size(); i++) {
                    observers.get(i).onPathChanged(path, null);
                }
            }
        }
        // If the target is on an other floor
        else {
            ArrayList<FloorAccess> initialFloorAccesses = currentMap.getFloorAccesses();
            ArrayList<FloorAccess> possibleFloorAccesses = new ArrayList<>();
            // Get preferences corresponding to specific attributes : will say if we need to take
            // elevator or not
            String specificAttribute = sharedPreferences.getString("specific_attribute_user", "0");
            currentSpecificAttribute = getCurrentSpecificAttribute(specificAttribute);
            // Set default value to floorAccessType
            FloorAccess.FloorAccessType floorAccessType = FloorAccess.FloorAccessType.STAIRS;
            for (int i = 0; i < initialFloorAccesses.size(); i++) {
                FloorAccess currentFloorAccess = initialFloorAccesses.get(i);
                if (currentFloorAccess.getFloorsPossibilities().contains(floorTarget)) {
                    switch (currentSpecificAttribute) {
                        case NONE:
                        case SOUND_GUIDANCE:
                            if (currentFloorAccess.getFloorAccessType() == FloorAccess.FloorAccessType.STAIRS) {
                                possibleFloorAccesses.add(currentFloorAccess);
                                floorAccessType = FloorAccess.FloorAccessType.STAIRS;
                            }
                            break;

                        case ELEVATOR:
                        case BOTH:
                            if (currentFloorAccess.getFloorAccessType() == FloorAccess.FloorAccessType.ELEVATOR) {
                                Log.d(TAG, "definePathTo: elevator access");
                                possibleFloorAccesses.add(currentFloorAccess);
                                floorAccessType = FloorAccess.FloorAccessType.ELEVATOR;
                            }
                            break;

                        default:

                            break;
                    }
                }
            }
            Pair<ArrayList<Pair<Integer, Integer>>, Integer> path = getFastestPath(possibleFloorAccesses);
            for (int i = 0; i < observers.size(); i++) {
                observers.get(i).onPathChanged(path, floorAccessType);
            }
        }
    }

    private Pair<ArrayList<Pair<Integer, Integer>>, Integer> getFastestPath(ArrayList<FloorAccess> possibleFloorAccesses) {
        Pair<ArrayList<Pair<Integer, Integer>>, Integer> fastestPath = null;
        for (int i = 0; i < possibleFloorAccesses.size(); i++) {
            Pair<ArrayList<Pair<Integer, Integer>>, Integer> currentPath =
                    mapHelper.pathFrom(currentUserLocation.indexPath,
                            possibleFloorAccesses.get(i).getPosition());
            if (fastestPath == null || currentPath.second < fastestPath.second) {
                fastestPath = currentPath;
                if (fastestPath != null && fastestPath.first != null) {
                    fastestPath.first.add(possibleFloorAccesses.get(i).getPosition());
                }
            }
        }
        return fastestPath;
    }

    // Method called when the user pick a target
    public void setTarget(Pair<Integer, Integer> target, int floor) {
        this.target = target;
        this.floorTarget = floor;

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String specificAttribute = sharedPreferences.getString("specific_attribute_values_array", "0");
        getCurrentSpecificAttribute(specificAttribute);
        definePathTo(currentUserLocation.indexPath, target, floorTarget);
    }

    private SpecificAttribute getCurrentSpecificAttribute(String specificAttribute) {
        switch (specificAttribute) {
            case "0":
                currentSpecificAttribute = SpecificAttribute.NONE;
                break;

            case "V":
                currentSpecificAttribute = SpecificAttribute.SOUND_GUIDANCE;
                break;

            case "A":
                currentSpecificAttribute = SpecificAttribute.ELEVATOR;
                break;

            case "VA":
                currentSpecificAttribute = SpecificAttribute.BOTH;
                break;

            default:
                currentSpecificAttribute = SpecificAttribute.NONE;
                break;
        }
        return currentSpecificAttribute;
    }

    public GABeaconMapHelper getMapHelper() {
        return mapHelper;
    }
}
