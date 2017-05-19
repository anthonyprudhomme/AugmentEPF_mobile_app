package com.filiereticsa.arc.augmentepf.localization;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.ImageView;

/**
 * Created by anthonyprudhomme on 21/10/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */

public class LayoutOverlayImageView extends ImageView {

    private Paint eraser;
    private Bitmap overlay;
    private Pair<Integer, Integer> userPosition;
    private Pair<Integer, Integer> gridDimension;
    private int imageHeight;
    private int imageWidth;
    private Pair<Integer, Integer> userCoordinates = null;
    private int radius = 30;

    public LayoutOverlayImageView(Context context, int height, int width) {
        super(context);
        setBackgroundColor(Color.BLACK);
        setAlpha(0.3f);
        overlay = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        overlay.eraseColor(Color.TRANSPARENT);
        eraser = new Paint();
        eraser.setStyle(Paint.Style.FILL);
        eraser.setColor(Color.TRANSPARENT);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
    }

    public LayoutOverlayImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        if (userCoordinates == null && userPosition != null) {
            userCoordinates = getCoordinatesFromIndexPath(userPosition);
        }
        if (userCoordinates != null) {
            canvas.drawCircle(userCoordinates.first+radius/2, userCoordinates.second+radius/2, radius, eraser);
            canvas.drawBitmap(overlay, 0, 0, null);
        }
    }


    public Pair<Integer, Integer> getCoordinatesFromIndexPath(Pair<Integer, Integer> position) {
        int nbRow = gridDimension.first;
        int nbCol = gridDimension.second;
        return new Pair<>((position.second+1) * (imageHeight / nbRow), (position.first+1) * (imageWidth / nbCol));
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
}
