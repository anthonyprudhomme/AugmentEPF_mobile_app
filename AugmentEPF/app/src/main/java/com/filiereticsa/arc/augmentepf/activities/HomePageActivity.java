package com.filiereticsa.arc.augmentepf.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import com.filiereticsa.arc.augmentepf.AppUtils;
import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.fragments.CameraFragment;
import com.filiereticsa.arc.augmentepf.fragments.OptionsFragment;
import com.filiereticsa.arc.augmentepf.fragments.SearchFragment;
import com.filiereticsa.arc.augmentepf.interfaces.DestinationSelectedInterface;
import com.filiereticsa.arc.augmentepf.interfaces.HTTPRequestInterface;
import com.filiereticsa.arc.augmentepf.localization.BeaconDetector;
import com.filiereticsa.arc.augmentepf.localization.GABeacon;
import com.filiereticsa.arc.augmentepf.localization.GABeaconMap;
import com.filiereticsa.arc.augmentepf.localization.GAFrameworkUserTracker;
import com.filiereticsa.arc.augmentepf.localization.LocalizationFragment;
import com.filiereticsa.arc.augmentepf.managers.FileManager;
import com.filiereticsa.arc.augmentepf.managers.HTTP;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;
import com.filiereticsa.arc.augmentepf.models.Class;
import com.filiereticsa.arc.augmentepf.models.ICalTimeTable;
import com.filiereticsa.arc.augmentepf.models.Place;

import org.altbeacon.beacon.BeaconManager;
import org.json.JSONException;
import org.json.JSONObject;

import static com.filiereticsa.arc.augmentepf.activities.ConnectionActivity.ATTRIBUTE;
import static com.filiereticsa.arc.augmentepf.activities.ConnectionActivity.GET_ATTRIBUTE;
import static com.filiereticsa.arc.augmentepf.activities.ConnectionActivity.GET_EMAIL;
import static com.filiereticsa.arc.augmentepf.activities.ConnectionActivity.GET_ICAL;
import static com.filiereticsa.arc.augmentepf.activities.ConnectionActivity.GET_TYPE;
import static com.filiereticsa.arc.augmentepf.activities.ConnectionActivity.ICAL;
import static com.filiereticsa.arc.augmentepf.activities.ConnectionActivity.SAVE_CRED;
import static com.filiereticsa.arc.augmentepf.activities.ConnectionActivity.TYPE;
import static com.filiereticsa.arc.augmentepf.activities.ConnectionActivity.TYPE_USER;
import static com.filiereticsa.arc.augmentepf.activities.CreateAccountActivity.CREDENTIALS_JSON;
import static com.filiereticsa.arc.augmentepf.activities.CreateAccountActivity.NAME;
import static com.filiereticsa.arc.augmentepf.activities.CreateAccountActivity.PASSWORD;

