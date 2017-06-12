package com.filiereticsa.arc.augmentepf.localization;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.interfaces.DestinationSelectedInterface;
import com.filiereticsa.arc.augmentepf.interfaces.HomePageInterface;
import com.filiereticsa.arc.augmentepf.localization.guidage.Guidance;
import com.filiereticsa.arc.augmentepf.localization.guidage.TrajectorySegment;
import com.filiereticsa.arc.augmentepf.models.Place;

import java.util.ArrayList;

/**
 * Created by anthonyprudhomme on 20/10/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */

public class LocalizationFragment
        extends Fragment
        implements GAFrameworkUserTrackerObserver, HomePageInterface, DestinationSelectedInterface {

    public static final float DEFAULT_ZOOM = 0.4f;
    private static final String TAG = "Ici";
    public static HomePageInterface homePageInterface;
    public static DestinationSelectedInterface destinationSelectedInterface;
    private View rootView = null;
    private ImageView currentMapImageView;
    private FloatingActionButton floatingActionButton;
    private UserAndPathView userAndPathView = null;
    private GABeaconMap currentMap;
    private int currentMapHeight = 0;
    private int currentMapWidth = 0;
    private Pair<Integer, Integer> gridDimensions;
    private FrameLayout mapContainer;
    private float mScale = 1f;
    private float effectiveScale = 1f;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector gestureDetector;
    private int numberOfFingerTouchingTheScreen = 0;
    private int screenWidth;
    private int screenHeight;
    private Pair<Integer, Integer> oldPosition;
    private Pair<Integer, Integer> oldUserPosition;
    private boolean isUserMovingTheMap = false;
    private boolean fullScreenModeEnabled = false;
    private boolean gestureEnabled = false;
    private Bitmap mapBitmap;
    private Guidance guidance;
    private int index;

    public boolean isGestureEnabled() {
        return gestureEnabled;
    }

    public GestureDetector getGestureDetector() {
        return gestureDetector;
    }

    public ScaleGestureDetector getScaleDetector() {
        return mScaleDetector;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_localization, container, false);
        setRetainInstance(true);
        homePageInterface = this;
        destinationSelectedInterface = this;
        new GAFrameworkUserTracker(getActivity());
        GAFrameworkUserTracker.sharedTracker().registerObserver(this);
        GAFrameworkUserTracker.sharedTracker().startTrackingUser();
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        RelativeLayout indoorLayout = (RelativeLayout) rootView.findViewById(R.id.indoorLayout);
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
                    if (!isUserMovingTheMap && fullScreenModeEnabled) {
                        floatingActionButton.setVisibility(View.VISIBLE);
                    }
                    isUserMovingTheMap = true;
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
        floatingActionButton.setVisibility(View.GONE);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUserMovingTheMap = false;
                if (currentMap != null) {
                    userMovedToIndexPath(oldUserPosition, 0, 0, "");
                    floatingActionButton.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), R.string.not_localized, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rootView;
    }

    @Override
    public void userMovedToMap(final GABeaconMap map) {
        if (isAdded()) {
            currentMap = map;
            Log.d(TAG, "user moved to map");
            // TODO Uncomment this and give the real path to the map
            //BitmapManager bitmapManager = new BitmapManager();
            //final Bitmap mapBitmap = bitmapManager.loadBitmapFromFile("PATH_TO_MAP");
            if (mapBitmap != null) {
                mapBitmap.recycle();
            }
            mapBitmap = BitmapFactory.decodeResource(getContext().getResources(),
                    map.getImageResId());
            if (mapBitmap != null) {
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
                gridDimensions = currentMap.getMapDimensions();
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
    public void userMovedToIndexPath(Pair<Integer, Integer> position, double heading, double magneticHeading, String direction) {
        oldUserPosition = position;
        if ((userAndPathView == null || userAndPathView.getWidth() == 0 || userAndPathView.getHeight() == 0)
                && currentMapHeight != 0 && currentMapWidth != 0) {
            if (isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userAndPathView = new UserAndPathView(getActivity(), currentMapHeight, currentMapWidth);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        userAndPathView.requestLayout();
                        layoutParams.width = currentMapWidth;
                        layoutParams.height = currentMapHeight;
                        userAndPathView.setLayoutParams(layoutParams);
                        if (userAndPathView.getParent() != null) {
                            ((ViewGroup) userAndPathView.getParent()).removeView(userAndPathView);
                        }
                        if (userAndPathView.getParent() != null) {
                            ((ViewGroup) userAndPathView.getParent()).removeView(userAndPathView);
                        }
                        mapContainer.addView(userAndPathView);
                        userAndPathView.bringToFront();
                        userAndPathView.setScaleType(ImageView.ScaleType.FIT_XY);
                    }
                });
            }
        }
        if (currentMapWidth != 0 && currentMapHeight != 0) {
            if (userAndPathView != null) {
                userAndPathView.dimensionChanged(gridDimensions, currentMapHeight, currentMapWidth);
                final PositionAnimation animation = new PositionAnimation(userAndPathView, position);
                animation.setDuration(500);
                if (isAdded()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userAndPathView.startAnimation(animation);
                        }
                    });
                }
                //debug
                userAndPathView.setHeading(heading);
                userAndPathView.setMagneticHeading(magneticHeading);
                userAndPathView.setDirection(direction);
            }
        }

        if (!isUserMovingTheMap) {
            if (oldPosition == null) {
                oldPosition = new Pair<>(0, 0);
            }
            Pair<Integer, Integer> positionCoordinates = getCoordinatesFromIndexPath(gridDimensions, position);
            final MapAnimation mapAnimation = new MapAnimation(mapContainer, positionCoordinates, oldPosition, mScale, screenWidth, screenHeight);
            mScale = DEFAULT_ZOOM;
            oldPosition = positionCoordinates;
            mapAnimation.setDuration(500);
            if (isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mapContainer.startAnimation(mapAnimation);
                    }
                });
            }
        }
    }

    public Pair<Integer, Integer> getCoordinatesFromIndexPath(Pair<Integer, Integer> gridDimension, Pair<Integer, Integer> position) {
        int nbRow = gridDimension.first;
        int nbCol = gridDimension.second;
        return new Pair<>((int) ((position.second) * (currentMapHeight / nbRow)), (int) ((position.first) * (currentMapWidth / nbCol)));
    }

    @Override
    public void userMovedToIndexPath(Pair<Integer, Integer> indexPath, ArrayList<Pair<Integer, Integer>> candidates) {

    }

    @Override
    public void userChangedDirection(Pair<Integer, Integer> newDirection) {
//        Log.e("user changed direction "+ newDirection.first + " "+ newDirection.second);
//        scrollLayout.setPivotX(userAndPathView.getUserCoordinates().first);
//        scrollLayout.setPivotY(userAndPathView.getUserCoordinates().second);
//        scrollLayout.setRotation((float) (Math.atan2(newDirection.second,newDirection.first)*180/Math.PI));
    }

    @Override
    public void onPathChanged(Pair<ArrayList<Pair<Integer, Integer>>, Integer> path,
                              FloorAccess.FloorAccessType floorAccessType) {
        if (userAndPathView != null) {
            userAndPathView.setCurrentPath(path, floorAccessType);
        }

        if (guidance == null && path != null) {
            guidance = new Guidance(path.first);
        }

        // There is a trajectory with instructions and it's not finished yet
        if (guidance != null && index != Integer.MAX_VALUE) {
            ArrayList<TrajectorySegment> trajectory = guidance.getTrajectory();

            // Get the index in the segment which correspond at the position
            index = guidance.getCurrentSegment(oldUserPosition, index);
            if (index == -1) { // Error with the position
                index = 0;
                if (path != null) { // There is a path defined previously
                    guidance.setPath(path.first);
                } else {
                    index = Integer.MAX_VALUE;
                    Log.d(TAG, "onPathChanged: " + "Congrats fdp!");
                }
            } else if (index == Integer.MAX_VALUE) { // The end of the path
                Log.d(TAG, "onPathChanged: " + "Congrats, you arrive at the destination!");
                // TODO Save the path on the DB, display that it's the end of the path
            } else {
                // TODO Display instructions on the screen
                Log.d(TAG, "onPathChanged: " + trajectory.get(index).getDirectionInstruction());
            }
        }
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

    public Point middlePoint(Point p1, Point p2) {
        return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    @Override
    public void onFullScreenModeChanged(boolean isActive) {
        fullScreenModeEnabled = isActive;
        if (isActive) {
            floatingActionButton.setVisibility(View.VISIBLE);
        } else {
            floatingActionButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestinationSelected(Place place) {
        mapContainer.invalidate();
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent event, MotionEvent event2,
                                final float distanceX, final float distanceY) {
            if (!isUserMovingTheMap && fullScreenModeEnabled) {
                floatingActionButton.setVisibility(View.VISIBLE);
            }
            isUserMovingTheMap = true;
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
    }
}

