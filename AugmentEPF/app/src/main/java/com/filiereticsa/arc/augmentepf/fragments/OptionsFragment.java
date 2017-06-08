package com.filiereticsa.arc.augmentepf.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.activities.HomePageActivity;

public class OptionsFragment extends Fragment {

    private Button imageButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_options, container, false);
        imageButton = (Button) view.findViewById(R.id.connect_button);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (HomePageActivity.isUserConnected){
            imageButton.setText(R.string.logout);
        }
    }

    public void changeLoginButtonText() {
        if (HomePageActivity.isUserConnected){
            imageButton.setText(R.string.logout);
        }else{
            imageButton.setText(R.string.connect);
        }
    }
}
