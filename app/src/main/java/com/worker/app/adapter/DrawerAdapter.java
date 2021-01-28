package com.worker.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.worker.app.R;
import com.worker.app.model.DrawerModel;
import com.worker.app.utility.MyTextView;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.MyViewHolder> {

    private List<DrawerModel> listItems;
    private ClickListener clickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyTextView txtTitle;
        private LinearLayout linearParent;

        public MyViewHolder(View view) {
            super(view);
            linearParent=view.findViewById(R.id.linearParent);
            txtTitle = view.findViewById(R.id.txtTitle);
        }
    }

    public DrawerAdapter(List<DrawerModel> listItems, ClickListener clickListener) {
        this.listItems = listItems;
        this.clickListener=clickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_drawer_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        DrawerModel movie = listItems.get(position);
        holder.txtTitle.setText(movie.getItemTitle());
        holder.linearParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.OnClickDrawerItem(listItems.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public interface ClickListener{
        void OnClickDrawerItem(DrawerModel model);
    }
}
