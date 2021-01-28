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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.worker.app.BaseActivity;
import com.worker.app.R;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    Context mContext;
    ImageView plus;
    SharedPreferences preferences, preferences_Login_Data;
    ProgressBar progress;
    Switch aSwitch_automaticLocation, aSwitch_marketingPreference, aSwitch_allowNotification;
    String Type = "", Value = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mContext = this;

        init();
    }

    public void init() {
        aSwitch_automaticLocation = findViewById(R.id.aSwitch_automaticLocation);
        aSwitch_marketingPreference = findViewById(R.id.aSwitch_marketingPreference);
        aSwitch_allowNotification = findViewById(R.id.aSwitch_allowNotification);
        progress = findViewById(R.id.progress);
        plus = findViewById(R.id.plus);
        plus.setVisibility(View.GONE);

        aSwitch_allowNotification.setOnClickListener(this);
        aSwitch_marketingPreference.setOnClickListener(this);
        aSwitch_automaticLocation.setOnClickListener(this);

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.aSwitch_allowNotification:
                if (Consts.isNetworkAvailable(mContext)) {
                    if (aSwitch_allowNotification.isChecked()) {
                        Type = "1";
                        Value = "1";
                    } else {
                        Type = "1";
                        Value = "0";
                    }
                    settingUpdateApi(Type, Value);
                } else {
                    Consts.getInstance().Act_vity="Setting";
                    Intent intent = new Intent(mContext, ReloadActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.aSwitch_marketingPreference:
                if (Consts.isNetworkAvailable(mContext)) {
                    if (aSwitch_marketingPreference.isChecked()) {
                        Type = "2";
                        Value = "1";
                    } else {
                        Type = "2";
                        Value = "0";
                    }
                    settingUpdateApi(Type, Value);
                } else {
                    Consts.getInstance().Act_vity="Setting";
                    Intent intent = new Intent(mContext, ReloadActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.aSwitch_automaticLocation:
                if (Consts.isNetworkAvailable(mContext)) {
                    if (aSwitch_automaticLocation.isChecked()) {
                        Type = "3";
                        Value = "1";
                    } else {
                        Type = "3";
                        Value = "0";
                    }
                    settingUpdateApi(Type,Value);
                } else {
                    Consts.getInstance().Act_vity="Setting";
                    Intent intent = new Intent(mContext, ReloadActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    private void settingListApi() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().SETTING_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            Log.e("response", "" + Consts.getInstance().SETTING_LIST + json_main);
                            if (json_main.getString("success").equalsIgnoreCase("true")) {

                                String IsNotification = json_main.getString("IsNotification");
                                String MarketingPreferences = json_main.getString("MarketingPreferences");
                                String AllowLocation = json_main.getString("AllowLocation");

                                if (IsNotification.equals("1")){
                                    aSwitch_allowNotification.setChecked(true);
                                } else {
                                    aSwitch_allowNotification.setChecked(false);
                                }

                                if (MarketingPreferences.equals("1")){
                                    aSwitch_marketingPreference.setChecked(true);
                                } else {
                                    aSwitch_marketingPreference.setChecked(false);
                                }

                                if (AllowLocation.equals("1")){
                                    aSwitch_automaticLocation.setChecked(true);
                                } else {
                                    aSwitch_automaticLocation.setChecked(false);
                                }
                            } else {
                                Toast.makeText(mContext, json_main.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            progress.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                });

            }
        },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        progress.setVisibility(View.GONE);
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("language", preferences.getString("language",""));
                params.put("UserID", preferences_Login_Data.getString("UserID",""));

                Log.e("params", "" + Consts.getInstance().SETTING_LIST + params);

                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().SETTING_LIST);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void settingUpdateApi(String Type, String Value) {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().SETTING_UPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            Log.e("response", "" + Consts.getInstance().SETTING_UPDATE + json_main);
                            if (json_main.getString("success").equalsIgnoreCase("true")) {
                                settingListApi();
                                Toast.makeText(mContext, json_main.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, json_main.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            progress.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                });

            }
        },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        progress.setVisibility(View.GONE);
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("language", preferences.getString("language",""));
                params.put("UserID", preferences_Login_Data.getString("UserID",""));
                params.put("Type", Type);
                params.put("Value", Value);
                Log.e("params", "" + Consts.getInstance().SETTING_UPDATE + params);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().SETTING_UPDATE);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    protected void onResume() {
        if (Consts.isNetworkAvailable(mContext)) {
            String UserID = preferences_Login_Data.getString("UserID", "");
            if (UserID.equals("")) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            } else {
                settingListApi();
            }
        } else {
            Consts.getInstance().Act_vity="Setting";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }
}
