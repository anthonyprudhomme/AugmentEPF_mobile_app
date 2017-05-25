package com.filiereticsa.arc.augmentepf.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.adapters.SearchListAdapter;
import com.filiereticsa.arc.augmentepf.models.ClassRoom;
import com.filiereticsa.arc.augmentepf.models.Place;
import com.filiereticsa.arc.augmentepf.models.PointOfInterest;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private ListView listView;
    private ArrayList<Place> availableClassroomList = new ArrayList<>();
    private ArrayList<Place> surroundingPoi = new ArrayList<>();
    private SearchListAdapter adapter;
    private AutoCompleteTextView searchInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initiateLists(view);
        setAutoCompleteSearch(view);
        setRetainInstance(true);
        return view;
    }

    public void initiateLists(View view){
        // Set the list of available classrooms
        listView = (ListView) view.findViewById(R.id.available_rooms);

        // Get the available classrooms ArrayList from Classroom class
        availableClassroomList = ClassRoom.getAvailableClassroomList();
        adapter = new SearchListAdapter(getContext(), availableClassroomList);

        // Set the list adapter
        listView.setAdapter(adapter);

        // Set the list of nearby points of interest
        listView = (ListView) view.findViewById(R.id.pi_around);

        // Get the nearby points of interest ArrayList from Point of interest class
        surroundingPoi = PointOfInterest.getSurroundingPoi();
        adapter = new SearchListAdapter(getContext(), surroundingPoi);

        // Set the list adapter
        listView.setAdapter(adapter);
    }

    public void setAutoCompleteSearch(View view){
        // Get the string array of all class names
        String[] allClassrooms = ClassRoom.getClassroomsAsStrings();

        // Get the AutoCompleteTextView from the search fragment
        searchInput = (AutoCompleteTextView) view.findViewById(R.id.search_input);

        // Create an autocompletion list with string array entryUser
        // "simple_dropdown_item_1line" is a display style
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, allClassrooms);

        // Put the autocompletion list in our object of autocompletion
        searchInput.setAdapter(adapter);

    }

}
