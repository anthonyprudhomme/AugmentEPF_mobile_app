package com.filiereticsa.arc.augmentepf.localization;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.filiereticsa.arc.augmentepf.R;

import java.util.ArrayList;

/**
 * Created by anthonyprudhomme on 20/10/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */

public class LocalizationFragment extends Fragment implements GAFrameworkUserTrackerObserver {

    private static final String TAG = "Ici";
    private View rootView = null;
    private RelativeLayout indoorLayout;
    private final int SHOW_INDOOR_MAP = 0;
    private final int SHOW_OUTDOOR_MAP = 1;
    private ImageView currentMapImageView;
    private LayoutOverlayImageView layoutOverlay = null;
    private GABeaconMap currentMap;
    private int currentMapHeight = 0;
    private int currentMapWidth = 0;
    private int currentMapState = SHOW_OUTDOOR_MAP;
    private Pair<Integer, Integer> gridDimensions;
    private FrameLayout mapContainer;
    private float mScale = 1f;
    private float effectiveScale = 1f;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector gestureDetector;
    private int numberOfFingerTouchingTheScreen = 0;
    private int screenWidth;
    private int screenHeight;

    public boolean isGestureEnabled() {
        return gestureEnabled;
    }

    public GestureDetector getGestureDetector() {
        return gestureDetector;
    }

    public ScaleGestureDetector getmScaleDetector() {
        return mScaleDetector;
    }


