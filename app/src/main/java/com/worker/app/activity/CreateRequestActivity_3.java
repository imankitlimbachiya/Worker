package com.worker.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.worker.app.BaseActivity;
import com.worker.app.R;
import com.worker.app.model.AgeRangeModel;
import com.worker.app.model.CatSubCatModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyButton;
import com.worker.app.utility.MyTextView;
import com.worker.app.utility.Utils;
import retrofit2.Call;
import retrofit2.Callback;

public class CreateRequestActivity_3 extends BaseActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private long lastClickTime = 0;
    //    ArrayList<CatSubCatModel.SubCatModel> listSubQty = new ArrayList<>();
    ImageView plus;
    MyButton btn_next;
    RecyclerView recycle_quantity_list;
    //    String[] Staff_Gender_Ar = {"على حد سواء", "الذكر", "إناثا"};
    //    String[] Staff_Gender = {"Both", "Male", "Female"};
    //    String[] Staff_Age_Ar = {"25 إلى 50", "50 إلى 60", "60 إلى 70"};
    //    String[] Staff_Age = {"25 to 50", "50 to 60", "60 to 70"};
    private List<String> listStaffAge = new ArrayList<>();
    private List<String> listGender = new ArrayList<>();
    int pos = -1;
    RelativeLayout rel_main;
    String countryId, RequestCategory;
    ProgressBar progress;
    Spinner spinner_gender, spinner_age;
    String gender, ageRange, subCatId, ageRangeId, genderId = "-1";
    SharedPreferences preferences, preferences_Login_Data;
    JSONArray json_main;
    MyTextView total_count, isAre;
    String language;
    Context mContext;
    private List<CatSubCatModel> listCategories = new ArrayList<>();
    private AgeRangeModel AgeModel;
//    ArrayList<SubCategoryModel> listCurrentSubQty = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_request_step_3);

        mContext = this;

        init();

        intent();

