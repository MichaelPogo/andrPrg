package com.example.nutel.finalproject.adapters;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.nutel.finalproject.Exercise;
import com.example.nutel.finalproject.R;
import com.example.nutel.finalproject.fragments.ProgramFragment;

public class AddProgAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Exercise> exercises;
    static CheckBox checkBox;

    public AddProgAdapter(Context context, ArrayList<Exercise>exercises){
        this.context = context;
        this.exercises = exercises;
    }
    @Override
    public int getCount() {
        return exercises.size();
    }

    @Override
    public Object getItem(int i) {
        return exercises.get(i);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View rView, ViewGroup parent) {
        if(rView==null) rView= LayoutInflater.from(context).inflate(R.layout.build_program_exercise,null);
        TextView description =(TextView)rView.findViewById(R.id.description);
        TextView name =(TextView)rView.findViewById(R.id.name);
        checkBox = (CheckBox)rView.findViewById(R.id.checkbox) ;
        if(ProgramFragment.getFinishBtn().getVisibility()==View.GONE){
            checkBox.setChecked(exercises.get(i).isDone());
            checkBox.setVisibility(View.VISIBLE);
        }else checkBox.setVisibility(View.GONE);

        description.setText(exercises.get(i).getDescription());
        name.setText(exercises.get(i).getName());
        description.setMovementMethod(new ScrollingMovementMethod());
        name.setMovementMethod(new ScrollingMovementMethod());
        return rView;
    }

}
