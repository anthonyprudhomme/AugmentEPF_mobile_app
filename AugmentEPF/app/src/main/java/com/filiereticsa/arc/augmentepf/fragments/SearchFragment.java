package com.filiereticsa.arc.augmentepf.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.activities.HomePageActivity;
import com.filiereticsa.arc.augmentepf.adapters.SearchListAdapter;
import com.filiereticsa.arc.augmentepf.interfaces.HTTPRequestInterface;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;
import com.filiereticsa.arc.augmentepf.models.ClassRoom;
import com.filiereticsa.arc.augmentepf.models.Place;
import com.filiereticsa.arc.augmentepf.models.PointOfInterest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements HTTPRequestInterface {

    private static final String TAG = "Ici";
    public static final String SUCCESS = "success";
    private ListView listViewClassRoom;
    private ListView listViewPoi;
    private ArrayList<Place> availableClassroomList = new ArrayList<>();
    private ArrayList<Place> surroundingPoi = new ArrayList<>();
    private SearchListAdapter classRoomAdapter;
    private SearchListAdapter pointOfInterestAdapter;
    public static HTTPRequestInterface httpRequestInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        httpRequestInterface = this;
        initClassRoomsLoading();
        initAvailableClassRoomsRequest();
        loadPOI();
        initiateLists(view);
        setAutoCompleteSearch(view);
        return view;
    }

    private void initAvailableClassRoomsRequest() {
        if (HomePageActivity.isNetworkAvailable()) {
            ClassRoom.askForAvailableClassRooms();
        } else {
            Log.d(TAG, "initAvailableClassRoomsRequest: no internet");
        }
    }

    private void loadPOI() {

    }

    private void initClassRoomsLoading() {
        if (HomePageActivity.isNetworkAvailable()) {
            ClassRoom.askForClassRooms();
        } else {
            Log.d(TAG, "initAvailableClassRoomsRequest: no internet");
            ClassRoom.loadClassRoomsFromFile();
        }
    }

    public void initiateLists(View view) {
        // Set the list of available classrooms
        listViewClassRoom = (ListView) view.findViewById(R.id.available_rooms);

        // Get the available classrooms ArrayList from Classroom class
        availableClassroomList = ClassRoom.getAvailableClassroomList();
        classRoomAdapter = new SearchListAdapter(getContext(), availableClassroomList);

        // Set the list adapter
        listViewClassRoom.setAdapter(classRoomAdapter);

        // Set the list of nearby points of interest
        listViewPoi = (ListView) view.findViewById(R.id.pi_around);

        // Get the nearby points of interest ArrayList from Point of interest class
        surroundingPoi = PointOfInterest.getSurroundingPoi();
        pointOfInterestAdapter = new SearchListAdapter(getContext(), surroundingPoi);

        // Set the list adapter
        listViewPoi.setAdapter(pointOfInterestAdapter);
    }

    public void setAutoCompleteSearch(View view) {
        // Get the string array of all class names
        String[] allClassrooms = ClassRoom.getClassroomsAsStrings();

        // Get the AutoCompleteTextView from the search fragment
        AutoCompleteTextView searchInput = (AutoCompleteTextView) view.findViewById(R.id.search_input);

        // Create an autocompletion list with string array entryUser
        // "simple_dropdown_item_1line" is a display style
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, allClassrooms);

        // Put the autocompletion list in our object of autocompletion
        searchInput.setAdapter(adapter);

    }

    // This method is called when the right sliding drawer is opened (see in HomePageActivity).
    public void onDrawerOpened() {
        // Get the available classrooms ArrayList from Classroom class
        availableClassroomList = ClassRoom.getAvailableClassroomList();
        classRoomAdapter = new SearchListAdapter(getContext(), availableClassroomList);
        listViewClassRoom.setAdapter(classRoomAdapter);

        // Get the nearby points of interest ArrayList from Point of interest class.
        surroundingPoi = PointOfInterest.getSurroundingPoi();
        pointOfInterestAdapter = new SearchListAdapter(getContext(), surroundingPoi);
        listViewPoi.setAdapter(pointOfInterestAdapter);
    }

    @Override
    public void onRequestDone(String result, int requestId) {
        switch (requestId) {
            case HTTPRequestManager.AVAILABLE_CLASSROOMS:
                ClassRoom.onAvailableRequestDone(result);
                break;

            case HTTPRequestManager.CLASSROOMS:
                if(result.equals("Error")){
                    ClassRoom.loadClassRoomsFromFile();
                }else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String success = jsonObject.getString(SUCCESS);
                        if (success.equals("success")) {
                            ClassRoom.onClassRoomsRequestDone(result);
                        } else {
                            ClassRoom.loadClassRoomsFromFile();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case HTTPRequestManager.POI:
                PointOfInterest.onPoiRequestDone(result);
                break;
            default:

                break;
        }

    }
}
