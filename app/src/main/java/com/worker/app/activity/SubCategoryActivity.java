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

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;

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
import com.worker.app.model.CatMatchModel;
import com.worker.app.model.WorkerModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyTextView;

public class SubCategoryActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView recyclerSubCategories, recyclerViewCat;
    private ImageView imgBackCategory, stripImage;
    LinearLayout linear_subcategory, lin_count;
    RelativeLayout rel_category_name;
    ArrayList<WorkerModel> listWorker = new ArrayList<WorkerModel>();
    ArrayList<CatMatchModel> listSlider = new ArrayList<CatMatchModel>();
    int Search_type, sortSelected = 0;
    String Search_by_value, StripImage, selectedSubCategory, BannerTitle, BannerTitleArabic;
    MyTextView txt_categoryName, txt_categoryNameArabic, txt_profile_count, txt_message;
    SharedPreferences preferences, preferences_Login_Data;
    public JSONArray array_Workers_list;
    ImageView imgFilter;
    CategoryWorkersAdapter mAdapter;
    Context mContext;
    ProgressBar progress;
    String OrderId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        mContext = this;

        init();

        intent();

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);

        selectedSubCategory = "0";

        if (!preferences_Login_Data.getString("OrderID", "0").equals("0") ||
                !preferences_Login_Data.getString("OrderID", "0").equals("")) {
            OrderId = preferences_Login_Data.getString("OrderID", "0");
        }
    }

    public void init() {
        imgBackCategory = findViewById(R.id.imgBackCategory);
        recyclerSubCategories = findViewById(R.id.recyclerSubCategories);
        recyclerViewCat = findViewById(R.id.recyclerViewCat);
        linear_subcategory = findViewById(R.id.linear_subcategory);
        rel_category_name = findViewById(R.id.rel_category_name);
        txt_categoryName = findViewById(R.id.txt_categoryName);
        txt_categoryNameArabic = findViewById(R.id.txt_categoryNameArabic);
        txt_profile_count = findViewById(R.id.txt_profile_count);
        lin_count = findViewById(R.id.lin_count);
        stripImage = findViewById(R.id.stripImage);
        imgFilter = findViewById(R.id.imgFilter);
        txt_message = findViewById(R.id.txt_message);
        progress = findViewById(R.id.progress);

        imgFilter.setOnClickListener(this);
        imgBackCategory.setOnClickListener(this);
    }

    public void intent() {
        Search_type = getIntent().getExtras().getInt("Search_type");
        Search_by_value = getIntent().getExtras().getString("Search_by_value");
        StripImage = getIntent().getExtras().getString("StripImage");

        Glide.with(mContext).load(StripImage).into(stripImage);

        if (Search_type == 2) {
            linear_subcategory.setVisibility(View.VISIBLE);
            rel_category_name.setVisibility(View.VISIBLE);
            String CategoryName = getIntent().getExtras().getString("CategoryName");
            String CategoryNameArabic = getIntent().getExtras().getString("CategoryNameArabic");
            txt_categoryName.setText(CategoryName);
            txt_categoryNameArabic.setText(CategoryNameArabic);
        } else if (Search_type == 3) {
            linear_subcategory.setVisibility(View.GONE);
            rel_category_name.setVisibility(View.VISIBLE);
            String CountryName = getIntent().getExtras().getString("CountryName");
            String CountryNameArabic = getIntent().getExtras().getString("CountryNameArabic");
            txt_categoryName.setText(CountryName);
            txt_categoryNameArabic.setText(CountryNameArabic);
        } else if (Search_type == 1) {
            rel_category_name.setVisibility(View.VISIBLE);
            linear_subcategory.setVisibility(View.GONE);
            String BannerTitle = getIntent().getExtras().getString("BannerTitle");
            String BannerTitleArabic = getIntent().getExtras().getString("BannerTitleArabic");
            txt_categoryName.setText(BannerTitle);
            txt_categoryNameArabic.setText(BannerTitleArabic);
        } else if (Search_type == 4) {
            rel_category_name.setVisibility(View.VISIBLE);
            linear_subcategory.setVisibility(View.GONE);
            String BannerTitle = getIntent().getExtras().getString("BannerTitle");
            String BannerTitleArabic = getIntent().getExtras().getString("BannerTitleArabic");
            txt_categoryName.setText(BannerTitle);
            txt_categoryNameArabic.setText(BannerTitleArabic);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBackCategory:
                finish();
                break;
            case R.id.imgFilter:
                PopupMenu popup = new PopupMenu(SubCategoryActivity.this, view);
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
                popup.show(); //showing popup menu
                break;
        }
    }

    private void findWorkerApi() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        listSlider.clear();
        listWorker.clear();
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().fIND_WORKERS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                try {
                    progress.setVisibility(View.GONE);
                    JSONObject json_main = new JSONObject(response);
                    Log.e("response", "" + Consts.getInstance().fIND_WORKERS_URL + json_main);
                    if (json_main.getString("success").equalsIgnoreCase("true")) {
                        if (json_main.has("subcategory")) {
                            JSONArray array_subcategory = json_main.getJSONArray("subcategory");
                            for (int i = 0; i < array_subcategory.length(); i++) {
                                CatMatchModel catMatchModel = new CatMatchModel();
                                catMatchModel.setSubCategoryID(array_subcategory.getJSONObject(i).getString("SubCategoryID"));
                                catMatchModel.setCategoryID(array_subcategory.getJSONObject(i).getString("CategoryID"));
                                catMatchModel.setSubCategoryName(array_subcategory.getJSONObject(i).getString("SubCategoryName"));
                                catMatchModel.setSubCategoryNameArabic(array_subcategory.getJSONObject(i).getString("SubCategoryNameArabic"));
                                listSlider.add(catMatchModel);
                            }
                            categoryAdapter categoryAdapter = new categoryAdapter(SubCategoryActivity.this, listSlider);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SubCategoryActivity.this, RecyclerView.HORIZONTAL, false);
                            recyclerViewCat.setLayoutManager(mLayoutManager);
                            recyclerViewCat.setItemAnimator(new DefaultItemAnimator());
                            recyclerViewCat.setAdapter(categoryAdapter);
                        }
                        array_Workers_list = json_main.getJSONArray("Workers_list");
                        setWorkerAdapter(selectedSubCategory);
                    } else {
                        txt_message.setVisibility(View.VISIBLE);
                        imgFilter.setVisibility(View.GONE);
                        lin_count.setVisibility(View.GONE);
                        array_Workers_list = json_main.getJSONArray("Workers_list");
                        txt_message.setText(json_main.getString("message"));
                        setWorkerAdapter(selectedSubCategory);
                    }
                } catch (Exception e) {
                    progress.setVisibility(View.GONE);
                    e.printStackTrace();
                }

                if (!OrderId.equals("0"))
                    request_order_list();
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
                params.put("SearchType", "" + Search_type);
                params.put("SearchTypeValue", "" + Search_by_value);
                params.put("OrderID", OrderID);
                Log.e("params", "" + Consts.getInstance().fIND_WORKERS_URL + params);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().HOME_URL);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void setWorkerAdapter(String SubCategoryID) throws JSONException {
        listWorker.clear();
        for (int i = 0; i < array_Workers_list.length(); i++) {
            if (SubCategoryID.equals("0") || SubCategoryID.equals(array_Workers_list.getJSONObject(i).getString("SubCategoryID"))) {
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
        txt_profile_count.setText(String.valueOf(listWorker.size()));
        mAdapter = new CategoryWorkersAdapter(SubCategoryActivity.this, listWorker);
        RecyclerView.LayoutManager LayoutManager = new LinearLayoutManager(SubCategoryActivity.this, RecyclerView.VERTICAL, false);
        recyclerSubCategories.setLayoutManager(LayoutManager);
        recyclerSubCategories.setItemAnimator(new DefaultItemAnimator());
        recyclerSubCategories.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        if (sortSelected != 0) {
            Sort(sortSelected);
        }
    }

    public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.MyViewHolder> {

        private List<CatMatchModel> listSlider;
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

        public categoryAdapter(Context mContext, List<CatMatchModel> listSlider) {
            this.mContext = mContext;
            this.listSlider = listSlider;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_category, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            CatMatchModel catMatchModel = listSlider.get(position);
            holder.text_category.setText(catMatchModel.getSubCategoryName());

            if (selectedSubCategory.equals(catMatchModel.getSubCategoryID())) {
                holder.text_category.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                holder.text_category.setBackgroundResource(R.drawable.category_select_bg);
            } else {
                holder.text_category.setBackgroundResource(R.drawable.category_bg);
                holder.text_category.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            }
            holder.rel_cat_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        selectedSubCategory = catMatchModel.getSubCategoryID();
                        setWorkerAdapter(catMatchModel.getSubCategoryID());
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

    public class CategoryWorkersAdapter extends RecyclerView.Adapter<CategoryWorkersAdapter.MyViewHolder> {

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
                //  progress = view.findViewById(R.id.progress);
                recyclerSubCategories = view.findViewById(R.id.recyclerSubCategories);
                preferences = mContext.getSharedPreferences("Language", MODE_PRIVATE);
                preferences_Login_Data = mContext.getSharedPreferences("Login_Data", MODE_PRIVATE);
            }
        }

        public CategoryWorkersAdapter(Context mContext, ArrayList<WorkerModel> listWorker) {
            this.mContext = mContext;
            this.listWorker = listWorker;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_category_worker, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            WorkerModel singleItem = listWorker.get(position);

            if (singleItem.getWorkerImage().equals("") || singleItem.getWorkerImage().equals("null") || singleItem.getWorkerImage().equals(null) || singleItem.getWorkerImage() == null) {
            } else {
                Glide.with(mContext)
                        .load(singleItem.getWorkerImage())
                        .into(holder.imgWorker);
            }

            if (singleItem.getContactFees().equals("") || singleItem.getContactFees().equals("null") || singleItem.getContactFees().equals(null) || singleItem.getContactFees() == null) {
            } else {
                holder.txtWorkerContractFees.setText(singleItem.getContactFees());
            }

            if (singleItem.getMonthlySalary().equals("") || singleItem.getMonthlySalary().equals("null") || singleItem.getMonthlySalary().equals(null) || singleItem.getMonthlySalary() == null) {
            } else {
                holder.txtWorkerMSalary.setText(singleItem.getMonthlySalary());
            }

            holder.txtWorkerName.setText(singleItem.getWorkerName());
            holder.txtWorkerCountry.setText(singleItem.getNationality());
            holder.txtWorkerReligion.setText(singleItem.getReligion());
            holder.txtWorkerAge.setText(singleItem.getAge());
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
                        Log.e("params response", "" + Consts.getInstance().ADD_ORDER_REQUEST + json_main);
                        String str_msg = json_main.getString("message");
                        if (json_main.getString("success").equalsIgnoreCase("true")) {
                            SharedPreferences preferences = mContext.getSharedPreferences("Login_Data", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("OrderID", json_main.getString("OrderID"));
                            editor.commit();

                            //firebase event logging
                            Bundle bundle = new Bundle();
                            bundle.putString(Consts.getInstance().EVENT_NAME, Consts.getInstance().CREATE_ORDER_REQUEST);
                            bundle.putString(Consts.getInstance().FIREBASE_USERID, preferences_Login_Data.getString("UserID", ""));
                            bundle.putString(Consts.getInstance().FIREBASE_ORDER_ID, json_main.getString("OrderID"));
                            bundle.putString(Consts.getInstance().FIREBASE_WORKERID, WorkerID);
                            bundle.putString(Consts.getInstance().WORKER_CONTRACT_FEES, ContractFees);
                            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "text");
                            mFirebaseAnylytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                            Log.e("params response", "## params resp order id :: " + json_main.getString("OrderID"));
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

                    String UserID = preferences_Login_Data.getString("UserID", "");
                    if (UserID.equals("")) {
                        UserID = "0";
                    }

                    String OrderID = preferences_Login_Data.getString("OrderID", "");
                    if (OrderID.equals("")) {
                        OrderID = "0";
                    }

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("language", preferences.getString("language", ""));
                    params.put("WorkerID", WorkerID);
                    params.put("WorkerContractFees", ContractFees);
                    params.put("UserID", UserID);
                    params.put("OrderID", OrderID);
                    Log.e("params response", "## params order id :: " + OrderID);
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
            findWorkerApi();
        } else {
            Consts.getInstance().Act_vity = "Subcat";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }

    private void request_order_list() {
        String tag_string_req = "req";
//        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().request_order_list, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            Log.e("params response", "" + Consts.getInstance().request_order_list + response);
                            if (json_main.getString("success").equalsIgnoreCase("True")) {
                                JSONArray array_RequestOrderlist = json_main.getJSONArray("RequestOrderlist");

                                SharedPreferences preferences = mContext.getSharedPreferences("Login_Data", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();

                                if (array_RequestOrderlist.length() > 0) {
                                    OrderId= json_main.getString("OrderID");

                                    editor.putString("OrderID", json_main.getString("OrderID"));
                                    editor.commit();
                                } else {
                                    OrderId= "0";

                                    editor.putString("RequestCount", "0");
                                    editor.putString("OrderID", "0");
                                    editor.commit();
                                }
                            } else {
                                OrderId= "0";
                                SharedPreferences preferences = mContext.getSharedPreferences("Login_Data", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("RequestCount", "0");
                                editor.putString("OrderID", "0");
                                editor.commit();
                            }
                            // request_worker_count();
                        } catch (Exception e) {
                            e.printStackTrace();

                            OrderId= "0";
                            SharedPreferences preferences = mContext.getSharedPreferences("Login_Data", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("OrderID", "0");
                            editor.commit();
                        }
                    }
                });

            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.toString());
//                progress.setVisibility(View.GONE);

                OrderId= "0";
                SharedPreferences preferences = mContext.getSharedPreferences("Login_Data", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("OrderID", "0");
                editor.commit();
                //Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {   //Post
                String OrderID = preferences_Login_Data.getString("OrderID", "");
                Log.e("response", "**** my requset  OrderID befff :: " + OrderID);
                if (OrderID.equals("")) {
                    OrderID = "0";
                }
                Log.e("response", "**** my requset  OrderID affff :: " + OrderID);

                String UserID = preferences_Login_Data.getString("UserID", "");
                if (UserID.equals("")) {
                    UserID = "0";
                }
                Map<String, String> params = new HashMap<String, String>();///params put, not used in get method
                params.put("OrderID", OrderID);
                params.put("language", preferences.getString("language", ""));
                params.put("UserID", UserID);
                Log.e("params", "" + Consts.getInstance().request_order_list + params);
                Log.e("params response", "## params list order id :: " + OrderID);
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

    public void Sort(int sortSelected) {
        if (sortSelected == 0) {
            try {
                setWorkerAdapter(selectedSubCategory);
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

