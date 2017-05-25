package com.filiereticsa.arc.augmentepf.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.interfaces.HTTPRequestInterface;
import com.filiereticsa.arc.augmentepf.localization.BeaconDetector;
import com.filiereticsa.arc.augmentepf.localization.GAFrameworkUserTracker;
import com.filiereticsa.arc.augmentepf.localization.LocalizationFragment;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;

import org.altbeacon.beacon.BeaconManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class HomePageActivity extends AppCompatActivity implements HTTPRequestInterface {
    private static final String TAG = "Ici";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    private SlidingDrawer leftSlidingDrawer;
    private SlidingDrawer rightSlidingDrawer;
    private boolean isShowingSlidingDrawer = true;
    private boolean touchedEditText = false;
    private boolean canMapBeMoved = true;
    private ImageButton drawerHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        askForPermission();
        setUpSlidingDrawers();
        setUpEditText();
        GAFrameworkUserTracker.sharedTracker().startTrackingUser();
        postRequestExample();
    }

    private void postRequestExample() {
        // Create the JSONObject that will be sent in the request
        JSONObject jsonObject = new JSONObject();
        try {
            // Add the different element in the JSONObject with the method put
            // The first parameter is the key and the second one is the value
            // The first parameter has to be a constant in order to change it easily
            // The second parameter shouldn't be hardcoded in most cases
            jsonObject.put("name","Anthony");
            jsonObject.put("type","Student");
            jsonObject.put("password","Lol1234");
            jsonObject.put("email","anthony.prudhomme@epfedu.fr");
            // If you need to add an array in the JSONObject do as the 3 next lines shows
            JSONArray jsonArray = new JSONArray();
            jsonArray.put("elevator");
            jsonArray.put("biggerText");
            jsonObject.put("specificAttributes",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Start the post request method that takes 3 parameters :
        // - The name of the method in the server (given by Guilhem)
        // - The data of the request : the JSONObject you've just created as a string
        // - A reference to the listener : see the implementation in the class declaration above
        //   There is an HTTPRequestInterface that contains a method called onRequestDone
        //   This method will be executed when the request is done and will give the result as a String
        //   You have to put the result in a JSONObject to use it. See the example below (onRequestDone)
        HTTPRequestManager.doPostRequest("accountCreation.php",jsonObject.toString(),this);
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

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
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
                rightSlidingDrawer.setVisibility(View.GONE);
                drawerHandle.setBackgroundResource(R.drawable.nav_left_bar_close);
                floatingActionButton.setVisibility(View.GONE);

            }

            @Override
            public void onScrollEnded() {
                canMapBeMoved = true;
            }
        });

        // When the drawer is closed
        leftSlidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                rightSlidingDrawer.setVisibility(View.VISIBLE);
                drawerHandle.setBackgroundResource(R.drawable.nav_left_bar_open);
                floatingActionButton.setVisibility(View.VISIBLE);
            }

        });


        /*==========================================================================================
        |                                Set right sliding drawer listeners                        |
        ==========================================================================================*/

        rightSlidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                leftSlidingDrawer.close();
                leftSlidingDrawer.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.GONE);
            }
        });

        // When the drawer is dragged
        rightSlidingDrawer.setOnDrawerScrollListener(new SlidingDrawer.OnDrawerScrollListener() {

            @Override
            public void onScrollStarted() {
                canMapBeMoved = false;
                leftSlidingDrawer.close();
                leftSlidingDrawer.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.GONE);

            }

            @Override
            public void onScrollEnded() {
                canMapBeMoved = true;
            }
        });

        // When the drawer is closed
        rightSlidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                leftSlidingDrawer.setVisibility(View.VISIBLE);
                if (touchedEditText == true){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput (InputMethodManager.SHOW_FORCED, 0);
                }
                floatingActionButton.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setUpEditText(){
        EditText searchInput = (EditText) findViewById(R.id.search_input);
        searchInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    touchedEditText=true;
                }else {
                    touchedEditText=false;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput (InputMethodManager.SHOW_FORCED, 0);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BeaconDetector.sharedBeaconDetector().bindBeaconManager();
        BeaconDetector.sharedBeaconDetector().setActivity(this);
        if (beaconManager.isBound(BeaconDetector.sharedBeaconDetector().getBeaconConsumer()))
            beaconManager.setBackgroundMode(false);
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
        if(canMapBeMoved) {
            for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
                Fragment currentFragment = getSupportFragmentManager().getFragments().get(i);
                if (currentFragment instanceof LocalizationFragment) {
                    if (((LocalizationFragment) currentFragment).isGestureEnabled()) {
                        LocalizationFragment currentLocalizationFragment = (LocalizationFragment) currentFragment;
                        currentLocalizationFragment.getGestureDetector().onTouchEvent(event);
                        currentLocalizationFragment.setPointerCount(event.getPointerCount());
                        return currentLocalizationFragment.getScaleDetector().onTouchEvent(event);
                    }
                }
            }
        }
        return false;
    }

    public void onGoClick(View view) {
    }

    public void onAdminClick(View view) {
    }

    public void onConnectClick(View view) {
        Intent intent = new Intent(this, ConnectionActivity.class);
        startActivity(intent);
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
    }

    public void onSettingsClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onContactClick(View view) {
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
    public void onRequestDone(String result) {
        try{
            // Put the result in a JSONObject to use it.
            JSONObject jsonObject = new JSONObject(result);
            // Show in the log the message given by the result : it will give error or success information
            Log.d(TAG, "onRequestDone: "+jsonObject.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
