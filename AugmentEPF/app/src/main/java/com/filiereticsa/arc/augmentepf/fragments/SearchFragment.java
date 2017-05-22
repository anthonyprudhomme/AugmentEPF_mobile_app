package com.filiereticsa.arc.augmentepf.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    SearchListAdapter adapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        listView = (ListView) view.findViewById(R.id.available_rooms);

        availableClassroomList = ClassRoom.getAvailableClassroomList();
        adapter = new SearchListAdapter(getContext(), availableClassroomList);

        listView.setAdapter(adapter);

        listView = (ListView) view.findViewById(R.id.pi_around);

        surroundingPoi = PointOfInterest.getSurroundingPoi();
        adapter = new SearchListAdapter(getContext(), surroundingPoi);

        listView.setAdapter(adapter);

        return view;
    }
}
