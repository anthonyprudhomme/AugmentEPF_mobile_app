package com.filiereticsa.arc.augmentepf.localization;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anthonyprudhomme on 11/10/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */

public class GABeaconLocalizer {

    private static final String TAG = "Ici";

    public enum CoordinatesCalcMethod {
        NEAREST,
        SQUARE,
        CENTROID
    }

    private enum ComparisonMethod {
        LAST_ACCURACY,
        AVERAGE_ACCURACY
    }

    private int historySize = 1;

    public void setHistorySize(int newHistorySize) {
        if (historySize > newHistorySize) {
            checkStatusesForHistorySize(newHistorySize);
        }
        historySize = newHistorySize;
    }

    private void checkStatusesForHistorySize(int size) {
        for (Map.Entry<String, ArrayList<LocalizedBeaconStatus>> entry : beaconsStatuses.entrySet()) {
            if (beaconsStatuses.get(entry.getKey()).size() > size) {
                for (int i = 0; i < beaconsStatuses.get(entry.getKey()).size() - size; i++) {
                    beaconsStatuses.get(entry.getKey()).remove(0);
                }
            }
        }
    }

    private double kUnknownAccuracyValue = 1;
    private Double unknownAccuracyReplaceValue = null;

    // Sorting methods to apply
    private ArrayList<ComparisonMethod> accSortComparatorStack = new ArrayList<>();
    private Integer lastAccuracySortIndex = null;
    public Boolean sortUsingLastAccuracy;

    public boolean isSortUsingLastAccuracy() {
        return (lastAccuracySortIndex != null);
    }

    public void setSortUsingLastAccuracy(Boolean useLastAccuracy) {
        if (!useLastAccuracy && lastAccuracySortIndex != null) {
            accSortComparatorStack.remove((int) lastAccuracySortIndex);
            lastAccuracySortIndex = null;
        }
        if (useLastAccuracy) {
            // If it was already enabled, we'll make sure it's applied last again
            if (lastAccuracySortIndex != null && lastAccuracySortIndex != (accSortComparatorStack.size() - 1)) {
                accSortComparatorStack.remove((int) lastAccuracySortIndex);
            }
            accSortComparatorStack.add(ComparisonMethod.LAST_ACCURACY);
            lastAccuracySortIndex = accSortComparatorStack.size() - 1;
        }
    }

    private int lastAccuracyComparison(ArrayList<LocalizedBeaconStatus> statuses1, ArrayList<LocalizedBeaconStatus> statuses2) {
        if (statuses2.size() != 0 && statuses1.size() != 0 && (statuses1.get(statuses1.size() - 1).accuracy <= statuses2.get(statuses2.size() - 1).accuracy)) {
            return -1;
        } else {
            return 1;
        }
    }

    private Integer avgAccuracySortIndex = null;
    public Boolean sortUsingAverageAccuracy = null;

    public Boolean isSortUsingAverageAccuracy() {
        return (avgAccuracySortIndex != null);
    }

    public void setSortUsingAverageAccuracy(Boolean useAvgAccuracy) {


        // If we want to disable it:
        if (!useAvgAccuracy && avgAccuracySortIndex != null) {
            accSortComparatorStack.remove((int) avgAccuracySortIndex);
            avgAccuracySortIndex = null;
        }
        if (useAvgAccuracy) {
            // If it was already enabled, we'll make sure it's applied last again
            if (avgAccuracySortIndex != null && avgAccuracySortIndex != (accSortComparatorStack.size() - 1)) {
                accSortComparatorStack.remove((int) avgAccuracySortIndex);
            }
            accSortComparatorStack.add(ComparisonMethod.AVERAGE_ACCURACY);
            avgAccuracySortIndex = accSortComparatorStack.size() - 1;
        }
    }

