package com.filiereticsa.arc.augmentepf.localization;

import android.util.Pair;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by anthony on 20/05/2017.
 */

public class PositionAnimation extends Animation {

    private UserAndPathView userAndPathView;

    private Pair<Integer, Integer> oldPosition;
    private Pair<Integer, Integer> newPosition;

    public PositionAnimation(UserAndPathView userAndPathView, Pair<Integer, Integer> newPosition) {
        this.oldPosition = userAndPathView.getUserPosition();
        this.newPosition = newPosition;
        this.userAndPathView = userAndPathView;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        userAndPathView.setUserPosition(newPosition);
        Pair<Integer, Integer> oldCoordinates;
        if (oldPosition != null) {
            oldCoordinates = userAndPathView.getCoordinatesFromIndexPath(oldPosition);
        } else {
            oldCoordinates = new Pair<>(0, 0);
        }
        Pair<Integer, Integer> newCoordinates = userAndPathView.getCoordinatesFromIndexPath(newPosition);
        int movementX = (int) (oldCoordinates.first + ((newCoordinates.first - oldCoordinates.first) * interpolatedTime));
        int movementY = (int) (oldCoordinates.second + ((newCoordinates.second - oldCoordinates.second) * interpolatedTime));
        userAndPathView.setUserCoordinates(new Pair<>(movementX, movementY));
        userAndPathView.requestLayout();
    }
}
