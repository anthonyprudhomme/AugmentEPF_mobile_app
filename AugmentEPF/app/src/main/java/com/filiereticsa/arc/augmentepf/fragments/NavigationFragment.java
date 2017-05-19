package com.filiereticsa.arc.augmentepf.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filiereticsa.arc.augmentepf.R;

/**
 * Created by Cécile on 16/05/2017.
 */

public class NavigationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

}
