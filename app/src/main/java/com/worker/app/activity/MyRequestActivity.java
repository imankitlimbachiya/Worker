package com.worker.app.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.worker.app.BaseActivity;
import com.worker.app.R;
import com.worker.app.model.RequestWorkerModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyButton;
import com.worker.app.utility.MyTextView;

public class MyRequestActivity extends BaseActivity implements View.OnClickListener {

    ArrayList<RequestWorkerModel> listRequestWorker = new ArrayList<>();
    RecyclerView recycleview_request;
    ImageView plus;
    MyButton btn_next;
    ProgressBar progress;
    MyTextView text_amount, text_count, txt_msg;
    SharedPreferences preferences, preferences_Login_Data;
    Context mContext;
    LinearLayout rel_request_detail;
    String OrderID;
    private BottomNavigationView bottom_navigation_view;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myrequest);

        Log.e("onCreate","onCreate");

        mContext = this;

        Init();

        if (Consts.isNetworkAvailable(MyRequestActivity.this)) {
            request_order_list();
        } else {
            Consts.getInstance().Act_vity="Myrequest";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
    }

    private void Init() {

        plus = (ImageView) findViewById(R.id.plus);
        plus.setVisibility(View.INVISIBLE);
        recycleview_request = (RecyclerView) findViewById(R.id.recycleview_request);
        btn_next = (MyButton) findViewById(R.id.btn_next);
        progress = (ProgressBar) findViewById(R.id.progress);
        text_amount = findViewById(R.id.text_amount);
        text_count = findViewById(R.id.text_count);
        txt_msg = findViewById(R.id.txt_msg);
        rel_request_detail = findViewById(R.id.rel_request_detail);
        rel_request_detail.setVisibility(View.GONE);
        bottom_navigation_view = findViewById(R.id.bottom_navigation_view);

        btn_next.setOnClickListener(this);

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);
        OrderID = preferences_Login_Data.getString("OrderID", "");
        Log.e("response","**** init OrderID :: " +OrderID);

        Log.e("COMMON","** OrderID :: "+OrderID);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next:
                //to avoid multiple clicks
                if (SystemClock.elapsedRealtime() - lastClickTime < Consts.getInstance().ClickTimeSeconds) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();


                if (Consts.isNetworkAvailable(MyRequestActivity.this)) {
                    String UserID = preferences_Login_Data.getString("UserID", "");
                    if (UserID.equals("")) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, VisaDetailActivity.class);
                        intent.putExtra("OrderID", OrderID);
                        mContext.startActivity(intent);
                    }
                } else {
                    Consts.getInstance().Act_vity="Myrequest";
                    Intent intent = new Intent(mContext, ReloadActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    private void request_order_list() {
        String tag_string_req = "req";
        listRequestWorker.clear();
        progress.setVisibility(View.VISIBLE);
        txt_msg.setVisibility(View.GONE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().request_order_list, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            Log.e("params response","" + Consts.getInstance().request_order_list + response);
                            if (json_main.getString("success").equalsIgnoreCase("True")) {
                                JSONArray array_RequestOrderlist = json_main.getJSONArray("RequestOrderlist");
                                OrderID = json_main.getString("OrderID");
                                Log.e("response","**** my requset  OrderID :: " +OrderID);
                                for (int i = 0; i < array_RequestOrderlist.length(); i++) {
                                    RequestWorkerModel requestWorkerModel = new RequestWorkerModel();
                                    requestWorkerModel.setOrderID(array_RequestOrderlist.getJSONObject(i).getString("OrderID"));
                                    requestWorkerModel.setRequestOrderID(array_RequestOrderlist.getJSONObject(i).getString("RequestOrderID"));
                                    requestWorkerModel.setName(array_RequestOrderlist.getJSONObject(i).getString("WorkerName"));
                                    requestWorkerModel.setWorkerID(array_RequestOrderlist.getJSONObject(i).getString("WorkerID"));
                                    requestWorkerModel.setNationality(array_RequestOrderlist.getJSONObject(i).getString("Nationality"));
                                    requestWorkerModel.setCategory(array_RequestOrderlist.getJSONObject(i).getString("CategoryName"));
                                    requestWorkerModel.setSubcat(array_RequestOrderlist.getJSONObject(i).getString("SubCategoryName"));
                                    requestWorkerModel.setContractfees(array_RequestOrderlist.getJSONObject(i).getString("WorkerContractFees"));
                                    listRequestWorker.add(requestWorkerModel);
                                }
                                if (listRequestWorker.size() > 0) {
                                    SharedPreferences preferences = mContext.getSharedPreferences("Login_Data", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("RequestCount", String.valueOf(listRequestWorker.size()));
                                    editor.commit();
                                    text_amount.setText(json_main.getString("Total"));
                                    text_count.setText(json_main.getString("count"));
                                    recycleview_request.setVisibility(View.VISIBLE);
                                    rel_request_detail.setVisibility(View.VISIBLE);
                                    RequestWorkersDetailAdapter mAdapter = new RequestWorkersDetailAdapter(MyRequestActivity.this, listRequestWorker);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                                    recycleview_request.setLayoutManager(mLayoutManager);
                                    recycleview_request.setItemAnimator(new DefaultItemAnimator());
                                    recycleview_request.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    SharedPreferences preferences = mContext.getSharedPreferences("Login_Data", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("RequestCount", "0");
                                    editor.putString("OrderID","0");
                                    editor.commit();
                                    rel_request_detail.setVisibility(View.GONE);
                                }
                            } else {
                                recycleview_request.setVisibility(View.GONE);
                                txt_msg.setVisibility(View.VISIBLE);
                                txt_msg.setText(json_main.getString("message"));
                                text_count.setText("0");
                                rel_request_detail.setVisibility(View.GONE);
                                SharedPreferences preferences = mContext.getSharedPreferences("Login_Data", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("RequestCount", "0");
                                editor.putString("OrderID", "0");
                                editor.commit();
                            }
                            // request_worker_count();
                        } catch (Exception e) {
                            progress.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                });

            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.toString());
                progress.setVisibility(View.GONE);
                //Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {   //Post
                String OrderID = preferences_Login_Data.getString("OrderID", "");
                Log.e("response","**** my requset  OrderID befff :: " +OrderID);
                if (OrderID.equals("")) {
                    OrderID = "0";
                }
                Log.e("response","**** my requset  OrderID affff :: " +OrderID);

                String UserID = preferences_Login_Data.getString("UserID", "");
                if (UserID.equals("")) {
                    UserID = "0";
                }
                Map<String, String> params = new HashMap<String, String>();///params put, not used in get method
                params.put("OrderID", OrderID);
                params.put("language", preferences.getString("language", ""));
                params.put("UserID", UserID);
                Log.e("params","" + Consts.getInstance().request_order_list + params);
                Log.e("params response", "## params list order id :: " +OrderID);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().request_order_list);///cache remove
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);//manage request
    }

    private void DeleteOrderRequestApi(String RequestOrderID) {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().DELETE_ORDER_REQUEST, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                try {
                    progress.setVisibility(View.GONE);
                    JSONObject json_main = new JSONObject(response);
                    Log.e("response", "" + json_main + response);
                    if (json_main.getString("success").equalsIgnoreCase("true")) {
                        OrderID = json_main.getString("OrderID");
                        Log.e("response","**** delete requset  OrderID :: " +OrderID);
                        SharedPreferences preferences = getSharedPreferences("Login_Data", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("OrderID", json_main.getString("OrderID"));
                        editor.commit();
                        requestWorkerCountApi();
                        request_order_list();
                    } else {
                        Toast.makeText(MyRequestActivity.this, json_main.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    progress.setVisibility(View.GONE);
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        progress.setVisibility(View.GONE);
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("language", preferences.getString("language", ""));
                params.put("RequestOrderID", RequestOrderID);
                params.put("OrderID", OrderID);
                Log.e("params","" + Consts.getInstance().DELETE_ORDER_REQUEST + params);
                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().DELETE_ORDER_REQUEST);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    public class RequestWorkersDetailAdapter extends RecyclerView.Adapter<RequestWorkersDetailAdapter.MyViewHolder> {

        private List<RequestWorkerModel> listWorker;
        private Context mContext;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private MyTextView txt_name, txt_nationality, txt_category, txt_contractfees;
            ImageView option;
            private CardView card_view;

            public MyViewHolder(View view) {
                super(view);
                txt_name = view.findViewById(R.id.txt_name);
                txt_nationality = view.findViewById(R.id.txt_nationality);
                txt_category = view.findViewById(R.id.txt_category);
                txt_contractfees = view.findViewById(R.id.txt_contractfees);
                option = view.findViewById(R.id.option);
                card_view = view.findViewById(R.id.card_view);
            }
        }

        public RequestWorkersDetailAdapter(Context mContext, List<RequestWorkerModel> listWorker) {
            this.mContext = mContext;
            this.listWorker = listWorker;
        }

        @Override
        public RequestWorkersDetailAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_request_worker_detail, parent, false);

            return new RequestWorkersDetailAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RequestWorkersDetailAdapter.MyViewHolder holder, int position) {
            RequestWorkerModel requestWorkerModel = listWorker.get(position);
            if (requestWorkerModel.getName().equals("") || requestWorkerModel.getName().equals("null") || requestWorkerModel.getName().equals(null) || requestWorkerModel.getName() == null) {
                holder.txt_name.setText("-");
            } else {
                holder.txt_name.setText(requestWorkerModel.getName());
            }
            if (requestWorkerModel.getNationality().equals("") || requestWorkerModel.getNationality().equals("null") || requestWorkerModel.getNationality().equals(null) || requestWorkerModel.getNationality() == null) {
                holder.txt_nationality.setText("-");
            } else {
                holder.txt_nationality.setText(requestWorkerModel.getNationality());
            }
            if (requestWorkerModel.getCategory().equals("") || requestWorkerModel.getCategory().equals("null") || requestWorkerModel.getCategory().equals(null) || requestWorkerModel.getCategory() == null) {
                holder.txt_category.setText("-");
            } else {
                holder.txt_category.setText(requestWorkerModel.getCategory() + " " + "-" + " " + requestWorkerModel.getSubcat());
            }
            if (requestWorkerModel.getContractfees().equals("") || requestWorkerModel.getContractfees().equals("null") || requestWorkerModel.getContractfees().equals(null) || requestWorkerModel.getContractfees() == null) {
                holder.txt_contractfees.setText("-");
            } else {
                holder.txt_contractfees.setText(requestWorkerModel.getContractfees());
            }

            holder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_find_worker = new Intent(MyRequestActivity.this, WorkerProfileActivity.class);
                    intent_find_worker.putExtra("WorkerID", requestWorkerModel.getWorkerID());
                    intent_find_worker.putExtra("isFromListing", true);
                    startActivity(intent_find_worker);
                }
            });

            holder.option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(mContext, view);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.poupup_menu, popup.getMenu());
                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (Consts.isNetworkAvailable(MyRequestActivity.this)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle(R.string.cancerequest);
                                builder.setMessage(R.string.alertdeleterequest);
                                builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        DeleteOrderRequestApi(requestWorkerModel.getRequestOrderID());
                                        dialog.dismiss();
                                    }
                                });
                                builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                                //alert.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
                               // alert.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
                            } else {
                                Toast.makeText(mContext, R.string.networkcheck, Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });
                    popup.show();//showing popup menu
                }
            });
        }

        @Override
        public int getItemCount() {
            return listWorker.size();
        }
    }

    /*public void request_worker_count() {
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottom_navigation_view.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(1);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
        itemView.removeView((MyRequestActivity.this).findViewById(R.id.notifications_badge));
        SharedPreferences preferences = getSharedPreferences("Login_Data", MODE_PRIVATE);
        String RequestWorkers = preferences.getString("RequestCount", "");
        if (RequestWorkers.equals("0") || RequestWorkers.equals("")) {
        } else {
            View badge = LayoutInflater.from(MyRequestActivity.this).inflate(R.layout.notification_base_layout, itemView, true);
            MyTextView notificationCount = badge.findViewById(R.id.notifications_badge);
            notificationCount.setText(RequestWorkers);
            notificationCount.bringToFront();
        }
    }*/

    @Override
    protected void onResume() {
//        bottom_navigation_view.setSelectedItemId(R.id.bottom2);
        super.onResume();
    }
}
