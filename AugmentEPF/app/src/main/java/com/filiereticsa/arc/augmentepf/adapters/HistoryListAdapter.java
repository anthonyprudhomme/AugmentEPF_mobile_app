package com.filiereticsa.arc.augmentepf.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.models.Path;
import com.filiereticsa.arc.augmentepf.models.Place;
import com.filiereticsa.arc.augmentepf.models.PlannedPath;
import com.filiereticsa.arc.augmentepf.models.Position;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by ARC© Team for AugmentEPF project on 07/06/2017.
 */

public class HistoryListAdapter extends ArrayAdapter<Path> {

    public HistoryListAdapter(Context context, ArrayList<Path> list) {
        super(context, 0, list);
    }

    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            LayoutInflater inflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // Inflate the view with the custom row item
            view = inflater.inflate(R.layout.item_history_path_list, parent, false);
        }

        // Get the graphic elements of the row item
        ImageView coloredBar = (ImageView) view.findViewById(R.id.colored_bar);
        ImageView historyIcon = (ImageView) view.findViewById(R.id.history_item_icon);

        // Get each textView of the row item view
        TextView departureName = (TextView) view.findViewById(R.id.departure_name);
        TextView arrivalName = (TextView) view.findViewById(R.id.arrival_name);
        TextView startTime = (TextView) view.findViewById(R.id.start_time);
        TextView startDate = (TextView) view.findViewById(R.id.start_date);

        // Get the path from the ArrayList by its position
        Path path = getItem(position);

        if (path != null) {

            // Set the corresponding item view graphics elements for either planned or history path
            // If path is an instance of PlannedPath
            if (path instanceof PlannedPath) {
                coloredBar.setBackgroundResource(R.color.colorPrimary);
                historyIcon.setBackgroundResource(R.drawable.planned_icon);

            } else { // else path is an history path
                coloredBar.setBackgroundResource(R.color.colorLightGrey);
                historyIcon.setBackgroundResource(R.drawable.history_icon);
            }

            // Get the departure and the arrival places from the path
            Position departure = path.getDeparture();
            Place arrival = path.getArrival();

            // Set the texts in the view
            // TODO /!\ departureName.setText(departure.getName());
            arrivalName.setText(arrival.getName());

            // Set the time format
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm a");
            // Get the departure time from the path and the departure date
            long dateTime = path.getDepartureDate().getTime();
            String time = timeFormat.format(dateTime);

            // Set the time in the corresponding textView
            startTime.setText(time);

            // Set date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy, EEEE dd MMMM ");
            String date = dateFormat.format(path.getDepartureDate());

            // Set the date in the corresponding textView
            startDate.setText(date);
        }

        return view;
    }
}