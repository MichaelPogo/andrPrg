package com.example.nutel.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nutel.finalproject.Exercise;
import com.example.nutel.finalproject.R;

import java.util.ArrayList;

public class ProgramContentAdpater extends BaseAdapter {
    private Context context;
    private ArrayList<Exercise> data;

    public ProgramContentAdpater(ArrayList <Exercise> data,Context context) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Exercise getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View rView, ViewGroup parent) {
        if(rView==null) rView= LayoutInflater.from(context).inflate(R.layout.build_program_exercise,null);
        TextView description =(TextView)rView.findViewById(R.id.description);
        TextView name =(TextView)rView.findViewById(R.id.name);
        description.setText(data.get(i).getDescription());
        name.setText(data.get(i).getName());
        return rView;
    }
}
