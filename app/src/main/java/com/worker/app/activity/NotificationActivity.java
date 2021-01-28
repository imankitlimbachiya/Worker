package com.worker.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.worker.app.BaseActivity;
import com.worker.app.R;
import com.worker.app.adapter.ListNotficationAdapter;
import com.worker.app.model.NotificationModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;

public class NotificationActivity extends BaseActivity implements View.OnClickListener {

    RecyclerView recycle_notification;
    ArrayList<NotificationModel> listNotification = new ArrayList<>();
    ProgressBar progress;
    SharedPreferences preferences, preferences_Login_Data;
    Switch aSwitch;
    String IsNotification;
    private BottomNavigationView bottom_navigation_view;
    ImageView plus;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mContext = this;

        Init();

        if (Consts.isNetworkAvailable(NotificationActivity.this)) {
            SharedPreferences preferences = getSharedPreferences("Login_Data", MODE_PRIVATE);
            String UserID = preferences.getString("UserID", "");
            if (UserID.equals("")) {
                Intent intent = new Intent(NotificationActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                notificationListApi();
            }
        } else {
            Consts.getInstance().Act_vity="Notification";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
    }

    public void Init() {
        plus = findViewById(R.id.plus);
        plus.setVisibility(View.GONE);
        recycle_notification = findViewById(R.id.recycle_notification);
        progress = findViewById(R.id.progress);
        aSwitch = findViewById(R.id.aSwitch);
        bottom_navigation_view = findViewById(R.id.bottom_navigation_view);

        aSwitch.setOnClickListener(this);

        aSwitch.setChecked(true);

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.aSwitch:
                if (Consts.getInstance().isNetworkAvailable(mContext)) {
                    if (aSwitch.isChecked()) {
                        IsNotification = "1";
                    } else {
                        IsNotification = "0";
                    }
                    notificationAlertApi();
                } else {
                    Consts.getInstance().Act_vity="Notification";
                    Intent intent = new Intent(mContext, ReloadActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    private void notificationListApi() {
        String tag_string_req = "req";
        listNotification.clear();
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().NOTIFICATION_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                Log.e("response", "" + Consts.getInstance().NOTIFICATION_LIST + response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            if (json_main.getString("success").equalsIgnoreCase("True")) {
                                IsNotification = json_main.getString("IsNotification");
                                if (IsNotification.equals("1")) {
                                    aSwitch.setChecked(true);
                                } else {
                                    aSwitch.setChecked(false);
                                }

                                BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottom_navigation_view.getChildAt(0);
                                View v = bottomNavigationMenuView.getChildAt(3);
                                BottomNavigationItemView itemView = (BottomNavigationItemView) v;
                                itemView.removeView((NotificationActivity.this).findViewById(R.id.notifications_badge_count));

                                JSONArray array_IsNotification = json_main.getJSONArray("NotificationList");
                                for (int i = 0; i < array_IsNotification.length(); i++) {
                                    NotificationModel notificationModel = new NotificationModel();
                                    notificationModel.setNotificationID(array_IsNotification.getJSONObject(i).getString("NotificationID"));
                                    notificationModel.setTitle(array_IsNotification.getJSONObject(i).getString("Title"));
                                    notificationModel.setTitleArabic(array_IsNotification.getJSONObject(i).getString("TitleArabic"));
                                    notificationModel.setNotification(array_IsNotification.getJSONObject(i).getString("Notification"));
                                    notificationModel.setNotificationArabic(array_IsNotification.getJSONObject(i).getString("NotificationArabic"));
                                    notificationModel.setType(array_IsNotification.getJSONObject(i).getString("Type"));
                                    notificationModel.setNotificationDate(array_IsNotification.getJSONObject(i).getString("NotificationDate"));
                                    notificationModel.setNotificationTime(array_IsNotification.getJSONObject(i).getString("NotificationTime"));
                                    listNotification.add(notificationModel);
                                }
                                ListNotficationAdapter mAdapter = new ListNotficationAdapter(NotificationActivity.this, listNotification);
                                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1, RecyclerView.VERTICAL, false);
                                recycle_notification.setLayoutManager(mLayoutManager);
                                recycle_notification.setAdapter(mAdapter);
                            }
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
            }
        }) {
            protected Map<String, String> getParams() {   //Post
                Map<String, String> params = new HashMap<String, String>();///params put, not used in get method
                params.put("language", preferences.getString("language", ""));
                params.put("UserID", preferences_Login_Data.getString("UserID", ""));
                Log.e("params", Consts.getInstance().NOTIFICATION_LIST + params);
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
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().NOTIFICATION_LIST);///cache remove
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);//manage request
    }

    private void notificationAlertApi() {
        String tag_string_req = "req";
        listNotification.clear();
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().NOTIFICATION_ALERT, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                Log.e("response", "" + Consts.getInstance().NOTIFICATION_ALERT + response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            if (json_main.getString("success").equalsIgnoreCase("True")) {
                                String message = json_main.getString("massege");
                            }

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
            }
        }) {
            protected Map<String, String> getParams() {   //Post
                Map<String, String> params = new HashMap<String, String>();///params put, not used in get method
                params.put("language", preferences.getString("language", ""));
                params.put("UserID", preferences_Login_Data.getString("UserID", ""));
                params.put("IsNotification", IsNotification);
                Log.e("params", Consts.getInstance().NOTIFICATION_ALERT + params);
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
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().NOTIFICATION_ALERT);///cache remove
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);//manage request
    }

    @Override
    protected void onResume() {
//        bottom_navigation_view.setSelectedItemId(R.id.bottom4);

        super.onResume();
    }
}

