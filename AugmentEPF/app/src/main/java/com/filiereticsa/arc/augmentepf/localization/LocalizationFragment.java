package com.filiereticsa.arc.augmentepf.localization;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.filiereticsa.arc.augmentepf.AppUtils;
import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.activities.HomePageActivity;
import com.filiereticsa.arc.augmentepf.interfaces.DestinationSelectedInterface;
import com.filiereticsa.arc.augmentepf.interfaces.HomePageInterface;
import com.filiereticsa.arc.augmentepf.localization.guidage.Guidance;
import com.filiereticsa.arc.augmentepf.localization.guidage.TrajectorySegment;
import com.filiereticsa.arc.augmentepf.models.CustomSnackBar;
import com.filiereticsa.arc.augmentepf.models.Place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by anthonyprudhomme on 20/10/16.
 * Copyright © 2016 Granite Apps. All rights reserved.
 */

public class LocalizationFragment
        extends Fragment
        implements
        GAFrameworkUserTrackerObserver,
        HomePageInterface,
        DestinationSelectedInterface {

    public static final float DEFAULT_ZOOM = 0.4f;
    private static final String TAG = "Ici";
    public static HomePageInterface homePageInterface;
    public static DestinationSelectedInterface destinationSelectedInterface;
    private View rootView = null;
    private ImageView currentMapImageView;
    private FloatingActionButton floatingActionButton;
    private UserAndPathView userAndPathView = null;
    private UserOrientationView userOrientationView = null;
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
    private Pair<Integer, Integer> oldPosition;
    private Pair<Integer, Integer> oldUserPosition;
    private boolean isUserMovingTheMap = false;
    private boolean fullScreenModeEnabled = false;
    private boolean gestureEnabled = false;
    private Bitmap mapBitmap;
    private Guidance guidance;
    private int index;
    private boolean isInAdminMode = false;
    private CustomSnackBar customSnackBar;

    private TextToSpeech textToSpeech;

    private HashMap<Integer, Bitmap> mapDict = new HashMap<>();

    public boolean isGestureEnabled() {
        return gestureEnabled;
    }

    public GestureDetector getGestureDetector() {
        return gestureDetector;
    }

    public ScaleGestureDetector getScaleDetector() {
        return mScaleDetector;
    }

    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_localization, container, false);
        setRetainInstance(true);
        homePageInterface = this;
        destinationSelectedInterface = this;
        GAFrameworkUserTracker.sharedTracker().registerObserver(this);
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
                        mapContainer.setTranslationX(
                                mapContainer.getTranslationX()
                                        + (mapContainer.getPivotX() - middle.x)
                                        * (1 - mapContainer.getScaleX()));
                        mapContainer.setTranslationY(
                                mapContainer.getTranslationY()
                                        + (mapContainer.getPivotY() - middle.y)
                                        * (1 - mapContainer.getScaleY()));
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
        mScaleDetector = new ScaleGestureDetector(getContext(),
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {

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
                    Toast.makeText(getContext(),
                            R.string.not_localized,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Init Text to speech

        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    // Set the language
                    Locale locale;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        locale = getContext().getResources().getConfiguration().getLocales().get(0);
                    } else {
                        locale = getContext().getResources().getConfiguration().locale;
                    }
                    textToSpeech.setLanguage(locale);
                    // Set the speech rate
                    textToSpeech.setSpeechRate(1.0f);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Close the Text to Speech Library
        if (textToSpeech != null) {

            textToSpeech.stop();
            textToSpeech.shutdown();
            Log.d(TAG, "TTS Destroyed");
        }
    }

    @Override
    public void userMovedToMap(final GABeaconMap map) {
        if (!isInAdminMode) {
            if (isAdded()) {
                currentMap = map;
                Log.d(TAG, "user moved to map");
                if (currentMap != null) {
                    if (!mapDict.containsKey(currentMap.getId())) {
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        if (mapBitmap != null) {
                            mapBitmap.recycle();
                        }
                        mapBitmap = BitmapFactory.decodeResource(getContext().getResources(),
                                currentMap.getImageResId(), options);
                    } else {
                        mapBitmap = mapDict.get(currentMap.getId());
                    }
                    if (mapBitmap != null) {
                        int height = mapBitmap.getHeight();
                        int width = mapBitmap.getWidth();
                        double pictureRatio = ((float) height) / ((float) width);
                        final FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        float imageHeight;
                        float imageWidth;
                        float heightRatio = height / AppUtils.screenHeight;
                        float widthRatio = width / AppUtils.screenWidth;
                        if (heightRatio > widthRatio) {
                            imageHeight = AppUtils.screenHeight;
                            imageWidth = (float) (AppUtils.screenHeight / pictureRatio);
                        } else {
                            imageHeight = (float) ((AppUtils.screenWidth * pictureRatio));
                            imageWidth = (AppUtils.screenWidth);
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
        }
    }

    @Override
    public void userMovedToIndexPath(
            Pair<Integer, Integer> position,
            double heading,
            double magneticHeading,
            String direction) {

        if (!isInAdminMode) {
            oldUserPosition = position;
            if ((userAndPathView == null
                    || userAndPathView.getWidth() == 0
                    || userAndPathView.getHeight() == 0)
                    && currentMapHeight != 0
                    && currentMapWidth != 0) {
                if (isAdded()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            userAndPathView = new UserAndPathView(
                                    getContext(),
                                    currentMapHeight,
                                    currentMapWidth);
                            LinearLayout.LayoutParams userAndPathLayoutParams
                                    = new LinearLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            userAndPathView.requestLayout();
                            userAndPathLayoutParams.width = currentMapWidth;
                            userAndPathLayoutParams.height = currentMapHeight;
                            userAndPathView.setLayoutParams(userAndPathLayoutParams);
                            if (userAndPathView.getParent() != null) {
                                ((ViewGroup) userAndPathView
                                        .getParent())
                                        .removeView(userAndPathView);
                            }
                            if (userAndPathView.getParent() != null) {
                                ((ViewGroup) userAndPathView
                                        .getParent())
                                        .removeView(userAndPathView);
                            }
                            mapContainer.addView(userAndPathView);
                            userAndPathView.bringToFront();
                            userAndPathView.setScaleType(ImageView.ScaleType.FIT_XY);

                            userOrientationView = new UserOrientationView(getContext());
                            LinearLayout.LayoutParams userOrientationLayoutParams
                                    = new LinearLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            userOrientationView.requestLayout();
                            userOrientationLayoutParams.width = currentMapWidth;
                            userOrientationLayoutParams.height = currentMapHeight;
                            userOrientationView.setLayoutParams(userOrientationLayoutParams);
                            if (userOrientationView.getParent() != null) {
                                ((ViewGroup) userOrientationView
                                        .getParent())
                                        .removeView(userOrientationView);
                            }
                            if (userOrientationView.getParent() != null) {
                                ((ViewGroup) userOrientationView
                                        .getParent())
                                        .removeView(userOrientationView);
                            }
                            mapContainer.addView(userOrientationView);
                            userOrientationView.bringToFront();
                            userOrientationView.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                    });
                }
            }
            if (currentMapWidth != 0 && currentMapHeight != 0) {
                if (userAndPathView != null) {
                    userAndPathView.dimensionChanged(gridDimensions,
                            currentMapHeight,
                            currentMapWidth);
                    final PositionAnimation userPositionAnimation =
                            new PositionAnimation(userAndPathView, position);
                    userPositionAnimation.setDuration(500);
                    if (isAdded()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                userAndPathView.startAnimation(userPositionAnimation);
                            }
                        });
                    }
                    //debug
                    userAndPathView.setHeading(heading);
                    userAndPathView.setMagneticHeading(magneticHeading);
                    userAndPathView.setDirection(direction);

                    if (userOrientationView != null) {
                        final OrientationAnimation userOrientationAnimation =
                                new OrientationAnimation(
                                        userOrientationView,
                                        userAndPathView.getCoordinatesFromIndexPath(position));
                        userOrientationAnimation.setDuration(500);
                        if (isAdded()) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    userOrientationView.startAnimation(userOrientationAnimation);
                                }
                            });
                        }
                    }
                }
            }

            if (!isUserMovingTheMap) {
                if (oldPosition == null) {
                    oldPosition = new Pair<>(0, 0);
                }
                Pair<Integer, Integer> positionCoordinates =
                        getCoordinatesFromIndexPath(gridDimensions, position);
                final MapAnimation mapAnimation = new MapAnimation(
                        mapContainer,
                        positionCoordinates,
                        oldPosition,
                        mScale,
                        AppUtils.screenWidth,
                        AppUtils.screenHeight);
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
    }

    public Pair<Integer, Integer> getCoordinatesFromIndexPath(
            Pair<Integer, Integer> gridDimension,
            Pair<Integer, Integer> position) {
        if (gridDimension != null) {
            int nbRow = gridDimension.first;
            int nbCol = gridDimension.second;
            return new Pair<>(
                    ((position.second) * (currentMapHeight / nbRow)),
                    ((position.first) * (currentMapWidth / nbCol)));
        }
        return null;
    }

    @Override
    public void userMovedToIndexPath(Pair<Integer, Integer> indexPath,
                                     ArrayList<Pair<Integer, Integer>> candidates) {

    }

    @Override
    public void userChangedDirection(Pair<Integer, Integer> newDirection) {
    }

    @Override
    public void onPathChanged(Pair<ArrayList<Pair<Integer, Integer>>, Integer> path,
                              FloorAccess.FloorAccessType floorAccessType) {

        if (userAndPathView != null) {
            userAndPathView.setCurrentPath(path, floorAccessType);
        }

        if (guidance == null && path != null) {
            // Launching the snackbar for guidance
            customSnackBar = CustomSnackBar.make((ViewGroup) HomePageActivity.rootView, CustomSnackBar.LENGTH_INDEFINITE);
            customSnackBar.show();

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
                // Dismiss guidance snackbar at the end of the path
                customSnackBar.dismiss();
            } else {
                customSnackBar.setText(trajectory.get(index).getDirectionInstruction());

                String toSpeak = trajectory.get(index).getDirectionInstruction();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
                Log.d(TAG, "onPathChanged: " + trajectory.get(index).getDirectionInstruction());
            }
        }
    }

    @Override
    public void onOrientationChange(double currentHeading) {
        if (!isInAdminMode) {
            if (userOrientationView != null) {
                userOrientationView.setHeading(currentHeading);
                userOrientationView.invalidate();
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
        if (!isInAdminMode) {
            mapContainer.invalidate();
        }
    }

    public void setInAdminMode(boolean inAdminMode) {
        isInAdminMode = inAdminMode;
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
                FrameLayout.LayoutParams layoutParams =
                        (FrameLayout.LayoutParams) mapContainer.getLayoutParams();
                layoutParams.leftMargin =
                        (int) (layoutParams.leftMargin - distanceX);
                layoutParams.rightMargin =
                        (int) (layoutParams.leftMargin - currentMapWidth * effectiveScale);
                layoutParams.topMargin =
                        (int) (layoutParams.topMargin - distanceY);
                layoutParams.bottomMargin =
                        (int) (layoutParams.topMargin - currentMapHeight * effectiveScale);
                mapContainer.setLayoutParams(layoutParams);
            }

            return true;
        }
    }
}

