package com.filiereticsa.arc.augmentepf.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;

import com.filiereticsa.arc.augmentepf.R;

/**
 * Created by ARC Team for AugmentEPF project on 11/06/2017.
 */

public class GuidanceView extends android.support.v7.widget.AppCompatImageView {

    public static final int TRIANGLE_SIZE = 400;
    private static final String TAG = "Ici";
    private int screenHeight;
    private int screenWidth;
    private Path path;
    private Paint paint;
    private double currentHeading;
    private String instruction;
    private String closestRoom;
    private String floor;
    private String destination;
    private double targetHeading = Integer.MAX_VALUE;
    private Pair<Integer, Integer> debugTargetHeading;

    public GuidanceView(Context context, int screenHeight, int screenWidth) {
        super(context);
        path = new Path();
        paint = new Paint();
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
    }

    public GuidanceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GuidanceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawUserOrientation(canvas, targetHeading, currentHeading);
        drawNavigationInfo(canvas);
    }

    private void drawUserOrientation(Canvas canvas, double targetHeading, double currentHeading) {
        if (targetHeading != Integer.MAX_VALUE) {
            paint.setColor(Color.parseColor("#DA0813"));
            paint.setStyle(Paint.Style.FILL);

            path.reset();
            int triangleHeight = (int) Math.sqrt((Math.pow(TRIANGLE_SIZE, 2) - Math.pow(TRIANGLE_SIZE / 2, 2)));
            int offsetY = 30;
            Point center = new Point(screenWidth / 2, screenHeight - triangleHeight + offsetY);
            Point leftCorner = new Point(center.x - TRIANGLE_SIZE / 2, center.y);
            Point middleCorner = new Point(center.x, center.y - triangleHeight / 2);
            Point rightCorner = new Point(leftCorner.x + TRIANGLE_SIZE, leftCorner.y);
            Point topCorner = new Point(center.x, (int) (leftCorner.y - triangleHeight * 1.5));
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(rightCorner.x, rightCorner.y);
            path.lineTo(topCorner.x, topCorner.y);
            path.lineTo(leftCorner.x, leftCorner.y);
            path.lineTo(middleCorner.x, middleCorner.y);
            path.lineTo(rightCorner.x, rightCorner.y);

            Matrix mMatrix = new Matrix();
            RectF bounds = new RectF();
            path.computeBounds(bounds, true);
            mMatrix.postRotate((float) -(currentHeading + targetHeading), bounds.centerX(), bounds.centerY());
            path.transform(mMatrix);
            path.close();

            canvas.drawPath(path, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(40);
            paint.setColor(Color.parseColor("#393939"));
//        paint.setColor(Color.parseColor("#DA0813"));
            canvas.drawPath(path, paint);
        }
    }

    private void drawNavigationInfo(Canvas canvas) {
        paint.setColor(Color.parseColor("#FFFFFF"));
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(100);

        float xPos = (float) screenWidth / 30;
        float yOffset = (float) screenHeight / 30;
        float ySeparation = (float) screenHeight / 20;

//        String currentDestination = "";
//        if (destination != null) {
//            currentDestination = getContext().getString(R.string.destination) + destination;
//        }
//
//        String currentClosestRoom = "";
//        if (closestRoom != null) {
//            currentClosestRoom = "Closest room : " + closestRoom;
//        }
//
//        String currentFloor = "";
//        if (floor != null) {
//            currentFloor = "Floor : " + floor;
//        }
//
//        String currentInstruction = "";
//        if (instruction != null) {
//            currentInstruction = "" + instruction;
//        }

        String currentDestination = "";
        currentDestination = "Alpha : " + (int) (currentHeading + targetHeading);

        String currentClosestRoom = "";
        currentClosestRoom = "Current heading : " + (int) currentHeading;

        String currentFloor = "";
        if (debugTargetHeading != null) {
            currentFloor = "coord : " + debugTargetHeading.first + " " + debugTargetHeading.second;
            Log.d(TAG, "drawNavigationInfo: "+currentFloor);
        }

        String currentInstruction = "";
        currentInstruction = "Destination : " + (int) targetHeading;

        canvas.drawText(currentDestination, xPos, yOffset + ySeparation, paint);
        canvas.drawText(currentClosestRoom, xPos, yOffset + ySeparation * 2, paint);
        canvas.drawText(currentFloor, xPos, yOffset + ySeparation * 3, paint);
        canvas.drawText(currentInstruction, xPos, yOffset + ySeparation * 4, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setColor(Color.parseColor("#000000"));

        canvas.drawText(currentDestination, xPos, yOffset + ySeparation, paint);
        canvas.drawText(currentClosestRoom, xPos, yOffset + ySeparation * 2, paint);
        canvas.drawText(currentFloor, xPos, yOffset + ySeparation * 3, paint);
        canvas.drawText(currentInstruction, xPos, yOffset + ySeparation * 4, paint);
    }

    public void setCurrentHeading(double currentHeading) {
        this.currentHeading = currentHeading;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public void setClosestRoom(String closestRoom) {
        this.closestRoom = closestRoom;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setTargetHeading(Pair<Integer, Integer> targetHeading) {
        debugTargetHeading = targetHeading;
        if (debugTargetHeading != null) {
            this.targetHeading = ((Math.atan2(-targetHeading.second, targetHeading.first) * 180) / Math.PI) - 90;
        }
    }
}
