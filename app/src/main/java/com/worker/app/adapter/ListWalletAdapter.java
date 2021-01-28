package com.worker.app.adapter;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.worker.app.R;
import com.worker.app.model.WalletModel;
import com.worker.app.utility.MyTextView;

public class ListWalletAdapter extends RecyclerView.Adapter<ListWalletAdapter.MyViewHolder> {

    private List<WalletModel> arraylist;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private MyTextView txt_satus, txt_date, txt_amount, txt_orderId, txt_currancy;

        public MyViewHolder(View view) {
            super(view);

            txt_satus = view.findViewById(R.id.txt_satus);
            txt_date = view.findViewById(R.id.txt_date);
            txt_amount = view.findViewById(R.id.txt_amount);
            txt_orderId = view.findViewById(R.id.txt_orderId);
            txt_currancy = view.findViewById(R.id.txt_currancy);
        }
    }

    public ListWalletAdapter(Context mContext, List<WalletModel> arraylist) {
        this.mContext = mContext;
        this.arraylist = arraylist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_wallet, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        WalletModel walletModel = arraylist.get(position);

        holder.txt_orderId.setText(walletModel.getOrderID());
        holder.txt_amount.setText(walletModel.getAmount());
        holder.txt_date.setText(walletModel.getDate());
        holder.txt_satus.setText(walletModel.getType());
        Log.e("Adp","** walletModel.getType() :: "+walletModel.getType());

        if (walletModel.getTrType().equals("1")) {
            String amount = "<font color='#87cc8e'>+" + walletModel.getAmount() + "</font>";
            holder.txt_amount.setText(Html.fromHtml(amount), TextView.BufferType.SPANNABLE);
            String currancy = "<font color='#87cc8e'>" + "SAR" + "</font>";
            holder.txt_currancy.setText(Html.fromHtml(currancy), TextView.BufferType.SPANNABLE);

        } else {
            String amount = "<font color='#e10000'>-" + walletModel.getAmount() + "</font>";
            holder.txt_amount.setText(Html.fromHtml(amount), TextView.BufferType.SPANNABLE);
            String currancy = "<font color='#e10000'>" + "SAR" + "</font>";
            holder.txt_currancy.setText(Html.fromHtml(currancy), TextView.BufferType.SPANNABLE);
        }

    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }
}
