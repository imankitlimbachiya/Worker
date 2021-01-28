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
import android.widget.RelativeLayout;
import android.widget.SearchView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.worker.app.BaseActivity;
import com.worker.app.R;
import com.worker.app.model.HomeCategoryModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyTextView;

public class ShowAllCategoryActivity extends BaseActivity {

    ArrayList<HomeCategoryModel> listCategories = new ArrayList<>();
    SearchView SearchCategory;
    ImageView plus;
    ProgressBar progress;
    CategoryAdapter mAdapter;
    private RecyclerView recyclerCategories;
    SharedPreferences preferences;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_category);

        mContext = this;

        Init();

        //hide search bar icon visibility from OS 6
        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = (ImageView) SearchCategory.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        SearchCategory.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String category) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String category) {
                category=category.trim();
                mAdapter.filter(category);
                return false;
            }
        });
    }

    public void Init() {
        recyclerCategories = findViewById(R.id.recyclerCategories);
        SearchCategory = findViewById(R.id.SearchCategory);
        progress = findViewById(R.id.progress);
        plus = findViewById(R.id.plus);
        plus.setVisibility(View.GONE);

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
    }

    private void category_list() {
        String tag_string_req = "req";
        listCategories.clear();
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().category_list, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            Log.e("response", "" + Consts.getInstance().category_list + json_main);
                            if (json_main.getString("success").equalsIgnoreCase("True")) {
                                JSONArray array_Category_list = json_main.getJSONArray("Category_list");
                                for (int i = 0; i < array_Category_list.length(); i++) {
                                    HomeCategoryModel categoryModel = new HomeCategoryModel();
                                    categoryModel.setCategoryID(array_Category_list.getJSONObject(i).getString("CategoryID"));
                                    categoryModel.setCategoryName(array_Category_list.getJSONObject(i).getString("CategoryName"));
                                    categoryModel.setCategoryNameArabic(array_Category_list.getJSONObject(i).getString("CategoryNameArabic"));
                                    categoryModel.setHomeScreenImage(array_Category_list.getJSONObject(i).getString("HomeScreenImage"));
                                    categoryModel.setStripImage(array_Category_list.getJSONObject(i).getString("StripImage"));
                                    listCategories.add(categoryModel);
                                }
                                if (listCategories.size() > 0) {
                                    mAdapter = new CategoryAdapter(ShowAllCategoryActivity.this, listCategories);
                                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2, RecyclerView.VERTICAL, false);
                                    recyclerCategories.setLayoutManager(mLayoutManager);
                                    recyclerCategories.setItemAnimator(new DefaultItemAnimator());
                                    recyclerCategories.setAdapter(mAdapter);
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

        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().category_list);///cache remove
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);//manage request
    }

    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

        private List<HomeCategoryModel> listSlider;
        private ArrayList<HomeCategoryModel> searchCategory;
        private Context mContext;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView imgCategory;
            public MyTextView english_name, arabic_name;
            private RelativeLayout rel_main;
            RecyclerView recycle_subgroup_list;

            public MyViewHolder(View view) {
                super(view);
                imgCategory = view.findViewById(R.id.imgCategory);
                arabic_name = view.findViewById(R.id.arabic_name);
                english_name = view.findViewById(R.id.english_name);
                rel_main = view.findViewById(R.id.rel_main);
                recycle_subgroup_list = view.findViewById(R.id.recycle_subgroup_list);
            }
        }

        public CategoryAdapter(Context mContext, ArrayList<HomeCategoryModel> listSlider) {
            this.mContext = mContext;
            this.listSlider = listSlider;
            this.searchCategory = new ArrayList<HomeCategoryModel>();
            this.searchCategory.addAll(listSlider);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_categories, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            HomeCategoryModel categoryModel = listSlider.get(position);

            Glide.with(mContext).load(categoryModel.getHomeScreenImage()).into(holder.imgCategory);

            holder.imgCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_find_worker = new Intent(mContext, SubCategoryActivity.class);
                    intent_find_worker.putExtra("Search_by_value", categoryModel.getCategoryID());
                    intent_find_worker.putExtra("CategoryName", categoryModel.getCategoryName());
                    intent_find_worker.putExtra("CategoryNameArabic", categoryModel.getCategoryNameArabic());
                    intent_find_worker.putExtra("StripImage", categoryModel.getStripImage());
                    intent_find_worker.putExtra("Search_type", 2);
                    mContext.startActivity(intent_find_worker);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listSlider.size();
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            listSlider.clear();
            if (charText.length() == 0) {
                listSlider.addAll(searchCategory);
            } else {
                for (HomeCategoryModel wp : searchCategory) {
                    if (wp.getCategoryName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        listSlider.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        if (Consts.getInstance().isNetworkAvailable(mContext)) {
            category_list();
        } else {
            Consts.getInstance().Act_vity="showallcat";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }
}
