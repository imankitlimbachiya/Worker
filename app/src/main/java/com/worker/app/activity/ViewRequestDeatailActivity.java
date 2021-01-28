package com.worker.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.worker.app.BaseActivity;
import com.worker.app.R;
import com.worker.app.model.CategoryModel;
import com.worker.app.model.SubCategoryModel;
import com.worker.app.model.WorkerModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyTextView;

public class ViewRequestDeatailActivity extends BaseActivity {

    private RecyclerView recycle_category;
    private ArrayList<CategoryModel> list_category = new ArrayList<>();
    private ArrayList<SubCategoryModel> list_Subcategory = new ArrayList<>();
    private ArrayList<WorkerModel> list_SubcategoryDetail = new ArrayList<>();
    ProgressBar progress;
    String RequestID;
    TextView tv_request_id, tv_request_date, tv_country, tv_Gender, tv_ageRange, tv_totalCat;
    subCategoryAdpter subCategoryAdpter;
    int pos = -1;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_request_detail);

        mContext = this;

        init();

        intent();
    }

    public void init() {
        recycle_category = findViewById(R.id.recycle_category);
        progress = findViewById(R.id.progress);
        tv_request_id = findViewById(R.id.tv_request_id);
        tv_request_date = findViewById(R.id.tv_request_date);
        tv_country = findViewById(R.id.tv_country);
        tv_Gender = findViewById(R.id.tv_Gender);
        tv_ageRange = findViewById(R.id.tv_ageRange);
        tv_totalCat = findViewById(R.id.tv_totalCat);
    }

    public void intent() {
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {
            RequestID = (String) bd.get("RequestID");
        }
    }

    private void request_worker_detail() {
        String tag_string_req = "req";
        list_category.clear();
        list_SubcategoryDetail.clear();
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().request_worker_detail, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            Log.e("response", "" + Consts.getInstance().request_worker_detail + json_main);
                            if (json_main.getString("success").equalsIgnoreCase("True")) {
                                JSONArray array_Request_Detail = json_main.getJSONArray("Request_Detail");
                                for (int i = 0; i < array_Request_Detail.length(); i++) {
                                    tv_request_id.setText(array_Request_Detail.getJSONObject(i).getString("RequestID"));
                                    tv_request_date.setText(array_Request_Detail.getJSONObject(i).getString("RequestDate"));
                                    tv_country.setText(array_Request_Detail.getJSONObject(i).getString("CountryName"));
                                    tv_Gender.setText(array_Request_Detail.getJSONObject(i).getString("Gender"));
                                    tv_ageRange.setText(array_Request_Detail.getJSONObject(i).getString("AgeRenge"));
                                    tv_totalCat.setText(array_Request_Detail.getJSONObject(i).getString("TotalCategory"));
                                    JSONArray array_Category = array_Request_Detail.getJSONObject(i).getJSONArray("Category");
                                    for (int j = 0; j < array_Category.length(); j++) {
                                        CategoryModel categoryModel = new CategoryModel();
                                        categoryModel.setCategoryName(array_Category.getJSONObject(j).getString("CategoryName"));
                                        categoryModel.setSubCat(array_Category.getJSONObject(j).getString("SubCategory"));
                                        list_category.add(categoryModel);
                                    }
                                }
                                if (list_category.size() > 0) {
                                    CategoryAdpter categoryAdpter = new CategoryAdpter(ViewRequestDeatailActivity.this, list_category);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                                    recycle_category.setLayoutManager(mLayoutManager);
                                    recycle_category.setItemAnimator(new DefaultItemAnimator());
                                    recycle_category.setAdapter(categoryAdpter);
                                }
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
                SharedPreferences preferences_language = getSharedPreferences("Language", MODE_PRIVATE);
                params.put("language", preferences_language.getString("language", ""));
                params.put("RequestID", RequestID);
                Log.e("params", Consts.getInstance().request_worker_detail + params);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().request_worker_detail);///cache remove
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);//manage request
    }

    public class CategoryAdpter extends RecyclerView.Adapter<CategoryAdpter.MyViewHolder> {

        private List<CategoryModel> arraylist;
        Context mContext;
        JSONArray array_SubCategory;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private MyTextView text_category;
            private RecyclerView recycler_subCate;

            public MyViewHolder(View view) {
                super(view);

                text_category = view.findViewById(R.id.text_category);
                recycler_subCate = view.findViewById(R.id.recycler_subCate);
            }
        }

        public CategoryAdpter(Context mContext, List<CategoryModel> listSlider) {
            this.mContext = mContext;
            this.arraylist = listSlider;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_adpter, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            CategoryModel categoryModel = arraylist.get(position);
            if (categoryModel.getCategoryName().equals("") || categoryModel.getCategoryName().equals("null") || categoryModel.getCategoryName().equals(null) || categoryModel.getCategoryName() == null) {
                holder.text_category.setText("-");
            } else {
                holder.text_category.setText(categoryModel.getCategoryName());
            }

            try {
                list_Subcategory.clear();
                array_SubCategory = new JSONArray(categoryModel.getSubCat());
                for (int i = 0; i < array_SubCategory.length(); i++) {
                    Log.e("", "" + array_SubCategory.toString());
                    SubCategoryModel model = new SubCategoryModel();
                    model.setSubCategoryID(array_SubCategory.getJSONObject(i).getString("SubCategoryID"));
                    model.setSubCategoryName(array_SubCategory.getJSONObject(i).getString("SubCategoryName"));
                    model.setQnty(array_SubCategory.getJSONObject(i).getString("Quantity"));
                    model.setWorkerCount(array_SubCategory.getJSONObject(i).getString("WorkerCount"));
                    model.setWorkers(array_SubCategory.getJSONObject(i).getString("Workers"));
                    list_Subcategory.add(model);
                }
                if (list_Subcategory.size() > 0) {
                    subCategoryAdpter = new subCategoryAdpter(ViewRequestDeatailActivity.this, list_Subcategory);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                    holder.recycler_subCate.setLayoutManager(mLayoutManager);
                    holder.recycler_subCate.setItemAnimator(new DefaultItemAnimator());
                    holder.recycler_subCate.setAdapter(subCategoryAdpter);
                    subCategoryAdpter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return arraylist.size();
        }
    }

    public class subCategoryAdpter extends RecyclerView.Adapter<subCategoryAdpter.MyViewHolder> {

        private List<SubCategoryModel> arraylist;
        Context mContext;
        String check = "0";
        String firstTime = "1";

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private MyTextView text_category, txt_qnty, txt_number;
            private RecyclerView recycler_subCateDetail;
            private LinearLayout linear_subcate;
            private ImageView img_arraow;

            public MyViewHolder(View view) {
                super(view);
                text_category = view.findViewById(R.id.text_category);
                txt_qnty = view.findViewById(R.id.txt_qnty);
                txt_number = view.findViewById(R.id.txt_number);
                linear_subcate = view.findViewById(R.id.linear_subcate);
                img_arraow = view.findViewById(R.id.img_arraow);
                recycler_subCateDetail = view.findViewById(R.id.recycler_subCateDetail);
            }
        }

        public subCategoryAdpter(Context mContext, ArrayList<SubCategoryModel> listSlider) {
            this.mContext = mContext;
            this.arraylist = listSlider;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_subcategory_adpter, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            SubCategoryModel SubCategoryModel = arraylist.get(position);

            holder.txt_number.setText(String.valueOf(position + 1));

            if (SubCategoryModel.getSubCategoryName().equals("") || SubCategoryModel.getSubCategoryName().equals("null") || SubCategoryModel.getSubCategoryName().equals(null) || SubCategoryModel.getSubCategoryName() == null) {
                holder.text_category.setText("-");
            } else {
                holder.text_category.setText(SubCategoryModel.getSubCategoryName());
            }

            if (SubCategoryModel.getQnty().equals("") || SubCategoryModel.getQnty().equals("null") || SubCategoryModel.getQnty().equals(null) || SubCategoryModel.getQnty() == null) {
                holder.txt_qnty.setText("-");
            } else {
                holder.txt_qnty.setText(SubCategoryModel.getQnty());
            }

            try {
                list_SubcategoryDetail.clear();
                JSONArray array_Workers = new JSONArray(SubCategoryModel.getWorkers());
                for (int i = 0; i < array_Workers.length(); i++) {
                    WorkerModel model = new WorkerModel();
                    model.setWorkerName(array_Workers.getJSONObject(i).getString("WorkerName"));
                    model.setAge(array_Workers.getJSONObject(i).getString("Age"));
                    model.setwGender(array_Workers.getJSONObject(i).getString("Gender"));
                    model.setContactFees(array_Workers.getJSONObject(i).getString("ContactFees"));
                    model.setReligion(array_Workers.getJSONObject(i).getString("Religion"));
                    model.setWorkerID(array_Workers.getJSONObject(i).getString("WorkerID"));
                    list_SubcategoryDetail.add(model);
                }
                if (list_SubcategoryDetail.size() > 0) {
                    holder.recycler_subCateDetail.setVisibility(View.VISIBLE);
                    holder.img_arraow.setBackgroundResource(R.drawable.icondown);
                    subCategoryDetailAdpter subCategoryDetailAdpter = new subCategoryDetailAdpter(ViewRequestDeatailActivity.this, list_SubcategoryDetail);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                    holder.recycler_subCateDetail.setLayoutManager(mLayoutManager);
                    holder.recycler_subCateDetail.setItemAnimator(new DefaultItemAnimator());
                    holder.recycler_subCateDetail.setAdapter(subCategoryDetailAdpter);
                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.linear_subcate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (check.equals("1")) {
                        check = "0";
                        try {
                            list_SubcategoryDetail.clear();
                            JSONArray array_Workers = new JSONArray(SubCategoryModel.getWorkers());
                            for (int i = 0; i < array_Workers.length(); i++) {
                                WorkerModel model = new WorkerModel();
                                model.setWorkerName(array_Workers.getJSONObject(i).getString("WorkerName"));
                                model.setAge(array_Workers.getJSONObject(i).getString("Age"));
                                model.setwGender(array_Workers.getJSONObject(i).getString("Gender"));
                                model.setContactFees(array_Workers.getJSONObject(i).getString("ContactFees"));
                                model.setReligion(array_Workers.getJSONObject(i).getString("Religion"));
                                model.setWorkerID(array_Workers.getJSONObject(i).getString("WorkerID"));
                                list_SubcategoryDetail.add(model);
                            }
                            if (list_SubcategoryDetail.size() > 0) {
                                holder.recycler_subCateDetail.setVisibility(View.VISIBLE);
                                holder.img_arraow.setBackgroundResource(R.drawable.icondown);
                                subCategoryDetailAdpter subCategoryDetailAdpter = new subCategoryDetailAdpter(ViewRequestDeatailActivity.this, list_SubcategoryDetail);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                                holder.recycler_subCateDetail.setLayoutManager(mLayoutManager);
                                holder.recycler_subCateDetail.setItemAnimator(new DefaultItemAnimator());
                                holder.recycler_subCateDetail.setAdapter(subCategoryDetailAdpter);
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        check = "1";
                        try {
                            list_SubcategoryDetail.clear();
                            JSONArray array_Workers = new JSONArray(SubCategoryModel.getWorkers());
                            for (int i = 0; i < array_Workers.length(); i++) {
                                WorkerModel model = new WorkerModel();
                                model.setWorkerName(array_Workers.getJSONObject(i).getString("WorkerName"));
                                model.setAge(array_Workers.getJSONObject(i).getString("Age"));
                                model.setwGender(array_Workers.getJSONObject(i).getString("Gender"));
                                model.setContactFees(array_Workers.getJSONObject(i).getString("ContactFees"));
                                model.setReligion(array_Workers.getJSONObject(i).getString("Religion"));
                                model.setWorkerID(array_Workers.getJSONObject(i).getString("WorkerID"));
                                list_SubcategoryDetail.add(model);
                            }
                            if (list_SubcategoryDetail.size() > 0) {
                                holder.recycler_subCateDetail.setVisibility(View.GONE);
                                holder.img_arraow.setBackgroundResource(R.drawable.icon001);
                                subCategoryDetailAdpter subCategoryDetailAdpter = new subCategoryDetailAdpter(ViewRequestDeatailActivity.this, list_SubcategoryDetail);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                                holder.recycler_subCateDetail.setLayoutManager(mLayoutManager);
                                holder.recycler_subCateDetail.setItemAnimator(new DefaultItemAnimator());
                                holder.recycler_subCateDetail.setAdapter(subCategoryDetailAdpter);
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return arraylist.size();
        }
    }

    public class subCategoryDetailAdpter extends RecyclerView.Adapter<subCategoryDetailAdpter.MyViewHolder> {

        private List<WorkerModel> arraylist;
        Context mContext;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private MyTextView txt_Name, txt_Gender, txt_Religon, txt_Age, txt_fees, txt_SubcatNumber, txtYear;
            LinearLayout linear_wDetails;

            public MyViewHolder(View view) {
                super(view);
                txt_Name = view.findViewById(R.id.txt_Name);
                txt_Gender = view.findViewById(R.id.txt_Gender);
                txt_Religon = view.findViewById(R.id.txt_Religon);
                txt_Age = view.findViewById(R.id.txt_Age);
                txt_fees = view.findViewById(R.id.txt_fees);
                txt_SubcatNumber = view.findViewById(R.id.txt_SubcatNumber);
                linear_wDetails = view.findViewById(R.id.linear_wDetails);
                txtYear = view.findViewById(R.id.txtYear);
            }
        }

        public subCategoryDetailAdpter(Context mContext, List<WorkerModel> listSlider) {
            this.mContext = mContext;
            this.arraylist = listSlider;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subcate_deatil_adpter, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            WorkerModel workerModel = arraylist.get(position);

            holder.txt_SubcatNumber.setText(String.valueOf(position + 1));

            if (workerModel.getWorkerName().equals("") || workerModel.getWorkerName().equals("null") || workerModel.getWorkerName().equals(null) || workerModel.getWorkerName() == null) {
                holder.txt_Name.setText("-");
            } else {
                holder.txt_Name.setText(workerModel.getWorkerName());
            }
            if (workerModel.getwGender().equals("") || workerModel.getwGender().equals("null") || workerModel.getwGender().equals(null) || workerModel.getwGender() == null) {
                holder.txt_Gender.setText("-");
            } else {
                holder.txt_Gender.setText(workerModel.getwGender());
            }
            if (workerModel.getAge().equals("") || workerModel.getAge().equals("null") || workerModel.getAge().equals(null) || workerModel.getAge() == null) {
                holder.txt_Age.setText("-");
                holder.txtYear.setVisibility(View.GONE);
            } else {
                holder.txt_Age.setText(workerModel.getAge());
            }

            if (workerModel.getReligion().equals("") || workerModel.getReligion().equals("null") || workerModel.getReligion().equals(null) || workerModel.getReligion() == null) {
                holder.txt_Religon.setText("-");
            } else {
                holder.txt_Religon.setText(workerModel.getReligion());
            }
            if (workerModel.getContactFees().equals("") || workerModel.getContactFees().equals("null") || workerModel.getContactFees().equals(null) || workerModel.getContactFees() == null) {
                holder.txt_fees.setText("-");
            } else {
                holder.txt_fees.setText(workerModel.getContactFees());
            }

            holder.linear_wDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_find_worker = new Intent(ViewRequestDeatailActivity.this, WorkerProfileActivity.class);
                    intent_find_worker.putExtra("WorkerID", workerModel.getWorkerID());
                    startActivity(intent_find_worker);
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
        if (Consts.isNetworkAvailable(ViewRequestDeatailActivity.this)) {
            SharedPreferences preferences = getSharedPreferences("Login_Data", MODE_PRIVATE);
            String UserID = preferences.getString("UserID", "");
            if (UserID.equals("")) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            } else {
                request_worker_detail();
            }
        } else {
            Consts.getInstance().Act_vity="Viewrequestdetail";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(mContext, MyRequestListingActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
