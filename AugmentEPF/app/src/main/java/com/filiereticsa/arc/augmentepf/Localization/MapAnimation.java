package com.filiereticsa.arc.augmentepf.localization;

import android.util.Pair;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

/**
 * Created by anthonyprudhomme on 24/10/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */

public class MapAnimation extends Animation {

    private FrameLayout mapContainer;

    private Pair<Integer, Integer> oldPosition;
    private Pair<Integer, Integer> newPosition;
    private float oldAngle;
    private float newAngle = LocalizationFragment.DEFAULT_ZOOM;
    private int screenWidth;
    private int screenHeight;

    public MapAnimation(FrameLayout mapContainer, Pair<Integer, Integer> newPosition, Pair<Integer, Integer> oldPosition, float oldAngle, int screenWidth, int screenHeight) {
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.mapContainer = mapContainer;
        this.oldAngle = oldAngle;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        int movementY = (int) (oldPosition.first + ((newPosition.first - oldPosition.first) * interpolatedTime));
        int movementX = (int) (oldPosition.second + ((newPosition.second - oldPosition.second) * interpolatedTime));
        float angleMovement = (oldAngle + ((newAngle - oldAngle) * interpolatedTime));
        final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mapContainer.getLayoutParams();
        mapContainer.setPivotX(movementX);
        mapContainer.setPivotY(movementY);
        mapContainer.setScaleX(1 / angleMovement);
        mapContainer.setScaleY(1 / angleMovement);
        layoutParams.leftMargin = screenWidth / 2 - movementX;
        layoutParams.topMargin = screenHeight / 2 - movementY;
        mapContainer.setLayoutParams(layoutParams);
        mapContainer.requestLayout();
    }
}
