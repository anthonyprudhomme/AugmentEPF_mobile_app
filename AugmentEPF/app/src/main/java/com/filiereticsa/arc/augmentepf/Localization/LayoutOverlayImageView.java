package com.filiereticsa.arc.augmentepf.Localization;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Pair;

import java.util.ArrayList;

/**
 * Created by anthonyprudhomme on 21/10/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */

public class LayoutOverlayImageView extends android.support.v7.widget.AppCompatImageView {

    private static final String TAG = "Ici";
    private Paint paint;
    private Path path;
    private Pair<Integer, Integer> userPosition;
    private Pair<Integer, Integer> gridDimension;

    private int imageHeight;
    private int imageWidth;
    private static final int STROKE_WIDTH = 10;

    private Pair<Integer, Integer> userCoordinates = null;
    private Pair<Integer, Integer> pathCoordinates = null;
    private double debugHeading = 0;

    private ArrayList<Pair<Integer, Integer>> currentPath;
    private double magneticHeading = 0;
    private String direction;


    //private int radius = 30;

    public LayoutOverlayImageView(Context context, int height, int width) {
        super(context);
        paint = new Paint();
        path = new Path();
    }

    public LayoutOverlayImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (userCoordinates == null && userPosition != null) {
            userCoordinates = getCoordinatesFromIndexPath(userPosition);
        }
        if (userCoordinates != null) {
            int radius = Math.min(imageWidth / gridDimension.first, imageHeight / gridDimension.second) * 2;
            // Draw the path first for the user position to be above the path
            if (currentPath!=null) {
                drawPath(canvas, radius);
            }
            // Draw the user position above the path
            drawUserPosition(canvas, radius);
            // Draw the debug text that shows info that helps debugging
            drawDebugText(canvas);
        }
    }

    public void drawPath(Canvas canvas, int radius) {
        path.reset();
        //Set the paint color to blue
        paint.setColor(Color.parseColor("#4286f4"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
        if (currentPath.size() != 0) {
            path.moveTo(userCoordinates.second, userCoordinates.first);
        }
        for (int i = 0; i < currentPath.size(); i++) {
            pathCoordinates = getCoordinatesFromIndexPath(currentPath.get(i));
            if (i < currentPath.size() - 1) {
                path.lineTo(pathCoordinates.second, pathCoordinates.first);
            }else {
                canvas.drawCircle(pathCoordinates.second, pathCoordinates.first, (int)(radius/1.5), paint);
            }
        }
        canvas.drawPath(path, paint);
    }

    public void drawUserPosition(Canvas canvas,int radius) {
        // Set paint color to red
        paint.setColor(Color.parseColor("#fa232e"));
        paint.setShadowLayer(20, 0, 0, Color.parseColor("#787878"));
        paint.setStyle(Paint.Style.FILL);
        setLayerType(LAYER_TYPE_SOFTWARE, paint);

        canvas.drawCircle(userCoordinates.second, userCoordinates.first, radius, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        // Set paint color to grey
        paint.setColor(Color.parseColor("#e0e0e0"));
        canvas.drawCircle(userCoordinates.second, userCoordinates.first, radius, paint);
    }

    public void drawDebugText(Canvas canvas) {
        //debug
        paint.setColor(Color.parseColor("#fa232e"));
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(50);
        canvas.drawText(/*"x: " + userPosition.first + " y: " + userPosition.second + */
                " ang: " + (int) debugHeading + " mag: " + (int) magneticHeading + " " + direction, 50, 50, paint);
    }


    public Pair<Integer, Integer> getCoordinatesFromIndexPath(Pair<Integer, Integer> position) {
        int nbRow = gridDimension.first;
        int nbCol = gridDimension.second;
        return new Pair<>((position.second) * (imageHeight / nbRow), (position.first) * (imageWidth / nbCol));
    }

    public void dimensionChanged(Pair<Integer, Integer> gridDimension, int imageHeight, int imageWidth) {
        this.gridDimension = gridDimension;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
    }

    public Pair<Integer, Integer> getUserPosition() {
        return this.userPosition;
    }

    public void setUserCoordinates(Pair<Integer, Integer> position) {
        this.userCoordinates = position;
    }

    public void setUserPosition(Pair<Integer, Integer> userPosition) {
        this.userPosition = userPosition;
    }

    public Pair<Integer, Integer> getUserCoordinates() {
        return userCoordinates;
    }

    public void setHeading(double heading) {
        this.debugHeading = heading;
    }

    public void setCurrentPath(Pair<ArrayList<Pair<Integer, Integer>>, Integer> path) {
        if (path!=null) {
            currentPath = path.first;
        }
    }

    public void setMagneticHeading(double magneticHeading) {
        this.magneticHeading = magneticHeading;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
