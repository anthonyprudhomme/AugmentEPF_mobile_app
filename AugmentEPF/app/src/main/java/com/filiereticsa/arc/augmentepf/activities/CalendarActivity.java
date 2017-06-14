package com.filiereticsa.arc.augmentepf.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.interfaces.HTTPRequestInterface;
import com.filiereticsa.arc.augmentepf.localization.GAFrameworkUserTracker;
import com.filiereticsa.arc.augmentepf.managers.HTTP;
import com.filiereticsa.arc.augmentepf.managers.HTTPRequestManager;
import com.filiereticsa.arc.augmentepf.models.Class;
import com.filiereticsa.arc.augmentepf.models.ICalTimeTable;
import com.filiereticsa.arc.augmentepf.models.Place;
import com.filiereticsa.arc.augmentepf.views.CalendarClassView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity implements HTTPRequestInterface {

    public static final int HOUR_DELTA = 7;
    public static final String TOKEN = "token";
    public static final String ID = "id";
    private static final String TAG = "Ici";
    private static final String YES = "y";
    private static final String TRUE = "true";
    private HorizontalScrollView horizontalScrollView;
    private int columnWidth;
    private int rowHeight;
    private GridLayout gridLayout;
    private int staticIndex = 0;
    private int dynamicIndex = 0;
    private int columnNumber = 8;
    private int rowNumber = 14;
    private Calendar calendar = Calendar.getInstance();
    private TextView currentWeek;

    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.gridLayoutContainer);
        currentWeek = (TextView) findViewById(R.id.current_week);
        setCurrentWeekText(calendar);
        initGridLayout();
        askForCalendar();

    }

    private void initGridLayout() {
        gridLayout = new GridLayout(this);
        gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        gridLayout.setColumnCount(columnNumber);
        gridLayout.setRowCount(rowNumber);
        horizontalScrollView.addView(gridLayout);
        addHours();
        addBackGroundCells();
    }

    private void askForCalendar() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ID, ConnectionActivity.idUser);
            jsonObject.put(TOKEN, ConnectionActivity.token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HTTPRequestManager.doPostRequest("getCalendar.php", jsonObject.toString(), this, HTTPRequestManager.CALENDAR);
    }

    private void addBackGroundCells() {
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
                gridLayout.addView(view, staticIndex++);
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
            gridLayout.addView(hourTextView, staticIndex++);
        }
    }

    private void addDays() {
        TextView dayTextView;
        for (int i = 1; i < columnNumber; i++) {
            dayTextView = new TextView(this);
            String day = "";
            switch (i) {
                case 1:
                    day = getString(R.string.monday);
                    break;

                case 2:
                    day = getString(R.string.tuesday);
                    break;

                case 3:
                    day = getString(R.string.wednesday);
                    break;

                case 4:
                    day = getString(R.string.thurday);
                    break;

                case 5:
                    day = getString(R.string.friday);
                    break;

                case 6:
                    day = getString(R.string.saturday);
                    break;

                case 7:
                    day = getString(R.string.sunday);
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
            gridLayout.addView(dayTextView, staticIndex++);
        }
    }

    private void addClasses(Calendar calendar) {
        clearGridLayout();
        if (ICalTimeTable.iCalInstance != null) {
            HashMap<Integer, ArrayList<Class>> classes = ICalTimeTable.iCalInstance.getClasses();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int today = calendar.get(Calendar.DAY_OF_MONTH);

            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
            String weekDay = dayFormat.format(calendar.getTime());
            int daysAfterMonday = getDaysAfterMondayFor(weekDay);
            int indexOfDay = 1;
            Log.d(TAG, "addClasses: before loop");
            for (int currentDay = today - daysAfterMonday; currentDay < today - daysAfterMonday + 7; currentDay++) {
                Log.d(TAG, "addClasses: current day " + currentDay);
                String fixedDay = String.valueOf(currentDay);
                if (fixedDay.charAt(0) == '-') {
                    fixedDay = "0" + fixedDay.charAt(1);
                }
                if (fixedDay.length() < 2) {
                    fixedDay = "0" + fixedDay;
                }
                String fixedMonth = String.valueOf(month);
                if (fixedMonth.length() < 2) {
                    fixedMonth = "0" + fixedMonth;
                }
                String currentDayString = String.valueOf(year) + fixedMonth + fixedDay;
                ArrayList<Class> classesForCurrentDay = classes.get(Integer.valueOf(currentDayString));
                Log.d(TAG, "addClasses: current day and size " + currentDayString + " " + classes.size());
                if (classesForCurrentDay != null) {
                    Log.d(TAG, "addClasses: size: " + classesForCurrentDay.size());
                    for (int i = 0; i < classesForCurrentDay.size(); i++) {
                        Class currentClass = classesForCurrentDay.get(i);
                        Log.d(TAG, "addClasses: " + currentClass.getName());
                        Log.d(TAG, "addClasses: " + currentClass.getStartDate());
                        Log.d(TAG, "addClasses: " + currentClass.getEndDate());
                        Calendar currentStartCalendar = toCalendar(currentClass.getStartDate());
                        Calendar currentEndCalendar = toCalendar(currentClass.getEndDate());
                        addAClass(indexOfDay,
                                currentStartCalendar.get(Calendar.HOUR),
                                currentStartCalendar.get(Calendar.MINUTE),
                                currentEndCalendar.get(Calendar.HOUR),
                                currentEndCalendar.get(Calendar.MINUTE),
                                currentClass);
                    }
                }
                indexOfDay++;
            }
        } else {
            int today = calendar.get(Calendar.DAY_OF_MONTH);
            addAClass(1, ((today + 1) % (rowNumber - 2)) + HOUR_DELTA, today, ((today + 1) % (rowNumber - 2)) + 2 + HOUR_DELTA, today, "2L");
            addAClass(2, ((today + 3) % (rowNumber - 2)) + HOUR_DELTA, today, ((today + 3) % (rowNumber - 2)) + 2 + HOUR_DELTA, today + 20, "2L");
            addAClass(4, ((today + 5) % (rowNumber - 2)) + HOUR_DELTA, today, ((today + 5) % (rowNumber - 2)) + 2 + HOUR_DELTA, today + 10, "3L");
            addAClass(5, ((today + 2) % (rowNumber - 2)) + HOUR_DELTA, today, ((today + 2) % (rowNumber - 2)) + 2 + HOUR_DELTA, today + 5, "4L");
            addAClass(6, ((today + 6) % (rowNumber - 2)) + HOUR_DELTA, today, ((today + 6) % (rowNumber - 2)) + 2 + HOUR_DELTA, today - 5, "5L");
        }
    }

    private void addAClass(int dayNumber, double beginningHour, double beginningMinute,
                           double endHour, double endMinute, final String currentClassName) {

        double hourDelta = endHour - beginningHour;

        ArrayList<CalendarClassView> classViews = new ArrayList<>();
        if (hourDelta == 0) {
            double percentage = ((endMinute - beginningMinute) / 60) * 100;
            classViews.add(new CalendarClassView(this,
                    rowHeight,
                    columnWidth,
                    percentage,
                    beginningMinute,
                    currentClassName,
                    -1, -1));
        } else {
            for (int i = 0; i <= hourDelta; i++) {
                if (i == 0) {
                    double percentage = ((60 - beginningMinute) / 60) * 100;
                    classViews.add(new CalendarClassView(this,
                            rowHeight,
                            columnWidth,
                            percentage,
                            beginningMinute,
                            currentClassName,
                            i, hourDelta));
                } else {
                    if (i == hourDelta) {
                        double percentage = (endMinute / 60) * 100;
                        classViews.add(new CalendarClassView(this,
                                rowHeight,
                                columnWidth,
                                percentage,
                                0,
                                currentClassName,
                                i, hourDelta));
                    } else {
                        classViews.add(new CalendarClassView(this,
                                rowHeight,
                                columnWidth,
                                100,
                                0,
                                currentClassName,
                                i, hourDelta));
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
            if (dynamicIndex == 0) {
                dynamicIndex = staticIndex + dynamicIndex++;
            } else {
                dynamicIndex++;
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create the dialog box
                    final AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this);

                    // Set the style of the dialog box
                    builder.setTitle(R.string.define_path)
                            .setMessage(getString(R.string.go_to) + " " + currentClassName + " ?")
                            // If the user click on "OK"
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    GAFrameworkUserTracker.sharedTracker().setTarget(
                                            Place.getPlaceFromName(currentClassName));
                                    Intent intent = new Intent(CalendarActivity.this, HomePageActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            // Display the dialog box
                            .show();
                }
            });
            gridLayout.addView(view, dynamicIndex);
        }
    }

    private void addAClass(int dayNumber, double beginningHour, double beginningMinute,
                           double endHour, double endMinute, final Class currentClass) {
        double hourDelta = endHour - beginningHour;
        ArrayList<CalendarClassView> classViews = new ArrayList<>();
        if (hourDelta == 0) {
            double percentage = ((endMinute - beginningMinute) / 60) * 100;
            classViews.add(new CalendarClassView(this,
                    rowHeight,
                    columnWidth,
                    percentage,
                    beginningMinute,
                    currentClass,
                    -1, -1));
        } else {
            for (int i = 0; i < hourDelta + 1; i++) {
                if (i == 0) {
                    double percentage = ((60 - beginningMinute) / 60) * 100;
                    classViews.add(new CalendarClassView(this,
                            rowHeight,
                            columnWidth,
                            percentage,
                            beginningMinute,
                            currentClass,
                            i, hourDelta));
                } else {
                    if (i == hourDelta) {
                        double percentage = (endMinute / 60) * 100;
                        classViews.add(new CalendarClassView(this,
                                rowHeight,
                                columnWidth,
                                percentage,
                                0,
                                currentClass,
                                i, hourDelta));
                    } else {
                        classViews.add(new CalendarClassView(this,
                                rowHeight,
                                columnWidth,
                                100,
                                0,
                                currentClass,
                                i, hourDelta));
                    }
                }
            }
        }
        for (int i = 0; i < classViews.size(); i++) {
            Log.d(TAG, "addAClass: " + dayNumber + " " + (int) (beginningHour + HOUR_DELTA + i));
            if (dayNumber > 0 && (int) (beginningHour + HOUR_DELTA + i) > 0) {
                CalendarClassView view = classViews.get(i);
                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.height = LinearLayout.LayoutParams.MATCH_PARENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                param.rightMargin = 3;
                param.leftMargin = 3;
                param.topMargin = 0;
                param.setGravity(Gravity.CENTER);
                param.columnSpec = GridLayout.spec(dayNumber);
                param.rowSpec = GridLayout.spec((int) (beginningHour + HOUR_DELTA + i));
                view.setLayoutParams(param);
                if (dynamicIndex == 0) {
                    dynamicIndex = staticIndex + dynamicIndex++;
                } else {
                    dynamicIndex++;
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Create the dialog box
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

                        // Set the style of the dialog box
                        if (currentClass != null && currentClass.getClassRoom() != null) {
                            builder.setTitle(R.string.define_path)
                                    .setMessage(getString(R.string.go_to) + currentClass.getClassRoom().getName() + " ?")
                                    // If the user click on "OK"
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            GAFrameworkUserTracker
                                                    .sharedTracker()
                                                    .setTarget(currentClass.getClassRoom());

                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    // Display the dialog box
                                    .show();
                        }
                    }
                });
                gridLayout.addView(view, dynamicIndex);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (columnWidth != gridLayout.getWidth() / columnNumber) {
            Log.d(TAG, "onWindowFocusChanged: ");
            columnWidth = gridLayout.getWidth() / columnNumber;
            rowHeight = gridLayout.getHeight() / rowNumber;
            addDays();
            addClasses(calendar);

        }
    }

    private void clearGridLayout() {
        for (int i = dynamicIndex; i >= staticIndex; i--) {
            gridLayout.removeViewAt(i);
        }
        dynamicIndex = 0;
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

    @Override
    public void onRequestDone(String result, int requestId) {
        switch (requestId) {

            case HTTPRequestManager.CALENDAR:
                Log.d(TAG, "onRequestDone: " + result);
                if (result.equals(HTTP.ERROR)) {
                    Toast.makeText(this, R.string.failed_connect_calendar, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String success = jsonObject.getString(HTTP.STATE);
                        if (success.equals(HTTP.YES)) {
                            new ICalTimeTable(jsonObject);
                        } else {
                            ICalTimeTable.loadTimeTableFromFile();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case HTTPRequestManager.ICAL:
                if (result.equals(HTTP.ERROR)) {
                    Toast.makeText(this, R.string.error_server, Toast.LENGTH_SHORT).show();
                }
                // Put the result in a JSONObject to use it.
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);
                    String state = jsonObject.getString(HTTP.STATE);
                    // check is the request was a success or not
                    if (state.equals(HTTP.TRUE)) {
                        askForCalendar();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }

    public void onIcalClicked(View view) {
        // Create the dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_ical_link_calendar_activity, null);

        // Get the text in all fields
        final EditText icalEditText = (EditText) rootView.findViewById(R.id.ical_link);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String iCalValue = sharedPreferences.getString("ical", "");
        icalEditText.setText(iCalValue);
        // Set the style of the dialog box
        builder.setView(rootView)
                // Set the title of the dialog box
                .setTitle(R.string.enter_ical)
                // If the user click on "OK"
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Transform text in fields in String
                        String iCal = icalEditText.getText().toString();
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                        prefEditor.putString("ical", iCal);
                        prefEditor.apply();
                        sendIcalLink(sharedPreferences);
                    }
                })
                // Display the dialog box
                .show();
    }

    public void sendIcalLink(SharedPreferences sharedPreferences) {
        if (!sharedPreferences.getString("ical", "").equals("")) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", ConnectionActivity.idUser);
                jsonObject.put("token", ConnectionActivity.token);
                jsonObject.put("ical", sharedPreferences.getString("ical", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HTTPRequestManager.doPostRequest("addIcalLink.php", jsonObject.toString(), this, HTTPRequestManager.ICAL);
        }
    }

    private void setCurrentWeekText(Calendar calendar) {
        SimpleDateFormat dayAndMonthFormat = new SimpleDateFormat("d MMMM", Locale.FRANCE);
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.FRANCE);
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        // get current weekday as a string
        String weekDay = dayFormat.format(calendar.getTime());
        // get the number of days after monday from the current calendar
        int daysAfterMonday = getDaysAfterMondayFor(weekDay);
        // set the calendar to the beginning of the week
        calendar.add(Calendar.DAY_OF_YEAR, -daysAfterMonday);

        String beginningWeekString = dayAndMonthFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, +6);
        String endingWeekString = dayAndMonthFormat.format(calendar.getTime());
        String yearString = yearFormat.format(calendar.getTime());
        currentWeek.setText(beginningWeekString + " " + getResources().getString(R.string.to) + " " + endingWeekString + " " + yearString);
    }

    public void onPreviousClicked(View view) {
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        setCurrentWeekText(calendar);
        addClasses(calendar);
    }

    public void onCurrentClicked(View view) {
        calendar = Calendar.getInstance();
        setCurrentWeekText(calendar);
        addClasses(calendar);
    }

    public void onNextClicked(View view) {
        calendar.add(Calendar.DAY_OF_YEAR, +7);
        setCurrentWeekText(calendar);
        addClasses(calendar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
