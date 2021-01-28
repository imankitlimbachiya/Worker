package com.worker.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.worker.app.BaseActivity;
import com.worker.app.R;
import com.worker.app.adapter.AnnouncementAdapter;
import com.worker.app.model.AnnouncementModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyTextView;

public class AnnouncementActivity extends BaseActivity {

    RecyclerView recycle_announcement;
    ArrayList<AnnouncementModel> listAnnouncement = new ArrayList<AnnouncementModel>();
    ProgressBar progress;
    MyTextView error;
    SharedPreferences preferences;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        mContext = this;

        init();

        if (Consts.getInstance().isNetworkAvailable(mContext)) {
            announcement_list();
        } else {
            Consts.getInstance().Act_vity="annoucement";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
    }

    public void init() {
        recycle_announcement = findViewById(R.id.recycle_announcement);
        progress = findViewById(R.id.progress);
        error = findViewById(R.id.error);

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
    }

    private void announcement_list() {
        String tag_string_req = "req";
        listAnnouncement.clear();
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().announcement_list, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                Log.e("response", "" + Consts.getInstance().announcement_list + response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            Log.e("json_main", "" + Consts.getInstance().announcement_list + json_main);
                            if (json_main.getString("success").equalsIgnoreCase("True")) {
                                json_main.getString("AnnouncementCount");
                                JSONArray array_Announcement_list = json_main.getJSONArray("Announcement_list");
                                for (int i = 0; i < array_Announcement_list.length(); i++) {
                                    AnnouncementModel model = new AnnouncementModel();
                                    model.setAnnouncementImage(array_Announcement_list.getJSONObject(i).getString("AnnouncementImage"));
                                    model.setAnnouncementID(array_Announcement_list.getJSONObject(i).getString("AnnouncementID"));
                                    model.setAnnouncementTitle(array_Announcement_list.getJSONObject(i).getString("AnnouncementTitle"));
                                    model.setAnnouncementTitleArabic(array_Announcement_list.getJSONObject(i).getString("AnnouncementTitleArabic"));
                                    model.setCategoryID(array_Announcement_list.getJSONObject(i).getString("CategoryID"));
                                    model.setCategoryName(array_Announcement_list.getJSONObject(i).getString("CategoryName"));
                                    model.setCategoryNameArabic(array_Announcement_list.getJSONObject(i).getString("CategoryNameArabic"));
                                    model.setCountryID(array_Announcement_list.getJSONObject(i).getString("CountryID"));
                                    model.setWorkerName(array_Announcement_list.getJSONObject(i).getString("WorkerName"));
                                    model.setStripImage(array_Announcement_list.getJSONObject(i).getString("StripImage"));
                                    model.setWorkerID(array_Announcement_list.getJSONObject(i).getString("WorkerID"));
                                    listAnnouncement.add(model);
                                }
                                if (listAnnouncement.size() > 0) {
                                    error.setVisibility(View.GONE);
                                    AnnouncementAdapter announcementAdapter = new AnnouncementAdapter(AnnouncementActivity.this, listAnnouncement);
                                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1, RecyclerView.VERTICAL, false);
                                    recycle_announcement.setLayoutManager(mLayoutManager);
                                    recycle_announcement.setItemAnimator(new DefaultItemAnimator());
                                    recycle_announcement.setAdapter(announcementAdapter);
                                } else {
                                    error.setVisibility(View.VISIBLE);
                                    error.setText(json_main.getString("message"));
                                }
                            } else {
                                error.setVisibility(View.VISIBLE);
                                error.setText(json_main.getString("message"));
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
                //Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {   //Post
                Map<String, String> params = new HashMap<String, String>();///params put, not used in get method
                params.put("language", preferences.getString("language", ""));
                Log.e("params", Consts.getInstance().announcement_list + params);
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
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().announcement_list);///cache remove
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);//manage request
    }


}
