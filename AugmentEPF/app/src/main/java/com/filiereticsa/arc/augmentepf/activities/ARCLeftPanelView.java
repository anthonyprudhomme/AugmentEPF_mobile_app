package com.filiereticsa.arc.augmentepf.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Cécile on 15/05/2017.
 */

public class ARCLeftPanelView extends View {
    public ARCLeftPanelView(Context context) {
        super(context);
    }

    public ARCLeftPanelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ARCLeftPanelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ARCLeftPanelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
