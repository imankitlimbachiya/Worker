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
import com.worker.app.model.HomeCategoryModel;


public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.MyViewHolder> {

    private List<HomeCategoryModel> listCategory;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgCategory;

        public MyViewHolder(View view) {
            super(view);
            imgCategory = view.findViewById(R.id.imgCategory);
        }
    }

    public HomeCategoryAdapter(Context mContext, List<HomeCategoryModel> listCategory) {
        this.mContext=mContext;
        this.listCategory = listCategory;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_categories, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HomeCategoryModel singleItem = listCategory.get(position);
        if (singleItem.getHomeScreenImage().equals("") || singleItem.getHomeScreenImage().equals("null") || singleItem.getHomeScreenImage().equals(null) || singleItem.getHomeScreenImage() == null) {
        } else {
            Glide.with(mContext).load(singleItem.getHomeScreenImage()).into(holder.imgCategory);
        }
        holder.imgCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_find_worker = new Intent(mContext, SubCategoryActivity.class);
                intent_find_worker.putExtra("Search_by_value", singleItem.getCategoryID());
                intent_find_worker.putExtra("CategoryName", singleItem.getCategoryName());
                intent_find_worker.putExtra("CategoryNameArabic", singleItem.getCategoryNameArabic());
                intent_find_worker.putExtra("StripImage", singleItem.getStripImage());
                intent_find_worker.putExtra("Search_type",2);
                mContext.startActivity(intent_find_worker);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCategory.size();
    }
}
