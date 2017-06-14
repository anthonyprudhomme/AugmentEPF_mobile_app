package com.filiereticsa.arc.augmentepf.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.models.Class;

/**
 * Created by ARC© Team for AugmentEPF project on 06/06/2017.
 */

public class CalendarClassView extends android.support.v7.widget.AppCompatButton {

    private static final String TAG = "Ici";
    private static final int STROKE_WIDTH = 10;
    private Paint paint;
    private Path path;
    private double height;
    private double width;
    private double classPercentage;
    private double beginning;
    private RectF rectF;
    private String roomName;
    private boolean mustDisplayText = false;

    public CalendarClassView(Context context,
                             double height,
                             double width,
                             double classPercentage,
                             double beginning,
                             Class currentClass,
                             int currentPartOfClass, double hourDelta) {
        super(context);
        Log.d(TAG, "CalendarClassView() called with: context = [" + context + "], height = [" + height + "], width = [" + width + "], classPercentage = [" + classPercentage + "], beginning = [" + beginning + "], currentClass = [" + currentClass + "], currentPartOfClass = [" + currentPartOfClass + "], hourDelta = [" + hourDelta + "]");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setBackgroundColor(getResources().getColor(R.color.transparent, null));
        } else {
            setBackgroundColor(getResources().getColor(R.color.transparent));
        }
        this.paint = new Paint();
        this.path = new Path();
        this.height = height;
        this.width = width;
        this.classPercentage = classPercentage;
        this.beginning = beginning;
        this.roomName = currentClass.getClassRoomName();
        if (currentPartOfClass == -1) {
            mustDisplayText = true;
        }
        if (hourDelta % 2 == 0) {
            if (currentPartOfClass == hourDelta / 2) {
                mustDisplayText = true;
            }
        }
        if (hourDelta % 2 == 1) {
            if (currentPartOfClass == (hourDelta / 2) + 1) {
                mustDisplayText = true;
            }
        }
        double top = (beginning / 60) * height;
        rectF = new RectF(0, (int) top, (int) width, (int) (top + classPercentage / 100 * height));
    }

    public CalendarClassView(Context context,
                             double height,
                             double width,
                             double classPercentage,
                             double beginning,
                             String currentClassName,
                             int currentPartOfClass, double hourDelta) {
        super(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setBackgroundColor(getResources().getColor(R.color.transparent, null));
        } else {
            setBackgroundColor(getResources().getColor(R.color.transparent));
        }
        this.paint = new Paint();
        this.path = new Path();
        this.height = height;
        this.width = width;
        this.classPercentage = classPercentage;
        this.beginning = beginning;
        this.roomName = currentClassName;
        if (currentPartOfClass == -1) {
            mustDisplayText = true;
        }
        if (hourDelta % 2 == 0) {
            if (currentPartOfClass == hourDelta / 2) {
                mustDisplayText = true;
            }
        }
        if (hourDelta % 2 == 1) {
            if (currentPartOfClass == (((int)hourDelta / 2) )) {
                mustDisplayText = true;
            }
        }
        double top = (beginning / 60) * height;
        rectF = new RectF(0, (int) top, (int) width, (int) (top + classPercentage / 100 * height));
    }

    public CalendarClassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(30);
        paint.setColor(Color.parseColor("#4286f4"));
        canvas.drawRect(rectF, paint);

        if (mustDisplayText) {
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(3);
            paint.setTextSize((float) (height / 2));
            paint.setColor(Color.parseColor("#000000"));
            paint.setTextAlign(Paint.Align.CENTER);
            int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
            canvas.drawText(roomName, (float) (width / 2), yPos, paint);
        }
    }
}
