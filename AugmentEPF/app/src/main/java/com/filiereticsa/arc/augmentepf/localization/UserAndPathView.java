package com.filiereticsa.arc.augmentepf.localization;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Pair;

import com.filiereticsa.arc.augmentepf.R;

import java.util.ArrayList;

/**
 * Created by anthonyprudhomme on 21/10/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */

public class UserAndPathView extends android.support.v7.widget.AppCompatImageView {

    private static final String TAG = "Ici";
    private static final int STROKE_WIDTH = 10;
    public static int radius;
    private Paint paint;
    private Path path;
    private Pair<Integer, Integer> userPosition;
    private Pair<Integer, Integer> gridDimension;
    private int imageHeight;
    private int imageWidth;
    private Pair<Integer, Integer> userCoordinates = null;
    private Pair<Integer, Integer> pathCoordinates = null;
    private double heading = 0;
    private ArrayList<Pair<Integer, Integer>> currentPath;
    private double magneticHeading = 0;
    private String direction;
    private FloorAccess.FloorAccessType currentFloorAccesType;
    private Bitmap elevatorBitmap;
    private Bitmap stairBitmap;

    public UserAndPathView(Context context, int height, int width) {
        super(context);
        paint = new Paint();
        path = new Path();
    }

    public UserAndPathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (userCoordinates == null && userPosition != null) {
            userCoordinates = getCoordinatesFromIndexPath(userPosition);
        }
        if (userCoordinates != null) {
            radius = Math.min(imageWidth / gridDimension.first, imageHeight / gridDimension.second) * 2;
            // Draw the path first for the user position to be above the path
            if (currentPath != null) {
                drawPath(canvas, radius);
            }
            // Draw the user position above the path
            drawUserPosition(canvas, radius);
            //drawUserOrientation(canvas, radius, heading);
            // Draw the debug text that shows info that helps debugging
            //drawDebugText(canvas);
        }
    }

    public void drawPath(Canvas canvas, int radius) {
        path.reset();
        //Set the paint color to blue
        paint.setColor(Color.parseColor("#4286f4"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setShadowLayer(5, 0, 0, Color.parseColor("#4286f4"));
        if (currentPath != null) {
            if (currentPath.size() != 0) {
                path.moveTo(userCoordinates.second, userCoordinates.first);
            }
            for (int i = 0; i < currentPath.size(); i++) {
                pathCoordinates = getCoordinatesFromIndexPath(currentPath.get(i));
                if (i < currentPath.size() - 1) {
                    path.lineTo(pathCoordinates.second, pathCoordinates.first);
                } else {
                    if (currentFloorAccesType == null) {
                        canvas.drawCircle(pathCoordinates.second, pathCoordinates.first, (int) (radius / 1.5), paint);
                    } else {
                        if (currentFloorAccesType == FloorAccess.FloorAccessType.ELEVATOR) {
                            drawIcon(paint, R.drawable.elevator_icon, pathCoordinates, radius, canvas);
                        } else {
                            if (currentFloorAccesType == FloorAccess.FloorAccessType.STAIRS) {
                                drawIcon(paint, R.drawable.stair_icon, pathCoordinates, radius, canvas);
                            }
                        }
                    }

                }
            }
            canvas.drawPath(path, paint);
        }
    }

    private void drawIcon(Paint paint, int imageResId, Pair<Integer, Integer> pathCoordinates, int radius, Canvas canvas) {
        Bitmap iconBitmap = null;
        switch (imageResId) {
            case R.drawable.stair_icon:
                if (stairBitmap == null) {
                    stairBitmap = BitmapFactory.decodeResource(getResources(), imageResId);
                }
                iconBitmap = stairBitmap;
                break;

            case R.drawable.elevator_icon:
                if (elevatorBitmap == null) {
                    elevatorBitmap = BitmapFactory.decodeResource(getResources(), imageResId);
                }
                iconBitmap = elevatorBitmap;
                break;
        }

        ColorFilter filter;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            filter = new PorterDuffColorFilter(getContext().getColor(R.color.blue), PorterDuff.Mode.SRC_IN);
        } else {
            filter = new PorterDuffColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_IN);
        }
        paint.setColorFilter(filter);
        RectF rectF = new RectF(pathCoordinates.second - radius, pathCoordinates.first - radius,
                pathCoordinates.second + radius, pathCoordinates.first + radius);
        canvas.drawBitmap(iconBitmap, null, rectF, paint);
        paint.setColorFilter(null);
    }

    public void drawUserPosition(Canvas canvas, int radius) {
        // Set paint color to red
        paint.setColor(Color.parseColor("#fa232e"));
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(30);
        canvas.drawCircle(userCoordinates.second, userCoordinates.first, radius * 4, paint);
        paint.setAlpha(255);
        paint.setColor(Color.parseColor("#fa232e"));
        paint.setShadowLayer(50, 0, 0, Color.parseColor("#fa232e"));
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
                " ang: " + (int) heading + " mag: " + (int) magneticHeading + " " + direction, 50, 50, paint);
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

    public void setUserPosition(Pair<Integer, Integer> userPosition) {
        this.userPosition = userPosition;
    }

    public Pair<Integer, Integer> getUserCoordinates() {
        return userCoordinates;
    }

    public void setUserCoordinates(Pair<Integer, Integer> position) {
        this.userCoordinates = position;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public void setCurrentPath(Pair<ArrayList<Pair<Integer, Integer>>, Integer> path, FloorAccess.FloorAccessType floorAccessType) {
        if (path != null) {
            currentPath = path.first;
            currentFloorAccesType = floorAccessType;
        } else {
            currentPath = null;
        }
    }

    public void setMagneticHeading(double magneticHeading) {
        this.magneticHeading = magneticHeading;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
