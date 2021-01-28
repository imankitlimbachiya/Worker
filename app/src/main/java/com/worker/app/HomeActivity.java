package com.worker.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
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

import com.worker.app.activity.ReloadActivity;
import com.worker.app.activity.ShowAllCategoryActivity;
import com.worker.app.activity.ShowAllCountryActivity;
import com.worker.app.adapter.HomeCategoryAdapter;
import com.worker.app.adapter.HomeCountryAdapter;
import com.worker.app.adapter.SliderAdapter;
import com.worker.app.model.BannerModel;
import com.worker.app.model.CountryModel;
import com.worker.app.model.HomeCategoryModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.LinePagerIndicatorDecoration;
import com.worker.app.utility.MyTextView;

public class HomeActivity extends BaseActivity implements View.OnClickListener, LifecycleObserver {

    private RecyclerView recyclerviewSlider, recyclerviewCategory, recyclerviewCountries;
    ArrayList<BannerModel> listSlider = new ArrayList<BannerModel>();
    ArrayList<HomeCategoryModel> listCategory = new ArrayList<HomeCategoryModel>();
    ArrayList<CountryModel> listCountry = new ArrayList<CountryModel>();
    MyTextView all_country, all_category;
    Context mContext;
    LinearLayout lin_country, lin_category;
    ProgressBar progress;
    private ImageView indicator1, indicator2, indicator3;
    private ImageView indicatorC1,indicatorC2,indicatorC3;
    private GridLayoutManager categoryLayoutManager;
    private LinearLayoutManager countryLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mContext = this;

        init();

