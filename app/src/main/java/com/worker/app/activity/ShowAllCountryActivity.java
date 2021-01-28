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
import com.worker.app.model.CountryModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyTextView;

public class ShowAllCountryActivity extends BaseActivity {

    ArrayList<CountryModel> listCountry = new ArrayList<CountryModel>();
    SearchView SearchCountry;
    ImageView plus;
    ProgressBar progress;
    CountryAdapter mAdapter;
    private RecyclerView recyclerCountries;
    SharedPreferences preferences;
    int pos = -1;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_country);

        mContext = this;

        Init();

        //hide search bar icon visibility from OS 6
        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = (ImageView) SearchCountry.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        SearchCountry.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String country) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String country) {
                country=country.trim();
                mAdapter.filter(country);
                return false;
            }
        });
    }

    public void Init() {
        recyclerCountries = findViewById(R.id.recyclerCountries);
        SearchCountry = findViewById(R.id.SearchCountry);
        progress = findViewById(R.id.progress);
        plus = findViewById(R.id.plus);
        plus.setVisibility(View.GONE);

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
    }

    private void country_list() {
        String tag_string_req = "req";
        listCountry.clear();
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().country_list, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            Log.e("json_main", "" + Consts.getInstance().country_list + json_main);
                            if (json_main.getString("success").equalsIgnoreCase("True")) {
                                JSONArray array_Country_list = json_main.getJSONArray("Country_list");
                                for (int i = 0; i < array_Country_list.length(); i++) {
                                    CountryModel countryStepModel = new CountryModel();
                                    countryStepModel.setCountryId(array_Country_list.getJSONObject(i).getString("CountryId"));
                                    countryStepModel.setCountryName(array_Country_list.getJSONObject(i).getString("CountryName"));
                                    countryStepModel.setCountryNameArabic(array_Country_list.getJSONObject(i).getString("CountryNameArabic"));
                                    countryStepModel.setCountryIsoCode(array_Country_list.getJSONObject(i).getString("CountryIsoCode"));
                                    countryStepModel.setCountryIsdCode(array_Country_list.getJSONObject(i).getString("CountryIsdCode"));
                                    countryStepModel.setCountryFlag(array_Country_list.getJSONObject(i).getString("CountryFlag"));
                                    countryStepModel.setStripImage(array_Country_list.getJSONObject(i).getString("StripImage"));
                                    listCountry.add(countryStepModel);
                                }
                                if (listCountry.size() > 0) {
                                    mAdapter = new CountryAdapter(ShowAllCountryActivity.this, listCountry);
                                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2, RecyclerView.VERTICAL, false);
                                    recyclerCountries.setLayoutManager(mLayoutManager);
                                    recyclerCountries.setItemAnimator(new DefaultItemAnimator());
                                    recyclerCountries.setAdapter(mAdapter);
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
            }
        }) {
            protected Map<String, String> getParams() {   //Post
                Map<String, String> params = new HashMap<String, String>();///params put, not used in get method
                params.put("language", preferences.getString("language", ""));
                Log.e("params", Consts.getInstance().country_list + params);
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
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().country_list);///cache remove
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);//manage request
    }

    public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.MyViewHolder> {

        private List<CountryModel> listSlider;
        private ArrayList<CountryModel> searchCountry;
        private Context mContext;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView imgCountry;
            private MyTextView txtArabic, txtEnglish;
            private RelativeLayout rel_main;

            public MyViewHolder(View view) {
                super(view);
                imgCountry = view.findViewById(R.id.imgCountry);
                txtArabic = view.findViewById(R.id.txtArabic);
                txtEnglish = view.findViewById(R.id.txtEnglish);
                rel_main = view.findViewById(R.id.rel_main);
            }
        }

        public CountryAdapter(Context mContext, List<CountryModel> listSlider) {
            this.mContext = mContext;
            this.listSlider = listSlider;
            this.searchCountry = new ArrayList<CountryModel>();
            this.searchCountry.addAll(listSlider);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_create_request_country, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            CountryModel countryStepModel = listSlider.get(position);

            if (countryStepModel.getCountryNameArabic().equals("") || countryStepModel.getCountryNameArabic().equals("null") || countryStepModel.getCountryNameArabic().equals(null) || countryStepModel.getCountryNameArabic() == null) {
                holder.txtArabic.setText("-");
            } else {
                holder.txtArabic.setText(countryStepModel.getCountryNameArabic());
            }
            if (countryStepModel.getCountryName().equals("") || countryStepModel.getCountryName().equals("null") || countryStepModel.getCountryName().equals(null) || countryStepModel.getCountryName() == null) {
                holder.txtArabic.setText("-");
            } else {
                holder.txtEnglish.setText(countryStepModel.getCountryName());
            }


            Glide.with(mContext).load(countryStepModel.getCountryFlag()).into(holder.imgCountry);

            if (pos == position) {

            } else {
                holder.rel_main.setBackgroundResource(R.drawable.bg_grey_border_rect);
            }

            holder.rel_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pos = position;
                 //  countryId = countryStepModel.getCountryId();
                    //  Log.e("countryId",""+countryStepModel.getCountryId());

                    Intent intent_find_worker = new Intent(mContext, SubCategoryActivity.class);
                    intent_find_worker.putExtra("Search_by_value", countryStepModel.getCountryId());
                    intent_find_worker.putExtra("CountryName", countryStepModel.getCountryName());
                    intent_find_worker.putExtra("CountryNameArabic", countryStepModel.getCountryNameArabic());
                    intent_find_worker.putExtra("StripImage", countryStepModel.getStripImage());
                    intent_find_worker.putExtra("Search_type", 3);
                    mContext.startActivity(intent_find_worker);
                    notifyDataSetChanged();
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
                listSlider.addAll(searchCountry);
            } else {
                for (CountryModel wp : searchCountry) {
                    if (wp.getCountryName().toLowerCase(Locale.getDefault()).contains(charText)) {
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
            country_list();
        } else {
            Consts.getInstance().Act_vity="Showallcountry";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }
}
