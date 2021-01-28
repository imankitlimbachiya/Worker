package com.worker.app.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.worker.app.R;
import com.worker.app.model.OrderDeliveryModel;
import com.worker.app.utility.MyTextView;

public class ListOrderDeliveryAdapter extends RecyclerView.Adapter<ListOrderDeliveryAdapter.MyViewHolder> {

    private List<OrderDeliveryModel> arraylist;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image_greenRound, image_shortline, image_shortline2, image_shortline4;
        MyTextView title, date, time_shift, txt_description;

        public MyViewHolder(View view) {
            super(view);
            image_greenRound = view.findViewById(R.id.image_greenRound);
            title = view.findViewById(R.id.title);
            date = view.findViewById(R.id.date);
            time_shift = view.findViewById(R.id.time_shift);
            txt_description = view.findViewById(R.id.txt_description);
        }
    }

    public ListOrderDeliveryAdapter(Context mContext, List<OrderDeliveryModel> arraylist) {
        this.mContext = mContext;
        this.arraylist = arraylist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_order_delivery_detail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        OrderDeliveryModel orderDeliveryModel = arraylist.get(position);
        holder.title.setText(orderDeliveryModel.getStatus());
        holder.date.setText(orderDeliveryModel.getCreatedDate());
        holder.time_shift.setText(orderDeliveryModel.getTime());
        holder.txt_description.setText(orderDeliveryModel.getDescription());

        if ("Order Closed".equals(orderDeliveryModel.getTitle())) {
            String title = "<b><font color='#c9c9c9'>" + orderDeliveryModel.getTitle() + "</b></font>";
            holder.title.setText(Html.fromHtml(title), TextView.BufferType.SPANNABLE);
            String date = "<font color='#c9c9c9'>" + orderDeliveryModel.getCreatedDate() + orderDeliveryModel.getShift() + "</font>";
            holder.date.setText(Html.fromHtml(date), TextView.BufferType.SPANNABLE);
            holder.image_shortline.setVisibility(View.GONE);
            holder.image_shortline2.setVisibility(View.GONE);
            holder.image_greenRound.setVisibility(View.GONE);
            holder.image_shortline4.setVisibility(View.GONE);
        }
        if ("Thanks for your review".equals(orderDeliveryModel.getDetail())) {
            holder.image_shortline4.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }
}
