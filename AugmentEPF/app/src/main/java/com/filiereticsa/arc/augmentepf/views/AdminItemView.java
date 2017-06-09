package com.filiereticsa.arc.augmentepf.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.constraint.solver.ArrayLinkedVariables;
import android.util.ArrayMap;
import android.util.AttributeSet;

import com.filiereticsa.arc.augmentepf.localization.GABeaconMap;

import java.util.ArrayList;

/**
 * Created by Jim Hawk on 6/9/2017.
 */

public class AdminItemView extends android.support.v7.widget.AppCompatImageView {

    private static final float RADIUS = 15;
    private ArrayList<ItemType> itemTypes;
    private int cellHeight;
    private int cellWidth;
    private Context context;
    private Paint paint;
    private RectF borderRect;
    private RectF innerRect;

    public AdminItemView(Context context, int cellHeight, int cellWidth, ArrayList<ItemType> itemTypes) {
        super(context);
        this.context = context;
        this.paint = new Paint();
        this.cellHeight = cellHeight;
        this.cellWidth = cellWidth;
        this.borderRect = new RectF(0, 0, cellWidth, cellHeight);
        this.innerRect = new RectF(RADIUS, RADIUS, cellWidth - RADIUS, cellHeight - RADIUS);
        this.itemTypes = itemTypes;
    }

    public AdminItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdminItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < itemTypes.size(); i++) {
            ItemType currentItemType = itemTypes.get(i);
            switch (currentItemType) {
                case EMPTY:
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.GRAY);
                    paint.setAlpha(255);
                    canvas.drawRect(borderRect, paint);
                    break;
                case PATH:
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.BLACK);
                    paint.setAlpha(255);
                    canvas.drawRect(borderRect, paint);
                    break;
                case BEACON:
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.BLACK);
                    paint.setAlpha(100);
                    canvas.drawCircle(cellWidth / 2, cellHeight / 2, RADIUS, paint);
                    break;
                case ROOM:
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.GREEN);
                    paint.setAlpha(100);
                    canvas.drawRect(innerRect, paint);
                    break;
                case POI:
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.GREEN);
                    paint.setAlpha(255);
                    canvas.drawRect(borderRect, paint);
                    break;
                case FLOOR_ACCESS:
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.YELLOW);
                    paint.setAlpha(100);
                    canvas.drawRect(innerRect, paint);
                    break;
            }
        }

    }

    public enum ItemType {
        EMPTY,
        PATH,
        BEACON,
        ROOM,
        POI,
        FLOOR_ACCESS
    }

}
