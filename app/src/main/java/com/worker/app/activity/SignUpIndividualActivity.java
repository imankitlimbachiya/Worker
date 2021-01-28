package com.worker.app.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.worker.app.BaseActivity;
import com.worker.app.HomeActivity;
import com.worker.app.R;
import com.worker.app.adapter.SpinnerCityAdapter;
import com.worker.app.model.CityModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyButton;
import com.worker.app.utility.MyEditText;


public class SignUpIndividualActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext;
    MyButton sign_up;
    ImageView plus, down_arrow;
    MyEditText edt_name, edt_email, edt_mobile, edt_password, edt_confirm;
//    MyTextView spinner_City;;
    ProgressBar progress;
    private PopupMenu popup;
    ArrayList<CityModel> City = new ArrayList<>();
    String city, cityId = "-1";
    Spinner spinnerCity;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_individual);

        mContext = this;

        Init();

        if (Consts.getInstance().isNetworkAvailable(mContext)) {
            city_list();
        } else {
            Consts.getInstance().Act_vity="SignupIndividual";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
    }

    public void Init() {
        imgBack=findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);
        plus = findViewById(R.id.plus);
        plus.setVisibility(View.GONE);
        sign_up = findViewById(R.id.sign_up);
        edt_name = findViewById(R.id.edt_name);
        edt_email = findViewById(R.id.edt_email);
        edt_confirm = findViewById(R.id.edt_confirm);
        edt_password = findViewById(R.id.edt_password);
        edt_mobile = findViewById(R.id.edt_mobile);
        progress = findViewById(R.id.progress);
