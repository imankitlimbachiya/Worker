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

public class WorkerEducationAdapter extends RecyclerView.Adapter<WorkerEducationAdapter.MyViewHolder> {

    private List<WorkerProfileResponse.Education> listNotifications;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private MyTextView txtDegree, txtUni, txtYear;
//        private View viewLine;

        public MyViewHolder(View view) {
            super(view);
            txtDegree = view.findViewById(R.id.txtDegree);
            txtUni = view.findViewById(R.id.txtUni);
            txtYear = view.findViewById(R.id.txtYear);
//            viewLine=view.findViewById(R.id.viewLine);
        }
    }

    public WorkerEducationAdapter(List<WorkerProfileResponse.Education> listNotifications) {
        this.listNotifications = listNotifications;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_education_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        WorkerProfileResponse.Education notificationInfo = listNotifications.get(position);

        Log.e("NA", "## notificationInfo.getTitle() :: " + notificationInfo.getDegree());
        if (!notificationInfo.getDegree().equals("null"))
            holder.txtDegree.setText(notificationInfo.getDegree());

        if (!notificationInfo.getCollege().equals("null"))
            holder.txtUni.setText(notificationInfo.getCollege());

        if (notificationInfo.getDate() != null)
            holder.txtYear.setText(notificationInfo.getDate());

//        if(position==listNotifications.size()-1)
//            holder.viewLine.setVisibility(View.GONE);
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
