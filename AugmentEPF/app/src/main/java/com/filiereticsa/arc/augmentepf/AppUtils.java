package com.filiereticsa.arc.augmentepf;

import android.content.Context;
import android.graphics.Point;
import android.util.Pair;
import android.view.Display;
import android.view.WindowManager;

import com.filiereticsa.arc.augmentepf.localization.GABeaconMapHelper;
import com.filiereticsa.arc.augmentepf.localization.GAFrameworkUserTracker;
import com.filiereticsa.arc.augmentepf.models.Place;
import com.filiereticsa.arc.augmentepf.models.Position;
import com.filiereticsa.arc.augmentepf.models.SpecificAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Harpe-e on 11/06/2017.
 */

public class AppUtils {

    public static int screenHeight;
    public static int screenWidth;

    public static String[] concateneStringsArrays(String[] firstArray, String[] secondArray) {
        int firstArrayLength = firstArray.length;
        int secondArrayLength = secondArray.length;
        String[] concatenatedArray = new String[firstArrayLength + secondArrayLength];
        System.arraycopy(firstArray, 0, concatenatedArray, 0, firstArrayLength);
        System.arraycopy(secondArray, 0, concatenatedArray, firstArrayLength, secondArrayLength);
        return concatenatedArray;
    }

    public static void setScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    public static ArrayList<Place> sortByClosest(ArrayList<Place> listToSort) {
        ArrayList<Pair<Place, Integer>> placeWithDistances = new ArrayList<>();
        if (GAFrameworkUserTracker.sharedTracker() != null
                && GAFrameworkUserTracker.sharedTracker().getCurrentUserLocation() != null) {
            Pair<Integer, Integer> currentPosition
                    = GAFrameworkUserTracker.sharedTracker().getCurrentUserLocation().indexPath;

            if (currentPosition != null) {
                GABeaconMapHelper mapHelper = GAFrameworkUserTracker.sharedTracker().getMapHelper();
                ArrayList<Place> placesToRemove = new ArrayList<>();
                for (int i = 0; i < listToSort.size(); i++) {
                    Place currentPlace = listToSort.get(i);
                    if (currentPlace.getPosition().getFloor() == mapHelper.getMapFloor()) {
                        placesToRemove.add(currentPlace);
                        placeWithDistances.add(
                                new Pair<>(
                                        currentPlace,
                                        mapHelper.pathFrom(currentPosition,
                                                new Pair<>(
                                                        currentPlace.getPosition().getPositionX(),
                                                        currentPlace.getPosition().getPositionY())
                                        ).second));
                    }
                }
                listToSort.removeAll(placesToRemove);
                Collections.sort(placeWithDistances, new Comparator<Pair<Place, Integer>>() {
                    public int compare(Pair<Place, Integer> o1, Pair<Place, Integer> o2) {
                        if (o1.second < o2.second) {
                            return -1;
                        } else {
                            if (o1.second > o2.second) {
                                return 0;
                            } else {
                                return 0;
                            }
                        }
                    }
                });
                for (int i = placeWithDistances.size() - 1; i >= 0; i--) {
                    listToSort.add(0, placeWithDistances.get(i).first);
                }
            }
        }
        return listToSort;
    }

    public static ArrayList<Place> sortByClosest(ArrayList<Place> listToSort, Position position) {
        ArrayList<Pair<Place, Integer>> placeWithDistances = new ArrayList<>();
        if (GAFrameworkUserTracker.sharedTracker() != null
                && GAFrameworkUserTracker.sharedTracker().getCurrentUserLocation() != null) {
            Pair<Integer, Integer> currentPosition
                    = GAFrameworkUserTracker.sharedTracker().getCurrentUserLocation().indexPath;

            if (currentPosition != null) {
                GABeaconMapHelper mapHelper = GAFrameworkUserTracker.sharedTracker().getMapHelper();
                ArrayList<Place> placesToRemove = new ArrayList<>();
                for (int i = 0; i < listToSort.size(); i++) {
                    Place currentPlace = listToSort.get(i);
                    if (currentPlace.getPosition().getFloor() == mapHelper.getMapFloor()) {
                        placesToRemove.add(currentPlace);
                        placeWithDistances.add(
                                new Pair<>(
                                        currentPlace,
                                        mapHelper.pathFrom(currentPosition,
                                                new Pair<>(
                                                        currentPlace.getPosition().getPositionX(),
                                                        currentPlace.getPosition().getPositionY())
                                        ).second));
                    }
                }
                listToSort.removeAll(placesToRemove);
                Collections.sort(placeWithDistances, new Comparator<Pair<Place, Integer>>() {
                    public int compare(Pair<Place, Integer> o1, Pair<Place, Integer> o2) {
                        if (o1.second < o2.second) {
                            return -1;
                        } else {
                            if (o1.second > o2.second) {
                                return 0;
                            } else {
                                return 0;
                            }
                        }
                    }
                });
                for (int i = placeWithDistances.size() - 1; i >= 0; i--) {
                    listToSort.add(0, placeWithDistances.get(i).first);
                }
            }
        }
        return listToSort;
    }

    public static SpecificAttribute getCurrentSpecificAttribute(String specificAttribute) {
        SpecificAttribute currentSpecificAttribute;
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

    public static boolean mustTakeElevator(SpecificAttribute specificAttribute) {
        switch (specificAttribute) {

            case NONE:
            case SOUND_GUIDANCE:
                return false;

            case ELEVATOR:
            case BOTH:
                return true;

            default:
                return false;
        }
    }
}
