package com.worker.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import com.worker.app.R;
import com.worker.app.model.CityModel;
import com.worker.app.utility.MyTextView;

public class SpinnerCityAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflter;
    private ArrayList<CityModel> listCity=new ArrayList<>();

    public SpinnerCityAdapter(Context applicationContext, ArrayList<CityModel> listCity) {
        this.context = applicationContext;
        this.listCity = listCity;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return listCity.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner_city_item, null);
        MyTextView txtCityName = view.findViewById(R.id.txtCityName);

        txtCityName.setText(listCity.get(i).getCityName());
        return view;
    }
}