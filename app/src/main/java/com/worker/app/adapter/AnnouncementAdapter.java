package com.worker.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import com.worker.app.R;
import com.worker.app.activity.SubCategoryActivity;
import com.worker.app.model.AnnouncementModel;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.MyViewHolder> {

    private List<AnnouncementModel> listSlider;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
        }
    }

    public AnnouncementAdapter(Context mContext, List<AnnouncementModel> listSlider) {
        this.mContext = mContext;
        this.listSlider = listSlider;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_announcement, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        AnnouncementModel model = listSlider.get(position);

        if (model.getAnnouncementImage().equals("") || model.getAnnouncementImage().equals("null") || model.getAnnouncementImage().equals(null) || model.getAnnouncementImage() == null) {
        } else {
            Glide.with(mContext).load(model.getAnnouncementImage()).into(holder.image);
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.e("AD","## model.getCategoryID() : "+model.getCategoryID());
//                Log.e("AD","## model.getWorkerID() : "+model.getWorkerID());
                if (model.getCountryID() != null) {
                    Log.e("AD", "## ids not null ");
                    if (!model.getCountryID().equals("0")) {
                        Log.e("AD", "## ids not zero ");
                        if (!model.getCountryID().equals("null")) {
                            Log.e("AD", "## model.getCountryID() : " + model.getCountryID());
                            Intent intent_find_worker = new Intent(mContext, SubCategoryActivity.class);
                            intent_find_worker.putExtra("Search_by_value", model.getCountryID());
                            intent_find_worker.putExtra("Search_type", 3);
                            intent_find_worker.putExtra("BannerTitle", model.getCategoryName());
                            intent_find_worker.putExtra("BannerTitleArabic", model.getCountryNameArabic());
                            intent_find_worker.putExtra("StripImage", model.getStripImage());
                            mContext.startActivity(intent_find_worker);
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listSlider.size();
    }
}
