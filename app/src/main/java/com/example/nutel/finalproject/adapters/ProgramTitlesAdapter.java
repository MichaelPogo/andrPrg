package com.example.nutel.finalproject.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nutel.finalproject.R;
import java.util.List;
import java.util.Map;

public class ProgramTitlesAdapter extends BaseAdapter {

    private final String key;
    private final Context context;
    private final List<Map> categories;

    public ProgramTitlesAdapter(Context context, List <Map> categories, String key){
        this.context = context;
        this.categories = categories;
        this.key=key;
    }
    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Map getItem(int i) {
        return categories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View rView, ViewGroup parent) {
        if(rView==null)rView = LayoutInflater.from(context).inflate(R.layout.category,null);
        final Map ctg=categories.get(i);//get category data by index
        ((TextView)rView).setText(""+ctg.get(key));


        return rView;
    }
}
