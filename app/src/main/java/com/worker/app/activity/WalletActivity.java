package com.worker.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.worker.app.adapter.ListWalletAdapter;
import com.worker.app.model.WalletModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyTextView;

public class WalletActivity extends BaseActivity {

    Context mContext;
    RecyclerView recycle_walletDetail;
    ArrayList<WalletModel> listWalletDetail = new ArrayList<>();
    ImageView plus;
    ProgressBar progress;
    SharedPreferences preferences,preferences_Login_Data;
    MyTextView txt_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        mContext = this;

        Init();
    }

    public void Init(){
        plus=findViewById(R.id.plus);
        plus.setVisibility(View.GONE);
        recycle_walletDetail = findViewById(R.id.recycle_walletDeatail);
        progress = findViewById(R.id.progress);
        txt_total = findViewById(R.id.txt_total);

        preferences = getSharedPreferences("Language",MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);
    }

    private void walletDetail() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().WALLET_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            if (json_main.getString("success").equalsIgnoreCase("true")) {
                                txt_total.setText(json_main.getString("WalletTotal"));
                                JSONArray array_WalletList = json_main.getJSONArray("WalletList");
                                for (int i=0; i<array_WalletList.length(); i++){
                                    WalletModel walletModel = new WalletModel();
                                    walletModel.setUserWalletID(array_WalletList.getJSONObject(i).getString("UserWalletID"));
                                    walletModel.setOrderID(array_WalletList.getJSONObject(i).getString("OrderID"));
                                    walletModel.setAmount(array_WalletList.getJSONObject(i).getString("Amount"));
                                    walletModel.setTrType(array_WalletList.getJSONObject(i).getString("TrType"));
                                    walletModel.setDescription(array_WalletList.getJSONObject(i).getString("Description"));
                                    walletModel.setType(array_WalletList.getJSONObject(i).getString("Type"));
                                    walletModel.setDate(array_WalletList.getJSONObject(i).getString("Date"));
                                    listWalletDetail.add(walletModel);
                                }

                                ListWalletAdapter mAdapter = new ListWalletAdapter(WalletActivity.this, listWalletDetail);
                                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1, RecyclerView.VERTICAL, false);
                                recycle_walletDetail.setLayoutManager(mLayoutManager);
                                recycle_walletDetail.setAdapter(mAdapter);

                            } else {
                                Toast.makeText(WalletActivity.this, json_main.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("language",preferences.getString("language",""));
                params.put("UserID", preferences_Login_Data.getString("UserID",""));

                Log.e("params", "" + Consts.getInstance().WALLET_DETAIL + params);
                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().WALLET_DETAIL);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
                    walletDetail();
            }
        } else {
            Consts.getInstance().Act_vity="Wallet";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }
}

