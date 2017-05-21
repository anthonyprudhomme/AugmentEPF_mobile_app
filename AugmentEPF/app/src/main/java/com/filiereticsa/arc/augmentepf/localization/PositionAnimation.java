package com.filiereticsa.arc.augmentepf.localization;

import android.util.Pair;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by anthony on 20/05/2017.
 */

public class PositionAnimation extends Animation {

    private LayoutOverlayImageView layoutOverlayImageView;

    private Pair<Integer, Integer> oldPosition;
    private Pair<Integer, Integer> newPosition;

    public PositionAnimation(LayoutOverlayImageView layoutOverlayImageView, Pair<Integer, Integer> newPosition) {
        this.oldPosition = layoutOverlayImageView.getUserPosition();
        this.newPosition = newPosition;
        this.layoutOverlayImageView = layoutOverlayImageView;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        layoutOverlayImageView.setUserPosition(newPosition);
        Pair<Integer, Integer> oldCoordinates;
        if (oldPosition != null) {
            oldCoordinates = layoutOverlayImageView.getCoordinatesFromIndexPath(oldPosition);
        } else {
            oldCoordinates = new Pair<>(0, 0);
        }
        Pair<Integer, Integer> newCoordinates = layoutOverlayImageView.getCoordinatesFromIndexPath(newPosition);
        int movementX = (int) (oldCoordinates.first + ((newCoordinates.first - oldCoordinates.first) * interpolatedTime));
        int movementY = (int) (oldCoordinates.second + ((newCoordinates.second - oldCoordinates.second) * interpolatedTime));
        layoutOverlayImageView.setUserCoordinates(new Pair<>(movementX, movementY));
        layoutOverlayImageView.requestLayout();
    }
}