    private boolean gestureEnabled = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_localization, container, false);
        }
        setRetainInstance(true);
        new GAFrameworkUserTracker(getActivity());
        GAFrameworkUserTracker.sharedTracker().registerObserver(this);
        GAFrameworkUserTracker.sharedTracker().startTrackingUser();
        indoorLayout = (RelativeLayout) rootView.findViewById(R.id.indoorLayout);
        indoorLayout.setVisibility(View.VISIBLE);
        currentMapImageView = (ImageView) rootView.findViewById(R.id.currentMap);
        mapContainer = (FrameLayout) rootView.findViewById(R.id.mapContainer);
        mapContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getPointerCount() == 2) {
                    Point finger1 = new Point((int) event.getX(0), (int) event.getY(0));
                    Point finger2 = new Point((int) event.getX(1), (int) event.getY(1));
                    Point middle = middlePoint(finger1, finger2);
                    if (event.getAction() != 3) {
                        effectiveScale = mScale;
                        mapContainer.setTranslationX(mapContainer.getTranslationX() + (mapContainer.getPivotX() - middle.x) * (1 - mapContainer.getScaleX()));
                        mapContainer.setTranslationY(mapContainer.getTranslationY() + (mapContainer.getPivotY() - middle.y) * (1 - mapContainer.getScaleY()));
                        mapContainer.setPivotX(middle.x);
                        mapContainer.setPivotY(middle.y);
                        mapContainer.setScaleX(1 / effectiveScale);
                        mapContainer.setScaleY(1 / effectiveScale);
                    }
                }
                return true;
            }
        });
        gestureDetector = new GestureDetector(getContext(), new GestureListener());
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (numberOfFingerTouchingTheScreen == 2) {
                    float scale = 1 - detector.getScaleFactor();
                    mScale += scale;

                    if (mScale < 0.1f) // Minimum scale condition:
                        mScale = 0.1f;

                    if (mScale > 1f) // Maximum scale condition:
                        mScale = 1f;
                }
                return true;
            }
        });
        return rootView;
    }

    @Override
    public void userMovedToMap(final GABeaconMap map) {
        if (isAdded()) {
            currentMap = map;
            Log.d(TAG,"user moved to map");
            // TODO Uncomment this and give the real path to the map
            //BitmapManager bitmapManager = new BitmapManager();
            //final Bitmap mapBitmap = bitmapManager.loadBitmapFromFile("PATH_TO_MAP");
            final Bitmap mapBitmap = BitmapFactory.decodeResource(getContext().getResources(),
                    R.drawable.plan_etage1);
            if(mapBitmap!=null) {
                int height = mapBitmap.getHeight();
                int width = mapBitmap.getWidth();
                double pictureRatio = ((float) height) / ((float) width);
                WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                screenWidth = size.x;
                screenHeight = size.y;
                final FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                float imageHeight;
                float imageWidth;
                float heightRatio = height / screenHeight;
                float widthRatio = width / screenWidth;
                if (heightRatio > widthRatio) {
                    imageHeight = screenHeight;
                    imageWidth = (float) (screenHeight / pictureRatio);
                } else {
                    imageHeight = (float) ((screenWidth * pictureRatio));
                    imageWidth = (screenWidth);
                }
                imageParams.height = (int) imageHeight;
                imageParams.width = (int) imageWidth;
                currentMapHeight = (int) imageHeight;
                currentMapWidth = (int) imageWidth;
                if (isAdded()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currentMapImageView.setLayoutParams(imageParams);
                            currentMapImageView.setImageBitmap(mapBitmap);
                            gestureEnabled = true;
                        }
                    });
                }
            }
        }
    }

    @Override
    public void userMovedToIndexPath(Pair<Integer, Integer> position) {
        Log.d(TAG,"user moved" + position);
        if ((layoutOverlay == null || layoutOverlay.getWidth() == 0 || layoutOverlay.getHeight() == 0)
                && currentMapHeight != 0 && currentMapWidth != 0) {
            if (isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layoutOverlay = new LayoutOverlayImageView(getActivity(), currentMapHeight, currentMapWidth);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutOverlay.requestLayout();
                        layoutParams.width = currentMapWidth;
                        layoutParams.height = currentMapHeight;
                        layoutOverlay.setLayoutParams(layoutParams);
                        mapContainer.addView(layoutOverlay);
                        layoutOverlay.bringToFront();
                        layoutOverlay.setScaleType(ImageView.ScaleType.FIT_XY);
                    }
                });
            }
        }
        if (currentMapWidth != 0 && currentMapHeight != 0) {
            if (gridDimensions == null) {
                gridDimensions = currentMap.getDebugMapDimensions();
            }
            if (layoutOverlay != null) {
                layoutOverlay.dimensionChanged(gridDimensions, currentMapHeight, currentMapWidth);
                PositionAnimation animation = new PositionAnimation(layoutOverlay, position);
                animation.setDuration(1000);
                layoutOverlay.startAnimation(animation);
            }
        }
    }

    @Override
    public void userMovedToIndexPath(Pair<Integer, Integer> indexPath, ArrayList<Pair<Integer, Integer>> candidates) {

    }

    @Override
    public void userChangedDirection(Pair<Integer, Integer> newDirection) {
//        Log.e("user changed direction "+ newDirection.first + " "+ newDirection.second);
//        scrollLayout.setPivotX(layoutOverlay.getUserCoordinates().first);
//        scrollLayout.setPivotY(layoutOverlay.getUserCoordinates().second);
//        scrollLayout.setRotation((float) (Math.atan2(newDirection.second,newDirection.first)*180/Math.PI));
    }

    @Override
    public void onResume() {
        super.onResume();
        GAFrameworkUserTracker.sharedTracker().setCurrentMap(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        gestureEnabled = false;
    }

    public void setPointerCount(int pointerCount) {
        this.numberOfFingerTouchingTheScreen = pointerCount;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent event, MotionEvent event2,
                                final float distanceX, final float distanceY) {
            Log.d(TAG, "onScroll: ");
            if (numberOfFingerTouchingTheScreen == 1) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mapContainer.getLayoutParams();
                layoutParams.leftMargin = (int) (layoutParams.leftMargin - distanceX);
                layoutParams.rightMargin = (int) (layoutParams.leftMargin - currentMapWidth * effectiveScale);
                layoutParams.topMargin = (int) (layoutParams.topMargin - distanceY);
                layoutParams.bottomMargin = (int) (layoutParams.topMargin - currentMapHeight * effectiveScale);
                mapContainer.setLayoutParams(layoutParams);
            }

            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mScale = 1;
            effectiveScale = 1;
            mapContainer.setPivotX(0);
            mapContainer.setPivotY(0);
            mapContainer.setScaleX(1 / effectiveScale);
            mapContainer.setScaleY(1 / effectiveScale);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mapContainer.getLayoutParams();
            layoutParams.leftMargin = 0;
            layoutParams.topMargin = 0;
            layoutParams.rightMargin = -currentMapWidth;
            layoutParams.bottomMargin = -currentMapHeight;
            mapContainer.setLayoutParams(layoutParams);
            rootView.findViewById(R.id.horizontalScrollViewLayout).setScrollX(0);
            rootView.findViewById(R.id.scrollViewLayout).setScrollY(0);
            mapContainer.setX(0);
            mapContainer.setY(0);
            int[] map = new int[2];
            mapContainer.getLocationInWindow(map);
            Log.d(TAG,"position: " + mapContainer.getX() + " " + ((FrameLayout.LayoutParams) mapContainer.getLayoutParams()).leftMargin
                    + " " + map[0] + " " + map[1]);
            mapContainer.postInvalidate();
            return true;
        }
    }

    public Point middlePoint(Point p1, Point p2) {
        return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }
}

