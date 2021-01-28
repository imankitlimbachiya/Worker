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
import com.worker.app.activity.WorkerProfileActivity;
import com.worker.app.model.MatchingCategoryModel;
import com.worker.app.utility.MyTextView;

public class MatchingWorkersAdapter extends RecyclerView.Adapter<MatchingWorkersAdapter.MyViewHolder> {

    private List<MatchingCategoryModel> listWorker;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgWorker;
        private MyTextView txtWorkerName,txtWorkerCountry,txtWorkerContractFees, txtWorkerReligion, txtWorkerAge;
        private MyTextView txtWorkerMSalary,txtViewProfile,txtProfession,txtHireNow;

        public MyViewHolder(View view) {
            super(view);
            imgWorker = view.findViewById(R.id.imgWorker);
            txtWorkerName = view.findViewById(R.id.txtWorkerName);
            txtWorkerCountry = view.findViewById(R.id.txtWorkerCountry);
            txtWorkerContractFees= view.findViewById(R.id.txtWorkerContractFees);
            txtWorkerReligion = view.findViewById(R.id.txtWLabelReligion);
            txtWorkerAge = view.findViewById(R.id.txtWorkerAge);
            txtWorkerMSalary = view.findViewById(R.id.txtWorkerMSalary);
            txtViewProfile = view.findViewById(R.id.txtViewProfile);
            txtProfession = view.findViewById(R.id.txtProfession);
            txtHireNow = view.findViewById(R.id.txtHireNow);
        }
    }

    public MatchingWorkersAdapter(Context mContext, List<MatchingCategoryModel> listWorker) {
        this.mContext=mContext;
        this.listWorker = listWorker;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_category_matching_worker, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MatchingCategoryModel MatchingCategoryModel = listWorker.get(position);
        Glide.with(mContext).load(MatchingCategoryModel.getWorkerImgProfile()).into(holder.imgWorker);

        holder.txtWorkerName.setText(MatchingCategoryModel.getWorkerName());
        holder.txtWorkerCountry.setText(MatchingCategoryModel.getwCountry());
        holder.txtWorkerContractFees.setText(MatchingCategoryModel.getwContractFees());
        holder.txtWorkerReligion.setText(MatchingCategoryModel.getwReligion());
        holder.txtWorkerAge.setText(MatchingCategoryModel.getwAge());
        holder.txtWorkerMSalary.setText(MatchingCategoryModel.getwSalaryMonthly());

        holder.imgWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, WorkerProfileActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listWorker.size();
    }
}
