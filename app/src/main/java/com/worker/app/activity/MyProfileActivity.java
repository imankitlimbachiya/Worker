package com.worker.app.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;

import com.android.volley.AuthFailureError;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.worker.app.BaseActivity;
import com.worker.app.R;
import com.worker.app.adapter.SpinnerCityAdapter;
import com.worker.app.model.CityModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyButton;
import com.worker.app.utility.MyEditText;
import com.worker.app.utility.MyTextView;

public class MyProfileActivity extends BaseActivity implements View.OnClickListener {

    Context mContext;
    ImageView plus, down_arrow;
    RelativeLayout Rel_change;
    private MyTextView txtCompName;
    MyEditText edt_mobile, edt_email, edt_name, edt_compName;
    MyButton btn_submit;
    ProgressBar progress;
    MyTextView City, text_userName;
    String cityId, city;
    private PopupMenu popup;
    SharedPreferences preferences, preferences_Login_Data;
    ArrayList<CityModel> cityList = new ArrayList<CityModel>();
    private Spinner spinnerCity;
    private String AcType = "";
    private boolean firstTime = true;
    private LinearLayout linearBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mContext = this;

        Init();

        /*if (Consts.isNetworkAvailable(mContext)) {
            String UserID = preferences_Login_Data.getString("UserID", "");
            if (UserID.equals("")) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            } else {
                myProfile();

                city_list();
            }
        } else {
            Consts.getInstance().Act_vity="Profile";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }*/
    }

    public void Init() {
        linearBox=findViewById(R.id.linearBox);
        txtCompName = findViewById(R.id.txtCompName);
        edt_compName = findViewById(R.id.edt_compName);
        spinnerCity = findViewById(R.id.spinnerCity);
        plus = findViewById(R.id.plus);
        plus.setVisibility(View.GONE);
        Rel_change = findViewById(R.id.Rel_change);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_email = findViewById(R.id.edt_email);
        edt_name = findViewById(R.id.edt_name);
        progress = findViewById(R.id.progress);
        btn_submit = findViewById(R.id.btn_submit);
        City = findViewById(R.id.City);
        down_arrow = findViewById(R.id.down_arrow);
        text_userName = findViewById(R.id.text_userName);

        btn_submit.setOnClickListener(this);
        Rel_change.setOnClickListener(this);
        down_arrow.setOnClickListener(this);

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);

        AcType = preferences_Login_Data.getString("Type", "");
        Log.e("MPA", "## AcType :: " + AcType);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Rel_change:
                setChangePassword();
                break;
            case R.id.down_arrow:
                popup = new PopupMenu(mContext, view, Gravity.CENTER_HORIZONTAL);
                popup.getMenuInflater().inflate(R.menu.country_list_menu, popup.getMenu());
                popup.getMenu().removeGroup(0);
                for (int i = 0; i < cityList.size(); i++) {
                    popup.getMenu().add(cityList.get(i).getCityName());
                }
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        City.setText(menuItem.getTitle());
                        for (int i = 0; i < cityList.size(); i++) {
                            String cityName = cityList.get(i).getCityName();
                            if (menuItem.getTitle().equals(cityName)) {
                                cityId = cityList.get(i).getCityID();
                            }
                        }
                        return true;
                    }
                });
                break;
            case R.id.btn_submit:
                if (Consts.getInstance().isNetworkAvailable(mContext)) {
                    if (edt_name.getText().toString().equals("")) {
                        Toast.makeText(mContext, R.string.alertfirstname, Toast.LENGTH_SHORT).show();
                    } else if (edt_email.getText().toString().equals("")) {
                        Toast.makeText(mContext, R.string.alertEmailAddress, Toast.LENGTH_SHORT).show();
                    }else if (!isValidEmail(edt_email.getText().toString())) {
                        Toast.makeText(mContext, R.string.alertvalidemail, Toast.LENGTH_SHORT).show();
                    } else if (!isValidMobile(edt_mobile.getText().toString())) {
                        Toast.makeText(mContext, R.string.alertvalidmobile, Toast.LENGTH_SHORT).show();
                    } else if (edt_mobile.getText().toString().equals("")) {
                        Toast.makeText(mContext, R.string.alertmobile, Toast.LENGTH_SHORT).show();
                    } else if (AcType.equals("Corporate")) {
                        if (edt_compName.getText().toString().equals("")) {
                            Toast.makeText(mContext, R.string.alertcompName, Toast.LENGTH_SHORT).show();
                        } else {
                            updateProfile();
                        }
                    } else {
                        updateProfile();
                    }
                } else {
                    Consts.getInstance().Act_vity = "Profile";
                    Intent intent = new Intent(mContext, ReloadActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            // if(phone.length() < 6 || phone.length() > 13) {
            if (phone.length() > 7) {
                check = true;
            } else {
                check = false;
            }
        } else {
            check = false;
        }
        return check;
    }

    private boolean isValidEmail(String email) {
        // TODO Auto-generated method stub
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern
                .compile(EMAIL_PATTERN);
        Matcher matcher = pattern
                .matcher(email);
        return matcher.matches();
    }

    @Override
    protected void onResume() {
//        bottom_navigation_view.setSelectedItemId(R.id.bottom5);
        if (Consts.isNetworkAvailable(mContext)) {
            String UserID = preferences_Login_Data.getString("UserID", "");
            if (UserID.equals("")) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            } else {
                myProfile();

                city_list();
            }
        } else {
            Consts.getInstance().Act_vity = "Profile";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }

    private void city_list() {
        String tag_string_req = "req";
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().city_list, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            cityList.clear();
                            //progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            Log.e("response", "" + Consts.getInstance().city_list + json_main);
                            if (json_main.getString("success").equalsIgnoreCase("true")) {
                                JSONArray array_City_list = json_main.getJSONArray("City_list");
                                for (int i = 0; i < array_City_list.length(); i++) {
                                    CityModel cityModel = new CityModel();
                                    cityModel.setCityID(array_City_list.getJSONObject(i).getString("CityID"));
                                    cityModel.setCityName(array_City_list.getJSONObject(i).getString("CityName"));
                                    cityModel.setCityNameArabic(array_City_list.getJSONObject(i).getString("CityNameArabic"));
                                    cityList.add(cityModel);
                                }

                                if (cityList != null) {
                                    if (cityList.size() > 0) {
                                        SpinnerCityAdapter customAdapter = new SpinnerCityAdapter(
                                                MyProfileActivity.this, cityList);
                                        spinnerCity.setAdapter(customAdapter);

                                        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                Log.e("SIN", "## i : " + i);
                                                if (!firstTime) {
                                                    Log.e("SIN", "## second time ");
                                                    if (cityList != null) {
                                                        if (cityList.size() > 0) {
                                                            cityId = cityList.get(i).getCityID();
                                                            Log.e("SIN", "## cityId : " + cityId);
                                                        }
                                                    }
                                                } else {
                                                    firstTime = false;

                                                    //pre-select city
                                                    Log.e("MPA", "## before selectSpinnerItemByValue : ");
                                                    selectSpinnerItemByValue(spinnerCity, cityId);
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                            }
                                        });
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        //progress.setVisibility(View.GONE);
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences preferences = getSharedPreferences("Language", MODE_PRIVATE);
                params.put("language", preferences.getString("language", ""));
                Log.e("params", "" + Consts.getInstance().city_list + params);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().city_list);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private boolean isValidPassword(String password) {
        boolean check = false;
        if (password.length() < 5) {
            check = false;
        } else {
            check = true;
        }
        return check;
    }

    public void setChangePassword() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_password);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MyEditText edt_old_password = (MyEditText) dialog.findViewById(R.id.edt_old_password);
        MyEditText edt_new_password = (MyEditText) dialog.findViewById(R.id.edt_new_password);
        MyEditText edt_confirm_password = (MyEditText) dialog.findViewById(R.id.edt_confirm_password);
        ProgressBar progressbar = (ProgressBar) dialog.findViewById(R.id.progressbar);

        ImageView imgCloseDialog = dialog.findViewById(R.id.imgCloseDialog);
        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btnChangePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_old_password.getText().toString().equals("")) {
                    Toast.makeText(mContext, R.string.alertoldpassword, Toast.LENGTH_SHORT).show();
                } else if (edt_new_password.getText().toString().equals("")) {
                    Toast.makeText(mContext, R.string.alertnewpassword, Toast.LENGTH_SHORT).show();
                } else if (!isValidPassword(edt_new_password.getText().toString())) {
                    Toast.makeText(mContext, R.string.alertvalidpassword, Toast.LENGTH_SHORT).show();
                } else if (edt_confirm_password.getText().toString().equals("")) {
                    Toast.makeText(mContext, R.string.alertConfirmpassword, Toast.LENGTH_SHORT).show();
                } else if (!edt_new_password.getText().toString().equals(edt_confirm_password.getText().toString())) {
                    Toast.makeText(mContext, R.string.alertConfirmpasswordandpassword, Toast.LENGTH_SHORT).show();
                } else {
                    if (Consts.isNetworkAvailable(mContext)) {
                        changePassword();
                    } else {
                        Consts.getInstance().Act_vity = "Profile";
                        Intent intent_about = new Intent(mContext, ReloadActivity.class);
                        startActivity(intent_about);
                    }
                }
            }

            private void changePassword() {
                String tag_string_req = "req";
                progressbar.setVisibility(View.VISIBLE);
                StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().CHANGE_PASSWORD,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response != null) {
                                    try {
                                        progressbar.setVisibility(View.GONE);
                                        JSONObject json_main = new JSONObject(response);
                                        Log.e("response", "" + Consts.getInstance().CHANGE_PASSWORD + json_main);
                                        if (json_main.getString("success").equalsIgnoreCase("True")) {
                                            Toast.makeText(MyProfileActivity.this, json_main.getString("message"), Toast.LENGTH_SHORT).show();
                                            dialog.cancel();
                                        } else {
                                            Toast.makeText(MyProfileActivity.this, json_main.getString("message"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        progressbar.setVisibility(View.GONE);
                                        e.printStackTrace();
                                    }
                                } else {
                                    progressbar.setVisibility(View.GONE);

                                }
                                progressbar.setVisibility(View.GONE);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("ERROR", "error => " + error.toString());
                                progressbar.setVisibility(View.GONE);
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("language", preferences.getString("language", ""));
                        params.put("UserID", preferences_Login_Data.getString("UserID", ""));
                        params.put("OldPassword", edt_old_password.getText().toString());
                        params.put("NewPassword", edt_new_password.getText().toString());
                        params.put("ConfirmPassword", edt_confirm_password.getText().toString());
                        Log.e("params", "" + Consts.getInstance().CHANGE_PASSWORD + params);
                        return params;
                    }
                };
                strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().CHANGE_PASSWORD);
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
            }

        });
        dialog.show();
    }

    private void updateProfile() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().UPDATE_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            try {
                                progress.setVisibility(View.GONE);
                                JSONObject json_main = new JSONObject(response);
                                Log.e("response", "" + Consts.getInstance().UPDATE_PROFILE + json_main);
                                if (json_main.getString("success").equalsIgnoreCase("True")) {
                                    Log.e("response", "** successss");
                                    Toast.makeText(mContext, json_main.getString("massege"), Toast.LENGTH_SHORT).show();
                                    myProfile();
                                } else {
                                    Log.e("response", "** falseeeee");
                                    Toast.makeText(mContext, json_main.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.e("response", "** eee :" + e.getMessage());
                                progress.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                        } else {
                            progress.setVisibility(View.GONE);

                        }
                        progress.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "error => " + error.toString());
                        progress.setVisibility(View.GONE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("language", preferences.getString("language", ""));
                params.put("UserID", preferences_Login_Data.getString("UserID", ""));
                params.put("Name", edt_name.getText().toString());

                //send company name if corporate account
                if (AcType.equals("Corporate")) {
                    params.put("CompanyName", edt_compName.getText().toString());
                }

                params.put("MobileNo", edt_mobile.getText().toString());
                params.put("Email", edt_email.getText().toString());
                params.put("CityID", cityId);
                Log.e("## params", "" + Consts.getInstance().UPDATE_PROFILE + params);

                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().UPDATE_PROFILE);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void myProfile() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().MY_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            try {
                                Log.e("response", "" + response);
                                progress.setVisibility(View.GONE);
                                JSONObject json_main = new JSONObject(response);
                                if (json_main.getString("success").equalsIgnoreCase("True")) {

                                    JSONObject object_UserProfile = json_main.getJSONObject("UserProfile");
                                    cityId = object_UserProfile.getString("CityID");
                                    city = object_UserProfile.getString("CityID");
                                    edt_name.setText(object_UserProfile.getString("Name"));
                                    City.setText(object_UserProfile.getString("CityName"));

                                    String strGuest = getString(R.string.hiii) + " , " + object_UserProfile.getString("Name");
                                    text_userName.setText(strGuest);

                                    edt_email.setText(object_UserProfile.getString("Email"));
                                    edt_mobile.setText(object_UserProfile.getString("MobileNo"));
                                    SharedPreferences preferences = getSharedPreferences("Login_Data", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("Name", object_UserProfile.getString("Name"));

                                    if (object_UserProfile.getString("Type").equals("Corporate")) {
                                        edt_compName.setVisibility(View.VISIBLE);
                                        txtCompName.setVisibility(View.VISIBLE);

                                        edt_compName.setText(object_UserProfile.getString("CompanyName"));
                                    } else {
                                        edt_compName.setVisibility(View.GONE);
                                        txtCompName.setVisibility(View.GONE);
                                    }

                                    linearBox.setVisibility(View.VISIBLE);
                                    editor.commit();
                                } else {
                                    Toast.makeText(MyProfileActivity.this, json_main.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                progress.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                        } else {
                            progress.setVisibility(View.GONE);

                        }
                        progress.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "error => " + error.toString());
                        progress.setVisibility(View.GONE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("language", preferences.getString("language", ""));
                params.put("UserID", preferences_Login_Data.getString("UserID", ""));
                Log.e("params MY_PROFILE", "" + Consts.getInstance().MY_PROFILE + params);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().MY_PROFILE);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void selectSpinnerItemByValue(Spinner spnr, String cityID) {
        if (!cityID.equals("")) {
            //find city name first
            int cityPosition = -1;
            for (int i = 0; i < cityList.size(); i++) {
                if (cityList.get(i).getCityID().equals(cityID)) {
                    cityPosition = i;
                    break;
                }
            }
            if (cityPosition != -1) {
                spinnerCity.setSelection(cityPosition);
            }
        }
    }
}
