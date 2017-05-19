package com.filiereticsa.arc.augmentepf.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.filiereticsa.arc.augmentepf.R;

public class NavigationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        /*FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        NavigationFragment fragment = new NavigationFragment();
        fragmentTransaction.add(R.id.navigation_fragment, fragment);
        fragmentTransaction.commit();*/
    }
}
