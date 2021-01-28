package com.worker.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.worker.app.BaseActivity;
import com.worker.app.R;
import com.worker.app.model.MyWorkersModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyTextView;

public class MyWorkersActivity extends BaseActivity implements View.OnClickListener {

    ArrayList<MyWorkersModel> listMyWorkers = new ArrayList<>();
    ImageView plus;
    RecyclerView recycleMyWorkers;
    LinearLayout allMyWorkers, completed, onGoing;
    View lineOnGoing, lineCompleted, lineAllMyWorker;
    ProgressBar progress;
    MyTextView txtOnGoing, txtCompleted, txtAllMyWorkers, error;
    ListMyWorkersAdapter mAdapter;
    JSONArray array_Workerlist;
    SharedPreferences preferences, preferences_Login_Data;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_workers);

        mContext = this;

        init();

        AllMyWorkers();
    }

    public void init() {

        plus = findViewById(R.id.plus);
        plus.setVisibility(View.GONE);
        recycleMyWorkers = findViewById(R.id.recycleMyWorkers);
        allMyWorkers = findViewById(R.id.allMyWorkers);
        completed = findViewById(R.id.completed);
        onGoing = findViewById(R.id.onGoing);
        lineCompleted = findViewById(R.id.lineCompleted);
        lineOnGoing = findViewById(R.id.lineOnGoing);
        lineAllMyWorker = findViewById(R.id.lineAllMyWorker);
        progress = findViewById(R.id.progress);
        txtOnGoing = findViewById(R.id.txtOnGoing);
        txtCompleted = findViewById(R.id.txtCompleted);
        txtAllMyWorkers = findViewById(R.id.txtAllMyWorkers);
        error = findViewById(R.id.error);

        allMyWorkers.setOnClickListener(this);
        completed.setOnClickListener(this);
        onGoing.setOnClickListener(this);

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);

        lineCompleted.setVisibility(View.GONE);
        lineOnGoing.setVisibility(View.GONE);
        lineAllMyWorker.setVisibility(View.VISIBLE);
        txtAllMyWorkers.setTextColor(Color.parseColor("#000000"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.allMyWorkers:
                txtAllMyWorkers.setTextColor(Color.parseColor("#000000"));
                lineAllMyWorker.setVisibility(View.VISIBLE);
                lineCompleted.setVisibility(View.GONE);
                txtCompleted.setTextColor(Color.parseColor("#848484"));
                lineOnGoing.setVisibility(View.GONE);
                txtOnGoing.setTextColor(Color.parseColor("#848484"));
                if (array_Workerlist.length() == 0) {
                    error.setVisibility(View.VISIBLE);
                    error.setText(getResources().getString(R.string.no_records_found));
                } else {
                    setAdapter(1);
                }
                break;
            case R.id.completed:
                lineCompleted.setBackgroundColor(Color.parseColor("#000000"));
                txtCompleted.setTextColor(Color.parseColor("#000000"));
                lineCompleted.setVisibility(View.VISIBLE);
                lineAllMyWorker.setVisibility(View.GONE);
                txtAllMyWorkers.setTextColor(Color.parseColor("#848484"));
                txtOnGoing.setTextColor(Color.parseColor("#848484"));
                lineOnGoing.setVisibility(View.GONE);
                if (array_Workerlist.length() == 0) {
                    error.setVisibility(View.VISIBLE);
                    error.setText(getResources().getString(R.string.no_records_found));
                } else {
                    setAdapter(2);
                }
                break;
            case R.id.onGoing:
                lineOnGoing.setBackgroundColor(Color.parseColor("#000000"));
                txtOnGoing.setTextColor(Color.parseColor("#000000"));
                lineOnGoing.setVisibility(View.VISIBLE);
                lineAllMyWorker.setVisibility(View.GONE);
                txtCompleted.setTextColor(Color.parseColor("#848484"));
                lineCompleted.setVisibility(View.GONE);
                txtAllMyWorkers.setTextColor(Color.parseColor("#848484"));
                if (array_Workerlist.length() == 0) {
                    error.setVisibility(View.VISIBLE);
                    error.setText(getResources().getString(R.string.no_records_found));
                } else {
                    setAdapter(3);
                }
                break;
        }
    }

    private void AllMyWorkers() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        listMyWorkers.clear();
        StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().MY_WORKERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        progress.setVisibility(View.GONE);
                        listMyWorkers.clear();
                        JSONObject json_main = new JSONObject(response);
                        Log.e("response", "" + Consts.getInstance().MY_WORKERS + json_main);
                        if (json_main.getString("success").equalsIgnoreCase("True")) {
                            array_Workerlist = json_main.getJSONArray("Workerlist");
                            setAdapter(1);
                        } else {
                            array_Workerlist = json_main.getJSONArray("Workerlist");
                            setAdapter(1);
                        }

                    } catch (Exception e) {
                        progress.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                } else {
                    progress.setVisibility(View.GONE);

                }
                progress.setVisibility(View.GONE);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "error => " + error.toString());
                        progress.setVisibility(View.GONE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("language", preferences.getString("language", ""));
                params.put("UserID", preferences_Login_Data.getString("UserID", ""));
                Log.e("params", "" + Consts.getInstance().MY_WORKERS + params);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().MY_WORKERS);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void setAdapter(int check) {
        try {
            listMyWorkers.clear();
            for (int i = 0; i < array_Workerlist.length(); i++) {
                MyWorkersModel myWorkersModel = new MyWorkersModel();
                myWorkersModel.setWorkerID(array_Workerlist.getJSONObject(i).getString("WorkerID"));
                myWorkersModel.setUserId(array_Workerlist.getJSONObject(i).getString("UserID"));
                myWorkersModel.setWorkerImage(array_Workerlist.getJSONObject(i).getString("WorkerImage"));
                myWorkersModel.setWorkerName(array_Workerlist.getJSONObject(i).getString("WorkerName"));
                myWorkersModel.setTotalStatus(array_Workerlist.getJSONObject(i).getString("TotalStatus"));
                myWorkersModel.setCompletedSatus(array_Workerlist.getJSONObject(i).getString("CompletedSatus"));
                myWorkersModel.setContactFees(array_Workerlist.getJSONObject(i).getString("ContractFees"));
                myWorkersModel.setOrderID(array_Workerlist.getJSONObject(i).getString("OrderID"));
                myWorkersModel.setStatus(array_Workerlist.getJSONObject(i).getString("Status"));
                myWorkersModel.setTimeline(array_Workerlist.getJSONObject(i).getString("Timeline"));
                myWorkersModel.setOrderStatus(array_Workerlist.getJSONObject(i).getString("OrderStatus"));
                myWorkersModel.setIsComplete(array_Workerlist.getJSONObject(i).getString("Is_Complete"));

                Log.e("MWA", "## Is_Complete :: " + array_Workerlist.getJSONObject(i).getString("Is_Complete"));
                Log.e("MWA", "## Status :: " + array_Workerlist.getJSONObject(i).getString("Status"));

                if (array_Workerlist.getJSONObject(i).getString("StatusCode").equals("5") && check == 2) {
                    listMyWorkers.add(myWorkersModel);
                } else if (check == 3) {
                    if (!array_Workerlist.getJSONObject(i).getString("StatusCode").equals("5") &&
                            !array_Workerlist.getJSONObject(i).getString("StatusCode").equals("3")) {
                        Log.e("MWA", "## check == 3 Status :: " + array_Workerlist.getJSONObject(i).getString("Status"));
                        listMyWorkers.add(myWorkersModel);
                    }
                } else if (check == 1) {
                    listMyWorkers.add(myWorkersModel);
                }
            }
            if (listMyWorkers.size() > 0) {
                mAdapter = new ListMyWorkersAdapter(MyWorkersActivity.this, listMyWorkers);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(MyWorkersActivity.this, 1, RecyclerView.VERTICAL, false);
                recycleMyWorkers.setLayoutManager(mLayoutManager);
                recycleMyWorkers.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                error.setVisibility(View.GONE);
            } else {
                error.setVisibility(View.VISIBLE);
                error.setText(getResources().getString(R.string.no_records_found));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ListMyWorkersAdapter extends RecyclerView.Adapter<ListMyWorkersAdapter.MyViewHolder> {

        private List<MyWorkersModel> arraylist;
        private Context mContext;
        int maxStatus, TotalStatus, CompletedSatus;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private MyTextView txt_name, txt_contractFees, txt_orderId, txt_status, txt_timeline;
            ImageView image_worker;
            LinearLayout lin_main;
            ProgressBar Simpleprogress;

            public MyViewHolder(View view) {
                super(view);
                txt_name = view.findViewById(R.id.txt_name);
                txt_contractFees = view.findViewById(R.id.txt_contractFees);
                txt_orderId = view.findViewById(R.id.txt_orderId);
                txt_status = view.findViewById(R.id.txt_status);
                txt_timeline = view.findViewById(R.id.txt_timeline);
                image_worker = view.findViewById(R.id.image_worker);
                lin_main = view.findViewById(R.id.lin_main);
                Simpleprogress = view.findViewById(R.id.Simpleprogress);
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

            if (myWorkersModel.getTimeline().equals("") || myWorkersModel.getTimeline().equals("null") || myWorkersModel.getTimeline().equals(null) || myWorkersModel.getTimeline() == null) {
                holder.txt_timeline.setText("N/A");
            } else {
                holder.txt_timeline.setText(myWorkersModel.getTimeline());
            }

            if (myWorkersModel.getTotalStatus().equals("") || myWorkersModel.getTotalStatus().equals("null") || myWorkersModel.getTotalStatus().equals(null) || myWorkersModel.getTotalStatus() == null) {
            } else {
                TotalStatus = Integer.parseInt(myWorkersModel.getTotalStatus());
            }

            if (myWorkersModel.getCompletedSatus().equals("") || myWorkersModel.getCompletedSatus().equals("null") || myWorkersModel.getCompletedSatus().equals(null) || myWorkersModel.getCompletedSatus() == null) {
            } else {
                CompletedSatus = Integer.parseInt(myWorkersModel.getCompletedSatus());
            }

            Glide.with(mContext).load(myWorkersModel.getWorkerImage()).into(holder.image_worker);

            if (TotalStatus != 0 && CompletedSatus != 0) {
                maxStatus = ((100 * CompletedSatus) / TotalStatus);
                holder.Simpleprogress.setProgress(maxStatus);
            } else {
                holder.Simpleprogress.setProgress(0);
            }

            holder.lin_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ViewOrdreDeliveryActivity.class);
                    intent.putExtra("workerName", myWorkersModel.getWorkerName());
                    intent.putExtra("contractFees", myWorkersModel.getContactFees());
                    intent.putExtra("orderId", myWorkersModel.getOrderID());
                    intent.putExtra("status", myWorkersModel.getStatus());
                    intent.putExtra("timeline", myWorkersModel.getTimeline());
                    intent.putExtra("workerImage", myWorkersModel.getWorkerImage());
                    intent.putExtra("OrderStatsus", myWorkersModel.getOrderStatus());
                    intent.putExtra("CompletedSatus", myWorkersModel.getCompletedSatus());
                    intent.putExtra("TotalStatus", myWorkersModel.getTotalStatus());
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return arraylist.size();
        }
    }

    @Override
    protected void onResume() {
        if (Consts.isNetworkAvailable(mContext)) {
            SharedPreferences preferences = getSharedPreferences("Login_Data", MODE_PRIVATE);
            String UserID = preferences.getString("UserID", "");
            if (UserID.equals("")) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            } else {
                // AllMyWorkers();
            }
        } else {
            Consts.getInstance().Act_vity = "MyWorker";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }
}
