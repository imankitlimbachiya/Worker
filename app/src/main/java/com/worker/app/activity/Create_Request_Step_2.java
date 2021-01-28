package com.worker.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.worker.app.BaseActivity;
import com.worker.app.R;
import com.worker.app.model.CategoryModel;
import com.worker.app.model.RequestSubcatModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyButton;
import com.worker.app.utility.MyEditText;
import com.worker.app.utility.MyTextView;
import com.worker.app.utility.Utils;

public class Create_Request_Step_2 extends BaseActivity implements View.OnClickListener {

    ArrayList<CategoryModel> listCategories = new ArrayList<>();
    ArrayList<RequestSubcatModel> listsubcat = new ArrayList<>();
    private RecyclerView recycle_group_list;
    RelativeLayout rel_bottom, rel_main, rel_other;
    MyTextView txt_category;
    int pos = -1;
    ImageView plus;
    String countryId, string, language;
    MyButton btn_next;
    ProgressBar progress;
    CreateRequestStepAdapter2 mAdapter;
    SubCategoryAdapter subCategoryAdapter;
    ArrayList<String> selectedId = new ArrayList<String>();
    JSONArray json_Category, json_subcategory;
    JSONObject jobj_Category;
    JSONArray json_subcategory_other;
    SharedPreferences preferences, preferences_Login_Data;
    LinearLayout lin_main;
    ScrollView scroll_item;
    MyButton btnSearch;
    MyEditText edt_search;
    ImageView image_gone;
    RelativeLayout rel_forgot;
    ListView list_item;
    ArrayList<String> arrayList = new ArrayList<String>();
    ListAdapter ListAdapter;
    JSONObject jobj_subCategory, jobj_Category_other;
    Context mContext;
    private ImageView imgCloseDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_request_step2);

        mContext = this;

        init();

        intent();

        if (Consts.getInstance().isNetworkAvailable(mContext)) {
            category_list();
        } else {
            Consts.getInstance().Act_vity="Createstep2";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
    }

    private void init() {
        imgCloseDialog=findViewById(R.id.imgCloseDialog);
        imgCloseDialog.setOnClickListener(this);
        recycle_group_list = findViewById(R.id.recycle_group_list);
        txt_category = findViewById(R.id.txt_category);
        rel_bottom = findViewById(R.id.rel_bottom);
        rel_main = findViewById(R.id.rel_main);
        plus = findViewById(R.id.plus);
        plus.setVisibility(View.GONE);
        btn_next = findViewById(R.id.btn_next);
        progress = findViewById(R.id.progress);
        lin_main = findViewById(R.id.lin_main);
        scroll_item = findViewById(R.id.scroll_item);
        rel_other = findViewById(R.id.rel_other);
        btnSearch = findViewById(R.id.btnSearch);
        edt_search = findViewById(R.id.edt_search);
        image_gone = findViewById(R.id.image_gone);
        rel_forgot = findViewById(R.id.rel_forgot);
        list_item = findViewById(R.id.list_item);

        json_Category = new JSONArray();
        json_subcategory_other = new JSONArray();

        btn_next.setOnClickListener(this);
        rel_other.setOnClickListener(this);
        image_gone.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);
        language = preferences.getString("language", "");
    }

    public void intent() {
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {
            countryId = (String) bd.get("countryId");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgCloseDialog:
                Utils.getInstance().hideKeyboard(Create_Request_Step_2.this);
                rel_forgot.setVisibility(View.GONE);
                break;
            case R.id.btn_next:
                Intent intent = new Intent(mContext, CreateRequestActivity_3.class);
                intent.putExtra("countryId", countryId);
                intent.putExtra("RequestCategory", json_Category.toString());
                startActivity(intent);
                break;
            case R.id.rel_other:
                rel_forgot.setVisibility(View.VISIBLE);
                break;
            /*case R.id.image_gone:
                Utils.getInstance().hideKeyboard(Create_Request_Step_2.this);
                rel_forgot.setVisibility(View.GONE);
                break;*/
            case R.id.btnSearch://save ad requested category
                if (edt_search.getText().toString().equals("")) {
                    Toast.makeText(Create_Request_Step_2.this, getString(R.string.categ), Toast.LENGTH_LONG).show();
                } else {
                    rel_bottom.setVisibility(View.VISIBLE);
                    arrayList.add(edt_search.getText().toString());
                    try {
                        jobj_subCategory = new JSONObject();
                        jobj_Category_other = new JSONObject();

                        //create random between 500 and greater for generate new sub category id
                        Random random = new Random();
                        int randomSubCatID = random.nextInt(1000 - 500 + 1) + 500;
                        Log.e("create2", "** randomSubCatID :: " + randomSubCatID);
                        jobj_subCategory.put("SubCategoryID", randomSubCatID);
                        jobj_subCategory.put("SubCategoryName", edt_search.getText().toString());
                        jobj_subCategory.put("SubCategoryNameArabic", edt_search.getText().toString());
                        jobj_subCategory.put("Quantity", "1");
                        json_subcategory_other.put(jobj_subCategory);
                        int match_id = 0;
                        boolean match = false;
                        if (json_Category.length() > 0) {
                            for (int i = 0; i < json_Category.length(); i++) {
                                try {
                                    if (json_Category.getJSONObject(i).getString("CategoryID").equals("0")) {
                                        match = true;
                                        match_id = i;
                                        break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (match) {
                            jobj_Category_other.put("CategoryName", "Others");
                            jobj_Category_other.put("CategoryNameArabic", "الآخرين");
                            jobj_Category_other.put("CategoryID", "0");
                            jobj_Category_other.put("SubCategory", json_subcategory_other);
                            json_Category.put(match_id, jobj_Category_other);
                        } else {
                            jobj_Category_other.put("CategoryName", "Others");
                            jobj_Category_other.put("CategoryNameArabic", "الآخرين");
                            jobj_Category_other.put("CategoryID", "0");
                            jobj_Category_other.put("SubCategory", json_subcategory_other);
                            json_Category.put(jobj_Category_other);
                        }
                        ListAdapter = new ListAdapter(mContext, arrayList);
                        list_item.setAdapter(ListAdapter);
                        ListAdapter.notifyDataSetChanged();
                        edt_search.setText("");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                SetBottomCategoryView();
                break;
        }
    }

    /*private void category_list() {
        String tag_string_req = "req";
        listCategories.clear();
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().CATEGORY_LIST_TEST, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                Log.e("response", "" + Consts.getInstance().CATEGORY_LIST_TEST + response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            if (json_main.getString("success").equalsIgnoreCase("True")) {
                                // rel_other.setVisibility(View.VISIBLE);
                                JSONArray array_Category_list = json_main.getJSONArray("Category_list");
                                for (int i = 0; i < array_Category_list.length(); i++) {
                                    CategoryModel categoryModel = new CategoryModel();
                                    categoryModel.setCategoryID(array_Category_list.getJSONObject(i).getString("CategoryID"));
                                    categoryModel.setEnglish_name(array_Category_list.getJSONObject(i).getString("CategoryName"));
                                    categoryModel.setArabic_name(array_Category_list.getJSONObject(i).getString("CategoryNameArabic"));
                                    categoryModel.setImgCategory(array_Category_list.getJSONObject(i).getString("StripImage"));
                                    categoryModel.setSubCategory(array_Category_list.getJSONObject(i).getString("SubCategory"));
                                    listCategories.add(categoryModel);
                                }
                                if (listCategories.size() > 0) {
                                    mAdapter = new CreateRequestStepAdapter2(Create_Request_Step_2.this, listCategories);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                                    recycle_group_list.setLayoutManager(mLayoutManager);
                                    recycle_group_list.setItemAnimator(new DefaultItemAnimator());
                                    recycle_group_list.setAdapter(mAdapter);
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
                params.put("language", preferences.getString("language", ""));
                Log.e("params", Consts.getInstance().CATEGORY_LIST_TEST + params);
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
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().CATEGORY_LIST_TEST);///cache remove
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);//manage request
    }*/

    private void category_list() {
        String tag_string_req = "req";
        listCategories.clear();
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().category_list, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                Log.e("response", "" + Consts.getInstance().category_list + response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            if (json_main.getString("success").equalsIgnoreCase("True")) {
//                                rel_other.setVisibility(View.VISIBLE);
                                JSONArray array_Category_list = json_main.getJSONArray("Category_list");
                                for (int i = 0; i < array_Category_list.length(); i++) {
                                    CategoryModel categoryModel = new CategoryModel();
                                    categoryModel.setCategoryID(array_Category_list.getJSONObject(i).getString("CategoryID"));
                                    categoryModel.setEnglish_name(array_Category_list.getJSONObject(i).getString("CategoryName"));
                                    categoryModel.setArabic_name(array_Category_list.getJSONObject(i).getString("CategoryNameArabic"));
                                    categoryModel.setImgCategory(array_Category_list.getJSONObject(i).getString("StripImage"));
                                    categoryModel.setSubCategory(array_Category_list.getJSONObject(i).getString("SubCategory"));
                                    listCategories.add(categoryModel);
                                }
                                //add other category in list
                                CategoryModel categoryModel = new CategoryModel();
                                categoryModel.setCategoryID("0");
                                categoryModel.setEnglish_name(getString(R.string.other_category));
                                categoryModel.setArabic_name(getString(R.string.arabic_other_category));
                                categoryModel.setImgCategory(array_Category_list.getJSONObject(0).getString("StripImage"));
                                categoryModel.setSubCategory("");
                                listCategories.add(categoryModel);

                                if (listCategories.size() > 0) {
                                    mAdapter = new CreateRequestStepAdapter2(Create_Request_Step_2.this, listCategories);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                                    recycle_group_list.setLayoutManager(mLayoutManager);
                                    recycle_group_list.setItemAnimator(new DefaultItemAnimator());
                                    recycle_group_list.setAdapter(mAdapter);
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
                params.put("language", preferences.getString("language", ""));
                Log.e("params", Consts.getInstance().category_list + params);
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
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().category_list);///cache remove
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);//manage request
    }

    public class ListAdapter extends ArrayAdapter<String> {

        TextView txt_timer_view;
        ImageView img_delete;
        private ArrayList<String> arrayList = new ArrayList<String>();
        public Context mContext;
        int i = 0;

        public ListAdapter(Context context, ArrayList<String> arrayList) {
            super(context, R.layout.listview_adapter, arrayList);
            this.mContext = context;
            this.arrayList = arrayList;
        }

        public void clear() {
            this.arrayList.clear();
        }

        public int getCount() {
            return arrayList.size();
        }

        public String getItem(int position) {

            return arrayList.get(position);
        }

        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(final int position, View view1, ViewGroup arg2) {
            View view = view1;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_adapter, null);
            img_delete = view.findViewById(R.id.img_delete);
            txt_timer_view = view.findViewById(R.id.txt_timer_view);
            string = arrayList.get(position);
            txt_timer_view.setText(string);

            img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rel_bottom.setVisibility(View.VISIBLE);
                    JSONObject jobj_Category_other = new JSONObject();
                    try {
                        jobj_Category_other.put("CategoryName", "Others");
                        jobj_Category_other.put("CategoryNameArabic", "الآخرين");
                        jobj_Category_other.put("CategoryID", "0");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    remove(arrayList.get(position));
                    Boolean match = false;
                    int match_id = 0;
                    for (int i = 0; i < json_Category.length(); i++) {
                        try {
                            if (json_Category.getJSONObject(i).getString("CategoryID").equals("0")) {
                                match = true;
                                match_id = i;
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    JSONArray jtemp_subcategory = new JSONArray();
                    try {
                        jtemp_subcategory = json_Category.getJSONObject(match_id).getJSONArray("SubCategory");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        jtemp_subcategory.remove(position);
                    }
                    if (jtemp_subcategory.length() > 0) {
                        try {
                            jobj_Category_other.put("SubCategory", jtemp_subcategory);
                            json_Category.put(match_id, jobj_Category_other);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            json_Category.remove(match_id);
                        }
                    }
                    SetBottomCategoryView();
                }
            });
            return view;
        }
    }

    public class CreateRequestStepAdapter2 extends RecyclerView.Adapter<CreateRequestStepAdapter2.MyViewHolder> {

        private List<CategoryModel> listSlider;
        private Context mContext;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public ImageView imgCat;
            public MyTextView english_name, arabic_name;
            private RelativeLayout rel_main;
            RecyclerView recycle_subgroup_list;

            public MyViewHolder(View view) {
                super(view);

                imgCat = view.findViewById(R.id.imgCat);
                arabic_name = view.findViewById(R.id.arabic_name);
                english_name = view.findViewById(R.id.english_name);
                rel_main = view.findViewById(R.id.rel_main);
                recycle_subgroup_list = view.findViewById(R.id.recycle_subgroup_list);
            }
        }

        public CreateRequestStepAdapter2(Context mContext, List<CategoryModel> listSlider) {
            this.mContext = mContext;
            this.listSlider = listSlider;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_create_request_category, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            CategoryModel categoryModel = listSlider.get(position);

            if (categoryModel.getArabic_name().equals("") || categoryModel.getArabic_name().equals("null") || categoryModel.getArabic_name().equals(null) || categoryModel.getArabic_name() == null) {
                holder.arabic_name.setText("-");
            } else {
                holder.arabic_name.setText(categoryModel.getArabic_name());
            }
            if (categoryModel.getEnglish_name().equals("") || categoryModel.getEnglish_name().equals("null") || categoryModel.getEnglish_name().equals(null) || categoryModel.getEnglish_name() == null) {
                holder.english_name.setText("-");
            } else {
                holder.english_name.setText(categoryModel.getEnglish_name());
            }

            Glide.with(mContext).load(categoryModel.getImgCategory()).into(holder.imgCat);

            listsubcat.clear();

            holder.recycle_subgroup_list.setVisibility(View.GONE);

            if (position != listCategories.size() - 1) {
                if (pos == position) {
                    holder.rel_main.setBackgroundResource(R.drawable.select_country_bg);
                    try {
                        JSONArray array_SubCategory = new JSONArray(categoryModel.getSubCategory());
                        for (int i = 0; i < array_SubCategory.length(); i++) {
                            RequestSubcatModel requestSubcatModel = new RequestSubcatModel();
                            requestSubcatModel.setSubCategoryID(array_SubCategory.getJSONObject(i).getString("SubCategoryID"));
                            requestSubcatModel.setSubcategoryName(array_SubCategory.getJSONObject(i).getString("SubCategoryName"));
                            listsubcat.add(requestSubcatModel);
                        }
                        if (listsubcat.size() > 0) {
                            subCategoryAdapter = new SubCategoryAdapter(mContext, listsubcat);
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2, RecyclerView.VERTICAL, false);
                            holder.recycle_subgroup_list.setLayoutManager(mLayoutManager);
                            holder.recycle_subgroup_list.setItemAnimator(new DefaultItemAnimator());
                            holder.recycle_subgroup_list.setAdapter(subCategoryAdapter);
                            holder.recycle_subgroup_list.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (pos == position) {
                    holder.recycle_subgroup_list.setVisibility(View.VISIBLE);
                } else {
                    holder.recycle_subgroup_list.setVisibility(View.GONE);
                }
            }

            holder.rel_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position != listCategories.size() - 1) {
                        try {
                            jobj_Category = new JSONObject();
                            jobj_Category.put("CategoryID", categoryModel.getCategoryID());
                            jobj_Category.put("CategoryName", categoryModel.getEnglish_name());
                            jobj_Category.put("CategoryNameArabic", categoryModel.getArabic_name());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pos = position;
                        mAdapter.notifyDataSetChanged();
                    }else{
                        rel_forgot.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return listSlider.size();
        }
    }

    public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder> {

        private List<RequestSubcatModel> listSlider;
        private Context mContext;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private MyTextView text_subCategory;
            private RelativeLayout rel_cat_main;

            public MyViewHolder(View view) {
                super(view);
                text_subCategory = view.findViewById(R.id.text_subCategory);
                rel_cat_main = view.findViewById(R.id.rel_cat_main);
            }
        }

        public SubCategoryAdapter(Context mContext, List<RequestSubcatModel> listSlider) {
            this.mContext = mContext;
            this.listSlider = listSlider;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_matching_subcategory, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            RequestSubcatModel requestSubcatModel = listSlider.get(position);

            if (requestSubcatModel.getSubcategoryName().equals("") || requestSubcatModel.getSubcategoryName().equals("null") || requestSubcatModel.getSubcategoryName().equals(null) || requestSubcatModel.getSubcategoryName() == null) {
                holder.text_subCategory.setText("-");
            } else {
                holder.text_subCategory.setText(requestSubcatModel.getSubcategoryName());
            }
            if (selectedId.contains(requestSubcatModel.getSubCategoryID())) {
                holder.text_subCategory.setTextColor(getResources().getColor(R.color.colorWhite));
                holder.text_subCategory.setBackgroundResource(R.drawable.category_select_bg);
            } else {
                holder.text_subCategory.setBackgroundResource(R.drawable.category_bg);
                holder.text_subCategory.setTextColor(getResources().getColor(R.color.colorAccent));
            }

            holder.rel_cat_main.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    rel_bottom.setVisibility(View.VISIBLE);
                    if (selectedId.contains(requestSubcatModel.getSubCategoryID())) {
                        selectedId.remove(requestSubcatModel.getSubCategoryID());
                        holder.text_subCategory.setBackgroundResource(R.drawable.category_bg);
                        holder.text_subCategory.setTextColor(getResources().getColor(R.color.colorAccent));
                        if (json_Category.length() > 0) {
                            boolean match = false;
                            int match_id = 0;
                            for (int i = 0; i < json_Category.length(); i++) {
                                try {
                                    if (json_Category.getJSONObject(i).getString("CategoryID").equals(jobj_Category.getString("CategoryID"))) {
                                        Log.e("json_Category", "" + json_Category.toString());
                                        match = true;
                                        match_id = i;
                                        break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (match == true) {
                                JSONArray jtemp_subcategory = new JSONArray();
                                try {
                                    jtemp_subcategory = json_Category.getJSONObject(match_id).getJSONArray("SubCategory");
                                    boolean matchsub = false;
                                    int matchsub_id = 0;
                                    for (int i = 0; i < jtemp_subcategory.length(); i++) {
                                        if (jtemp_subcategory.getJSONObject(i).getString("SubCategoryID").equals(requestSubcatModel.getSubCategoryID())) {
                                            matchsub = true;
                                            matchsub_id = i;
                                            break;
                                        }
                                    }
                                    jtemp_subcategory.remove(matchsub_id);
                                    if (jtemp_subcategory.length() > 0) {
                                        jobj_Category.put("SubCategory", jtemp_subcategory);
                                        json_Category.put(match_id, jobj_Category);
                                    } else {
                                        json_Category.remove(match_id);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        selectedId.add(requestSubcatModel.getSubCategoryID());
                        holder.text_subCategory.setTextColor(getResources().getColor(R.color.colorWhite));
                        holder.text_subCategory.setBackgroundResource(R.drawable.category_select_bg);
                        try {
                            JSONObject jobj_subCategory = new JSONObject();
                            json_subcategory = new JSONArray();
                            jobj_subCategory.put("SubCategoryID", requestSubcatModel.getSubCategoryID());
                            jobj_subCategory.put("SubCategoryName", requestSubcatModel.getSubcategoryName());
                            jobj_subCategory.put("Quantity", "1");
                            if (json_Category.length() > 0) {
                                boolean match = false;
                                int match_id = 0;
                                for (int i = 0; i < json_Category.length(); i++) {
                                    if (json_Category.getJSONObject(i).getString("CategoryID").equals(jobj_Category.getString("CategoryID"))) {
                                        match = true;
                                        match_id = i;
                                        break;
                                    }
                                }
                                JSONArray jtemp_subcategory = new JSONArray();
                                if (match == true) {
                                    jtemp_subcategory = json_Category.getJSONObject(match_id).getJSONArray("SubCategory");
                                    jtemp_subcategory.put(jobj_subCategory);
                                    jobj_Category.put("SubCategory", jtemp_subcategory);
                                    json_Category.put(match_id, jobj_Category);
                                } else {
                                    json_subcategory.put(jobj_subCategory);//array
                                    jobj_Category.put("SubCategory", json_subcategory);
                                    json_Category.put(jobj_Category);
                                }
                            } else {
                                json_subcategory.put(jobj_subCategory);//array
                                jobj_Category.put("SubCategory", json_subcategory);
                                json_Category.put(jobj_Category);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    SetBottomCategoryView();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listSlider.size();
        }
    }

    public void SetBottomCategoryView() {
        if (json_Category.length() > 0) {
            for (int i = 0; i < json_Category.length(); i++) {
                try {
                    if (language.equals("English")) {
                        // English;
                        if (i == 0) {
                            txt_category.setText(json_Category.getJSONObject(i).getString("CategoryName"));
                        } else {
                            txt_category.setText(txt_category.getText() + "\n" + json_Category.getJSONObject(i).getString("CategoryName"));
                        }
                    } else {
                        // Arabic;
                        if (i == 0) {
                            txt_category.setText(json_Category.getJSONObject(i).getString("CategoryNameArabic"));
                        } else {
                            txt_category.setText(txt_category.getText() + "\n" + json_Category.getJSONObject(i).getString("CategoryNameArabic"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            setMargins(lin_main, 0, 0, 0, 350);
        } else {
            txt_category.setText("");
            rel_bottom.setVisibility(View.GONE);
            setMargins(lin_main, 0, 0, 0, 0);
        }
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        /*if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }*/
    }

}



/*public class Create_Request_Step_2 extends BaseActivity implements View.OnClickListener {

    ArrayList<CategoryModel> listCategories = new ArrayList<>();
    ArrayList<RequestSubcatModel> listsubcat = new ArrayList<>();
    private RecyclerView recycle_group_list;
    RelativeLayout rel_bottom, rel_main, rel_other;
    MyTextView txt_category;
    int pos = -1;
    ImageView plus;
    String countryId, string, language;
    MyButton btn_next;
    ProgressBar progress;
    CreateRequestStepAdapter2 mAdapter;
    SubCategoryAdapter subCategoryAdapter;
    ArrayList<String> selectedId = new ArrayList<String>();
    JSONArray json_Category, json_subcategory;
    JSONObject jobj_Category;
    JSONArray json_subcategory_other;
    SharedPreferences preferences, preferences_Login_Data;
    LinearLayout lin_main;
    ScrollView scroll_item;
    MyButton btnSearch;
    MyEditText edt_search;
    ImageView image_gone;
    RelativeLayout rel_forgot;
    ListView list_item;
    ArrayList<String> arrayList = new ArrayList<String>();
    ListAdapter ListAdapter;
    JSONObject jobj_subCategory, jobj_Category_other;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_request_step2);

        mContext = this;

        init();

        intent();
    }

    private void init() {
        recycle_group_list = findViewById(R.id.recycle_group_list);
        txt_category = findViewById(R.id.txt_category);
        rel_bottom = findViewById(R.id.rel_bottom);
        rel_main = findViewById(R.id.rel_main);
        plus = findViewById(R.id.plus);
        plus.setVisibility(View.GONE);
        btn_next = findViewById(R.id.btn_next);
        progress = findViewById(R.id.progress);
        lin_main = findViewById(R.id.lin_main);
        scroll_item = findViewById(R.id.scroll_item);
        rel_other = findViewById(R.id.rel_other);
        btnSearch = findViewById(R.id.btnSearch);
        edt_search = findViewById(R.id.edt_search);
        image_gone = findViewById(R.id.image_gone);
        rel_forgot = findViewById(R.id.rel_forgot);
        list_item = findViewById(R.id.list_item);

        json_Category = new JSONArray();
        json_subcategory_other = new JSONArray();

        btn_next.setOnClickListener(this);
        rel_other.setOnClickListener(this);
        image_gone.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);
        language = preferences.getString("language", "");
    }

    public void intent() {
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {
            countryId = (String) bd.get("countryId");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                Intent intent = new Intent(mContext, CreateRequestActivity_3.class);
                intent.putExtra("countryId", countryId);
                intent.putExtra("RequestCategory", json_Category.toString());
                startActivity(intent);
                break;
            case R.id.rel_other:
                rel_forgot.setVisibility(View.VISIBLE);
                break;
            case R.id.image_gone:
                rel_forgot.setVisibility(View.GONE);
                break;
            case R.id.btnSearch:
                if (edt_search.getText().toString().equals("")) {
                    Toast.makeText(Create_Request_Step_2.this, "Please enter Category", Toast.LENGTH_LONG).show();
                } else {
                    rel_bottom.setVisibility(View.VISIBLE);
                    arrayList.add(edt_search.getText().toString());
                    try {
                        jobj_subCategory = new JSONObject();
                        jobj_Category_other = new JSONObject();
                        jobj_subCategory.put("SubCategoryID", "0");
                        jobj_subCategory.put("SubCategoryName", edt_search.getText().toString());
                        jobj_subCategory.put("SubCategoryNameArabic", edt_search.getText().toString());
                        jobj_subCategory.put("Quantity", "1");
                        json_subcategory_other.put(jobj_subCategory);
                        int match_id = 0;
                        boolean match = false;
                        if (json_Category.length() > 0) {
                            for (int i = 0; i < json_Category.length(); i++) {
                                try {
                                    if (json_Category.getJSONObject(i).getString("CategoryID").equals("0")) {
                                        match = true;
                                        match_id = i;
                                        break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (match) {
                            jobj_Category_other.put("CategoryName", "Others");
                            jobj_Category_other.put("CategoryNameArabic", "الآخرين");
                            jobj_Category_other.put("CategoryID", "0");
                            jobj_Category_other.put("SubCategory", json_subcategory_other);
                            json_Category.put(match_id, jobj_Category_other);
                        } else {
                            jobj_Category_other.put("CategoryName", "Others");
                            jobj_Category_other.put("CategoryNameArabic", "الآخرين");
                            jobj_Category_other.put("CategoryID", "0");
                            jobj_Category_other.put("SubCategory", json_subcategory_other);
                            json_Category.put(jobj_Category_other);
                        }
                        ListAdapter = new ListAdapter(mContext, arrayList);
                        list_item.setAdapter(ListAdapter);
                        ListAdapter.notifyDataSetChanged();
                        edt_search.setText("");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                SetBottomCategoryView();
                break;
        }
    }

    *//*private void category_list() {
        String tag_string_req = "req";
        listCategories.clear();
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().CATEGORY_LIST_TEST, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                Log.e("response", "" + Consts.getInstance().CATEGORY_LIST_TEST + response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            if (json_main.getString("success").equalsIgnoreCase("True")) {
                                // rel_other.setVisibility(View.VISIBLE);
                                JSONArray array_Category_list = json_main.getJSONArray("Category_list");
                                for (int i = 0; i < array_Category_list.length(); i++) {
                                    CategoryModel categoryModel = new CategoryModel();
                                    categoryModel.setCategoryID(array_Category_list.getJSONObject(i).getString("CategoryID"));
                                    categoryModel.setEnglish_name(array_Category_list.getJSONObject(i).getString("CategoryName"));
                                    categoryModel.setArabic_name(array_Category_list.getJSONObject(i).getString("CategoryNameArabic"));
                                    categoryModel.setImgCategory(array_Category_list.getJSONObject(i).getString("StripImage"));
                                    categoryModel.setSubCategory(array_Category_list.getJSONObject(i).getString("SubCategory"));
                                    listCategories.add(categoryModel);
                                }
                                if (listCategories.size() > 0) {
                                    mAdapter = new CreateRequestStepAdapter2(Create_Request_Step_2.this, listCategories);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                                    recycle_group_list.setLayoutManager(mLayoutManager);
                                    recycle_group_list.setItemAnimator(new DefaultItemAnimator());
                                    recycle_group_list.setAdapter(mAdapter);
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
                params.put("language", preferences.getString("language", ""));
                Log.e("params", Consts.getInstance().CATEGORY_LIST_TEST + params);
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
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().CATEGORY_LIST_TEST);///cache remove
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);//manage request
    }*//*

    private void category_list() {
        String tag_string_req = "req";
        listCategories.clear();
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().category_list, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                Log.e("response", "" + Consts.getInstance().category_list + response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            if (json_main.getString("success").equalsIgnoreCase("True")) {
                                rel_other.setVisibility(View.VISIBLE);
                                JSONArray array_Category_list = json_main.getJSONArray("Category_list");
                                for (int i = 0; i < array_Category_list.length(); i++) {
                                    CategoryModel categoryModel = new CategoryModel();
                                    categoryModel.setCategoryID(array_Category_list.getJSONObject(i).getString("CategoryID"));
                                    categoryModel.setEnglish_name(array_Category_list.getJSONObject(i).getString("CategoryName"));
                                    categoryModel.setArabic_name(array_Category_list.getJSONObject(i).getString("CategoryNameArabic"));
                                    categoryModel.setImgCategory(array_Category_list.getJSONObject(i).getString("StripImage"));
                                    categoryModel.setSubCategory(array_Category_list.getJSONObject(i).getString("SubCategory"));
                                    listCategories.add(categoryModel);
                                }
                                if (listCategories.size() > 0) {
                                    mAdapter = new CreateRequestStepAdapter2(Create_Request_Step_2.this, listCategories);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                                    recycle_group_list.setLayoutManager(mLayoutManager);
                                    recycle_group_list.setItemAnimator(new DefaultItemAnimator());
                                    recycle_group_list.setAdapter(mAdapter);
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
                params.put("language", preferences.getString("language", ""));
                Log.e("params", Consts.getInstance().category_list + params);
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
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().category_list);///cache remove
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);//manage request
    }

    public class ListAdapter extends ArrayAdapter<String> {

        TextView txt_timer_view;
        ImageView img_delete;
        private ArrayList<String> arrayList = new ArrayList<String>();
        public Context mContext;
        int i = 0;

        public ListAdapter(Context context, ArrayList<String> arrayList) {
            super(context, R.layout.listview_adapter, arrayList);
            this.mContext = context;
            this.arrayList = arrayList;
        }

        public void clear() {
            this.arrayList.clear();
        }

        public int getCount() {
            return arrayList.size();
        }

        public String getItem(int position) {

            return arrayList.get(position);
        }

        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(final int position, View view1, ViewGroup arg2) {
            View view = view1;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_adapter, null);
            img_delete = view.findViewById(R.id.img_delete);
            txt_timer_view = view.findViewById(R.id.txt_timer_view);
            string = arrayList.get(position);
            txt_timer_view.setText(string);

            img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rel_bottom.setVisibility(View.VISIBLE);
                    JSONObject jobj_Category_other = new JSONObject();
                    try {
                        jobj_Category_other.put("CategoryName", "Others");
                        jobj_Category_other.put("CategoryNameArabic", "الآخرين");
                        jobj_Category_other.put("CategoryID", "0");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    remove(arrayList.get(position));
                    Boolean match = false;
                    int match_id = 0;
                    for (int i = 0; i < json_Category.length(); i++) {
                        try {
                            if (json_Category.getJSONObject(i).getString("CategoryID").equals("0")) {
                                match = true;
                                match_id = i;
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    JSONArray jtemp_subcategory = new JSONArray();
                    try {
                        jtemp_subcategory = json_Category.getJSONObject(match_id).getJSONArray("SubCategory");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        jtemp_subcategory.remove(position);
                    }
                    if (jtemp_subcategory.length() > 0) {
                        try {
                            jobj_Category_other.put("SubCategory", jtemp_subcategory);
                            json_Category.put(match_id, jobj_Category_other);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            json_Category.remove(match_id);
                        }
                    }
                    SetBottomCategoryView();
                }
            });
            return view;
        }
    }

    public class CreateRequestStepAdapter2 extends RecyclerView.Adapter<CreateRequestStepAdapter2.MyViewHolder> {

        private List<CategoryModel> listSlider;
        private Context mContext;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public ImageView imgCat;
            public MyTextView english_name, arabic_name;
            private RelativeLayout rel_main;
            RecyclerView recycle_subgroup_list;

            public MyViewHolder(View view) {
                super(view);

                imgCat = view.findViewById(R.id.imgCat);
                arabic_name = view.findViewById(R.id.arabic_name);
                english_name = view.findViewById(R.id.english_name);
                rel_main = view.findViewById(R.id.rel_main);
                recycle_subgroup_list = view.findViewById(R.id.recycle_subgroup_list);
            }
        }

        public CreateRequestStepAdapter2(Context mContext, List<CategoryModel> listSlider) {
            this.mContext = mContext;
            this.listSlider = listSlider;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_create_request_category, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            CategoryModel categoryModel = listSlider.get(position);

            if (categoryModel.getArabic_name().equals("") || categoryModel.getArabic_name().equals("null") || categoryModel.getArabic_name().equals(null) || categoryModel.getArabic_name() == null) {
                holder.arabic_name.setText("-");
            } else {
                holder.arabic_name.setText(categoryModel.getArabic_name());
            }
            if (categoryModel.getEnglish_name().equals("") || categoryModel.getEnglish_name().equals("null") || categoryModel.getEnglish_name().equals(null) || categoryModel.getEnglish_name() == null) {
                holder.english_name.setText("-");
            } else {
                holder.english_name.setText(categoryModel.getEnglish_name());
            }

            Glide.with(mContext).load(categoryModel.getImgCategory()).into(holder.imgCat);

            listsubcat.clear();

            holder.recycle_subgroup_list.setVisibility(View.GONE);

            if (pos == position) {
                holder.rel_main.setBackgroundResource(R.drawable.select_country_bg);
                try {
                    JSONArray array_SubCategory = new JSONArray(categoryModel.getSubCategory());
                    for (int i = 0; i < array_SubCategory.length(); i++) {
                        RequestSubcatModel requestSubcatModel = new RequestSubcatModel();
                        requestSubcatModel.setSubCategoryID(array_SubCategory.getJSONObject(i).getString("SubCategoryID"));
                        requestSubcatModel.setSubcategoryName(array_SubCategory.getJSONObject(i).getString("SubCategoryName"));
                        listsubcat.add(requestSubcatModel);
                    }
                    if (listsubcat.size() > 0) {
                        subCategoryAdapter = new SubCategoryAdapter(mContext, listsubcat);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2, RecyclerView.VERTICAL, false);
                        holder.recycle_subgroup_list.setLayoutManager(mLayoutManager);
                        holder.recycle_subgroup_list.setItemAnimator(new DefaultItemAnimator());
                        holder.recycle_subgroup_list.setAdapter(subCategoryAdapter);
                        holder.recycle_subgroup_list.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (pos == position) {
                holder.recycle_subgroup_list.setVisibility(View.VISIBLE);
            } else {
                holder.recycle_subgroup_list.setVisibility(View.GONE);
            }

            *//*if (categoryModel.getCategoryID().equals("0")) {
                holder.rel_main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rel_forgot.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                rel_forgot.setVisibility(View.GONE);
                holder.rel_main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            jobj_Category = new JSONObject();
                            jobj_Category.put("CategoryID", categoryModel.getCategoryID());
                            jobj_Category.put("CategoryName", categoryModel.getEnglish_name());
                            jobj_Category.put("CategoryNameArabic", categoryModel.getArabic_name());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pos = position;
                        mAdapter.notifyDataSetChanged();
                    }
                });

            }*//*

            holder.rel_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        jobj_Category = new JSONObject();
                        jobj_Category.put("CategoryID", categoryModel.getCategoryID());
                        jobj_Category.put("CategoryName", categoryModel.getEnglish_name());
                        jobj_Category.put("CategoryNameArabic", categoryModel.getArabic_name());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pos = position;
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listSlider.size();
        }
    }

    public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder> {

        private List<RequestSubcatModel> listSlider;
        private Context mContext;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private MyTextView text_subCategory;
            private RelativeLayout rel_cat_main;

            public MyViewHolder(View view) {
                super(view);
                text_subCategory = view.findViewById(R.id.text_subCategory);
                rel_cat_main = view.findViewById(R.id.rel_cat_main);
            }
        }

        public SubCategoryAdapter(Context mContext, List<RequestSubcatModel> listSlider) {
            this.mContext = mContext;
            this.listSlider = listSlider;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_matching_subcategory, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            RequestSubcatModel requestSubcatModel = listSlider.get(position);

            if (requestSubcatModel.getSubcategoryName().equals("") || requestSubcatModel.getSubcategoryName().equals("null") || requestSubcatModel.getSubcategoryName().equals(null) || requestSubcatModel.getSubcategoryName() == null) {
                holder.text_subCategory.setText("-");
            } else {
                holder.text_subCategory.setText(requestSubcatModel.getSubcategoryName());
            }
            if (selectedId.contains(requestSubcatModel.getSubCategoryID())) {
                holder.text_subCategory.setTextColor(getResources().getColor(R.color.colorWhite));
                holder.text_subCategory.setBackgroundResource(R.drawable.category_select_bg);
            } else {
                holder.text_subCategory.setBackgroundResource(R.drawable.category_bg);
                holder.text_subCategory.setTextColor(getResources().getColor(R.color.colorAccent));
            }

            holder.rel_cat_main.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    rel_bottom.setVisibility(View.VISIBLE);
                    if (selectedId.contains(requestSubcatModel.getSubCategoryID())) {
                        selectedId.remove(requestSubcatModel.getSubCategoryID());
                        holder.text_subCategory.setBackgroundResource(R.drawable.category_bg);
                        holder.text_subCategory.setTextColor(getResources().getColor(R.color.colorAccent));
                        if (json_Category.length() > 0) {
                            boolean match = false;
                            int match_id = 0;
                            for (int i = 0; i < json_Category.length(); i++) {
                                try {
                                    if (json_Category.getJSONObject(i).getString("CategoryID").equals(jobj_Category.getString("CategoryID"))) {

                                        match = true;
                                        match_id = i;
                                        break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (match == true) {
                                JSONArray jtemp_subcategory = new JSONArray();
                                try {
                                    jtemp_subcategory = json_Category.getJSONObject(match_id).getJSONArray("SubCategory");
                                    boolean matchsub = false;
                                    int matchsub_id = 0;
                                    for (int i = 0; i < jtemp_subcategory.length(); i++) {
                                        if (jtemp_subcategory.getJSONObject(i).getString("SubCategoryID").equals(requestSubcatModel.getSubCategoryID())) {
                                            matchsub = true;
                                            matchsub_id = i;
                                            break;
                                        }
                                    }
                                    jtemp_subcategory.remove(matchsub_id);
                                    if (jtemp_subcategory.length() > 0) {
                                        jobj_Category.put("SubCategory", jtemp_subcategory);
                                        json_Category.put(match_id, jobj_Category);
                                        Log.e("json_Category","" + json_Category.toString());
                                    } else {
                                        json_Category.remove(match_id);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        selectedId.add(requestSubcatModel.getSubCategoryID());
                        holder.text_subCategory.setTextColor(getResources().getColor(R.color.colorWhite));
                        holder.text_subCategory.setBackgroundResource(R.drawable.category_select_bg);
                        try {
                            JSONObject jobj_subCategory = new JSONObject();
                            json_subcategory = new JSONArray();
                            jobj_subCategory.put("SubCategoryID", requestSubcatModel.getSubCategoryID());
                            jobj_subCategory.put("SubCategoryName", requestSubcatModel.getSubcategoryName());
                            jobj_subCategory.put("Quantity", "1");
                            if (json_Category.length() > 0) {
                                boolean match = false;
                                int match_id = 0;
                                for (int i = 0; i < json_Category.length(); i++) {
                                    if (json_Category.getJSONObject(i).getString("CategoryID").equals(jobj_Category.getString("CategoryID"))) {
                                        match = true;
                                        match_id = i;
                                        break;
                                    }
                                }
                                JSONArray jtemp_subcategory = new JSONArray();
                                if (match == true) {
                                    jtemp_subcategory = json_Category.getJSONObject(match_id).getJSONArray("SubCategory");
                                    jtemp_subcategory.put(jobj_subCategory);
                                    jobj_Category.put("SubCategory", jtemp_subcategory);
                                    json_Category.put(match_id, jobj_Category);
                                    Log.e("json_Category","" + json_Category.toString());
                                } else {
                                    json_subcategory.put(jobj_subCategory);//array
                                    jobj_Category.put("SubCategory", json_subcategory);
                                    json_Category.put(jobj_Category);
                                    Log.e("json_Category","" + json_Category.toString());
                                }
                            } else {
                                json_subcategory.put(jobj_subCategory);//array
                                jobj_Category.put("SubCategory", json_subcategory);
                                json_Category.put(jobj_Category);
                                Log.e("json_Category","" + json_Category.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    SetBottomCategoryView();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listSlider.size();
        }
    }

    public void SetBottomCategoryView() {
        if (json_Category.length() > 0) {
            for (int i = 0; i < json_Category.length(); i++) {
                try {
                    if (language.equals("English")) {
                        // English;
                        if (i == 0) {
                            txt_category.setText(json_Category.getJSONObject(i).getString("CategoryName"));
                        } else {
                            txt_category.setText(txt_category.getText() + "\n" + json_Category.getJSONObject(i).getString("CategoryName"));
                        }
                    } else {
                        // Arabic;
                        if (i == 0) {
                            txt_category.setText(json_Category.getJSONObject(i).getString("CategoryNameArabic"));
                        } else {
                            txt_category.setText(txt_category.getText() + "\n" + json_Category.getJSONObject(i).getString("CategoryNameArabic"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            setMargins(lin_main, 0, 0, 0, 350);
        } else {
            txt_category.setText("");
            rel_bottom.setVisibility(View.GONE);
            setMargins(lin_main, 0, 0, 0, 0);
        }
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    @Override
    protected void onResume() {
        if (Consts.getInstance().isNetworkAvailable(mContext)) {
            category_list();
        } else {
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }
}*/