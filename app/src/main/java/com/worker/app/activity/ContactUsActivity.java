package com.worker.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.worker.app.BaseActivity;
import com.worker.app.HomeActivity;
import com.worker.app.R;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyButton;
import com.worker.app.utility.MyEditText;

public class ContactUsActivity extends BaseActivity implements View.OnClickListener {

    Context mContext;
    MyEditText edt_name, edt_email, edt_phone, edt_question;
    MyButton save;
    ProgressBar progress;
    SharedPreferences preferences, preferences_Login_Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);

        mContext = this;

        Init();
    }

    public void Init() {
        progress = findViewById(R.id.progress);
        edt_name = findViewById(R.id.edt_name);
        edt_phone = findViewById(R.id.edt_phone);
        edt_email = findViewById(R.id.edt_email);
        edt_question = findViewById(R.id.edt_question);
        save = findViewById(R.id.save);

        save.setOnClickListener(this);

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                if (edt_name.getText().toString().equals("")) {
                    Toast.makeText(mContext, R.string.alertfirstname, Toast.LENGTH_SHORT).show();
                } else if (edt_email.getText().toString().equals("")) {
                    Toast.makeText(mContext, R.string.alertEmailAddress, Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(edt_email.getText().toString())) {
                    Toast.makeText(mContext, R.string.alertvalidemail, Toast.LENGTH_SHORT).show();
                } else if (edt_phone.getText().toString().equals("")) {
                    Toast.makeText(mContext, R.string.alertmobile, Toast.LENGTH_SHORT).show();
                } else if (!isValidMobile(edt_phone.getText().toString())) {
                    Toast.makeText(mContext, R.string.alertvalidmobile, Toast.LENGTH_SHORT).show();
                } else if (edt_question.getText().toString().equals("")) {
                    Toast.makeText(mContext, R.string.alertquestions, Toast.LENGTH_SHORT).show();
                } else {
                    if (Consts.getInstance().isNetworkAvailable(mContext)) {
                        addContactUsApi();
                    } else {
                        Toast.makeText(mContext, R.string.networkcheck, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void addContactUsApi() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().ADD_CONTACT_US, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            Log.e("response", "" + Consts.getInstance().ADD_CONTACT_US + json_main);
                            if (json_main.getString("success").equalsIgnoreCase("true")) {
                                Toast.makeText(mContext, json_main.getString("message"), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(mContext, HomeActivity.class);
                                startActivity(intent);
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
                params.put("language", preferences.getString("language",""));
                params.put("UserID", preferences_Login_Data.getString("UserID", ""));
                params.put("Name", edt_name.getText().toString());
                params.put("Email", edt_email.getText().toString());
                params.put("Phone", edt_phone.getText().toString());
                params.put("Question", edt_question.getText().toString());
                Log.e("params", "" + Consts.getInstance().ADD_CONTACT_US + params);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().ADD_CONTACT_US);
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
            if (phone.length()>9) {
                check = true;
            } else {
                check = false;
            }
        } else {
            check = false;
        }
        return check;
    }

    @Override
    protected void onResume() {
        if (Consts.isNetworkAvailable(mContext)) {
            /*String UserID = preferences_Login_Data.getString("UserID", "");
            if (UserID.equals("")) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            }*/
        } else {
            Consts.getInstance().Act_vity="Contactus";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }
}