    private int averageAccuracyComparison(ArrayList<LocalizedBeaconStatus> statuses1, ArrayList<LocalizedBeaconStatus> statuses2) {
        if (statuses1.size() != 0 && statuses2.size() != 0) {
            double avg1 = 0;
            for (int i = 0; i < statuses1.size(); i++) {
                avg1 += statuses1.get(i).accuracy;
            }
            avg1 = avg1 / (double) (statuses1.size());

            double avg2 = 0;
            for (int i = 0; i < statuses2.size(); i++) {
                avg2 += statuses1.get(i).accuracy;
            }
            avg2 = avg2 / (double) (statuses2.size());
            if (avg1 <= avg2) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }

    private CoordinatesCalcMethod phoneCoordinateMethod;

    CoordinatesCalcMethod phoneCoordinateCalcMethod = CoordinatesCalcMethod.CENTROID;

    public void setPhoneCoordinateCalcMethod(CoordinatesCalcMethod phoneCoordinateCalcMethod) {
        switch (phoneCoordinateCalcMethod) {
            case SQUARE:
                phoneCoordinateMethod = CoordinatesCalcMethod.SQUARE;
                break;
            case CENTROID:
                phoneCoordinateMethod = CoordinatesCalcMethod.CENTROID;
                break;
            case NEAREST:
                phoneCoordinateMethod = CoordinatesCalcMethod.NEAREST;
                break;
        }
    }

    private Map<String, ArrayList<LocalizedBeaconStatus>> beaconsStatuses = new HashMap<>();

    public void clearHistory(String uuid) {
        if (uuid == null) {
            beaconsStatuses.clear();
        } else {
            for (Map.Entry<String, ArrayList<LocalizedBeaconStatus>> entry : beaconsStatuses.entrySet()) {
                if (entry.getValue().get(0).regionUUIDString.equalsIgnoreCase(uuid)) {
                    beaconsStatuses.remove(entry.getKey());
                }
            }
        }
    }

    // Returns the N closest beacon
    public ArrayList<GABeacon> nearestBeacons(Integer beaconsNr) {
        if (beaconsNr == null) {
            beaconsNr = 1;
        }
        if (beaconsStatuses.size() != 0) {
            // We need a coordinate method
            if (phoneCoordinateMethod == null) {
                phoneCoordinateCalcMethod = CoordinatesCalcMethod.CENTROID;
            }

            // Let's apply data filters to beacon statuses
            for (Map.Entry<String, ArrayList<LocalizedBeaconStatus>> entry : beaconsStatuses.entrySet()) {
                beaconsStatuses.put(entry.getKey(), replaceUnknownAccuracy(kUnknownAccuracyValue, unknownAccuracyReplaceValue, entry.getValue()));
            }
            // Let's sort the beacons using their last accuracy or other method
            // Let's use last comparator method in stack
            ArrayList<String> sortedKeys;
            ComparisonMethod sortingMethod = null;
            if (accSortComparatorStack != null && accSortComparatorStack.size() != 0) {
                sortingMethod = accSortComparatorStack.get(accSortComparatorStack.size() - 1);
            }
            if (sortingMethod != null) {
                sortedKeys = sortedBeaconKeys(beaconsStatuses, sortingMethod);
            } else {
                sortedKeys = new ArrayList<>(this.beaconsStatuses.keySet());
            }
            ArrayList<LocalizedBeaconStatus> statuses = new ArrayList<>();
            for (int i = 0; i < Math.min(sortedKeys.size(), beaconsNr ); i++) {
                String key = sortedKeys.get(i);
                LocalizedBeaconStatus lastStatus = null;
                if (beaconsStatuses != null && beaconsStatuses.get(key) != null && beaconsStatuses.get(key).size() != 0) {
                    lastStatus = beaconsStatuses.get(key).get(beaconsStatuses.get(key).size() - 1);
                }
                if (lastStatus != null) {
                    statuses.add(lastStatus);
                }
            }
            return this.beaconsFromStatuses(statuses);
        } else {
            return null;
        }
    }

    // MARK: - Ranged beacons
    public void addRangedBeacons(ArrayList<GABeacon> beacons) {
        // Add new data
        for (int i = 0; i < beacons.size(); i++) {
            GABeacon currentBeacon = beacons.get(i);
            if (beaconsStatuses.get(currentBeacon.getKeyString()) == null) {
                beaconsStatuses.put(currentBeacon.getKeyString(), new ArrayList<LocalizedBeaconStatus>());
            }
            // Using map index path
            Point beaconCoordinates;
            if (currentBeacon.mapIndexPath != null) {
                beaconCoordinates = new Point(currentBeacon.mapIndexPath.first, currentBeacon.mapIndexPath.second);
            } else {
                // Using coordinates
                beaconCoordinates = new Point(currentBeacon.xCoord, currentBeacon.yCoord);
            }

            if (currentBeacon.getAccuracy() != -1) {
                beaconsStatuses.get(currentBeacon.getKeyString()).add(new LocalizedBeaconStatus(currentBeacon.getUuid()
                        , currentBeacon.getProximity()
                        , currentBeacon.getAccuracy()
                        , beaconCoordinates
                        , currentBeacon.getMapId()
                        , currentBeacon.getMapIndexPath()
                        , currentBeacon.getKeyString()));
            }

            // If history's too big, remove first values
            if (beaconsStatuses.get(currentBeacon.getKeyString()).size() > historySize) {
                beaconsStatuses.get(currentBeacon.getKeyString()).remove(0);
            }
        }
    }


    private ArrayList<String> sortedBeaconKeys(final Map<String, ArrayList<LocalizedBeaconStatus>> statusesDic, final ComparisonMethod comparisonMethod) {

        ArrayList<String> keys = new ArrayList<>(statusesDic.keySet());
        Collections.sort(keys, new Comparator<String>() {
            @Override
            public int compare(String string1, String string2) {
                return getSortingMethod(comparisonMethod, statusesDic.get(string1), statusesDic.get(string2));
            }
        });
        return keys;
    }

    private ArrayList<LocalizedBeaconStatus> replaceUnknownAccuracy(double unknownAccuracyValue, Double toAccuracy, ArrayList<LocalizedBeaconStatus> statuses) {
        int statusIdx = 0;
        while (statusIdx < statuses.size()) {
            if (statuses.get(statusIdx).accuracy == unknownAccuracyValue) {
                if (toAccuracy != null) {
                    statuses.get(statusIdx).accuracy = toAccuracy;
                } else {
                    statuses.remove(statusIdx);
                    statusIdx -= 1;
                }
            }
            statusIdx += 1;
        }
        return statuses;
    }


    private ArrayList<GABeacon> beaconsFromStatuses(ArrayList<LocalizedBeaconStatus> statuses) {
        ArrayList<GABeacon> beacons = new ArrayList<>();
        ArrayList<GABeacon> allBeacons = GABeacon.allBeacons;
        for (int i = 0; i < statuses.size(); i++) {
            for (int j = 0; j < allBeacons.size(); j++) {
                if (allBeacons.get(j).getKeyString().equalsIgnoreCase(statuses.get(i).keyString)) {
                    GABeacon beacon = allBeacons.get(j);
                    beacon.setDistance(statuses.get(i).accuracy);
                    beacons.add(beacon);
                    Log.d(TAG,"beacon " + (i + 1) + " " + beacon.getAccuracy() + " " + beacon.getMinor());
                }
            }
        }
//        Log.e("Closest beacons size: " +beacons.size());
        return beacons;
    }

    private int getSortingMethod(ComparisonMethod comparisonMethod, ArrayList<LocalizedBeaconStatus> statuses1, ArrayList<LocalizedBeaconStatus> statuses2) {
        switch (comparisonMethod) {
            case LAST_ACCURACY:
                return lastAccuracyComparison(statuses1, statuses2);

            case AVERAGE_ACCURACY:
                return averageAccuracyComparison(statuses1, statuses2);

            default:
                return 0;
        }
    }

    // Sort beacons using proximity
    private boolean lastProximityComparison(ArrayList<LocalizedBeaconStatus> status1, ArrayList<LocalizedBeaconStatus> status2) {

        if (status1.size() != 0 && status2.size() != 0) {
            switch (status2.get(status2.size() - 1).proximity.toLowerCase()) {
                case "far":
                    return (!status1.get(status1.size() - 1).proximity.toLowerCase().equalsIgnoreCase("unknown"));
                case "near":
                    return ((!status1.get(status1.size() - 1).proximity.toLowerCase().equalsIgnoreCase("immediate"))
                            || (!status1.get(status1.size() - 1).proximity.toLowerCase().equalsIgnoreCase("near")));
                case "immediate":
                    return false;
                default:
                    return true;
            }
        } else {
            return false;
        }
    }

    // Simple localization: looks for the 4 closest beacons
    private Point simpleLocalization4() {
        if (beaconsStatuses.size() != 0) {
            // We need a coordinate method
            if (phoneCoordinateMethod == null) {
                phoneCoordinateCalcMethod = CoordinatesCalcMethod.CENTROID;
            }

            // Let's apply data filters to beacon statuses
            for (Map.Entry<String, ArrayList<LocalizedBeaconStatus>> entry : beaconsStatuses.entrySet()) {
                beaconsStatuses.put(entry.getKey(), replaceUnknownAccuracy(kUnknownAccuracyValue, unknownAccuracyReplaceValue, beaconsStatuses.get(entry.getKey())));
            }

            // Let's sort the beacons using their last accuracy or other method
            // Let's use last comparator method in stack
            ArrayList<String> sortedKeys;
            ComparisonMethod sortingMethod = accSortComparatorStack.get(accSortComparatorStack.size() - 1);
            if (sortingMethod != null) {
                sortedKeys = sortedBeaconKeys(beaconsStatuses, sortingMethod);
            } else {
                sortedKeys = new ArrayList<>(this.beaconsStatuses.keySet());
            }

            ArrayList<Point> coords = new ArrayList<>();
            for (int i = 0; i < sortedKeys.size(); i++) {
                if (beaconsStatuses.get(i).size() > 0) {
                    coords.add(beaconsStatuses.get(i).get(beaconsStatuses.get(i).size() - 1).coordinates);
                }
            }

            return meshCoordinates(coords, phoneCoordinateMethod);
        } else {
            return null;
        }

    }


    private Point meshCoordinates(ArrayList<Point> beaconsCoordinates, CoordinatesCalcMethod method) {
        switch (method) {
            case SQUARE:
                return squareBasedCoordinates(beaconsCoordinates);

            case NEAREST:
                return nearestBeaconCoordinates(beaconsCoordinates);

            case CENTROID:
                return centroidBasedCoordinates(beaconsCoordinates);

            default:
                return null;
        }
    }

    private Point meshCoordinates(ArrayList<GABeacon> beacons) {
        if (beacons.size() != 0) {
            Collections.sort(beacons, new Comparator<GABeacon>() {
                @Override
                public int compare(GABeacon beacon1, GABeacon beacon2) {
                    if (beacon1.xCoord <= beacon2.xCoord && beacon1.yCoord <= beacon2.yCoord) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
            return new Point(beacons.get(0).xCoord, beacons.get(0).yCoord);
        } else {
            return null;
        }
    }


    private Point nearestBeaconCoordinates(ArrayList<Point> coordinates) {
        if (coordinates != null && coordinates.size() != 0) {
            return coordinates.get(0);
        }
        return null;
    }

    private Point squareBasedCoordinates(ArrayList<Point> coordinates) {
        if (coordinates != null && coordinates.size() != 0) {
            if (coordinates.size() >= 4) {
                ArrayList<Point> fourFirstCoordinates = new ArrayList<>();
                fourFirstCoordinates.add(coordinates.get(0));
                fourFirstCoordinates.add(coordinates.get(1));
                fourFirstCoordinates.add(coordinates.get(2));
                fourFirstCoordinates.add(coordinates.get(3));
                return topLeftCoordinates(fourFirstCoordinates);
            }
        }
        return null;
    }

    private Point centroidBasedCoordinates(ArrayList<Point> coordinates) {
        // We need coordinates
        if (coordinates != null && coordinates.size() != 0) {
            if (coordinates.size() >= 3) {
                ArrayList<Point> extractedPoints = new ArrayList<>();
                extractedPoints.add(coordinates.get(0));
                extractedPoints.add(coordinates.get(1));
                // Append first point in coordinates that is not aligned with the two others
                int index = 2;
                while (extractedPoints.size() < 3 && index < coordinates.size()) {
                    if (!isAligned(coordinates.get(index), extractedPoints)) {
                        extractedPoints.add(coordinates.get(index));
                    }
                    index += 1;
                }

                return topLeftCoordinates(extractedPoints);
            }
        }
        return null;
    }

    private boolean isAligned(Point point, ArrayList<Point> pointsArray) {
        return pointsArray.size() >= 2 && ((point.x == pointsArray.get(0).x && point.x == pointsArray.get(1).x) || (point.y == pointsArray.get(0).y && point.y == pointsArray.get(1).y));
    }

    // Returns the top-left coordinates in an array of points in the mesh
    private Point topLeftCoordinates(ArrayList<Point> coordinatesArray) {
        // 4 values: top left value
        if (coordinatesArray.size() == 4) {
            ArrayList<Point> sortedPoints = null;
            Collections.sort(coordinatesArray, new Comparator<Point>() {
                @Override
                public int compare(Point point1, Point point2) {
                    if (point1.x <= point2.x && point1.y <= point2.y) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
            if (sortedPoints != null) {
                return sortedPoints.get(0);
            } else {
                return null;
            }
        }

        // 3 Values: compute the centroid's coordinates (x,y) and
        // return the top left coordinate (floor(x), floor(y)).
        if (coordinatesArray.size() == 3) {
            // centroid
            Point a = coordinatesArray.get(0);
            Point b = coordinatesArray.get(1);
            Point c = coordinatesArray.get(2);
            int x = (int) ((a.x + b.x + c.x) / 3.0);
            int y = (int) ((a.y + b.y + c.y) / 3.0);
            return new Point(x, y);
        }
        return null;
    }

}
