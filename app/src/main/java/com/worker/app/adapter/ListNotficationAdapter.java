package com.worker.app.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.worker.app.R;
import com.worker.app.model.NotificationModel;
import com.worker.app.utility.MyTextView;

public class ListNotficationAdapter extends RecyclerView.Adapter<ListNotficationAdapter.MyViewHolder> {

    private List<NotificationModel> arraylist;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        private MyTextView txt_time,txt_day,txt_notification;

        public MyViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            txt_time = view.findViewById(R.id.txt_time);
            txt_day = view.findViewById(R.id.txt_day);
            txt_notification = view.findViewById(R.id.txt_notification);
        }
    }

    public ListNotficationAdapter(Context mContext, List<NotificationModel> arraylist) {
        this.mContext=mContext;
        this.arraylist = arraylist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_notification, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NotificationModel notificationModel=arraylist.get(position);

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
        String inputDateStr = notificationModel.getNotificationDate();
        Date date = null;
        try {
            date = inputFormat.parse(inputDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputDateStr = outputFormat.format(date);
        holder.txt_time.setText(notificationModel.getNotificationTime());
        holder.txt_day.setText(outputDateStr);
        String string = "<font color='#767877'>" + notificationModel.getNotification()+"</font>"+"<b><font color='#2969cb'> " + "</b></font>";
        holder.txt_notification.setText(Html.fromHtml(string), TextView.BufferType.SPANNABLE);

    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }
}
