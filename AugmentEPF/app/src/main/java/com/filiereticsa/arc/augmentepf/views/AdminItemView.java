package com.filiereticsa.arc.augmentepf.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import java.util.ArrayList;

/**
 * Created by Jim Hawk on 6/9/2017.
 */

public class AdminItemView extends android.support.v7.widget.AppCompatImageView {


    private ItemType itemTypes;
    private ArrayList<String> names;
    private int xPos, yPos;
    private Context context;
    private Paint paint;
    private RectF cellDisplay;
    private boolean mustBeHighlighted;

    public AdminItemView(Context context, int cellHeight, int cellWidth, int xPos, int yPos, ItemType itemTypes, ArrayList<String> names) {
        super(context);
        this.context = context;
        this.paint = new Paint();
        this.xPos = xPos;
        this.yPos = yPos;
        this.cellDisplay = new RectF(0, 0, cellWidth, cellHeight);
        this.itemTypes = itemTypes;
        this.names = names;
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
        paint.setStrokeWidth(2);
        switch (itemTypes) {
            case EMPTY:
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.GRAY);
                paint.setAlpha(255);
                canvas.drawRect(cellDisplay, paint);
                break;
            case PATH:
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.GREEN);
                paint.setAlpha(127);
                canvas.drawRect(cellDisplay, paint);
                break;
            case BEACON:
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.RED);
                paint.setAlpha(127);
                canvas.drawRect(cellDisplay, paint);
                break;
            case ROOM:
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.RED);
                paint.setAlpha(127);
                canvas.drawRect(cellDisplay, paint);
                break;
            case POI:
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.RED);
                paint.setAlpha(127);
                canvas.drawRect(cellDisplay, paint);
                break;
            case FLOOR_ACCESS:
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.YELLOW);
                paint.setAlpha(127);
                canvas.drawRect(cellDisplay, paint);
                break;
        }
        if (mustBeHighlighted) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);
            paint.setColor(Color.RED);
            paint.setAlpha(255);
            canvas.drawRect(cellDisplay, paint);
        }
    }

    public void highlight() {
        this.mustBeHighlighted = true;
        invalidate();
    }

    public void unHighlight() {
        this.mustBeHighlighted = false;
        invalidate();
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
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
