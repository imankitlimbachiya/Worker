package com.worker.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import com.worker.app.R;
import com.worker.app.activity.SubCategoryActivity;
import com.worker.app.model.BannerModel;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.MyViewHolder> {

    private List<BannerModel> listSlider;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgSlider;

        public MyViewHolder(View view) {
            super(view);
            imgSlider = view.findViewById(R.id.imgSlider);
        }
    }

    public SliderAdapter(Context mContext, List<BannerModel> listSlider) {
        this.mContext = mContext;
        this.listSlider = listSlider;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_slider, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BannerModel singleItem = listSlider.get(position);
        if (singleItem.getBannerImage().equals("") || singleItem.getBannerImage().equals("null") || singleItem.getBannerImage().equals(null) || singleItem.getBannerImage() == null) {
        } else {
            Glide.with(mContext).load(singleItem.getBannerImage()).into(holder.imgSlider);
        }
        holder.imgSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (singleItem.getCriteriaType().equals("0")) {
                    holder.imgSlider.setClickable(false);
                } else {
                    Intent intent_find_worker = new Intent(mContext, SubCategoryActivity.class);
                    intent_find_worker.putExtra("Search_by_value", singleItem.getBannerID());
                    intent_find_worker.putExtra("Search_type",1);
                    intent_find_worker.putExtra("StripImage", singleItem.getStripImage());
                    intent_find_worker.putExtra("BannerTitleArabic", singleItem.getBannerTitleArabic());
                    intent_find_worker.putExtra("BannerTitle", singleItem.getBannerTitle());
                    mContext.startActivity(intent_find_worker);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listSlider.size();
    }
}
