package com.worker.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import com.worker.app.R;
import com.worker.app.activity.ViewOrdreDeliveryActivity;
import com.worker.app.model.MyWorkersModel;
import com.worker.app.utility.MyTextView;

public class ListMyWorkersAdapter extends RecyclerView.Adapter<ListMyWorkersAdapter.MyViewHolder> {

    private List<MyWorkersModel> arraylist;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private MyTextView txt_name, txt_contractFees, txt_orderId, txt_status, txt_timeline;
        ImageView image_worker;
        LinearLayout lin_main;

        public MyViewHolder(View view) {
            super(view);
            txt_name = view.findViewById(R.id.txt_name);
            txt_contractFees = view.findViewById(R.id.txt_contractFees);
            txt_orderId = view.findViewById(R.id.txt_orderId);
            txt_status = view.findViewById(R.id.txt_status);
            txt_timeline = view.findViewById(R.id.txt_timeline);
            image_worker = view.findViewById(R.id.image_worker);
            lin_main = view.findViewById(R.id.lin_main);

        }
    }

    public ListMyWorkersAdapter(Context mContext, List<MyWorkersModel> arraylist) {
        this.mContext = mContext;
        this.arraylist = arraylist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_my_worker_details, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyWorkersModel myWorkersModel = arraylist.get(position);
        holder.txt_name.setText(myWorkersModel.getWorkerName());
        holder.txt_contractFees.setText(myWorkersModel.getContactFees());
        holder.txt_orderId.setText(myWorkersModel.getOrderID());
        holder.txt_status.setText(myWorkersModel.getStatus());
        holder.txt_timeline.setText(myWorkersModel.getTimeline());
        Glide.with(mContext)
                .load(myWorkersModel.getWorkerImage())
                .into(holder.image_worker);
        holder.lin_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, ViewOrdreDeliveryActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }
}
