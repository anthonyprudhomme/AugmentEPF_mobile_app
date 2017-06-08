package com.filiereticsa.arc.augmentepf.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.interfaces.HTTPRequestInterface;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;
import com.filiereticsa.arc.augmentepf.models.Class;
import com.filiereticsa.arc.augmentepf.models.ICalTimeTable;
import com.filiereticsa.arc.augmentepf.views.CalendarClassView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.filiereticsa.arc.augmentepf.activities.HomePageActivity.ERROR;
import static com.filiereticsa.arc.augmentepf.activities.HomePageActivity.STATE;

public class CalendarActivity extends AppCompatActivity implements HTTPRequestInterface {

    private static final String TAG = "Ici";
    public static final int HOUR_DELTA = 7;
    public static final String TOKEN = "token";
    public static final String ID = "id";
    private static final String YES = "y";
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
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ID, ConnectionActivity.idUser);
            jsonObject.put(TOKEN, ConnectionActivity.token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HTTPRequestManager.doPostRequest("getCalendar.php", jsonObject.toString(), this, HTTPRequestManager.CALENDAR);
    }

    private void addAClass(int dayNumber, double beginningHour, double beginningMinute, double endHour, double endMinute) {
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
            CalendarClassView view = classViews.get(i);
            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = LinearLayout.LayoutParams.MATCH_PARENT;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            param.rightMargin = 3;
            param.leftMargin = 3;
            param.topMargin = 0;
            param.setGravity(Gravity.CENTER);
            param.columnSpec = GridLayout.spec(dayNumber);
            param.rowSpec = GridLayout.spec((int) (beginningHour - HOUR_DELTA + i));
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
            addClasses();

        }
    }

    private void addClasses() {
        if (ICalTimeTable.iCalInstance != null) {
            HashMap<String, ArrayList<Class>> classes = ICalTimeTable.iCalInstance.getClasses();
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int today = calendar.get(Calendar.DAY_OF_MONTH);

            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
            String weekDay = dayFormat.format(calendar.getTime());
            int daysAfterMonday = getDaysAfterMondayFor(weekDay);
            int indexOfDay = 1;
            for (int currentDay = today - daysAfterMonday; currentDay < today - daysAfterMonday + 7; currentDay++) {
                String currentDayString = String.valueOf(year) + String.valueOf(currentDay) + String.valueOf(month);
                ArrayList<Class> classesForCurrentDay = classes.get(currentDayString);
                for (int i = 0; i < classesForCurrentDay.size(); i++) {
                    Class currentClass = classesForCurrentDay.get(i);
                    Calendar currentStartCalendar = toCalendar(currentClass.getStartDate());
                    Calendar currentEndCalendar = toCalendar(currentClass.getEndDate());
                    addAClass(indexOfDay,
                            currentStartCalendar.get(Calendar.HOUR),
                            currentStartCalendar.get(Calendar.MINUTE),
                            currentEndCalendar.get(Calendar.HOUR),
                            currentEndCalendar.get(Calendar.MINUTE));
                }
                indexOfDay++;
            }
        }
    }

    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    private int getDaysAfterMondayFor(String weekDay) {
        switch (weekDay) {
            case "monday":
            case "Monday":
            case "MONDAY":
                return 0;

            case "tuesday":
            case "Tuesday":
            case "TUESDAY":
                return 1;

            case "wednesday":
            case "Wednesday":
            case "WEDNESDAY":
                return 2;

            case "thursday":
            case "Thursday":
            case "THURSDAY":
                return 3;

            case "friday":
            case "Friday":
            case "FRIDAY":
                return 4;

            case "saturday":
            case "Saturday":
            case "SATURDAY":
                return 5;

            case "sunday":
            case "Sunday":
            case "SUNDAY":
                return 6;

            default:
                return 0;
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

    @Override
    public void onRequestDone(String result, int requestId) {
        switch (requestId) {

            case HTTPRequestManager.CALENDAR:
                if (result.equals(ERROR)) {
                    Toast.makeText(this, R.string.failed_connect_calendar, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String success = jsonObject.getString(STATE);
                        if (success.equals(YES)) {
                            new ICalTimeTable(jsonObject);
                        } else {
                            ICalTimeTable.loadTimeTableFromFile();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void onIcalClicked(View view) {

    }

    public void onPreviousClicked(View view) {

    }

    public void onCurrentClicked(View view) {

    }

    public void onNextClicked(View view) {

    }
}