//        spinner_City = findViewById(R.id.spinner_City);
        down_arrow = findViewById(R.id.down_arrow);

        sign_up.setOnClickListener(this);
        down_arrow.setOnClickListener(this);

        spinnerCity=findViewById(R.id.spinnerCity);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                //check if this is last activity
                ActivityManager mngr = (ActivityManager) getSystemService( ACTIVITY_SERVICE );
                List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

                if(taskList.get(0).numActivities == 1) {
                    startActivity(new Intent(SignUpIndividualActivity.this,HomeActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(mContext, RegisterActivity.class));
                    finish();
                }
                break;
            case R.id.sign_up:
                if (edt_name.getText().toString().equals("")) {
                    Toast.makeText(mContext, R.string.alertfirstname, Toast.LENGTH_SHORT).show();
                } else if (edt_email.getText().toString().equals("")) {
                    Toast.makeText(mContext, R.string.alertEmailAddress, Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(edt_email.getText().toString())) {
                    Toast.makeText(mContext, R.string.alertvalidemail, Toast.LENGTH_SHORT).show();
                } else if (edt_mobile.getText().toString().equals("")) {
                    Toast.makeText(mContext, R.string.alertmobile, Toast.LENGTH_SHORT).show();
                } else if (!isValidMobile(edt_mobile.getText().toString())) {
                    Toast.makeText(mContext, R.string.alertvalidmobile, Toast.LENGTH_SHORT).show();
                } else if (cityId.equals("-1")) {
                    Toast.makeText(mContext, R.string.alertcity, Toast.LENGTH_SHORT).show();
                } else if (edt_password.getText().toString().equals("")) {
                    Toast.makeText(mContext, R.string.alertpassword, Toast.LENGTH_SHORT).show();
                } else if (!isValidPassword(edt_password.getText().toString())) {
                    Toast.makeText(mContext, R.string.alertvalidpassword, Toast.LENGTH_SHORT).show();
                } else if (edt_confirm.getText().toString().equals("")) {
                    Toast.makeText(mContext, R.string.alertConfirmpassword, Toast.LENGTH_SHORT).show();
                } else if (!edt_confirm.getText().toString().equals(edt_password.getText().toString())) {
                    Toast.makeText(mContext, R.string.alertConfirmpasswordandpassword, Toast.LENGTH_SHORT).show();
                } else {
                    if (Consts.getInstance().isNetworkAvailable(mContext)) {
                        SignUp_individual();
                    } else {
                        Consts.getInstance().Act_vity="SignupIndividual";
                        Intent intent = new Intent(mContext, ReloadActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            /*case R.id.down_arrow:
                popup = new PopupMenu(mContext, spinner_City);
                popup.getMenuInflater().inflate(R.menu.country_list_menu, popup.getMenu());
                popup.getMenu().removeGroup(0);
                for (int i = 0; i < City.size(); i++) {
                    popup.getMenu().add(City.get(i).getCityName());
                }
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        spinner_City.setText(menuItem.getTitle());
                        for (int i = 0; i < City.size(); i++) {
                            city = City.get(i).getCityName();
                            if (menuItem.getTitle().equals(city)) {
                                cityId = City.get(i).getCityID();
                                Log.e("cityId", "" + cityId);
                            }
                        }
                        return true;
                    }
                });
                break;*/
        }
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
                            JSONObject json_main = new JSONObject(response);
                            Log.e("response", "" + Consts.getInstance().city_list + json_main);
                            if (json_main.getString("success").equalsIgnoreCase("true")) {
                                json_main.getString("CityCount");
                                JSONArray array_City_list = json_main.getJSONArray("City_list");

                                CityModel cityModel1 = new CityModel();
                                cityModel1.setCityID("-1");
                                cityModel1.setCityName(getString(R.string.city_select));
                                cityModel1.setCityNameArabic(getString(R.string.city_select));
                                City.add(cityModel1);

                                for (int i = 0; i < array_City_list.length(); i++) {
                                    CityModel cityModel = new CityModel();
                                    cityModel.setCityID(array_City_list.getJSONObject(i).getString("CityID"));
                                    cityModel.setCityName(array_City_list.getJSONObject(i).getString("CityName"));
                                    cityModel.setCityNameArabic(array_City_list.getJSONObject(i).getString("CityNameArabic"));
                                    City.add(cityModel);
                                }

                                if(City!=null){
                                    if(City.size()>0){
                                        SpinnerCityAdapter customAdapter=new SpinnerCityAdapter(
                                                SignUpIndividualActivity.this, City);
                                        spinnerCity.setAdapter(customAdapter);

                                        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                Log.e("SIN","## i : "+i);
                                                if(i==0)
                                                    cityId="-1";
                                                else {
                                                    if(City!=null){
                                                        if(City.size()>0){
                                                            cityId = City.get(i).getCityID();
                                                            Log.e("SIN","## cityId : "+cityId);
                                                        }
                                                    }
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

    private void SignUp_individual() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().SIGN_IN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            Log.e("response", "" + Consts.getInstance().SIGN_IN_URL + json_main);
                            if (json_main.getString("success").toLowerCase().equalsIgnoreCase("true")) {
                                String str_msg = json_main.getString("message");
                                JSONObject object_User_Detail = json_main.getJSONObject("UserDetail");
                                SharedPreferences preferences = getSharedPreferences("Login_Data", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("UserID", object_User_Detail.getString("UserID"));
                                editor.putString("Name", object_User_Detail.getString("Name"));
                                editor.putString("Email", object_User_Detail.getString("Email"));
                                editor.putString("MobileNo", object_User_Detail.getString("MobileNo"));
                                editor.putString("Type", object_User_Detail.getString("Type"));
                                editor.putString("CityId", object_User_Detail.getString("CityID"));
                                editor.commit();

                                //check if this is last activity
                                ActivityManager mngr = (ActivityManager) getSystemService( ACTIVITY_SERVICE );
                                List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

                                if(taskList.get(0).numActivities == 1) {
                                    startActivity(new Intent(SignUpIndividualActivity.this,HomeActivity.class));
                                    finish();
                                }else
                                    finish();
                            } else {
                                Toast.makeText(mContext, json_main.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            progress.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                });

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
                params.put("Name", edt_name.getText().toString());
                params.put("Email", edt_email.getText().toString());
                params.put("MobileNo", edt_mobile.getText().toString());
                params.put("Password", edt_password.getText().toString());
                params.put("ConformPassword", edt_confirm.getText().toString());
                params.put("CityID", cityId);
                params.put("Type","1");
                Log.e("params", "" + Consts.getInstance().SIGN_IN_URL + params);

                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().SIGN_IN_URL);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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

    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            // if(phone.length() < 6 || phone.length() > 13) {
            if (phone.length()>7) {
                check = true;
            } else {
                check = false;
            }
        } else {
            check = false;
        }
        return check;
    }

    private boolean isValidPassword(String password) {
        boolean check = false;
        if(password.length() < 5) {
            check = false;
        } else {
            check = true;
        }
        return check;
    }
}
