package com.worker.app.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.worker.app.R;
import com.worker.app.model.WorkerProfileResponse;
import com.worker.app.utility.MyTextView;

public class WorkerExperienceAdapter extends RecyclerView.Adapter<WorkerExperienceAdapter.MyViewHolder> {

    private List<WorkerProfileResponse.Experience> listNotifications;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private MyTextView txtJobTitle, txtCompanyName, txtExp,txtDepartmnet;
//        private View viewLine;

        public MyViewHolder(View view) {
            super(view);
            txtJobTitle = view.findViewById(R.id.txtJobTitle);
            txtCompanyName = view.findViewById(R.id.txtCompanyName);
            txtExp = view.findViewById(R.id.txtExp);
//            viewLine=view.findViewById(R.id.viewLine);
            txtDepartmnet=view.findViewById(R.id.txtDepartmnet);
        }
    }

    public WorkerExperienceAdapter(List<WorkerProfileResponse.Experience> listNotifications) {
        this.listNotifications = listNotifications;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_exp_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        WorkerProfileResponse.Experience notificationInfo = listNotifications.get(position);

        Log.e("NA", "## notificationInfo.getTitle() :: " + notificationInfo.getJobTitle());
        if (!notificationInfo.getJobTitle().equals("null"))
            holder.txtJobTitle.setText(notificationInfo.getJobTitle());

        if (!notificationInfo.getCompanyName().equals("null"))
            holder.txtCompanyName.setText(notificationInfo.getCompanyName());

        if (notificationInfo.getYearsOfExp() != null)
            holder.txtExp.setText(notificationInfo.getYearsOfExp().replace(",",""));

        if (notificationInfo.getDepartment() != null)
            holder.txtDepartmnet.setText(notificationInfo.getDepartment());

       /* if(position==listNotifications.size()-1)
            holder.viewLine.setVisibility(View.GONE);*/
    }

    public String convertDateNotify(String dateOriginal) {
        String formattedDate = "";
        if (!dateOriginal.equals("")) {
            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);//2019-08-29 10:01:05
            DateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            Date date = null;
            try {
                date = originalFormat.parse(dateOriginal);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            formattedDate = targetFormat.format(date);  // 5 May
        }
        return formattedDate;
    }

    @Override
    public int getItemCount() {
        return listNotifications.size();
    }
}