public class HomePageActivity
        extends AppCompatActivity
        implements
        HTTPRequestInterface,
        DestinationSelectedInterface,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String NAVIGATION_MODE = "navigation_mode";
    private static final String TAG = "Ici";
    public static final String VIDEO_TUTORIAL = "videoTutorial";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    public static HTTPRequestInterface httpRequestInterface;
    public static DestinationSelectedInterface destinationSelectedInterface;
    public static boolean isUserConnected = false;
    private static boolean snackBarShown = false;
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    private SlidingDrawer leftSlidingDrawer;
    private SlidingDrawer rightSlidingDrawer;
    private boolean isShowingSlidingDrawer = true;
    private boolean touchedEditText = false;
    private boolean canMapBeMoved = true;
    private ImageButton drawerHandle;
    private SearchFragment searchFragment;
    private OptionsFragment optionsFragment;
    private LocalizationFragment localizationFragment;
    private CameraFragment cameraFragment;
    public static View rootView;
    private Class nextClass;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (GAFrameworkUserTracker.sharedTracker() == null) {
            new GAFrameworkUserTracker(this);
            GAFrameworkUserTracker.sharedTracker().startTrackingUser();
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //showTutorialOrNot();
        setContentView(R.layout.activity_home_page);
        rootView = findViewById(R.id.rootview);
        httpRequestInterface = this;
        logInIfNecessary();

        destinationSelectedInterface = this;
        AppUtils.setScreenSize(this);
        //loadBeaconsAndMaps();
        initFragments();
        showPreferredNavigationMode();
        askForPermission();
        setUpSlidingDrawers();
        setUpEditText();
        showAdminButtonOrNot();
    }

    private void showTutorialOrNot() {
        boolean mustShowTutorial = sharedPreferences.getBoolean(VIDEO_TUTORIAL, true);
        if (mustShowTutorial) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            SharedPreferences.Editor positivePrefEditor = sharedPreferences.edit();
                            positivePrefEditor.putBoolean(VIDEO_TUTORIAL, false);
                            positivePrefEditor.apply();
                            Intent intent = new Intent(HomePageActivity.this, TutorialActivity.class);
                            startActivity(intent);
                            dialog.dismiss();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            SharedPreferences.Editor negativePrefEditor = sharedPreferences.edit();
                            negativePrefEditor.putBoolean(VIDEO_TUTORIAL, false);
                            negativePrefEditor.apply();
                            dialog.dismiss();
                            break;
                    }
                }
            };


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.watch_tuto).setPositiveButton(R.string.yes, dialogClickListener)
                    .setNegativeButton(R.string.cancel, dialogClickListener).show();

        }
    }

    private void showAdminButtonOrNot() {
        ImageButton adminButton = (ImageButton) findViewById(R.id.admin_button);
        if (sharedPreferences.getString(TYPE_USER, "V").equals("A") && isUserConnected) {
            adminButton.setVisibility(View.VISIBLE);
            adminButton.setClickable(true);
        }else{
            adminButton.setVisibility(View.GONE);
            adminButton.setClickable(false);
        }
    }

    private void logInIfNecessary() {
        if (!isUserConnected) {
            boolean saveCred = sharedPreferences.getBoolean(SAVE_CRED, false);
            if (saveCred) {
                connectToServer();
            }
        }
    }

    private void connectToServer() {
        JSONObject credentials = loadCredentials();
        try {
            String name = credentials.getString(NAME);
            String password = credentials.getString(PASSWORD);
            JSONObject jsonRequestData = new JSONObject();
            try {
                jsonRequestData.put(NAME, name);
                jsonRequestData.put(PASSWORD, password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HTTPRequestManager.doPostRequest(HTTP.CONNECTION_PHP, jsonRequestData.toString(),
                    this, HTTPRequestManager.CONNECTION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject loadCredentials() {
        FileManager fileManager = new FileManager(null, CREDENTIALS_JSON);
        JSONObject jsonDataRead = null;
        try {
            jsonDataRead = new JSONObject(fileManager.readFile());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonDataRead;
    }

    private void showPreferredNavigationMode() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String navigationMode = sharedPreferences.getString(NAVIGATION_MODE, "P");
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (navigationMode) {
            case "P":
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .hide(cameraFragment)
                        .commit();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .show(localizationFragment)
                        .commit();
                if (localizationFragment.getView() != null) {
                    localizationFragment.getView().setAlpha(1);
                }
                break;

            case "C":
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .show(cameraFragment)
                        .commit();
//                fragmentManager.beginTransaction()
//                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
//                        .hide(localizationFragment)
//                        .commit();
                if (localizationFragment.getView() != null) {
                    localizationFragment.getView().setAlpha(0.5f);
                }
                break;
        }
    }

    private void loadBeaconsAndMaps() {
        if (isNetworkAvailable()) {
            GABeacon.askForBeacons();
            GABeaconMap.askForMaps();
        } else {
            GABeacon.loadBeaconsFromFile();
            GABeaconMap.loadMapsFromFile();
        }
    }

    private void initFragments() {
        searchFragment = (SearchFragment)
                getSupportFragmentManager().findFragmentById(R.id.search_fragment);
        optionsFragment = (OptionsFragment)
                getSupportFragmentManager().findFragmentById(R.id.options_fragment);
        localizationFragment = (LocalizationFragment)
                getSupportFragmentManager().findFragmentById(R.id.localization_fragment);
        cameraFragment = (CameraFragment)
                getSupportFragmentManager().findFragmentById(R.id.camera_fragment);
    }

    private void askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.app_need_location);
                builder.setMessage(R.string.app_need_location_message);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    PERMISSION_REQUEST_COARSE_LOCATION);
                        }
                    }

                });
                builder.show();
            }
        }
    }

    public void setUpSlidingDrawers() {
        // initiate the SlidingDrawer
        leftSlidingDrawer = (SlidingDrawer) findViewById(R.id.leftSlidingDrawer);
        // initiate the SlidingDrawer
        rightSlidingDrawer = (SlidingDrawer) findViewById(R.id.rightSlidingDrawer);
        drawerHandle = (ImageButton) findViewById(R.id.left_handle);

        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);

        /*==========================================================================================
        |                                Set left sliding drawer listeners                        |
        ==========================================================================================*/

        // When the drawer is opened
        leftSlidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                canMapBeMoved = false;
                rightSlidingDrawer.close();
                rightSlidingDrawer.setVisibility(View.GONE);
                drawerHandle.setBackgroundResource(R.drawable.nav_left_bar_close);
                floatingActionButton.setVisibility(View.GONE);
            }
        });

        // When the drawer is dragged
        leftSlidingDrawer.setOnDrawerScrollListener(new SlidingDrawer.OnDrawerScrollListener() {

            @Override
            public void onScrollStarted() {
                canMapBeMoved = false;
                rightSlidingDrawer.close();
                drawerHandle.setBackgroundResource(R.drawable.nav_left_bar_close);
                floatingActionButton.setVisibility(View.GONE);
                showAdminButtonOrNot();
            }

            @Override
            public void onScrollEnded() {
                canMapBeMoved = true;
                if (!leftSlidingDrawer.isOpened()) {
                    rightSlidingDrawer.setVisibility(View.VISIBLE);
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
            }
        });

        // When the drawer is closed
        leftSlidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                canMapBeMoved = true;
                rightSlidingDrawer.setVisibility(View.VISIBLE);
                drawerHandle.setBackgroundResource(R.drawable.nav_left_bar_open);
                floatingActionButton.setVisibility(View.VISIBLE);
            }

        });


        /*==========================================================================================
        |                                Set right sliding drawer listeners                        |
        ==========================================================================================*/

        // When the drawer is opened
        rightSlidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                canMapBeMoved = false;
                leftSlidingDrawer.close();
                leftSlidingDrawer.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.GONE);
                searchFragment.onDrawerOpened();
                askForNextCourse();
                askForAvailableRooms();
            }
        });

        // When the drawer is dragged
        rightSlidingDrawer.setOnDrawerScrollListener(new SlidingDrawer.OnDrawerScrollListener() {

            @Override
            public void onScrollStarted() {
                canMapBeMoved = false;
                leftSlidingDrawer.close();
                floatingActionButton.setVisibility(View.GONE);

            }

            @Override
            public void onScrollEnded() {
                canMapBeMoved = true;
                if (!rightSlidingDrawer.isOpened()) {
                    leftSlidingDrawer.setVisibility(View.VISIBLE);
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
            }
        });

        // When the drawer is closed
        rightSlidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                canMapBeMoved = true;
                leftSlidingDrawer.setVisibility(View.VISIBLE);
                if (touchedEditText) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
                floatingActionButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void askForAvailableRooms() {
        HTTPRequestManager.doPostRequest(HTTP.GET_ROOMS_PHP, "", this, HTTPRequestManager.AVAILABLE_CLASSROOMS);
    }

    private void askForNextCourse() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(HTTP.ID_USER, ConnectionActivity.idUser);
            jsonObject.put(HTTP.TOKEN, ConnectionActivity.token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HTTPRequestManager.doPostRequest(HTTP.GET_NEXT_COURSE_PHP, jsonObject.toString(), this, HTTPRequestManager.NEXT_COURSE);
    }

    public void setUpEditText() {
        EditText searchInput = (EditText) findViewById(R.id.search_input);
        searchInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    touchedEditText = true;
                } else {
                    touchedEditText = false;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showAdminButtonOrNot();
        BeaconDetector.sharedBeaconDetector().bindBeaconManager();
        BeaconDetector.sharedBeaconDetector().setActivity(this);
        if (beaconManager.isBound(BeaconDetector.sharedBeaconDetector().getBeaconConsumer()))
            beaconManager.setBackgroundMode(false);
        showPreferredNavigationMode();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BeaconDetector.sharedBeaconDetector.stopMonitoring();
        beaconManager.unbind(BeaconDetector.sharedBeaconDetector.getBeaconConsumer());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(BeaconDetector.sharedBeaconDetector.getBeaconConsumer()))
            beaconManager.setBackgroundMode(true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        if (canMapBeMoved) {
            if (localizationFragment.isGestureEnabled()) {
                localizationFragment.getGestureDetector().onTouchEvent(event);
                localizationFragment.setPointerCount(event.getPointerCount());
                return localizationFragment.getScaleDetector().onTouchEvent(event);
            }
        }
        return false;
    }

    public void onGoClick(View view) {
        searchFragment.onGoClick();
        rightSlidingDrawer.close();
    }

    public void onAdminClick(View view) {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }

    public void onConnectClick(View view) {
        if (!isUserConnected) {
            Intent intent = new Intent(this, ConnectionActivity.class);
            startActivity(intent);
        } else {
            isUserConnected = false;
            optionsFragment.changeLoginButtonText();
            ImageButton adminButton = (ImageButton) findViewById(R.id.admin_button);
            adminButton.setVisibility(View.INVISIBLE);
            adminButton.setClickable(false);
        }
    }

    public void onPlannedClick(View view) {
        Intent intent = new Intent(this, PathPlanningActivity.class);
        startActivity(intent);
    }

    public void onHistoryClick(View view) {
        Intent intent = new Intent(this, PathConsultationActivity.class);
        startActivity(intent);
    }

    public void onCalendarClick(View view) {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }

    public void onSettingsClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onContactClick(View view) {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }

    public void onFullScreenClick(View view) {
        if (isShowingSlidingDrawer) {
            leftSlidingDrawer.setVisibility(View.GONE);
            rightSlidingDrawer.setVisibility(View.GONE);
            isShowingSlidingDrawer = false;
        } else {
            leftSlidingDrawer.setVisibility(View.VISIBLE);
            rightSlidingDrawer.setVisibility(View.VISIBLE);
            isShowingSlidingDrawer = true;
        }
        LocalizationFragment.homePageInterface.onFullScreenModeChanged(!isShowingSlidingDrawer);
    }

    @Override
    public void onRequestDone(String result, int requestId) {
        // Do the action corresponding to the request you did
        // You can retrieve your request thanks to the requestId
        switch (requestId) {
            case HTTPRequestManager.BEACONS:
                if (result.equals(HTTP.ERROR)) {
                    GABeacon.loadBeaconsFromFile();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String success = jsonObject.getString(HTTP.STATE);
                        if (success.equals(HTTP.TRUE)) {
                            GABeacon.onBeaconRequestDone(result);
                        } else {
                            GABeacon.loadBeaconsFromFile();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case HTTPRequestManager.MAPS:
                if (result.equals(HTTP.ERROR)) {
                    GABeaconMap.loadMapsFromFile();
                } else {
                    GABeaconMap.onMapsRequestDone(result);
                }
                break;

            case HTTPRequestManager.NEXT_COURSE:
                if (result.equals(HTTP.ERROR)) {

                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String state = jsonObject.getString(HTTP.STATE);
                    if (state.equals(HTTP.TRUE)) {
                        nextClass = new Class(jsonObject);
                        changeButtonColor();
                    } else {
                        if (ICalTimeTable.iCalInstance != null) {
                            nextClass = ICalTimeTable.iCalInstance.getNextCourse();
                        }
                        if (nextClass != null) {
                            changeButtonColor();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case HTTPRequestManager.CONNECTION:
                if (result.equals(HTTP.ERROR)) {
                    Toast.makeText(this, R.string.error_server, Toast.LENGTH_SHORT).show();
                }
                try {
                    // Put the result in a JSONObject to use it.
                    JSONObject jsonObject = new JSONObject(result);
                    // Show in the log the message given by the result:
                    // it will give error or success information
                    String success = jsonObject.getString(HTTP.MESSAGE);
                    if (success.equals(HTTP.SUCCESS_MESSAGE)) {
                        Toast.makeText(this, R.string.connected, Toast.LENGTH_SHORT).show();
                        HomePageActivity.isUserConnected = true;
                        ConnectionActivity.idUser = jsonObject.getInt(HTTP.ID_USER);
                        ConnectionActivity.token = jsonObject.getString(HTTP.TOKEN);
                        optionsFragment.changeLoginButtonText();
                        checkForNewAccountSettings();
                    } else {
                        // If request failed, shows the message from the server
                        String message = jsonObject.getString(HTTP.MESSAGE);
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        HomePageActivity.isUserConnected = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case HTTPRequestManager.GET_SETTINGS:
                Log.d(TAG, "onRequestDone: setting "+result);
                if (result.equals(HTTP.ERROR)) {
                    Toast.makeText(this, R.string.error_server, Toast.LENGTH_SHORT).show();
                }
                try {
                    // Put the result in a JSONObject to use it.
                    JSONObject jsonObject = new JSONObject(result);
                    // Show in the log the message given by the result : it will give error or success information
                    String success = jsonObject.getString(HTTP.STATE);
                    if (success.equals(HTTP.TRUE)) {

                        // Get sharedPreferences
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

                        // Update user specific attributes in SharedPreferences
                        String attribute = jsonObject.getString(ATTRIBUTE);
                        String[] attributes = attribute.split("/");
                        boolean elevator = false;
                        boolean soundGuidance = false;
                        for (int i = 0; i < attributes.length; i++) {
                            if (attributes[i].equals("soundGuidance")) {
                                soundGuidance = true;
                            }
                            if (attributes[i].equals("elevator")) {
                                elevator = true;
                            }
                        }
                        String specificAttributeValue = "0";
                        if (elevator && soundGuidance) {
                            specificAttributeValue = "VA";
                        } else if (elevator) {
                            specificAttributeValue = "A";
                        } else if (soundGuidance) {
                            specificAttributeValue = "V";
                        }

                        prefEditor.putString("specific_attribute_user", specificAttributeValue);


                        // Update user type in SharedPreferences
                        String userType = jsonObject.getString(TYPE);
                        String userTypeValue = "S";
                        switch (userType) {

                            case "Student":
                                userTypeValue = "S";
                                break;

                            case "Teacher":
                                userTypeValue = "T";
                                break;

                            case "Contributor":
                                userTypeValue = "C";
                                break;

                            case "Visitor":
                                userTypeValue = "V";
                                break;

                            case "Administrator":
                                userTypeValue = "A";
                                break;
                        }

                        prefEditor.putString(TYPE_USER, userTypeValue);
                        // Update user iCal link
                        String iCalLink = jsonObject.getString(ICAL);
                        prefEditor.putString(ICAL, iCalLink);
                        prefEditor.apply();
                        showAdminButtonOrNot();

                    } else {
                        // If request failed, shows the message from the server
                        String message = jsonObject.getString(HTTP.MESSAGE);
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case HTTPRequestManager.PATH:
                if (result.equals(HTTP.ERROR)) {

                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String state = jsonObject.getString(HTTP.STATE);
                    if (state.equals(HTTP.TRUE)) {

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    private void changeButtonColor() {
        Button nextClassButton = (Button) findViewById(R.id.next_class_button);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nextClassButton.setBackground(getResources().getDrawable(R.drawable.red_button, null));
        } else {
            nextClassButton.setBackground(getResources().getDrawable(R.drawable.red_button));
        }
    }

    @Override
    public void onDestinationSelected(Place place) {
        rightSlidingDrawer.close();
        GAFrameworkUserTracker.sharedTracker().setTarget(place);
    }

    public void onNextClassClicked(View view) {
        if (nextClass != null) {
            GAFrameworkUserTracker.sharedTracker().setTarget(nextClass.getClassRoom());
        } else {
            Toast.makeText(this, R.string.no_next_class_found, Toast.LENGTH_SHORT).show();
            askForNextCourse();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        showPreferredNavigationMode();
    }

    private void checkForNewAccountSettings() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(HTTP.ID, ConnectionActivity.idUser);
            jsonObject.put(HTTP.TOKEN, ConnectionActivity.token);
            jsonObject.put(GET_ATTRIBUTE, HTTP.TRUE);
            jsonObject.put(GET_EMAIL, HTTP.TRUE);
            jsonObject.put(GET_TYPE, HTTP.TRUE);
            jsonObject.put(GET_ICAL, HTTP.TRUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HTTPRequestManager.doPostRequest(HTTP.SETTINGS_PHP, jsonObject.toString(),
                this, HTTPRequestManager.GET_SETTINGS);
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) AugmentEPFApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!snackBarShown && !connected) {
            Snackbar.make(rootView, R.string.no_internet, Snackbar.LENGTH_LONG)
                    .show();
            snackBarShown = true;
        }
        return connected;
    }
}

