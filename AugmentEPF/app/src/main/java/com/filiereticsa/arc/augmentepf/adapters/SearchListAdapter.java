package com.filiereticsa.arc.augmentepf.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.filiereticsa.arc.augmentepf.Models.ClassRoom;
import com.filiereticsa.arc.augmentepf.R;

import java.util.List;

/**
 * Created by Cécile on 21/05/2017.
 */

public class SearchListAdapter extends ArrayAdapter<ClassRoom> implements SharedPreferences.OnSharedPreferenceChangeListener{

    public SearchListAdapter(Context context, List<ClassRoom> listClient) {
        super(context,0, listClient);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        notifyDataSetChanged();
    }

    public View getView(int position, View view, ViewGroup parent) {

        if(view == null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_search_list, parent,false);
        }

        ClassRoom classRoom = getItem(position);
        TextView name = (TextView)view.findViewById(R.id.room_name);
        name.setText(classRoom.getNom());

        return view;
    }

}
