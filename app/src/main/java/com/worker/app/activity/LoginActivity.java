package com.worker.app.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.worker.app.BaseActivity;
import com.worker.app.HomeActivity;
import com.worker.app.R;
import com.worker.app.model.UpdateLanguage;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyButton;
import com.worker.app.utility.MyEditText;
import com.worker.app.utility.MyTextView;
import com.worker.app.utility.Utils;
import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext;
    private ImageView plus;
    private RelativeLayout Rel_signup, Rel_about;
    MyButton sign_in;
    MyEditText edt_email_mobile, edt_password;
    ProgressBar progress;
    MyTextView txt_forgot;
    ImageView imagepassword;
    int passwordNotVisible = 1;
    SharedPreferences preferences, langPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;

        preferences = getSharedPreferences("Login_Data", MODE_PRIVATE);
        langPreferences = getSharedPreferences("Language", MODE_PRIVATE);
        init();
    }

    public void init() {

        plus = findViewById(R.id.plus);
        plus.setVisibility(View.GONE);
        progress = findViewById(R.id.progress);
        txt_forgot = findViewById(R.id.txt_forgot);
        sign_in = findViewById(R.id.sign_in);
        Rel_signup = findViewById(R.id.Rel_signup);
        Rel_about = findViewById(R.id.Rel_about);
        edt_password = findViewById(R.id.edt_password);
        edt_email_mobile = findViewById(R.id.edt_email_mobile);
        imagepassword = findViewById(R.id.imagepassword);

        sign_in.setOnClickListener(this);
        Rel_signup.setOnClickListener(this);
        Rel_about.setOnClickListener(this);
        txt_forgot.setOnClickListener(this);
        imagepassword.setOnClickListener(this);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in:
                if (edt_email_mobile.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, R.string.alertEmailAddressandmobile, Toast.LENGTH_SHORT).show();
                } else if (edt_password.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, R.string.alertpassword, Toast.LENGTH_SHORT).show();
                } else {
                    if (Consts.getInstance().isNetworkAvailable(LoginActivity.this)) {
                        Login();
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.networkcheck, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.Rel_signup:
                Intent intent_signup = new Intent(mContext, RegisterActivity.class);
                startActivity(intent_signup);
                finish();
                break;
            case R.id.Rel_about:
                Intent intent_about = new Intent(mContext, AboutActivity.class);
                startActivity(intent_about);
                break;
            case R.id.imagepassword:
                if (passwordNotVisible == 1) {
                    edt_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    imagepassword.setImageResource(R.drawable.passimg);
                    passwordNotVisible = 0;

                } else {
                    edt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imagepassword.setImageResource(R.drawable.passhide);
                    passwordNotVisible = 1;

                }
                break;
            case R.id.txt_forgot:
                setForgotPassword();
                break;
        }

    }

    private void Login() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            Log.e("response", "" + Consts.getInstance().LOGIN_URL + json_main);
                            if (json_main.getString("success").equalsIgnoreCase("true")) {
                                String str_msg = json_main.getString("message");
                                JSONObject object_User_Detail = json_main.getJSONObject("UserDetail");

                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("UserID", object_User_Detail.getString("UserID"));
                                editor.putString("Name", object_User_Detail.getString("Name"));
                                editor.putString("Email", object_User_Detail.getString("Email"));
                                editor.putString("MobileNo", object_User_Detail.getString("MobileNo"));
                                editor.putString("Type", object_User_Detail.getString("Type"));
                                editor.putString("CityID", object_User_Detail.getString("CityID"));
                                editor.commit();

                                changeLanguage(langPreferences.getString("language", ""));
                            } else {
                                Toast.makeText(LoginActivity.this, json_main.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("UserName", edt_email_mobile.getText().toString());
                params.put("Password", edt_password.getText().toString());
                Log.e("PARAMS", "" + Consts.getInstance().LOGIN_URL + params);
                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().LOGIN_URL);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void setForgotPassword() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.forgot_password);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MyEditText edt_email = (MyEditText) dialog.findViewById(R.id.edt_email);
        ProgressBar progressbar = (ProgressBar) dialog.findViewById(R.id.progressbar);
        ImageView imgCloseDialog = dialog.findViewById(R.id.imgCloseDialog);
        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_email.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, R.string.alertEmailAddressandmobile, Toast.LENGTH_SHORT).show();
                } else if (!isPhoneValid(edt_email)) {
                    Log.e("feedback", "** isPhoneValid false");
                    if (!isValidEmailId(edt_email)) {
                        Log.e("feedback", "** isValidEmailId false");
                        Toast.makeText(LoginActivity.this, R.string.alertValid_Email_Mobile, Toast.LENGTH_SHORT).show();
                    } else {
                        callApiForgot();
                    }
                } else {
                    callApiForgot();
                }
            }

            private void callApiForgot() {
                if (Consts.isNetworkAvailable(mContext)) {
                    forgotPassword();
                } else {
                    Consts.getInstance().Act_vity = "Login";
                    Intent intent_about = new Intent(mContext, ReloadActivity.class);
                    startActivity(intent_about);
                }
            }

            private void forgotPassword() {
                String tag_string_req = "req";
                progressbar.setVisibility(View.VISIBLE);
                final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().FORGOT_PASSWORD, new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    progressbar.setVisibility(View.GONE);
                                    JSONObject json_main = new JSONObject(response);
                                    Log.e("response", "" + Consts.getInstance().FORGOT_PASSWORD + json_main);
                                    if (json_main.getString("success").equalsIgnoreCase("true")) {
                                        String str_msg = json_main.getString("message");
                                        Toast.makeText(mContext, str_msg, Toast.LENGTH_LONG).show();
                                        edt_email.setText("");
                                        dialog.cancel();
                                    } else {
                                        Toast.makeText(mContext, json_main.getString("message"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    progressbar.setVisibility(View.GONE);
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                },
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                progressbar.setVisibility(View.GONE);
                            }
                        }) {
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        SharedPreferences preferences = getSharedPreferences("Language", MODE_PRIVATE);
                        params.put("language", preferences.getString("language", ""));
                        params.put("Email", edt_email.getText().toString());
                        Log.e("params", "" + Consts.getInstance().FORGOT_PASSWORD + params);
                        return params;
                    }
                };
                strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().FORGOT_PASSWORD);
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
            }
        });
        dialog.show();
    }

    private void changeLanguage(String selectedLang) {
        Log.e("params", "## changeLanguage :: " + selectedLang);

        if (Utils.getInstance().isInternetAvailable(mContext)) {
            progress.setVisibility(View.VISIBLE);

            HashMap<String, String> params = new HashMap<>();
            params.put("language", selectedLang);
            params.put("UserID", preferences.getString("UserID", ""));

            Log.e("params", "## update_language : " + params);
            Call<UpdateLanguage> resultCall = apiService.updateLanguage(params);
            resultCall.enqueue(new Callback<UpdateLanguage>() {
                @Override
                public void onResponse(@NonNull Call<UpdateLanguage> call, @NonNull retrofit2.Response<UpdateLanguage> response) {
                    progress.setVisibility(View.GONE);
                    try {
                        if (response.isSuccessful()) {
                            UpdateLanguage updateLanguage = response.body();
                            if (updateLanguage.getSuccess().equals("True")) {
                                //check if this is last activity
                                ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                                List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

                                if (taskList.get(0).numActivities == 1) {
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    finish();
                                } else
                                    finish();
                            } else {
                                //check if this is last activity
                                ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                                List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

                                if (taskList.get(0).numActivities == 1) {
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    finish();
                                } else
                                    finish();
                            }
                        }
                    } catch (Exception ee) {
                        ee.printStackTrace();
                        progress.setVisibility(View.GONE);
//                        Toast.makeText(mContext, "" + getResources().getString(R.string.somethingWrong), Toast.LENGTH_SHORT).show();
                        //check if this is last activity
                        ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

                        if (taskList.get(0).numActivities == 1) {
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        } else
                            finish();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UpdateLanguage> call, @NonNull Throwable t) {
                    progress.setVisibility(View.GONE);
                    t.printStackTrace();
//                    Toast.makeText(mContext, "" + getResources().getString(R.string.somethingWrong), Toast.LENGTH_SHORT).show();
                    Log.e("##Response-", "onFailure");
                    //check if this is last activity
                    ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

                    if (taskList.get(0).numActivities == 1) {
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else
                        finish();
                }
            });
        } else {
            progress.setVisibility(View.GONE);

            Consts.getInstance().Act_vity = "Workerprofile";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
    }

    private boolean isValidEmailId(MyEditText edtPhoneOrEmail) {
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(edtPhoneOrEmail.getText().toString().trim()).matches();
    }

    boolean isPhoneValid(MyEditText edtPhoneOrEmail) {
        boolean digitsOnly = TextUtils.isDigitsOnly(edtPhoneOrEmail.getText().toString().trim());
        if (digitsOnly) {
            return edtPhoneOrEmail.getText().toString().length() > 7 && edtPhoneOrEmail.getText().toString().length() < 14;
        } else
            return false;
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
        if (Consts.getInstance().isNetworkAvailable(mContext)) {
            SharedPreferences preferences = getSharedPreferences("Login_Data", MODE_PRIVATE);
            String UserID = preferences.getString("UserID", "");
            if (UserID.equals("")) {
            } else {
                finish();
            }
        } else {
            Consts.getInstance().Act_vity = "Login";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }
}
