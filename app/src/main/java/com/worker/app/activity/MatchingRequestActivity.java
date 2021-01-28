package com.worker.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.worker.app.BaseActivity;
import com.worker.app.R;
import com.worker.app.model.CategoryModel;
import com.worker.app.model.WorkerModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyTextView;

public class MatchingRequestActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView recyclerViewMatchCat, recyclerMatchSubCategories;
    ImageView back, plus, imgFilter;
    int pos = 0, sortSelected = 0;
    MyTextView txt_profile_count, txt_WorkersNo, txt_message;
    SharedPreferences preferences;
    String RequestID, selectedCategory;
    ArrayList<CategoryModel> listSlider = new ArrayList<CategoryModel>();
    ArrayList<WorkerModel> listWorker = new ArrayList<WorkerModel>();
    JSONArray array_Workers_list;
    Context mContext;
    MatchWorkersAdapter mAdapter;
    RelativeLayout rel_main;
    LinearLayout linearParent;
    ProgressBar progress;
    private ImageView imgBackCategory;
    private MyTextView txt_categoryNameArabic,txt_categoryName;
    private ImageView stripImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matchingrequest_activity);

        mContext = this;

        init();
    }

    public void init() {
        stripImage=findViewById(R.id.stripImage);
        txt_categoryNameArabic=findViewById(R.id.txt_categoryNameArabic);
        txt_categoryName=findViewById(R.id.txt_categoryName);

        imgBackCategory=findViewById(R.id.imgBackCategory);
        imgBackCategory.setOnClickListener(this);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        plus = findViewById(R.id.plus);
        plus.setVisibility(View.GONE);
        recyclerMatchSubCategories = findViewById(R.id.recyclerMatchSubCategories);
        recyclerViewMatchCat = findViewById(R.id.recyclerViewMatchCat);
        txt_profile_count = findViewById(R.id.txt_profile_count);
        txt_WorkersNo = findViewById(R.id.txt_WorkersNo);
        imgFilter = findViewById(R.id.imgFilter);
        txt_message = findViewById(R.id.txt_message);
        rel_main = findViewById(R.id.rel_main);
        linearParent = findViewById(R.id.linearParent);
        progress = findViewById(R.id.progress);

        imgFilter.setOnClickListener(this);

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
        RequestID = getIntent().getExtras().getString("RequestID");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBackCategory:
                Intent intent = new Intent(MatchingRequestActivity.this, MyRequestListingActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.imgFilter:
                PopupMenu popup = new PopupMenu(MatchingRequestActivity.this, view);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.poupup_sort, popup.getMenu());
                int positionOfMenuItem = sortSelected;
                MenuItem item = popup.getMenu().getItem(positionOfMenuItem);
                SpannableString s = new SpannableString(item.getTitle());
                s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, s.length(), 0);
                item.setTitle(s);
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.our_recommendation:
                                // TODO
                                sortSelected = 0;
                                Sort(sortSelected);
                                return true;
                            case R.id.price_low_to_high:
                                // TODO
                                sortSelected = 1;
                                Sort(sortSelected);
                                return true;
                            case R.id.price_high_to_low:
                                // TODO
                                sortSelected = 2;
                                Sort(sortSelected);
                                return true;
                            case R.id.name_a_to_z:
                                Sort(sortSelected);
                                // TODO
                                sortSelected = 3;
                                Sort(sortSelected);
                                return true;
                            case R.id.name_z_to_a:
                                // TODO
                                sortSelected = 4;
                                Sort(sortSelected);
                                return true;
                        }

                        return true;
                    }
                });
                popup.show();//showing popup menu
                break;
        }
    }

    private void matchingWorkerApi() {
        String tag_string_req = "req";
       progress.setVisibility(View.VISIBLE);
        listSlider.clear();
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().MATCHING_WORKERS_REQUEST, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                try {
                    progress.setVisibility(View.GONE);
                    JSONObject json_main = new JSONObject(response);
                    Log.e("response", "" + Consts.getInstance().MATCHING_WORKERS_REQUEST + json_main);
                    if (json_main.getString("success").equalsIgnoreCase("true")) {
                        String workerCount = json_main.getString("workerCount");
                        if (workerCount.equals("") || workerCount.equals("null") || workerCount.equals(null) || workerCount == null) {
                            txt_WorkersNo.setText(getResources().getString(R.string.we_not_found));
                        } else {
                            txt_WorkersNo.setText(getResources().getString(R.string.we_found) + " " + workerCount + " " + getResources().getString(R.string.worker_matching));
                        }
                        JSONArray array_category = json_main.getJSONArray("Category");
                        for (int i = 0; i < array_category.length(); i++) {
                            CategoryModel categoryModel = new CategoryModel();
                            categoryModel.setCategoryID(array_category.getJSONObject(i).getString("CategoryID"));
                            categoryModel.setCategoryName(array_category.getJSONObject(i).getString("CategoryName"));
                            categoryModel.setCategoryNameArabic(array_category.getJSONObject(i).getString("CategoryNameArabic"));
                            listSlider.add(categoryModel);
                        }
                        if (listSlider.size() > 0) {
                            MatchingCategoryAdapter matchingCategoryAdapter = new MatchingCategoryAdapter(MatchingRequestActivity.this, listSlider);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MatchingRequestActivity.this, RecyclerView.HORIZONTAL, false);
                            recyclerViewMatchCat.setLayoutManager(mLayoutManager);
                            recyclerViewMatchCat.setItemAnimator(new DefaultItemAnimator());
                            recyclerViewMatchCat.setAdapter(matchingCategoryAdapter);
                        }
                        array_Workers_list = json_main.getJSONArray("Workers_list");
                        selectedCategory = "0";
                        setWorkerAdapter(selectedCategory);
                    } else {
                        imgFilter.setVisibility(View.GONE);
                        recyclerMatchSubCategories.setVisibility(View.GONE);
                        array_Workers_list = json_main.getJSONArray("Workers_list");
                        selectedCategory = "0";
                        setWorkerAdapter(selectedCategory);
                        txt_message.setVisibility(View.VISIBLE);
                        txt_message.setText(json_main.getString("message"));
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
                params.put("RequestID", "" + RequestID);
                Log.e("params", "" + Consts.getInstance().MATCHING_WORKERS_REQUEST + params);
                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().MATCHING_WORKERS_REQUEST);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void setWorkerAdapter(String CategoryID) throws JSONException {
        listWorker.clear();
        for (int i = 0; i < array_Workers_list.length(); i++) {
            if (CategoryID.equals("0") || CategoryID.equals(array_Workers_list.getJSONObject(i).getString("CategoryID"))) {
                WorkerModel workerModel = new WorkerModel();
                workerModel.setWorkerID(array_Workers_list.getJSONObject(i).getString("WorkerID"));
                workerModel.setWorkerImage(array_Workers_list.getJSONObject(i).getString("WorkerImage"));
                workerModel.setWorkerName(array_Workers_list.getJSONObject(i).getString("WorkerName"));
                workerModel.setNationality(array_Workers_list.getJSONObject(i).getString("Nationality"));
                workerModel.setReligion(array_Workers_list.getJSONObject(i).getString("Religion"));
                workerModel.setAge(array_Workers_list.getJSONObject(i).getString("Age"));
                workerModel.setContactFees(array_Workers_list.getJSONObject(i).getString("ContactFees"));
                workerModel.setSetPrice(array_Workers_list.getJSONObject(i).getInt("ContactFees"));
                workerModel.setMonthlySalary(array_Workers_list.getJSONObject(i).getString("MonthlySalary"));
                workerModel.setSubCategoryName(array_Workers_list.getJSONObject(i).getString("SubCategoryName"));
                workerModel.setSubCategoryID(array_Workers_list.getJSONObject(i).getString("SubCategoryID"));


                listWorker.add(workerModel);
            }
        }
        if (listWorker.size() > 0) {
            //set header
            if(!array_Workers_list.getJSONObject(0).getString("CountryName").equals("")) {
                txt_categoryNameArabic.setText(array_Workers_list.getJSONObject(0).getString("CountryNameArabic"));
                txt_categoryName.setText(array_Workers_list.getJSONObject(0).getString("CountryName"));

                Glide.with(mContext)
                        .load(array_Workers_list.getJSONObject(0).getString("StripImage"))
                        .into(stripImage);
            }

            txt_profile_count.setText(listWorker.size() + " " + getResources().getString(R.string.profile_));
            mAdapter = new MatchWorkersAdapter(MatchingRequestActivity.this, listWorker);
            RecyclerView.LayoutManager LayoutManager = new LinearLayoutManager(MatchingRequestActivity.this, RecyclerView.VERTICAL, false);
            recyclerMatchSubCategories.setLayoutManager(LayoutManager);
            recyclerMatchSubCategories.setItemAnimator(new DefaultItemAnimator());
            recyclerMatchSubCategories.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            if (sortSelected != 0) {
                Sort(sortSelected);
            }
        }
    }

    public class MatchingCategoryAdapter extends RecyclerView.Adapter<MatchingCategoryAdapter.MyViewHolder> {

        private List<CategoryModel> listSlider;
        private Context mContext;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private MyTextView text_category;
            private RelativeLayout rel_cat_main;

            public MyViewHolder(View view) {
                super(view);
                text_category = view.findViewById(R.id.text_category);
                rel_cat_main = view.findViewById(R.id.rel_cat_main);

            }
        }

        public MatchingCategoryAdapter(Context mContext, ArrayList<CategoryModel> listSlider) {
            this.mContext = mContext;
            this.listSlider = listSlider;
        }

        @Override
        public MatchingCategoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_matching_category, parent, false);

            return new MatchingCategoryAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            CategoryModel categoryModel = listSlider.get(position);
            holder.text_category.setText(categoryModel.getCategoryName());
            if (pos == position) {
                holder.text_category.setTextColor(getResources().getColor(R.color.colorWhite));
                holder.text_category.setBackgroundResource(R.drawable.category_select_bg);
            } else {
                holder.text_category.setBackgroundResource(R.drawable.category_bg);
                holder.text_category.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            holder.rel_cat_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = position;
                    selectedCategory = categoryModel.getCategoryID();
                    try {
                        setWorkerAdapter(selectedCategory);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listSlider.size();
        }
    }

    public class MatchWorkersAdapter extends RecyclerView.Adapter<MatchWorkersAdapter.MyViewHolder> {

        private List<WorkerModel> listWorker;
        private Context mContext;
        RecyclerView recyclerSubCategories;
        SharedPreferences preferences, preferences_Login_Data;
        String WorkerID, ContractFees;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView imgWorker;
            private MyTextView txtWorkerName, txtWorkerCountry, txtWorkerContractFees, txtWorkerReligion, txtWorkerAge;
            private MyTextView txtWorkerMSalary, txtViewProfile, txtProfession, txtHireNow;
            RecyclerView recyclerSubCategories;


            public MyViewHolder(View view) {
                super(view);
                imgWorker = view.findViewById(R.id.imgWorker);
                txtWorkerName = view.findViewById(R.id.txtWorkerName);
                txtWorkerCountry = view.findViewById(R.id.txtWorkerCountry);
                txtWorkerContractFees = view.findViewById(R.id.txtWorkerContractFees);
                txtWorkerReligion = view.findViewById(R.id.txtWorkerReligion);
                txtWorkerAge = view.findViewById(R.id.txtWorkerAge);
                txtWorkerMSalary = view.findViewById(R.id.txtWorkerMSalary);
                txtViewProfile = view.findViewById(R.id.txtViewProfile);
                txtProfession = view.findViewById(R.id.txtProfession);
                txtHireNow = view.findViewById(R.id.txtHireNow);
                recyclerSubCategories = view.findViewById(R.id.recyclerSubCategories);
                preferences = mContext.getSharedPreferences("Language", MODE_PRIVATE);
                preferences_Login_Data = mContext.getSharedPreferences("Login_Data", MODE_PRIVATE);
            }
        }

        public MatchWorkersAdapter(Context mContext, ArrayList<WorkerModel> listWorker) {
            this.mContext = mContext;
            this.listWorker = listWorker;
        }

        @Override
        public MatchWorkersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_category_worker, parent, false);

            return new MatchWorkersAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MatchWorkersAdapter.MyViewHolder holder, int position) {

            WorkerModel singleItem = listWorker.get(position);

            if (singleItem.getWorkerImage().equals("") || singleItem.getWorkerImage().equals("null") || singleItem.getWorkerImage().equals(null) || singleItem.getWorkerImage() == null) {
            } else {
                Glide.with(mContext)
                        .load(singleItem.getWorkerImage())
                        .into(holder.imgWorker);
            }

            holder.txtWorkerName.setText(singleItem.getWorkerName());
            holder.txtWorkerCountry.setText(singleItem.getNationality());
            holder.txtWorkerContractFees.setText(singleItem.getContactFees());
            holder.txtWorkerReligion.setText(singleItem.getReligion());
            holder.txtWorkerAge.setText(singleItem.getAge());
            holder.txtWorkerMSalary.setText(singleItem.getMonthlySalary());
            holder.txtProfession.setText(singleItem.getSubCategoryName());

            holder.txtViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_find_worker = new Intent(mContext, WorkerProfileActivity.class);
                    intent_find_worker.putExtra("WorkerID", singleItem.getWorkerID());
                    mContext.startActivity(intent_find_worker);
                }
            });

            holder.txtHireNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WorkerID = singleItem.getWorkerID();
                    ContractFees = singleItem.getContactFees();
                    AddOrderRequestApi();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listWorker.size();
        }

        private void AddOrderRequestApi() {
            String tag_string_req = "req";
            progress.setVisibility(View.VISIBLE);
            final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().ADD_ORDER_REQUEST, new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {
                    Log.e("params response", "" + response);
                    try {
                        progress.setVisibility(View.GONE);
                        JSONObject json_main = new JSONObject(response);
                        Log.e("response", "" + Consts.getInstance().ADD_ORDER_REQUEST + json_main);
                        String str_msg = json_main.getString("message");
                        if (json_main.getString("success").equalsIgnoreCase("true")) {
                            SharedPreferences preferences = mContext.getSharedPreferences("Login_Data", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("OrderID", json_main.getString("OrderID"));
                            editor.commit();
                            Intent intent = new Intent(mContext, MyRequestActivity.class);
                            mContext.startActivity(intent);
                        } else {
                            Toast.makeText(mContext, str_msg, Toast.LENGTH_LONG).show();
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
                    String OrderID = preferences_Login_Data.getString("OrderID", "");
                    if (OrderID.equals("")) {
                        OrderID = "0";
                    }
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("language", preferences.getString("language", ""));
                    params.put("WorkerID", WorkerID);
                    params.put("WorkerContractFees", ContractFees);
                    params.put("UserID", preferences_Login_Data.getString("UserID", ""));
                    params.put("OrderID", OrderID);
                    Log.e("params", "" + Consts.getInstance().ADD_ORDER_REQUEST + params);
                    return params;
                }
            };
            strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().ADD_ORDER_REQUEST);
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }
    }

    @Override
    protected void onResume() {
        if (Consts.getInstance().isNetworkAvailable(mContext)) {
            matchingWorkerApi();
        } else {
            Consts.getInstance().Act_vity="matchingrequest";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }

    public void Sort(int sortSelected) {
        if (sortSelected == 0) {
            try {
                setWorkerAdapter(selectedCategory);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (sortSelected == 1) {

            LHContractSort();
            mAdapter.notifyDataSetChanged();

        } else if (sortSelected == 2) {

            HLContractSort();
            mAdapter.notifyDataSetChanged();

        } else if (sortSelected == 3) {

            AZSort();
            mAdapter.notifyDataSetChanged();

        } else if (sortSelected == 4) {

            ZASort();
            mAdapter.notifyDataSetChanged();
        }
    }

    public void AZSort() {
        Collections.sort(listWorker, new Comparator<WorkerModel>() {
            @Override
            public int compare(WorkerModel lhs, WorkerModel rhs) {
                return lhs.getWorkerName().compareTo(rhs.getWorkerName());
            }
        });
    }

    public void ZASort() {
        Collections.sort(listWorker, new Comparator<WorkerModel>() {
            @Override
            public int compare(WorkerModel lhs, WorkerModel rhs) {
                return rhs.getWorkerName().compareTo(lhs.getWorkerName());
            }
        });
    }

    public void LHContractSort() {
        Collections.sort(listWorker, new Comparator<WorkerModel>() {
            @Override
            public int compare(WorkerModel p1, WorkerModel p2) {
                return p1.getSetPrice() - p2.getSetPrice();
            }
        });
    }

    public void HLContractSort() {
        Collections.sort(listWorker, new Comparator<WorkerModel>() {
            @Override
            public int compare(WorkerModel p1, WorkerModel p2) {
                return p2.getSetPrice() - p1.getSetPrice();
            }
        });
    }
}
