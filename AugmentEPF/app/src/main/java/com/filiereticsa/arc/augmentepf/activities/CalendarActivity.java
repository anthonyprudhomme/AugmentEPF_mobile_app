package com.filiereticsa.arc.augmentepf.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.views.CalendarClassView;

import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity {

    private static final String TAG = "Ici";
    public static final int HOUR_DELTA = 7;
    private HorizontalScrollView horizontalScrollView;
    private int columnWidth;
    private int rowHeight;
    private GridLayout gridLayout;
    private int index = 0;
    private int columnNumber = 8;
    private int rowNumber = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        initGridLayout();
        addHours();
        addOtherCells();
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.gridLayoutContainer);
        horizontalScrollView.addView(gridLayout);
    }

    private void addAClass(int dayNumber,double beginningHour, double beginningMinute, double endHour, double endMinute) {
        double delta = endHour - beginningHour;
        ArrayList<CalendarClassView> classViews = new ArrayList<>();
        if (delta == 0) {
            double percentage = ((endMinute - beginningMinute) / 60) * 100;
            classViews.add(new CalendarClassView(this, rowHeight, columnWidth, percentage, beginningMinute));
        } else {
            for (int i = 0; i < delta + 1; i++) {
                if (i == 0) {
                    double percentage = ((60 - beginningMinute) / 60) * 100;
                    classViews.add(new CalendarClassView(this, rowHeight, columnWidth, percentage, beginningMinute));
                } else {
                    if (i == delta) {
                        double percentage = (endMinute / 60) * 100;
                        classViews.add(new CalendarClassView(this, rowHeight, columnWidth, percentage, 0));
                    } else {
                        classViews.add(new CalendarClassView(this, rowHeight, columnWidth, 100, 0));
                    }
                }
            }
        }
        for (int i = 0; i < classViews.size(); i++) {
//            Button view;
//            view = new Button(this);
            CalendarClassView view = classViews.get(i);
            //view.setBackgroundColor(Color.GREEN);
            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = LinearLayout.LayoutParams.MATCH_PARENT;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            param.rightMargin = 3;
            param.leftMargin = 3;
            param.topMargin = 0;
            param.setGravity(Gravity.CENTER);
            param.columnSpec = GridLayout.spec(dayNumber);
            param.rowSpec = GridLayout.spec((int)(beginningHour - HOUR_DELTA+i));
            view.setLayoutParams(param);
            gridLayout.addView(view, index++);
        }
    }

    private void addOtherCells() {
        Button view;
        for (int i = 0; i < columnNumber * rowNumber; i++) {
            if (i % rowNumber != 0 && i > rowNumber) {
                view = new Button(this);
                if (i % 2 == 0) {
                    view.setBackgroundColor(getResources().getColor(R.color.white));
                } else {
                    view.setBackgroundColor(getResources().getColor(R.color.grey_white));
                }
                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.height = LinearLayout.LayoutParams.MATCH_PARENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                param.topMargin = 0;
                param.setGravity(Gravity.CENTER);
                param.columnSpec = GridLayout.spec(i / rowNumber);
                param.rowSpec = GridLayout.spec(i % rowNumber);
                view.setLayoutParams(param);
                gridLayout.addView(view, index++);
            }
        }
    }

    private void addHours() {
        TextView hourTextView;
        for (int i = 0; i < rowNumber - 1; i++) {
            hourTextView = new TextView(this);
            int beginningHour = i + HOUR_DELTA;
            String hour = beginningHour + "h/" + (beginningHour + 1) + "h";

            hourTextView.setText(hour);

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = GridLayout.LayoutParams.MATCH_PARENT;
            param.rightMargin = 10;
            param.leftMargin = 3;
            param.setGravity(Gravity.RIGHT);
            param.columnSpec = GridLayout.spec(0);
            param.rowSpec = GridLayout.spec(i + 1);
            hourTextView.setLayoutParams(param);
            hourTextView.setGravity(Gravity.RIGHT);
            gridLayout.addView(hourTextView, index++);
        }
    }

    private void initGridLayout() {
        gridLayout = new GridLayout(this);
        gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        gridLayout.setColumnCount(columnNumber);
        gridLayout.setRowCount(rowNumber);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (columnWidth != gridLayout.getWidth() / columnNumber) {
            columnWidth = gridLayout.getWidth() / columnNumber;
            rowHeight = gridLayout.getHeight() / rowNumber;
            addDays();
            addAClass(1,12,15,13,15);
            addAClass(2,12,0,13,0);
            addAClass(3,13,0,17,0);
            addAClass(4,12,5,12,55);
            addAClass(4,13,0,15,30);

        }
    }

    private void addDays() {
        TextView dayTextView;
        for (int i = 1; i < columnNumber; i++) {
            dayTextView = new TextView(this);
            String day = "";
            switch (i) {
                case 1:
                    day = "Monday";
                    break;

                case 2:
                    day = "Tuesday";
                    break;

                case 3:
                    day = "Wednesday";
                    break;

                case 4:
                    day = "Thursday";
                    break;

                case 5:
                    day = "Friday";
                    break;

                case 6:
                    day = "Saturday";
                    break;

                case 7:
                    day = "Sunday";
                    break;
            }

            dayTextView.setText(day);

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = columnWidth;
            param.rightMargin = 3;
            param.leftMargin = 3;
            param.topMargin = 10;
            param.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            param.columnSpec = GridLayout.spec(i);
            param.rowSpec = GridLayout.spec(0);
            dayTextView.setLayoutParams(param);
            dayTextView.setTextSize(15);
            dayTextView.setGravity(Gravity.CENTER);
            gridLayout.addView(dayTextView, index++);
        }
    }
}
