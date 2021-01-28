package com.worker.app.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
import java.util.List;
import java.util.Map;

import com.worker.app.BaseActivity;
import com.worker.app.R;
import com.worker.app.model.WorkerDetailModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyButton;
import com.worker.app.utility.MyTextView;

public class MyRequestListingActivity extends BaseActivity {

    RecyclerView recycle_details;
    ProgressBar progress;
    ImageView plus;
    ArrayList<WorkerDetailModel> listWorkerDetail = new ArrayList<>();
    String UserID, RequestID;
    SharedPreferences preferences,preferences_Login_Data;
    MyTextView txt_message;
    ListRequestAdapter listRequestAdapter;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request_listing);

        mContext = this;

        init();
    }

    public void init() {
        recycle_details = findViewById(R.id.recycle_details);
        plus = findViewById(R.id.plus);
        plus.setVisibility(View.INVISIBLE);
        progress = findViewById(R.id.progress);
        txt_message = findViewById(R.id.txt_message);

        preferences = getSharedPreferences("Language",MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);
    }

    private void my_request_listing() {
        String tag_string_req = "req";
        listWorkerDetail.clear();
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().my_request_listing, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                Log.e("response", "" + Consts.getInstance().my_request_listing + response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            if (json_main.getString("success").equalsIgnoreCase("True")) {
                                JSONArray array_Request_list = json_main.getJSONArray("Request_list");
                                for (int i = 0; i < array_Request_list.length(); i++) {
                                    WorkerDetailModel workerDetailModel = new WorkerDetailModel();
                                    workerDetailModel.setRequestId(array_Request_list.getJSONObject(i).getString("RequestID"));
                                    workerDetailModel.setRequstDate(array_Request_list.getJSONObject(i).getString("RequestDate"));
                                    workerDetailModel.setStatus(array_Request_list.getJSONObject(i).getString("Status"));
                                    workerDetailModel.setNationality(array_Request_list.getJSONObject(i).getString("Nationality"));
                                    workerDetailModel.setCatagory(array_Request_list.getJSONObject(i).getString("CategoryName"));
                                    workerDetailModel.setRequestedWorker(array_Request_list.getJSONObject(i).getString("RequestWorkers"));
                                    workerDetailModel.setTotalMatcgWorker(array_Request_list.getJSONObject(i).getString("TotalMachingWorkers"));
                                    listWorkerDetail.add(workerDetailModel);
                                }
                                if (listWorkerDetail.size() > 0) {
                                    listRequestAdapter = new ListRequestAdapter(MyRequestListingActivity.this, listWorkerDetail);
                                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1, RecyclerView.VERTICAL, false);
                                    recycle_details.setLayoutManager(mLayoutManager);
                                    recycle_details.setAdapter(listRequestAdapter);
                                }

                            } else {

                                txt_message.setVisibility(View.VISIBLE);
                                txt_message.setText(json_main.getString("message"));
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
                params.put("UserID", preferences_Login_Data.getString("UserID", ""));
                params.put("language", preferences.getString("language", ""));
                Log.e("params", Consts.getInstance().my_request_listing + params);

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
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().my_request_listing);///cache remove
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);//manage request

    }

    public class ListRequestAdapter extends RecyclerView.Adapter<ListRequestAdapter.MyViewHolder> {

        private List<WorkerDetailModel> arraylist;
        private Context mContext;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private MyTextView tv_request_date, tv_request_id, tv_nationality, tv_category, tv_requestedWorkers, tv_matchingWorkers, tv_status, txt_tab_to_view;
            private MyButton view_request;
            ImageView image_option;

            public MyViewHolder(View view) {
                super(view);
                tv_request_id = view.findViewById(R.id.tv_request_id);
                tv_request_date = view.findViewById(R.id.tv_request_date);
                tv_nationality = view.findViewById(R.id.tv_nationality);
                tv_category = view.findViewById(R.id.tv_category);
                tv_requestedWorkers = view.findViewById(R.id.tv_requestedWorkers);
                tv_matchingWorkers = view.findViewById(R.id.tv_matchingWorkers);
                view_request = view.findViewById(R.id.view_request);
                tv_status = view.findViewById(R.id.tv_status);
                txt_tab_to_view = view.findViewById(R.id.txt_tab_to_view);
                image_option = view.findViewById(R.id.image_option);
            }
        }

        public ListRequestAdapter(Context mContext, List<WorkerDetailModel> arraylist) {
            this.mContext = mContext;
            this.arraylist = arraylist;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_request_listing, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            WorkerDetailModel workerDetailModel = arraylist.get(position);

            if (workerDetailModel.getRequestId().equals("") || workerDetailModel.getRequestId().equals("null") || workerDetailModel.getRequestId().equals(null) || workerDetailModel.getRequestId() == null) {
                holder.tv_request_id.setText("-");
            } else {
                holder.tv_request_id.setText(workerDetailModel.getRequestId());
            }
            if (workerDetailModel.getRequstDate().equals("") || workerDetailModel.getRequstDate().equals("null") || workerDetailModel.getRequstDate().equals(null) || workerDetailModel.getRequstDate() == null) {
                holder.tv_request_date.setText("-");
            } else {
                holder.tv_request_date.setText(workerDetailModel.getRequstDate());
            }
            if (workerDetailModel.getNationality().equals("") || workerDetailModel.getNationality().equals("null") || workerDetailModel.getNationality().equals(null) || workerDetailModel.getNationality() == null) {
                holder.tv_nationality.setText("-");
            } else {
                holder.tv_nationality.setText(workerDetailModel.getNationality());
            }
            if (workerDetailModel.getCatagory().equals("") || workerDetailModel.getCatagory().equals("null") || workerDetailModel.getCatagory().equals(null) || workerDetailModel.getCatagory() == null) {
                holder.tv_category.setText("-");
            } else {
                holder.tv_category.setText(workerDetailModel.getCatagory());
            }
            if (workerDetailModel.getRequestedWorker().equals("") || workerDetailModel.getRequestedWorker().equals("null") || workerDetailModel.getRequestedWorker().equals(null) || workerDetailModel.getRequestedWorker() == null) {
                holder.tv_requestedWorkers.setText("-");
            } else {
                holder.tv_requestedWorkers.setText(workerDetailModel.getRequestedWorker());
            }
            if (workerDetailModel.getTotalMatcgWorker().equals("") || workerDetailModel.getTotalMatcgWorker().equals("null") || workerDetailModel.getTotalMatcgWorker().equals(null) || workerDetailModel.getTotalMatcgWorker() == null) {
                holder.tv_matchingWorkers.setText("-");
            } else {
                if (Integer.parseInt(workerDetailModel.getTotalMatcgWorker()) > 0) {
                    holder.tv_matchingWorkers.setText(workerDetailModel.getTotalMatcgWorker());
                } else {
                    holder.tv_matchingWorkers.setText("0");
                    holder.txt_tab_to_view.setVisibility(View.GONE);
                }
            }
            if (workerDetailModel.getStatus().equals("") || workerDetailModel.getStatus().equals("null") || workerDetailModel.getStatus().equals(null) || workerDetailModel.getStatus() == null) {
                holder.tv_status.setText("-");
            } else {
                holder.tv_status.setText(workerDetailModel.getStatus());

                Log.e("MRLA","## workerDetailModel.getStatus() :: "+workerDetailModel.getStatus());
                if(workerDetailModel.getStatus().equals("Cancelled") || workerDetailModel.getStatus().equals("Completed") ||
                        workerDetailModel.getStatus().equals("ألغيت") || workerDetailModel.getStatus().equals("منجز")){
                    holder.image_option.setVisibility(View.GONE);
                    holder.image_option.setOnClickListener(null);
                }else{
                    holder.image_option.setVisibility(View.VISIBLE);
                    holder.image_option.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PopupMenu popup = new PopupMenu(mContext, view);
                            //Inflating the Popup using xml file
                            popup.getMenuInflater().inflate(R.menu.poupup_menu, popup.getMenu());
                            //registering popup with OnMenuItemClickListener
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                public boolean onMenuItemClick(MenuItem item) {
                                    if(Consts.isNetworkAvailable(mContext)){
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle(R.string.cancerequest);
                                        builder.setMessage(R.string.alertdeleterequest);
                                        builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                RequestID = workerDetailModel.getRequestId();
                                                if (Consts.isNetworkAvailable(MyRequestListingActivity.this)) {
                                                    DeleteRequestApi(workerDetailModel.getRequestId());
                                                } else {
                                                    Consts.getInstance().Act_vity="Myrequestlisting";
                                                    Intent intent = new Intent(mContext, ReloadActivity.class);
                                                    startActivity(intent);
                                                }
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
                                        //   alert.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
                                        //  alert.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
                                    } else {

                                        Toast.makeText(mContext, R.string.networkcheck,Toast.LENGTH_SHORT).show();
                                    }
                                    return true;
                                }
                            });
                            popup.show();//showing popup menu
                        }
                    });
                }
            }

            holder.view_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyDataSetChanged();
                    Intent intent = new Intent(mContext, ViewRequestDeatailActivity.class);
                    intent.putExtra("RequestID", workerDetailModel.getRequestId());
                    mContext.startActivity(intent);
                }
            });

            holder.txt_tab_to_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyDataSetChanged();
                    Intent intent = new Intent(mContext, MatchingRequestActivity.class);
                    intent.putExtra("RequestID", workerDetailModel.getRequestId());
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return arraylist.size();
        }
    }

    private void DeleteRequestApi(String RequestOrderID) {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().DELETE_REQUEST, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                Log.e("response", "" + response);
                try {
                    progress.setVisibility(View.GONE);
                    JSONObject json_main = new JSONObject(response);
                    Log.e("response", "" + Consts.getInstance().DELETE_REQUEST + json_main);

                    if (json_main.getString("success").equalsIgnoreCase("true")) {
                        String str_msg = json_main.getString("message");
                        my_request_listing();
                        listRequestAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MyRequestListingActivity.this, json_main.getString("message"), Toast.LENGTH_LONG).show();
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
                params.put("language",preferences.getString("language",""));
                params.put("RequestID", RequestID );

                Log.e("params", "" + Consts.getInstance().DELETE_REQUEST + params);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().DELETE_REQUEST);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    protected void onResume() {
        if (Consts.isNetworkAvailable(MyRequestListingActivity.this)) {
            SharedPreferences preferences = getSharedPreferences("Login_Data", MODE_PRIVATE);
            String UserID = preferences.getString("UserID", "");
            if (UserID.equals("")) {
                Intent intent = new Intent(MyRequestListingActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                my_request_listing();
            }
        } else {
            Consts.getInstance().Act_vity="Myrequestlisting";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }
}
