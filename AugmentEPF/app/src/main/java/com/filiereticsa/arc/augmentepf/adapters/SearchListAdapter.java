package com.filiereticsa.arc.augmentepf.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.filiereticsa.arc.augmentepf.R;
import com.filiereticsa.arc.augmentepf.models.ClassRoom;
import com.filiereticsa.arc.augmentepf.models.Place;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Cécile on 21/05/2017.
 */

public class SearchListAdapter extends ArrayAdapter<Place> implements SharedPreferences.OnSharedPreferenceChangeListener {

    public SearchListAdapter(Context context, ArrayList<Place> list) {
        super(context, 0,list);

    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        notifyDataSetChanged();
    }

    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_search_list, parent, false);
        }

        Place place = getItem(position);
        TextView name = (TextView) view.findViewById(R.id.room_name);
        name.setText(place.getName());

        return view;
    }

}
