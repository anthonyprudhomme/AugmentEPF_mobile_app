package com.filiereticsa.arc.augmentepf.localization;

/**
 * Created by ARC Team for AugmentEPF project on 11/06/2017.
 */

import android.util.Pair;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class OrientationAnimation extends Animation {

    private static final String TAG = "Ici";
    private UserOrientationView userOrientationView;

    private Pair<Integer, Integer> oldPosition;
    private Pair<Integer, Integer> newPosition;

    public OrientationAnimation(UserOrientationView userOrientationView, Pair<Integer, Integer> newPosition) {
        this.oldPosition = userOrientationView.getUserCoordinates();
        this.newPosition = newPosition;
        this.userOrientationView = userOrientationView;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        userOrientationView.setUserCoordinates(newPosition);
        Pair<Integer, Integer> oldCoordinates;
        if (oldPosition != null) {
            oldCoordinates = oldPosition;
        } else {
            oldCoordinates = new Pair<>(0, 0);
        }
        Pair<Integer, Integer> newCoordinates = newPosition;
        int movementX = (int) (oldCoordinates.first + ((newCoordinates.first - oldCoordinates.first) * interpolatedTime));
        int movementY = (int) (oldCoordinates.second + ((newCoordinates.second - oldCoordinates.second) * interpolatedTime));
        userOrientationView.setUserCoordinates(new Pair<>(movementX, movementY));
        userOrientationView.requestLayout();
    }
}