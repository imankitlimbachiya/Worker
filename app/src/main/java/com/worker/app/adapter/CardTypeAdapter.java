package com.worker.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.worker.app.R;
import com.worker.app.model.CardTypeModel;

public class CardTypeAdapter extends RecyclerView.Adapter<CardTypeAdapter.MyViewHolder> {

    private ArrayList<CardTypeModel> listCardType;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgCard;

        public MyViewHolder(View view) {
            super(view);
            imgCard = view.findViewById(R.id.imgCard);
        }
    }

    public CardTypeAdapter(ArrayList<CardTypeModel> listCardType) {
        this.listCardType = listCardType;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_card_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        CardTypeModel cardTypeModel = listCardType.get(position);
        holder.imgCard.setImageResource(cardTypeModel.getCardImag());

        if(listCardType.get(position).isSelected()){
            holder.imgCard.setBackgroundResource(R.drawable.bg_card);
        }else{
            holder.imgCard.setBackgroundResource(0);
        }

        holder.imgCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0;i<listCardType.size();i++){
                    if(listCardType.get(i).isSelected()){
                        listCardType.get(i).setSelected(false);
                        notifyItemChanged(i);
                        break;
                    }
                }

                listCardType.get(position).setSelected(true);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCardType.size();
    }
}

