package com.filiereticsa.arc.augmentepf.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Pair;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.interfaces.HTTPRequestInterface;
import com.filiereticsa.arc.augmentepf.localization.BeaconDetector;
import com.filiereticsa.arc.augmentepf.localization.BeaconDetectorInterface;
import com.filiereticsa.arc.augmentepf.localization.GABeacon;
import com.filiereticsa.arc.augmentepf.localization.GABeaconMap;
import com.filiereticsa.arc.augmentepf.localization.MapItem;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;
import com.filiereticsa.arc.augmentepf.models.ClassRoom;
import com.filiereticsa.arc.augmentepf.models.PointOfInterest;
import com.filiereticsa.arc.augmentepf.views.AdminItemView;

import org.altbeacon.beacon.BeaconManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity implements HTTPRequestInterface, BeaconDetectorInterface {
    public static final String CONTENT_TYPE = "contentType";
    public static final String CHANGE_TYPE = "changeType";
    public static final String CONTENT_INFORMATION = "contentInformation";
    public static final String ADMIN_MODIFICATION_PHP = "administrationChanges.php";
    public static final String ERROR = "Error";
    public static final String STATE = "state";
    public static final String TRUE = "true";
    public static final String MESSAGE = "message";
    public static final String GET_ELEMENT_PHP = "getElement.php";
    public static final String RESULT = "result";
    private static final String TAG = "Here";
    public static final int SECOND_FLOOR = 2;
    public static final int FIRST_FLOOR = 1;
    public static final int GROUND_FLOOR = 0;
    public static final int LOWER_FLOOR = -1;
    private static final int MAX_LENGTH = 5;

    private boolean editBeacon, existing, editRoom;
    private boolean hasUserAskedForClosestBeacon = false;
    public boolean gestureEnabled;
    private int currentFloor, screenWidth, screenHeight, currentMapHeight, currentMapWidth, cellHeight, cellWidth, nbCol, nbRow, itemXCoord, itemYCoord;
    private int numberOfFingerTouchingTheScreen = 0;
    private float effectiveScale = 1f;
    private float scale = 1f;
    public static BeaconDetectorInterface beaconDetectorInterface;
    private ImageView imageView;
    private TextView separator;
    private EditText xCoord, yCoord, poiName, beaconMajor, beaconMinor;
    private String itemName;
    private LinearLayout nameLayout;
    private JSONObject jsonObject = new JSONObject();
    private JSONArray jsonArray = new JSONArray();
    private JSONObject[] jsonObjects;
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    private Bitmap mapBitmap;
    private GABeaconMap gaBeaconMap;
    private Pair<Integer, Integer> gridDimensions;
    private FrameLayout mapContainer;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleDetector;
    private GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.admin_activity_title);
        }
        RadioButton radioButton = (RadioButton) findViewById(R.id.beaconEdit);

        xCoord = (EditText) findViewById(R.id.xCoordText);
        yCoord = (EditText) findViewById(R.id.yCoordText);

        imageView = (ImageView) findViewById(R.id.currentMap);

        nameLayout = (LinearLayout) findViewById(R.id.name_receiver);
        separator = new TextView(this);

        poiName = new EditText(this);
        poiName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        poiName.setSingleLine();
        poiName.setSingleLine();

        beaconMajor = new EditText(this);
        beaconMinor = new EditText(this);
        beaconMajor.setSingleLine();
        beaconMinor.setSingleLine();
        beaconMajor.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        beaconMinor.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});

        separator.setText("/");
        nameLayout.addView(beaconMajor);
        nameLayout.addView(separator);
        nameLayout.addView(beaconMinor);

        radioButton.setChecked(true);
        editBeacon = true;
        editRoom = false;
        currentFloor = 0;
        beaconDetectorInterface = this;

        //Get size of current user screen
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        mapContainer = (FrameLayout) findViewById(R.id.mapContainer);

        gestureDetector = new GestureDetector(this, new GestureListener());
        mapContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getPointerCount() == 2) {
                    Point finger1 = new Point((int) event.getX(0), (int) event.getY(0));
                    Point finger2 = new Point((int) event.getX(1), (int) event.getY(1));
                    Point middle = middlePoint(finger1, finger2);
                    if (event.getAction() != 3) {
                        effectiveScale = scale;
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
        scaleDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (numberOfFingerTouchingTheScreen == 2) {
                    float newScale = 1 - detector.getScaleFactor();
                    scale += newScale;

                    if (scale < 0.1f) // Minimum scale condition:
                        scale = 0.1f;

                    if (scale > 1f) // Maximum scale condition:
                        scale = 1f;
                }
                return true;
            }
        });

        setUpNewMap(FIRST_FLOOR, R.drawable.plan_epf_etage1);

    }

    private void setGridLayout(int columnNumber, int rowNumber) {
        if (gridLayout != null) {
            mapContainer.removeView(gridLayout);
        }
        gridLayout = new GridLayout(this);
        gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        mapContainer.addView(gridLayout);
        gridLayout.setColumnCount(columnNumber);
        gridLayout.setRowCount(rowNumber);
    }

    public boolean isGestureEnabled() {
        return gestureEnabled;
    }

    public GestureDetector getGestureDetector() {
        return gestureDetector;
    }

    public ScaleGestureDetector getScaleDetector() {
        return scaleDetector;
    }

    public void setPointerCount(int pointerCount) {
        this.numberOfFingerTouchingTheScreen = pointerCount;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        if (isGestureEnabled()) {
            setPointerCount(event.getPointerCount());
            getGestureDetector().onTouchEvent(event);
            return getScaleDetector().onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_second_floor:
                setUpNewMap(SECOND_FLOOR, R.drawable.plan_epf_etage2);
                break;
            case R.id.action_first_floor:
                setUpNewMap(FIRST_FLOOR, R.drawable.plan_epf_etage1);
                break;
            case R.id.action_ground_floor:
                setUpNewMap(GROUND_FLOOR, -1);
                break;
            case R.id.action_lower_floor:
                setUpNewMap(LOWER_FLOOR, -1);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addGridItem(int xPos, int yPos, AdminItemView.ItemType itemTypes, int cellHeight, int cellWidth, ArrayList<String> names) {
        AdminItemView itemView = new AdminItemView(this, cellHeight, cellWidth, xPos, yPos, itemTypes, names);
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = cellHeight;
        param.width = cellWidth;
        param.setGravity(Gravity.CENTER);
        param.columnSpec = GridLayout.spec(xPos);
        param.rowSpec = GridLayout.spec(yPos);
        itemView.setLayoutParams(param);
        itemView.setOnClickListener(gridItemListener);

        gridLayout.addView(itemView);
    }

    private void setUpNewMap(int mapFloor, int imageRes) {
        if (GABeaconMap.maps.containsKey(mapFloor)) {
            currentFloor = mapFloor;
            gaBeaconMap = GABeaconMap.maps.get(mapFloor);
            mapBitmap = BitmapFactory.decodeResource(getResources(), imageRes);
            int height = mapBitmap.getHeight();
            int width = mapBitmap.getWidth();
            nbCol = gaBeaconMap.getNbCol();
            nbRow = gaBeaconMap.getNbRow();
            setGridLayout(nbCol, nbRow);
            double pictureRatio = ((float) height) / ((float) width);
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
            cellHeight = currentMapHeight / nbRow;
            cellWidth = currentMapWidth / nbCol;
            gridDimensions = new Pair<>(nbRow, nbCol);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    imageView.setLayoutParams(imageParams);
                    imageView.setImageBitmap(mapBitmap);
                    gestureEnabled = true;
                }
            });
            drawGridAndItems();
        } else {
            Toast.makeText(this, R.string.admin_map_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void drawGridAndItems() {
        int x, y;
        int heightDelta = currentMapHeight - (cellHeight * nbRow);
        int widthDelta = currentMapWidth - (cellWidth * nbCol);
        int heightFixFrequency = 0;
        int widthFixFrequency = 0;
        if (heightDelta != 0) {
            heightFixFrequency = nbRow / heightDelta;
        }
        if (widthDelta != 0) {
            widthFixFrequency = nbCol / widthDelta;
        }

        AdminItemView.ItemType itemTypes = AdminItemView.ItemType.EMPTY;
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < nbCol; i++) {
            for (int j = 0; j < nbRow; j++) {
                int currentCellHeight = cellHeight;
                int currentCellWidth = cellWidth;
                if (widthFixFrequency != 0 && i % widthFixFrequency == widthFixFrequency - 1) {
                    currentCellWidth++;
                }
                if (heightFixFrequency != 0 && j % heightFixFrequency == heightFixFrequency - 1) {
                    currentCellHeight++;
                }
                addGridItem(i, j, itemTypes, currentCellHeight, currentCellWidth, names);
            }
        }

        ArrayList<MapItem> mapItems = gaBeaconMap.getMapItems();
        itemTypes = AdminItemView.ItemType.PATH;
        for (int i = 0; i < mapItems.size(); i++) {
            MapItem mapItem = mapItems.get(i);
            x = mapItem.coordinates.first - 1;
            y = mapItem.coordinates.second - 1;
            int currentCellHeight = cellHeight;
            int currentCellWidth = cellWidth;
            if (widthFixFrequency != 0 && x % widthFixFrequency == widthFixFrequency - 1) {
                currentCellWidth++;
            }
            if (heightFixFrequency != 0 && y % heightFixFrequency == heightFixFrequency - 1) {
                currentCellHeight++;
            }
            addGridItem(x, y, itemTypes, currentCellHeight, currentCellWidth, names);
        }

        if (editBeacon) {
            ArrayList<GABeacon> allBeacons = GABeacon.allBeacons;
            itemTypes = AdminItemView.ItemType.BEACON;
            for (int i = 0; i < allBeacons.size(); i++) {
                GABeacon gaBeacon = allBeacons.get(i);
                if (gaBeacon.getMapId() == currentFloor) {
                    x = gaBeacon.getxCoord() - 1;
                    y = gaBeacon.getyCoord() - 1;
                    names = new ArrayList<>();
                    names.add(gaBeacon.getName());
                    int currentCellHeight = cellHeight;
                    int currentCellWidth = cellWidth;
                    if (widthFixFrequency != 0 && x % widthFixFrequency == widthFixFrequency - 1) {
                        currentCellWidth++;
                    }
                    if (heightFixFrequency != 0 && y % heightFixFrequency == heightFixFrequency - 1) {
                        currentCellHeight++;
                    }
                    addGridItem(x, y, itemTypes, currentCellHeight, currentCellWidth, names);
                }
            }
        } else if (editRoom) {
            ArrayList<ClassRoom> classRooms = ClassRoom.getClassRooms();
            itemTypes = AdminItemView.ItemType.ROOM;
            for (int i = 0; i < classRooms.size(); i++) {
                ClassRoom classRoom = classRooms.get(i);
                if (classRoom.getPosition().getFloor() == currentFloor) {
                    x = classRoom.getPosition().getPositionX() - 1;
                    y = classRoom.getPosition().getPositionY() - 1;
                    names = new ArrayList<>();
                    names.add(classRoom.getName());
                    int currentCellHeight = cellHeight;
                    int currentCellWidth = cellWidth;
                    if (widthFixFrequency != 0 && x % widthFixFrequency == widthFixFrequency - 1) {
                        currentCellWidth++;
                    }
                    if (heightFixFrequency != 0 && y % heightFixFrequency == heightFixFrequency - 1) {
                        currentCellHeight++;
                    }
                    addGridItem(x, y, itemTypes, currentCellHeight, currentCellWidth, names);
                }
            }
        } else {
            ArrayList<PointOfInterest> pointOfInterests = PointOfInterest.getPointOfInterests();
            itemTypes = AdminItemView.ItemType.POI;
            for (int i = 0; i < pointOfInterests.size(); i++) {
                PointOfInterest pointOfInterest = pointOfInterests.get(i);
                if (pointOfInterest.getPosition().getFloor() == currentFloor) {
                    x = pointOfInterest.getPosition().getPositionX() - 1;
                    y = pointOfInterest.getPosition().getPositionY() - 1;
                    names = new ArrayList<>();
                    names.add(pointOfInterest.getName());
                    int currentCellHeight = cellHeight;
                    int currentCellWidth = cellWidth;
                    if (widthFixFrequency != 0 && x % widthFixFrequency == widthFixFrequency - 1) {
                        currentCellWidth++;
                    }
                    if (heightFixFrequency != 0 && y % heightFixFrequency == heightFixFrequency - 1) {
                        currentCellHeight++;
                    }
                    addGridItem(x, y, itemTypes, currentCellHeight, currentCellWidth, names);
                }
            }
        }
    }

    public void onBeaconClick(View view) {
        if (!editBeacon) {
            beaconMajor.setText("");
            beaconMinor.setText("");
            nameLayout.removeView(poiName);
            nameLayout.addView(beaconMajor);
            nameLayout.addView(separator);
            nameLayout.addView(beaconMinor);
            editBeacon = true;
            editRoom = false;
            switch (currentFloor) {
                case 2:
                    setUpNewMap(SECOND_FLOOR, R.drawable.plan_epf_etage2);
                    break;
                case 1:
                    setUpNewMap(FIRST_FLOOR, R.drawable.plan_epf_etage1);
                    break;
                case 0:
                    setUpNewMap(GROUND_FLOOR, -1);
                    break;
                case -1:
                    setUpNewMap(LOWER_FLOOR, -1);
                    break;
            }
        }
    }

    public void onPOIClick(View view) {
        if (editBeacon || editRoom) {
            if (editBeacon) {
                nameLayout.removeView(beaconMajor);
                nameLayout.removeView(separator);
                nameLayout.removeView(beaconMinor);
                nameLayout.addView(poiName);
            }
            poiName.setText("");
            editBeacon = false;
            editRoom = false;
            switch (currentFloor) {
                case 2:
                    setUpNewMap(SECOND_FLOOR, R.drawable.plan_epf_etage2);
                    break;
                case 1:
                    setUpNewMap(FIRST_FLOOR, R.drawable.plan_epf_etage1);
                    break;
                case 0:
                    setUpNewMap(GROUND_FLOOR, -1);
                    break;
                case -1:
                    setUpNewMap(LOWER_FLOOR, -1);
                    break;
            }
        }
    }

    public void onRoomClick(View view) {
        if (!editRoom) {
            if (editBeacon) {
                nameLayout.removeView(beaconMajor);
                nameLayout.removeView(separator);
                nameLayout.removeView(beaconMinor);
                nameLayout.addView(poiName);
            }
            poiName.setText("");
            editRoom = true;
            editBeacon = false;
            switch (currentFloor) {
                case 2:
                    setUpNewMap(SECOND_FLOOR, R.drawable.plan_epf_etage2);
                    break;
                case 1:
                    setUpNewMap(FIRST_FLOOR, R.drawable.plan_epf_etage1);
                    break;
                case 0:
                    setUpNewMap(GROUND_FLOOR, -1);
                    break;
                case -1:
                    setUpNewMap(LOWER_FLOOR, -1);
                    break;
            }
        }
    }

    public void onGPSClick(View view) {
        if (editBeacon) {
            hasUserAskedForClosestBeacon = true;
        }
    }

    public void onSaveClick(View view) {
        if (editBeacon) {
            if (beaconMajor.getText().toString().matches("") || beaconMinor.getText().toString().matches("")) {
                Toast.makeText(this, R.string.admin_name_missing, Toast.LENGTH_SHORT).show();
            } else checkForUpdate();
        } else if (poiName.getText().toString().matches("")) {
            Toast.makeText(this, R.string.admin_name_missing, Toast.LENGTH_SHORT).show();
        } else checkForUpdate();
    }

    public void onRemoveClick(View view) {
        if (editBeacon) {
            itemName = beaconMajor.getText().toString() + beaconMinor.getText().toString();
        } else {
            itemName = poiName.getText().toString();
        }
        if (itemName.matches("")) {
            Toast.makeText(this, R.string.admin_target_missing, Toast.LENGTH_SHORT).show();
        } else {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            remove();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.confirm).setPositiveButton(R.string.yes, dialogClickListener)
                    .setNegativeButton(R.string.cancel, dialogClickListener).show();
        }
    }

    private void checkForUpdate() {
        if (editBeacon) {
            try {
                jsonObject.put(CONTENT_TYPE, "beacon");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HTTPRequestManager.doPostRequest(GET_ELEMENT_PHP, jsonObject.toString(), this, HTTPRequestManager.ELEMENT);
        } else
            try {
                jsonObject.put(CONTENT_TYPE, "poi");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        HTTPRequestManager.doPostRequest(GET_ELEMENT_PHP, jsonObject.toString(), this, HTTPRequestManager.ELEMENT);
    }

    private void update() {
        if (editBeacon) {
            itemName = beaconMajor.getText().toString() + "/" + beaconMinor.getText().toString();
        } else {
            itemName = poiName.getText().toString();
        }
        itemXCoord = Integer.valueOf(xCoord.getText().toString()) + 1;
        itemYCoord = Integer.valueOf(yCoord.getText().toString()) + 1;

        if (editBeacon) {
            try {
                jsonObject.put(CONTENT_TYPE, "beacon");
                jsonObject.put(CHANGE_TYPE, "update");
                jsonArray.put(itemXCoord + "/" + itemYCoord + "/" + currentFloor);
                jsonArray.put(itemName);
                jsonObject.put(CONTENT_INFORMATION, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HTTPRequestManager.doPostRequest(ADMIN_MODIFICATION_PHP, jsonObject.toString(), this, HTTPRequestManager.BEACONS);
        } else
            try {
                jsonObject.put(CONTENT_TYPE, "poi");
                jsonObject.put(CHANGE_TYPE, "update");
                jsonArray.put(itemXCoord + "/" + itemYCoord + "/" + currentFloor);
                jsonArray.put(itemName);
                jsonObject.put(CONTENT_INFORMATION, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        HTTPRequestManager.doPostRequest(ADMIN_MODIFICATION_PHP, jsonObject.toString(), this, HTTPRequestManager.POI);
    }

    private void save() {
        if (editBeacon) {
            itemName = beaconMajor.getText().toString() + "/" + beaconMinor.getText().toString();
        } else {
            itemName = poiName.getText().toString();
        }
        itemXCoord = Integer.valueOf(xCoord.getText().toString()) + 1;
        itemYCoord = Integer.valueOf(yCoord.getText().toString()) + 1;

        if (editBeacon) {
            try {
                jsonObject.put(CONTENT_TYPE, "beacon");
                jsonObject.put(CHANGE_TYPE, "add");
                jsonArray.put(itemXCoord + "/" + itemYCoord + "/" + currentFloor);
                jsonArray.put(itemName);
                jsonObject.put(CONTENT_INFORMATION, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HTTPRequestManager.doPostRequest(ADMIN_MODIFICATION_PHP, jsonObject.toString(), this, HTTPRequestManager.BEACONS);
        } else
            try {
                jsonObject.put(CONTENT_TYPE, "poi");
                jsonObject.put(CHANGE_TYPE, "add");
                jsonArray.put(itemXCoord + "/" + itemYCoord + "/" + currentFloor);
                jsonArray.put(itemName);
                jsonObject.put(CONTENT_INFORMATION, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        HTTPRequestManager.doPostRequest(ADMIN_MODIFICATION_PHP, jsonObject.toString(), this, HTTPRequestManager.POI);
    }

    private void remove() {
        if (editBeacon) {
            itemName = beaconMajor.getText().toString() + "/" + beaconMinor.getText().toString();
        } else {
            itemName = poiName.getText().toString();
        }
        itemXCoord = Integer.valueOf(xCoord.getText().toString()) + 1;
        itemYCoord = Integer.valueOf(yCoord.getText().toString()) + 1;
        if (editBeacon) {
            try {
                jsonObject.put(CONTENT_TYPE, "beacon");
                jsonObject.put(CHANGE_TYPE, "remove");
                jsonArray.put("");
                jsonArray.put(itemName);
                jsonObject.put(CONTENT_INFORMATION, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HTTPRequestManager.doPostRequest(ADMIN_MODIFICATION_PHP, jsonObject.toString(), this, HTTPRequestManager.BEACONS);
        } else
            try {
                jsonObject.put(CONTENT_TYPE, "poi");
                jsonObject.put(CHANGE_TYPE, "remove");
                jsonArray.put("");
                jsonArray.put(itemName);
                jsonObject.put(CONTENT_INFORMATION, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        HTTPRequestManager.doPostRequest(ADMIN_MODIFICATION_PHP, jsonObject.toString(), this, HTTPRequestManager.POI);
    }

    @Override
    public void onRequestDone(String result, int requestId) {
        if (result.equals(ERROR)) {
            Toast.makeText(this, R.string.error_server, Toast.LENGTH_SHORT).show();
        } else
            switch (requestId) {
                case HTTPRequestManager.BEACONS:
                    try {
                        // Put the result in a JSONObject to use it.
                        JSONObject jsonObject = new JSONObject(result);
                        String success = jsonObject.getString(STATE);
                        String message = jsonObject.getString(MESSAGE);
                        if (success.equals(TRUE)) {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            // If request failed, shows the message from the server
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HTTPRequestManager.POI:
                    try {
                        // Put the result in a JSONObject to use it.
                        JSONObject jsonObject = new JSONObject(result);
                        String success = jsonObject.getString(STATE);
                        String message = jsonObject.getString(MESSAGE);
                        if (success.equals(TRUE)) {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            // If request failed, shows the message from the server
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HTTPRequestManager.ELEMENT:
                    getResult(result);
                    break;
            }

    }

    private void getResult(String result) {
        String targetName;
        if (editBeacon) {
            targetName = beaconMajor.getText().toString() + "/" + beaconMinor.getText().toString();
        } else {
            targetName = poiName.getText().toString();
        }
        JSONArray resultArray;
        try {
            // Put the result in a JSONObject to use it.
            JSONObject jsonObject = new JSONObject(result);
            String success = jsonObject.getString(STATE);
            String message = jsonObject.getString(MESSAGE);
            if (success.equals(TRUE)) {
                resultArray = jsonObject.getJSONArray(RESULT);
                jsonObjects = new JSONObject[resultArray.length()];
                existing = false;
                int i = 0;
                while (i < resultArray.length()) {
                    jsonObjects[i] = resultArray.getJSONObject(i);
                    if (jsonObjects[i].getString("name").equalsIgnoreCase(targetName)) {
                        existing = true;
                        break;
                    }
                    i++;
                }
                if (existing) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    update();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.admin_update).setPositiveButton(R.string.yes, dialogClickListener)
                            .setNegativeButton(R.string.cancel, dialogClickListener).show();
                } else {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    save();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.admin_save_new).setPositiveButton(R.string.yes, dialogClickListener)
                            .setNegativeButton(R.string.cancel, dialogClickListener).show();
                }
            } else {
                // If request failed, shows the message from the server
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BeaconDetector.sharedBeaconDetector().bindBeaconManager();
        BeaconDetector.sharedBeaconDetector().setActivity(this);
        if (beaconManager.isBound(BeaconDetector.sharedBeaconDetector().getBeaconConsumer()))
            beaconManager.setBackgroundMode(false);
        switch (currentFloor) {
            case 2:
                setUpNewMap(SECOND_FLOOR, R.drawable.plan_epf_etage2);
                break;
            case 1:
                setUpNewMap(FIRST_FLOOR, R.drawable.plan_epf_etage1);
                break;
            case 0:
                setUpNewMap(GROUND_FLOOR, -1);
                break;
            case -1:
                setUpNewMap(LOWER_FLOOR, -1);
                break;
        }
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
    public void rangedBeacons(ArrayList<GABeacon> beacons) {
        if (hasUserAskedForClosestBeacon) {
            GABeacon closestBeacon = null;
            double closestDistance = Double.MAX_VALUE;
            for (int i = 0; i < beacons.size(); i++) {
                GABeacon currentBeacon = beacons.get(i);
                if (currentBeacon.getAccuracy() < closestDistance) {
                    closestDistance = currentBeacon.getAccuracy();
                    closestBeacon = currentBeacon;
                }
            }
            if (closestBeacon != null) {
                final GABeacon finalClosestBeacon = closestBeacon;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        beaconMajor.setText(finalClosestBeacon.getMajor() + "");
                        beaconMinor.setText(finalClosestBeacon.getMinor() + "");
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), R.string.admin_detector_failed, Toast.LENGTH_SHORT).show();
                    }
                });
                hasUserAskedForClosestBeacon = false;
            }
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent event, MotionEvent event2,
                                final float distanceX, final float distanceY) {
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

    public Point middlePoint(Point p1, Point p2) {
        return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    private View.OnClickListener gridItemListener = new View.OnClickListener() {
        public void onClick(View v) {
            AdminItemView admindata = (AdminItemView) v;
            for (int i=0; i<gridLayout.getChildCount();i++){
                    AdminItemView refreshTarget = (AdminItemView) gridLayout.getChildAt(i);
                refreshTarget.refresh();
            }
            admindata.highlight();
            xCoord.setText(admindata.getxPos() + "");
            yCoord.setText(admindata.getyPos() + "");
            ArrayList<String> targetNames = admindata.getNames();
            if (!targetNames.isEmpty()) {
                if (targetNames.size() == 1) {
                    poiName.setText(targetNames.get(0));
                } else {
                    for (int i = 0; i < targetNames.size(); i++) {
                        poiName.setText("multiple targets"+i);
                    }
                }
            }
        }
    };
}
