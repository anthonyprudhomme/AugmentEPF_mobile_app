package com.filiereticsa.arc.augmentepf.localization;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by anthonyprudhomme on 03/11/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */

class LockableScrollView extends HorizontalScrollView {

    // true if we can scroll (not locked)
    // false if we cannot scroll (locked)
    private boolean scrollable = false;

    public LockableScrollView(Context context) {
        super(context);
    }

    public LockableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollingEnabled(boolean enabled) {
        scrollable = enabled;
    }

    public boolean isScrollable() {
        return scrollable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                if (scrollable) return super.onTouchEvent(ev);
                // only continue to handle the touch event if scrolling enabled
                return scrollable; // mScrollable is always false at this point
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't do anything with intercepted touch events if
        // we are not scrollable
        if (!scrollable) return false;
        else return super.onInterceptTouchEvent(ev);
    }

}