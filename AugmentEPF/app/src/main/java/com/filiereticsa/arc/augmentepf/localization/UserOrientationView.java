package com.filiereticsa.arc.augmentepf.localization;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Pair;

import com.filiereticsa.arc.augmentepf.R;

/**
 * Created by Harpe-e on 11/06/2017.
 */

public class UserOrientationView extends android.support.v7.widget.AppCompatImageView {


    private static final String TAG = "Ici";
    private double heading;
    private Paint paint;
    private Path path;
    private Pair<Integer, Integer> userCoordinates = null;

    public UserOrientationView(Context context) {
        super(context);
        paint = new Paint();
        path = new Path();
    }

    public UserOrientationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserOrientationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (userCoordinates != null) {
            drawUserOrientation(canvas, UserAndPathView.radius, heading);
        }
    }

    private void drawUserOrientation(Canvas canvas, int radius, double heading) {
        heading -= 90;
        paint.setColor(Color.parseColor("#4286f4"));
        paint.setStyle(Paint.Style.FILL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            paint.setShadowLayer(0, 0, 0, getResources().getColor(R.color.transparent, null));
        } else {
            paint.setShadowLayer(0, 0, 0, getResources().getColor(R.color.transparent));
        }

        path.reset();
        path.setFillType(Path.FillType.EVEN_ODD);
        int triangleSize = 20;
        int triangleHeight = (int) Math.sqrt((Math.pow(triangleSize, 2) - Math.pow(triangleSize / 2, 2)));
        Point center = new Point(
                (int) (userCoordinates.second + Math.cos((heading / 180) * Math.PI) * radius * 1.5),
                (int) (userCoordinates.first + Math.sin((heading / 180) * Math.PI) * radius * 1.5));
        Point a = new Point(center.x - triangleHeight / 2, center.y - triangleSize / 2);
        Point b = new Point(a.x, triangleSize + a.y);
        Point c = new Point((int) ((Math.cos((60 / 180) * Math.PI) * triangleSize) + a.x), triangleSize / 2 + a.y);
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        path.lineTo(b.x, b.y);

        Matrix mMatrix = new Matrix();
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        mMatrix.postRotate((float) heading, bounds.centerX(), bounds.centerY());
        path.transform(mMatrix);
        path.close();

        canvas.drawPath(path, paint);
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public Pair<Integer, Integer> getUserCoordinates() {
        return userCoordinates;
    }

    public void setUserCoordinates(Pair<Integer, Integer> userCoordinates) {
        this.userCoordinates = userCoordinates;
    }
}
