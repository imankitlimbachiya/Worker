package com.worker.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import com.worker.app.R;
import com.worker.app.activity.SubCategoryActivity;
import com.worker.app.model.CountryModel;
import com.worker.app.utility.MyTextView;

public class HomeCountryAdapter extends RecyclerView.Adapter<HomeCountryAdapter.MyViewHolder> {

    private List<CountryModel> listCountry;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgCountry;
        private MyTextView txtArabic,txtEnglish;
        RelativeLayout country;

        public MyViewHolder(View view) {
            super(view);

            imgCountry = view.findViewById(R.id.imgCountry);
            txtArabic = view.findViewById(R.id.txtArabic);
            txtEnglish = view.findViewById(R.id.txtEnglish);
            country = view.findViewById(R.id.country);
        }
    }

    public HomeCountryAdapter(Context mContext, List<CountryModel> listCountry) {
        this.mContext=mContext;
        this.listCountry = listCountry;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_country, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        CountryModel singleItem = listCountry.get(position);

        if (singleItem.getCountryName().equals("") || singleItem.getCountryName().equals("null") || singleItem.getCountryName().equals(null) || singleItem.getCountryName() == null) {

        } else {
            holder.txtEnglish.setText(singleItem.getCountryName());
        }

        if (singleItem.getCountryNameArabic().equals("") || singleItem.getCountryNameArabic().equals("null") || singleItem.getCountryNameArabic().equals(null) || singleItem.getCountryNameArabic() == null) {

        } else {
            holder.txtArabic.setText(singleItem.getCountryNameArabic());
        }

        if (singleItem.getCountryFlag().equals("") || singleItem.getCountryFlag().equals("null") || singleItem.getCountryFlag().equals(null) || singleItem.getCountryFlag() == null) {

        } else {
            Glide.with(mContext)
                    .load(singleItem.getCountryFlag())
                    .into(holder.imgCountry);
        }

        holder.country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_find_worker = new Intent(mContext, SubCategoryActivity.class);
                intent_find_worker.putExtra("Search_by_value", singleItem.getCountryId());
                intent_find_worker.putExtra("CountryName", singleItem.getCountryName());
                intent_find_worker.putExtra("CountryNameArabic", singleItem.getCountryNameArabic());
                intent_find_worker.putExtra("StripImage", singleItem.getStripImage());
                intent_find_worker.putExtra("Search_type",3);
                mContext.startActivity(intent_find_worker);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCountry.size();
    }
}