        AppController.getInstance().initForceUpgradeManager();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        if (isNetworkAvailable(mContext)) {
            homeApi();
        } else {
            Consts.getInstance().Act_vity = "Home";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }

    }

    private void categoryIndicatorLogic() {
        //category list logic
        if (listCategory.size() == 0) {
            indicator1.setVisibility(View.GONE);
            indicator2.setVisibility(View.GONE);
            indicator3.setVisibility(View.GONE);

            return;
        } else if (listCategory.size() <= 4) {
            indicator2.setVisibility(View.GONE);
            indicator3.setVisibility(View.GONE);
        } else if (listCategory.size() <= 8)
            indicator3.setVisibility(View.GONE);

        recyclerviewCategory.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisiblePosition = categoryLayoutManager.findFirstCompletelyVisibleItemPosition();
                int lastVisiblePosition = categoryLayoutManager.findLastCompletelyVisibleItemPosition();
                Log.e("HA", "## firstVisiblePosition :: " + firstVisiblePosition);
                Log.e("HA", "## lastVisiblePosition :: " + lastVisiblePosition);

                if (listCategory.size() <= 4) {
                } else if (listCategory.size() <= 8) {
                    if (firstVisiblePosition == 0) {
                        indicator1.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_fill));
                        indicator2.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_empty));
                    } else if (lastVisiblePosition == listCategory.size() - 1) {
                        indicator1.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_empty));
                        indicator2.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_fill));
                    }
                } else {
                    //more than 8 items
                    int divideCat = listCategory.size() / 3;
                    Log.e("HA", "## divideCat :: " + divideCat);
                    if (lastVisiblePosition <= divideCat) {
                        indicator1.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_fill));
                        indicator2.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_empty));
                        indicator3.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_empty));
                    } else if (lastVisiblePosition <= (divideCat + divideCat)) {
                        indicator1.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_empty));
                        indicator2.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_fill));
                        indicator3.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_empty));
                    } else {
                        indicator1.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_empty));
                        indicator2.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_empty));
                        indicator3.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_fill));
                    }
                }
            }
        });
    }

    private void countryIndicatorLogic() {
        //category list logic
        if (listCountry.size() == 0) {
            indicatorC1.setVisibility(View.GONE);
            indicatorC2.setVisibility(View.GONE);
            indicatorC3.setVisibility(View.GONE);

            return;
        } else if (listCountry.size() <= 2) {
            indicatorC2.setVisibility(View.GONE);
            indicatorC3.setVisibility(View.GONE);
        } else if (listCountry.size() <= 4)
            indicatorC3.setVisibility(View.GONE);

        recyclerviewCountries.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisiblePosition = countryLayoutManager.findFirstCompletelyVisibleItemPosition();
                int lastVisiblePosition = countryLayoutManager.findLastCompletelyVisibleItemPosition();
                Log.e("HA", "## firstVisiblePosition :: " + firstVisiblePosition);
                Log.e("HA", "## lastVisiblePosition :: " + lastVisiblePosition);

                if (listCountry.size() <= 2) {
                } else if (listCountry.size() <= 4) {
                    if (firstVisiblePosition == 0) {
                        indicatorC1.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_fill));
                        indicatorC2.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_empty));
                    } else if (lastVisiblePosition == listCountry.size() - 1) {
                        indicatorC1.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_empty));
                        indicatorC2.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_fill));
                    }
                } else {
                    //more than 4 items
                    int divideCat = listCountry.size() / 3;
                    Log.e("HA", "## divideCat :: " + divideCat);
                    if (lastVisiblePosition <= divideCat) {
                        indicatorC1.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_fill));
                        indicatorC2.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_empty));
                        indicatorC3.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_empty));
                    } else if (lastVisiblePosition <= (divideCat + divideCat)) {
                        indicatorC1.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_empty));
                        indicatorC2.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_fill));
                        indicatorC3.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_empty));
                    } else {
                        indicatorC1.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_empty));
                        indicatorC2.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_empty));
                        indicatorC3.setImageDrawable(getResources().getDrawable(R.drawable.bg_indicator_fill));
                    }
                }
            }
        });
    }

    public void init() {
        indicator1 = findViewById(R.id.indicator1);
        indicator2 = findViewById(R.id.indicator2);
        indicator3 = findViewById(R.id.indicator3);

        indicatorC1 = findViewById(R.id.indicatorC1);
        indicatorC2 = findViewById(R.id.indicatorC2);
        indicatorC3 = findViewById(R.id.indicatorC3);

        recyclerviewSlider = findViewById(R.id.recyclerviewSlider);
        recyclerviewCategory = findViewById(R.id.recyclerviewCategory);
        recyclerviewCountries = findViewById(R.id.recyclerviewCountries);
        all_country = findViewById(R.id.all_country);
        all_category = findViewById(R.id.all_category);
        lin_category = findViewById(R.id.lin_category);
        lin_country = findViewById(R.id.lin_country);
        progress = findViewById(R.id.progress);

        all_country.setOnClickListener(this);
        all_category.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.all_category:
                startActivity(new Intent(mContext, ShowAllCategoryActivity.class));
                break;
            case R.id.all_country:
                startActivity(new Intent(mContext, ShowAllCountryActivity.class));
                break;
        }
    }

    private void homeApi() {
        String tag_string_req = "req";
        listSlider.clear();
        listCategory.clear();
        listCountry.clear();
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().HOME_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                try {
                    progress.setVisibility(View.GONE);
                    lin_category.setVisibility(View.VISIBLE);
                    lin_country.setVisibility(View.VISIBLE);
                    JSONObject json_main = new JSONObject(response);
                    Log.e("response", "" + Consts.getInstance().HOME_URL + json_main);
                    if (json_main.getString("success").equalsIgnoreCase("true")) {
                        JSONArray array_Banner_list = json_main.getJSONArray("Banner_list");
                        for (int i = 0; i < array_Banner_list.length(); i++) {
                            BannerModel bannerModel = new BannerModel();
                            bannerModel.setStripImage(array_Banner_list.getJSONObject(i).getString("StripImage"));
                            bannerModel.setBannerImage(array_Banner_list.getJSONObject(i).getString("BannerImage"));
                            bannerModel.setBannerID(array_Banner_list.getJSONObject(i).getString("BannerID"));
                            bannerModel.setCriteriaType(array_Banner_list.getJSONObject(i).getString("CriteriaType"));
                            bannerModel.setCategoryID(array_Banner_list.getJSONObject(i).getString("CategoryID"));
                            bannerModel.setWorkerID(array_Banner_list.getJSONObject(i).getString("WorkerID"));
                            bannerModel.setBannerTitle(array_Banner_list.getJSONObject(i).getString("BannerTitle"));
                            bannerModel.setBannerTitleArabic(array_Banner_list.getJSONObject(i).getString("BannerTitleArabic"));
                            listSlider.add(bannerModel);
                        }
                        SliderAdapter mAdapter = new SliderAdapter(mContext, listSlider);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
                        recyclerviewSlider.setLayoutManager(mLayoutManager);
                        recyclerviewSlider.setItemAnimator(new DefaultItemAnimator());
                        recyclerviewSlider.setAdapter(mAdapter);
                        recyclerviewSlider.addItemDecoration(new LinePagerIndicatorDecoration());
                        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
                        pagerSnapHelper.attachToRecyclerView(recyclerviewSlider);

                        JSONArray array_Category_list = json_main.getJSONArray("Category_list");
                        for (int i = 0; i < array_Category_list.length(); i++) {
                            HomeCategoryModel homeCategoryModel = new HomeCategoryModel();
                            homeCategoryModel.setHomeScreenImage(array_Category_list.getJSONObject(i).getString("HomeScreenImage"));
                            homeCategoryModel.setStripImage(array_Category_list.getJSONObject(i).getString("StripImage"));
                            homeCategoryModel.setCategoryID(array_Category_list.getJSONObject(i).getString("CategoryID"));
                            homeCategoryModel.setCategoryName(array_Category_list.getJSONObject(i).getString("CategoryName"));
                            homeCategoryModel.setCategoryNameArabic(array_Category_list.getJSONObject(i).getString("CategoryNameArabic"));
                            homeCategoryModel.setDisplayOrder(array_Category_list.getJSONObject(i).getString("DisplayOrder"));
                            listCategory.add(homeCategoryModel);
                        }
                        HomeCategoryAdapter homeCategoryAdapter = new HomeCategoryAdapter(mContext, listCategory);
                        categoryLayoutManager = new GridLayoutManager(getApplicationContext(), 2, RecyclerView.HORIZONTAL, false);

                        recyclerviewCategory.setLayoutManager(categoryLayoutManager);
                        recyclerviewCategory.setItemAnimator(new DefaultItemAnimator());
                        recyclerviewCategory.setHasFixedSize(true);
                        recyclerviewCategory.setAdapter(homeCategoryAdapter);

                        categoryIndicatorLogic();

                        JSONArray array_Country_list = json_main.getJSONArray("Country_list");
                        for (int i = 0; i < array_Country_list.length(); i++) {
                            CountryModel countryModel = new CountryModel();
                            countryModel.setStripImage(array_Country_list.getJSONObject(i).getString("StripImage"));
                            countryModel.setCountryFlag(array_Country_list.getJSONObject(i).getString("CountryFlag"));
                            countryModel.setCountryName(array_Country_list.getJSONObject(i).getString("CountryName"));
                            countryModel.setCountryNameArabic(array_Country_list.getJSONObject(i).getString("CountryNameArabic"));
                            countryModel.setCountryId(array_Country_list.getJSONObject(i).getString("CountryId"));
                            countryModel.setCountryIsoCode(array_Country_list.getJSONObject(i).getString("CountryIsoCode"));
                            countryModel.setCountryIsdCode(array_Country_list.getJSONObject(i).getString("CountryIsdCode"));
                            listCountry.add(countryModel);
                        }

                        HomeCountryAdapter homeCountryAdapter = new HomeCountryAdapter(HomeActivity.this, listCountry);
                         countryLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
                        recyclerviewCountries.setLayoutManager(countryLayoutManager);
                        recyclerviewCountries.setItemAnimator(new DefaultItemAnimator());
                        recyclerviewCountries.setAdapter(homeCountryAdapter);

                        countryIndicatorLogic();
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
                SharedPreferences preferences = getSharedPreferences("Language", MODE_PRIVATE);
                params.put("language", preferences.getString("language", ""));
                Log.e("params", "" + Consts.getInstance().HOME_URL + params);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().HOME_URL);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