//        setArray();

        if (Consts.getInstance().isNetworkAvailable(mContext)) {
            RequestCategory();

            requestGetStaffAgeRange();
        } else {
            Consts.getInstance().Act_vity = "Createstep3";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
    }

    private void requestGetStaffAgeRange() {
        if (Utils.getInstance().isInternetAvailable(mContext)) {
            progress.setVisibility(View.VISIBLE);

            HashMap<String, String> params = new HashMap<>();
            params.put("language", preferences.getString("language", ""));

            Log.e("params", "" + Consts.getInstance().WORKER_DETAIL_URL + params);
            Call<AgeRangeModel> resultCall = apiService.getStaffAge(params);
            resultCall.enqueue(new Callback<AgeRangeModel>() {
                @Override
                public void onResponse(@NonNull Call<AgeRangeModel> call, @NonNull retrofit2.Response<AgeRangeModel> response) {
                    progress.setVisibility(View.GONE);
                    try {
                        if (response.isSuccessful()) {
                            AgeModel = response.body();

                            if (AgeModel.getSuccess().equals("True")) {
                                if (AgeModel.getAgeRange().size() > 0) {
                                    for (int i = 0; i < AgeModel.getAgeRange().size(); i++) {
                                        listStaffAge.add(AgeModel.getAgeRange().get(i).getAgeRange());
                                        Log.e("CRA", "## add item : " + AgeModel.getAgeRange().get(i).getAgeRange());
                                    }
                                    for (int i = 0; i < AgeModel.getGender().size(); i++) {
                                        if (preferences.getString("language", "").equals("English"))
                                            listGender.add(AgeModel.getGender().get(i).getGender());
                                        else
                                            listGender.add(AgeModel.getGender().get(i).getGenderArabic());
                                    }
                                    setArray();
                                }
                            } else {
                                Utils.getInstance().showSnackBar(btn_next, getResources().getString(R.string.somethingWrong));
                            }
                        }
                    } catch (Exception ee) {
                        ee.printStackTrace();
                        progress.setVisibility(View.GONE);
                        Utils.getInstance().showSnackBar(btn_next, getResources().getString(R.string.somethingWrong));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AgeRangeModel> call, @NonNull Throwable t) {
                    progress.setVisibility(View.GONE);
                    t.printStackTrace();
                    Utils.getInstance().showSnackBar(btn_next, getResources().getString(R.string.somethingWrong));
                    Log.e("##Response-", "onFailure");
                }
            });
        } else {
            progress.setVisibility(View.GONE);

            Consts.getInstance().Act_vity = "Workerprofile";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
    }

    private void init() {
        plus = findViewById(R.id.plus);
        plus.setVisibility(View.GONE);
        btn_next = findViewById(R.id.btn_next);
        recycle_quantity_list = findViewById(R.id.recycle_quantity_list);
        rel_main = findViewById(R.id.rel_main);
        progress = findViewById(R.id.progress);
        spinner_gender = (Spinner) findViewById(R.id.spinner_gender);
        spinner_age = (Spinner) findViewById(R.id.spinner_age);
        total_count = findViewById(R.id.total_count);
        isAre = findViewById(R.id.isAre);

        btn_next.setOnClickListener(this);

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);
        language = preferences.getString("language", "");
    }

    public void intent() {
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {
            countryId = (String) bd.get("countryId");
            RequestCategory = (String) bd.get("RequestCategory");
            Log.e("Create", "** countryId : " + countryId);
            Log.e("Create", "** RequestCategory : " + RequestCategory);
            try {
                json_main = new JSONArray(RequestCategory);

                for (int i = 0; i < json_main.length(); i++) {
                    CatSubCatModel catModel = new Gson().fromJson(json_main.get(i).toString(), CatSubCatModel.class);
                    listCategories.add(catModel);
                }
                Log.e("Create", "** listCategories :: " + listCategories.size());
            } catch (JSONException e) {
                Log.e("Create", "** cat sub modl e : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void setArray() {
        // English;
        Log.e("language", "" + language);
        spinner_gender.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter_gender = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listGender);
        arrayAdapter_gender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender.setAdapter(arrayAdapter_gender);

        spinner_age.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter_age = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listStaffAge);
        arrayAdapter_age.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_age.setAdapter(arrayAdapter_age);
        setMargins(rel_main, 0, 0, 0, 350);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        gender = spinner_gender.getSelectedItem().toString();
        ageRange = spinner_age.getSelectedItem().toString();

        for (int i = 0; i < listStaffAge.size(); i++) {
            Log.e("CRA", "## getAgeRange :: " + AgeModel.getAgeRange().get(i).getAgeRange());
            Log.e("CRA", "## getAgeRange list :: " + listStaffAge.get(i));
            Log.e("CRA", "## getAgeRange ageRange spinn :: " + ageRange);
            if (listStaffAge.get(i).equals(ageRange))
                ageRangeId = AgeModel.getAgeRange().get(i).getAgeID();
        }

        for (int i = 0; i < listGender.size(); i++) {
            Log.e("CRA", "## listGender from model :: " + AgeModel.getGender().get(i).getGender());
            Log.e("CRA", "## listGender list :: " + listGender.get(i));
            Log.e("CRA", "## listGender gender spinn :: " + gender);
            if (listGender.get(i).equals(gender))
                genderId = AgeModel.getGender().get(i).getGenderID();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                // preventing double, using threshold of 3000 ms
                if (SystemClock.elapsedRealtime() - lastClickTime < 6000) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                Log.e("params", "** clicked btn next :: ");
//                Toast.makeText(mContext, "clicked btn next", Toast.LENGTH_SHORT).show();

                SharedPreferences preferences = getSharedPreferences("Login_Data", MODE_PRIVATE);
                String UserID = preferences.getString("UserID", "");
                if (UserID.equals("")) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                } else {
                    request_add();
                }
                break;
        }
    }

    private void request_add() {
        Log.e("response", "** request_add method start");
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().request_add, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                Log.e("response", "** onResponse");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("response", "** run");
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            Log.e("response", "** onResponse ::::" + Consts.getInstance().request_add + json_main);
                            String message = "";
                            if (json_main.getString("success").equalsIgnoreCase("True")) {

                                Bundle bundle = new Bundle();
                                bundle.putString(Consts.getInstance().EVENT_NAME, Consts.getInstance().CREATE_WORKER_REQUEST);
                                bundle.putString(Consts.getInstance().FIREBASE_USERID, preferences_Login_Data.getString("UserID", ""));
                                bundle.putString(Consts.getInstance().CONTRY_ID, countryId);
                                bundle.putString(Consts.getInstance().AGE_RANGE_ID, ageRangeId);
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "text");
                                mFirebaseAnylytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                                message = json_main.getString("message");
                                Toast.makeText(CreateRequestActivity_3.this, message, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(CreateRequestActivity_3.this, ThankyouActivity.class);
                                startActivity(intent);
                            } else {
                                message = json_main.getString("message");
                                Toast.makeText(CreateRequestActivity_3.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.e("response", "** runOnUiThread e : " + e.getMessage());
                            progress.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "** " + error.toString());
                progress.setVisibility(View.GONE);
                //Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {   //Post
                Log.e("response", "** getParams calllledddddddddddddddddd");
                //convert list to json and send it to api

                for (int i = 0; i < listCategories.size(); i++) {
                    Log.e("response", "*** cat name : " + listCategories.get(i).getCategoryName());
                    if (listCategories.get(i).getCategoryName().equals("Others")) {
                        for (int j = 0; j < listCategories.get(i).getSubCategory().size(); j++) {
                            listCategories.get(i).getSubCategory().get(j).setSubCategoryID("0");
                        }
                    }
                }

                Gson gson = new Gson();
                String listString = gson.toJson(
                        listCategories,
                        new TypeToken<ArrayList<CatSubCatModel>>() {
                        }.getType());

                JSONArray jsArray = null;
                try {
                    jsArray = new JSONArray(listString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("params", "** jsArray :: " + jsArray.toString());

                Map<String, String> params = new HashMap<String, String>();///params put, not used in get method
                params.put("language", preferences.getString("language", ""));
                params.put("UserID", preferences_Login_Data.getString("UserID", ""));
                params.put("CountryID", countryId);
                params.put("RequestCategory", jsArray.toString());
                params.put("Gender", genderId);
                params.put("AgeRenge", ageRangeId);
                Log.e("params", Consts.getInstance().request_add + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().request_add);///cache remove
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);//manage request
    }

    private void RequestCategory() {
        try {
            if (listCategories.size() > 0) {
                QuantityAdapter mAdapter = new QuantityAdapter(CreateRequestActivity_3.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                recycle_quantity_list.setLayoutManager(mLayoutManager);
                recycle_quantity_list.setItemAnimator(new DefaultItemAnimator());
                recycle_quantity_list.setAdapter(mAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class QuantityAdapter extends RecyclerView.Adapter<QuantityAdapter.MyViewHolder> {

        //        private List<CategoryModel> listSlider;
        private Context mContext;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            MyTextView text_category;
            RecyclerView recycle_sub_quantity_list;
            RelativeLayout rel_main;

            public MyViewHolder(View view) {
                super(view);
                text_category = view.findViewById(R.id.text_category);
                recycle_sub_quantity_list = view.findViewById(R.id.recycle_sub_quantity_list);
                rel_main = view.findViewById(R.id.rel_main);
            }
        }

        public QuantityAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_row_group, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            CatSubCatModel categoryModel = listCategories.get(position);

            if (categoryModel.getCategoryName().equals("") || categoryModel.getCategoryName().equals("null") || categoryModel.getCategoryName() == null) {
                holder.text_category.setText("-");
            } else {
                if (preferences.getString("language", "").equals("English"))
                    holder.text_category.setText(categoryModel.getCategoryName());
                else
                    holder.text_category.setText(categoryModel.getCategoryNameArabic());
            }
            pos = position;

//            listSubQty.clear();
//            listSubQty.addAll(listCategories.get(position).getSubCategory());
            if (listCategories.get(pos).getSubCategory().size() > 0) {
                SubQuantityAdapter mAdapter = new SubQuantityAdapter(mContext, listCategories.get(pos).getSubCategory());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
                holder.recycle_sub_quantity_list.setLayoutManager(mLayoutManager);
                holder.recycle_sub_quantity_list.setItemAnimator(new DefaultItemAnimator());
                holder.recycle_sub_quantity_list.setAdapter(mAdapter);
            }
        }

        @Override
        public int getItemCount() {
            return listCategories.size();
        }
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public class SubQuantityAdapter extends RecyclerView.Adapter<SubQuantityAdapter.MyViewHolder> implements AdapterView.OnItemSelectedListener {

        private List<CatSubCatModel.SubCatModel> listSlider;
        private Context mContext;
        String[] quantity = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
                "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
                "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45",
                "46", "47", "48", "49", "50"};
        String qty, subcatId;
        String subcat;
        int sum = 0;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private MyTextView txt_subcategory;
            Spinner spinner_qty;

            public MyViewHolder(View view) {
                super(view);
                txt_subcategory = view.findViewById(R.id.txt_subcategory);
                spinner_qty = view.findViewById(R.id.spinner_qty);
            }
        }

        public SubQuantityAdapter(Context mContext, List<CatSubCatModel.SubCatModel> listSlider) {
            this.mContext = mContext;
            this.listSlider = listSlider;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_row_child, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            CatSubCatModel.SubCatModel model = listSlider.get(position);

            Log.e("sum", "** model.getSubCateName() :: " + model.getSubCategoryName());
            if (model.getSubCategoryName().equals("") || model.getSubCategoryName().equals("null") || model.getSubCategoryName() == null) {
                holder.txt_subcategory.setText("-");
            } else {
                holder.txt_subcategory.setText(model.getSubCategoryName());
            }

            subcatId = model.getSubCategoryID();
            ArrayAdapter arrayAdapter_quantity = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, quantity);
            arrayAdapter_quantity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinner_qty.setAdapter(arrayAdapter_quantity);
            holder.spinner_qty.setPrompt(subcatId);

            holder.spinner_qty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                    qty = adapterView.getItemAtPosition(pos).toString();
                    Log.e("sum", "** qty :: " + qty);
                    Log.e("sum", "** spinner_qty.getPrompt() " + holder.spinner_qty.getPrompt());
                    sum = 0;
                    try {
                        Log.e("sum", "** json_main :: " + json_main.length());
                        Log.e("sum", "** json_main toString :: " + json_main.toString());
                        for (int i = 0; i < listCategories.size(); i++) {
                            List<CatSubCatModel.SubCatModel> listSubs = listCategories.get(i).getSubCategory();
                            for (int j = 0; j < listSubs.size(); j++) {
                                if (listSubs.get(j).getSubCategoryID().contentEquals(holder.spinner_qty.getPrompt())) {
                                    //update main list with new count
                                    listCategories.get(i).getSubCategory().get(j).setQuantity(qty);

                                    //addition of all sub qty values
                                    sum = sum + Integer.parseInt(qty);
                                } else {
                                    sum = sum + Integer.parseInt(listSubs.get(j).getQuantity());
                                }
                            }
                        }
                        Log.e("sum", "** sum overall :: " + sum);
                        if (sum > 1) {
                            isAre.setText(getResources().getString(R.string.total_workers_required_are));
                        } else {
                            isAre.setText(getResources().getString(R.string.total_workers_required_is));
                        }
                        total_count.setText(String.valueOf(sum));

                        /*for (int i = 0; i < json_main.length(); i++) {
                            subcat = json_main.getJSONObject(i).getString("SubCategory");
                            JSONArray subArray = json_main.getJSONObject(i).getJSONArray("SubCategory");
                            Log.e("sum", "** subArray :: " + subArray.length());
                            JSONObject jobj_subCategory = new JSONObject();

                            for (int j = 0; j < subArray.length(); j++) {
                                Log.e("sum", "** SubCategoryName" + subArray.getJSONObject(j).getString("SubCategoryName")
                                        + " :::: " + subArray.getJSONObject(j).getString("Quantity"));

                                Log.e("sum", "** spin :: " + holder.spinner_qty.getPrompt() +
                                        " ::: json sub cat id :: " + subArray.getJSONObject(j).getString("SubCategoryID"));
                                if (subArray.getJSONObject(j).getString("SubCategoryID").contentEquals(holder.spinner_qty.getPrompt())) {
                                    jobj_subCategory = json_main.getJSONObject(i).getJSONArray("SubCategory").getJSONObject(j);
                                    jobj_subCategory.put("Quantity", qty);

                                    Log.e("sum", "** if qty " + qty + " updated for sub cat name: " + subArray.getJSONObject(j).getString("SubCategoryName"));
                                    json_main.getJSONObject(i).getJSONArray("SubCategory").put(j, jobj_subCategory);
                                    sum = sum + Integer.parseInt(qty);
                                } else {
                                    Log.e("sum", "** elseeeee");
                                    sum = sum + Integer.parseInt(subArray.getJSONObject(j).getString("Quantity"));
                                }
                            }
                        }
                        Log.e("sum", "** sum " + sum);
                        if (sum > 1) {
                            isAre.setText(getResources().getString(R.string.total_workers_required_are));
                        } else {
                            isAre.setText(getResources().getString(R.string.total_workers_required_is));
                        }
                        total_count.setText(String.valueOf(sum));*/
                    } catch (Exception ee) {
                        Log.e("sum", "** sub ee ::  " + ee.getMessage());
                        ee.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            /*try {
                int catMatch = 0;
                int subcatMatch = 0;
                for (int i = 0; i < json_main.length(); i++) {
                    json_main.getJSONObject(i).getString("CategoryID");
                    subcat = json_main.getJSONObject(i).getString("SubCategory");
                    JSONArray array_subcat = new JSONArray(subcat);
                    for (int j = 0; j < array_subcat.length(); j++) {
                        if (array_subcat.getJSONObject(j).getString("SubCategoryID").equals(spinner_qty.getPrompt())) {
                            catMatch = i;
                            subcatMatch = j;
                            break;
                        }
                    }
                }
                JSONObject jobj_subCategory = new JSONObject();
                jobj_subCategory = json_main.getJSONObject(catMatch).getJSONArray("SubCategory").getJSONObject(subcatMatch);
                jobj_subCategory.put("Quantity", qty);
                json_main.getJSONObject(catMatch).getJSONArray("SubCategory").put(subcatMatch, jobj_subCategory);
                sum = 0;
                for (int i = 0; i < json_main.length(); i++) {
                    subcat = json_main.getJSONObject(i).getString("SubCategory");
                    JSONArray array_subcat = new JSONArray(subcat);
                    for (int j = 0; j < array_subcat.length(); j++) {
                        sum = sum + Integer.parseInt(array_subcat.getJSONObject(j).getString("Quantity"));
                    }
                }
                Log.e("sum","" + sum);
                if (sum > 1) {
                    isAre.setText(getResources().getString(R.string.total_workers_required_are));
                } else {
                    isAre.setText(getResources().getString(R.string.total_workers_required_is));
                }
                total_count.setText("" + sum);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

        @Override
        public int getItemCount() {
            return listSlider.size();
        }
    }

    /*@Override
    protected void onResume() {
        if (Consts.getInstance().isNetworkAvailable(mContext)) {
            RequestCategory();
        } else {
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }*/
}
