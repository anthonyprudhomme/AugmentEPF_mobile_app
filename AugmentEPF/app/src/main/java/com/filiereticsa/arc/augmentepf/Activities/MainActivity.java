package com.filiereticsa.arc.augmentepf.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.filiereticsa.arc.augmentepf.R;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SlidingDrawer leftSlidingDrawer = (SlidingDrawer) findViewById(R.id.leftSlidingDrawer); // initiate the SlidingDrawer
        final SlidingDrawer rightSlidingDrawer = (SlidingDrawer) findViewById(R.id.rightSlidingDrawer); // initiate the SlidingDrawer


        leftSlidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                rightSlidingDrawer.close();
                rightSlidingDrawer.setVisibility(View.GONE);
            }
        });
        // implement setOnDrawerCloseListener event
        leftSlidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                rightSlidingDrawer.setVisibility(View.VISIBLE);
            }
        });


        rightSlidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                leftSlidingDrawer.close();
                leftSlidingDrawer.setVisibility(View.GONE);
            }
        });
        // implement setOnDrawerCloseListener event
        rightSlidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                leftSlidingDrawer.setVisibility(View.VISIBLE);
            }
        });
    }

    public void onGoClick(View view) {
    }

    public void onAdminClick(View view) {
    }

    public void onConnectClick(View view) {
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
}
