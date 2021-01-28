package com.worker.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import com.worker.app.BaseActivity;
import com.worker.app.R;
import com.worker.app.adapter.ListOrderDeliveryAdapter;
import com.worker.app.model.OrderDeliveryModel;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyTextView;

public class ViewOrdreDeliveryActivity extends BaseActivity {

    ArrayList<OrderDeliveryModel> listdeliverydetail = new ArrayList<>();
    RecyclerView recycle_details;
    MyTextView txt_name, txt_contractFees, txt_orderId, txt_status, txt_timeline;
    ImageView image_worker;
    String workerName, Conractfees, orderId, status, timeLine, workerImage, orderStatus;
    ProgressBar Simpleprogress;
    int maxStatus, TotalStatus, CompletedSatus;
    Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_order_detail);

        mContext = this;

        init();
    }

    public void init() {
        recycle_details = findViewById(R.id.recycle_details);
        txt_name = findViewById(R.id.txt_name);
        txt_contractFees = findViewById(R.id.txt_contractFees);
        txt_orderId = findViewById(R.id.txt_orderId);
        txt_status = findViewById(R.id.txt_status);
        txt_timeline = findViewById(R.id.txt_timeline);
        image_worker = findViewById(R.id.image_worker);
        Simpleprogress = findViewById(R.id.Simpleprogress);
    }

    public void intent() {
        workerName = getIntent().getExtras().getString("workerName");
        Conractfees = getIntent().getExtras().getString("contractFees");
        orderId = getIntent().getExtras().getString("orderId");
        status = getIntent().getExtras().getString("status");
        timeLine = getIntent().getExtras().getString("timeline");
        workerImage = getIntent().getExtras().getString("workerImage");
        orderStatus = getIntent().getExtras().getString("OrderStatsus");
        TotalStatus = Integer.parseInt(getIntent().getExtras().getString("TotalStatus"));
        CompletedSatus = Integer.parseInt(getIntent().getExtras().getString("CompletedSatus"));

        Glide.with(ViewOrdreDeliveryActivity.this).load(workerImage).into(image_worker);

        orderDelivery();

        txt_name.setText(workerName);
        txt_contractFees.setText(Conractfees);
        txt_orderId.setText(orderId);
        txt_status.setText(status);

        if (timeLine.equals("") || timeLine.equals("null") || timeLine.equals(null) || timeLine == null) {
            txt_timeline.setText("N/A");
        } else {
            txt_timeline.setText(timeLine);
        }

        if (TotalStatus != 0 && CompletedSatus != 0) {
            maxStatus = ((100 * CompletedSatus) / TotalStatus);
            Simpleprogress.setProgress(maxStatus);
        } else {
            Simpleprogress.setProgress(0);
        }
    }

    private void orderDelivery() {
        JSONArray array_orderStatus = null;
        try {
            array_orderStatus = new JSONArray(orderStatus);
            Log.e("array", "" + array_orderStatus.toString());
            for (int i = 0; i < array_orderStatus.length(); i++) {
                OrderDeliveryModel model = new OrderDeliveryModel();
                model.setOrderID(array_orderStatus.getJSONObject(i).getString("OrderID"));
                model.setStatus(array_orderStatus.getJSONObject(i).getString("Status"));
                model.setCreatedDate(array_orderStatus.getJSONObject(i).getString("CreatedDate"));
                model.setTime(array_orderStatus.getJSONObject(i).getString("Time"));
                model.setDescription(array_orderStatus.getJSONObject(i).getString("Description"));
                listdeliverydetail.add(model);
            }
            if (listdeliverydetail.size() > 0) {
                ListOrderDeliveryAdapter mAdapter = new ListOrderDeliveryAdapter(ViewOrdreDeliveryActivity.this, listdeliverydetail);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1, RecyclerView.VERTICAL, false);
                recycle_details.setLayoutManager(mLayoutManager);
                recycle_details.setAdapter(mAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        if (Consts.getInstance().isNetworkAvailable(mContext)) {
            intent();
        } else {
            Consts.getInstance().Act_vity="Vieworder";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }
}
