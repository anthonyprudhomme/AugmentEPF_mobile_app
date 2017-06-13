package com.filiereticsa.arc.augmentepf.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.models.ClassRoom;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PathPlanningActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "PathPlanningActivity";

    private SharedPreferences settings;

    private Calendar calendar;
    // Departure buttons
    private Button departureDateButton;
    private Button departureHourButton;
    // Arrival spinner
    private AutoCompleteTextView searchClassroom;
    // Warning buttons
    private Switch warningSwitch;
    private Button warningDateButton;
    private Button warningHourButton;
    private Spinner warningAlarmType;

    // To determine if it's departure or warning date (0: none|1: departure|2: warning)
    private int whichDate;
    private int whichHour; // Same for hours

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_planning);

        // Get preferences in settings.xml
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        // Hide fields for warning
        collapse(findViewById(R.id.warningLayout));

        // Put the date of today in the calendar
        calendar = Calendar.getInstance();

        /*==========================================================================================
        |                                The reference of the buttons                              |
        ==========================================================================================*/
        //Departure
        departureDateButton = (Button) findViewById(R.id.dateButton); // departure date
        departureHourButton = (Button) findViewById(R.id.hourButton); // departure hour
        //..........................................................................................
        // Warning
        warningSwitch = (Switch) findViewById(R.id.switch_warning); // warning switch
        warningDateButton = (Button) findViewById(R.id.warningDateButton); // warning date
        warningHourButton = (Button) findViewById(R.id.warningHourButton); // warning hour
        warningAlarmType = (Spinner) findViewById(R.id.warningAlerts);
        /*========================================================================================*/


        /*==========================================================================================
        |                              Autcompletion for classrooms                                |
        ==========================================================================================*/
        // Get the string array from the classroom list
        String[] allClassrooms = ClassRoom.getClassroomsAsStrings();

        // Get the AutoCompleteTextView created in file main.xml
        searchClassroom =
                (AutoCompleteTextView) findViewById(R.id.search_classroom);

        // Get the button to validate created in file main.xml
        Button searchButton = (Button) findViewById(R.id.button_send);

        // Create an autocompletion list with string array entryUser
        // "simple_dropdown_item_1line" is a display stlye
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, allClassrooms);

        // Put the autocompletion list in our object of autocompletion
        searchClassroom.setAdapter(adapter);

        // Put a listener on the button to validate to display a Toast with the text in the field
        searchButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(PathPlanningActivity.this, searchClassroom.getText(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        /*========================================================================================*/
    }

    public void setDepartureDate(View view) {
        // Create the dialog for the date
        DatePickerDialog datePicker = new DatePickerDialog(
                view.getContext(),
                PathPlanningActivity.this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        whichDate = 1; // To edit the good date

        // Display the dialog
        datePicker.show();
    }

    public void setWarningDate(View view) {
        // Create the dialog for the date
        DatePickerDialog datePicker = new DatePickerDialog(
                view.getContext(),
                PathPlanningActivity.this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        whichDate = 2; // To edit the good date

        // Display the dialog
        datePicker.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        /*Log.d(TAG, "onDateSet() called with: view = [" + view + "], year = [" + year + "], month = ["
                + month + "], dayOfMonth = [" + dayOfMonth + "]");*/

        // Set the date of today
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        // Set the format of date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // Set a date
        String date = simpleDateFormat.format(calendar.getTime());

        // Put the date in the date button corresponding
        switch (whichDate) {
            case 1: // Departure
                departureDateButton.setText(date);
                break;
            case 2: // Warning
                int goodDate = comparateDates(date);
                switch (goodDate) {
                    case -1: // No departure Date
                        Toast.makeText(PathPlanningActivity.this,
                                "Please enter the departure date",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case 0: // Error with date
                        Toast.makeText(PathPlanningActivity.this,
                                "Please choose a date before the departure date",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        warningDateButton.setText(date);
                        break;
                    default:
                        Toast.makeText(PathPlanningActivity.this,
                                "I don't know what is this error",
                                Toast.LENGTH_SHORT).show();
                }
        }
        whichDate = 0;
    }

    // Similar with the function below
    public void setDepartureHour(View view) {
        // Create the dialog for the hour
        TimePickerDialog hourPicker = new TimePickerDialog(
                view.getContext(),
                PathPlanningActivity.this,
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                true);

        whichHour = 1; // To edit the good hour

        // Display the dialog
        hourPicker.show();
    }

    // Similar with the function below
    public void setWarningHour(View view) {
        // Create the dialog for the hour
        TimePickerDialog hourPicker = new TimePickerDialog(
                view.getContext(),
                PathPlanningActivity.this,
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                true);

        whichHour = 2; // To edit the good hour

        // Display the dialog
        hourPicker.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        /*Log.d(TAG, "onTimeSet called with: view = [" + view + "], hourOfDay = [" + hourOfDay +
                "], minute = [" + minute + "]");*/

        // Set the date of today
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        // Set the format of hour
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");

        // Set an hour
        String hour = hourFormat.format(calendar.getTime());

        // Put the hour in the hour button corresponding
        switch (whichHour) {
            case 1: // Departure
                departureHourButton.setText(hour);
                break;
            case 2: // Warning
                warningHourButton.setText(hour);
                break;
        }
        whichHour = 0;
    }

    private int comparateDates(CharSequence warningDate) {
        // Boolean to know if the date is good, initialization: false
        int goodDate = 0;

        // Departure date
        CharSequence departureDate = departureDateButton.getText();
        if (Character.toString(departureDate.charAt(0)).equals("C") == false) {
            int departureDay = Integer.parseInt(
                    Character.toString(departureDate.charAt(0))
                            + Character.toString(departureDate.charAt(1))
            );
            int departureMonth = Integer.parseInt(
                    Character.toString(departureDate.charAt(3))
                            + Character.toString(departureDate.charAt(4))
            );
            int departureYear = Integer.parseInt(
                    Character.toString(departureDate.charAt(6))
                            + Character.toString(departureDate.charAt(7))
                            + Character.toString(departureDate.charAt(8))
                            + Character.toString(departureDate.charAt(9))
            );

            // Warning date
            int warningDay = Integer.parseInt(
                    Character.toString(warningDate.charAt(0))
                            + Character.toString(warningDate.charAt(1))
            );
            int warningMonth = Integer.parseInt(
                    Character.toString(warningDate.charAt(3))
                            + Character.toString(warningDate.charAt(4))
            );
            int warningYear = Integer.parseInt(
                    Character.toString(warningDate.charAt(6))
                            + Character.toString(warningDate.charAt(7))
                            + Character.toString(warningDate.charAt(8))
                            + Character.toString(warningDate.charAt(9))
            );

            // Comparison of dates
            if (warningYear <= departureYear) {
                if (warningMonth <= departureMonth) {
                    if (warningDay <= departureDay) {
                        goodDate = 1;
                    }
                }
            }
        } else {
            goodDate = -1;
        }

        return goodDate;
    }

    public void setWarningDisplay(View view) {
        if (warningSwitch.isChecked()) { // Display the other fields
            expand(findViewById(R.id.warningLayout));
        } else { // Hide fields
            collapse(findViewById(R.id.warningLayout));
        }
    }

    public void sendThePath(View view) {
        // TODO Find the ID of the user

        // Arrival position
        String arrivalClassroom = searchClassroom.getText().toString();
        // TODO Convert the arrivalClassroom to coordinates (x,y,z)

        // If the user take or not the elevator
        Boolean elevator = false;
        String elevatorValue = settings.getString("specific_attribute_user", "None");
        if (elevatorValue.equals("Elevator")) {
            elevator = true;
        }

        // The departure date
        String separator = "-";
        String departureDate = departureDateButton.getText().toString()
                + separator
                + departureHourButton.getText().toString();

        // The type of alarm
        String alarmType = warningAlarmType.toString();

        // TODO Post on the DB
    }
}