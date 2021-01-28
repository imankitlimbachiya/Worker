package com.worker.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;

import com.worker.app.BaseActivity;
import com.worker.app.HomeActivity;
import com.worker.app.R;
import com.worker.app.model.UpdateLanguage;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.Pref.PreferenceKeys;
import com.worker.app.utility.Utils;
import retrofit2.Call;
import retrofit2.Callback;

public class LanguageActivity extends BaseActivity implements View.OnClickListener {

    ImageView english_img, arabic_img, plus;
    RelativeLayout rel_topbar, Rel_promises, Rel_terms, Rel_Policy;
    NavigationView navigationView;
    private LinearLayout linearLogo;
    Context mContext;
    BottomNavigationView bottom_navigation_view;
    ProgressBar progress;
    SharedPreferences preferences, preferences_Login_Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        mContext = this;

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);

        init();

        if (getIntent() != null) {
            if (getIntent().hasExtra("fromWhere")) {
                if (getIntent().getStringExtra("fromWhere").equals("Intro")) {
                    linearLogo.setVisibility(View.VISIBLE);
                } else {
                    linearLogo.setVisibility(View.GONE);
                }
            }
        }
    }

    private void init() {
        progress = findViewById(R.id.progress);
        linearLogo = findViewById(R.id.linearLogo);
        arabic_img = (ImageView) findViewById(R.id.arabic_img);
        plus = (ImageView) findViewById(R.id.plus);
        plus.setVisibility(View.GONE);
        english_img = (ImageView) findViewById(R.id.english_img);
        rel_topbar = (RelativeLayout) findViewById(R.id.rel_topbar);
        Rel_Policy = (RelativeLayout) findViewById(R.id.Rel_Policy);
        Rel_terms = (RelativeLayout) findViewById(R.id.Rel_terms);
        Rel_promises = (RelativeLayout) findViewById(R.id.Rel_promises);
        navigationView = findViewById(R.id.nav_view_slider);
        bottom_navigation_view = findViewById(R.id.bottom_navigation_view);
        navigationView.setVisibility(View.GONE);

        arabic_img.setOnClickListener(this);
        english_img.setOnClickListener(this);
        Rel_Policy.setOnClickListener(this);
        Rel_terms.setOnClickListener(this);
        Rel_promises.setOnClickListener(this);

        Log.e("llllllllllllllang", "## lang Consts.getInstance().chk_lang. :: " + preferences.getString("chk_lang", "1"));
        if (preferences.getString("chk_lang", "1").equals("1")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("chk_lang", "0");
            editor.apply();

            rel_topbar.setVisibility(View.GONE);
            navigationView.setVisibility(View.GONE);
            bottom_navigation_view.setVisibility(View.GONE);

            String languageDef = Resources.getSystem().getConfiguration().locale.getLanguage();
            Log.e("llllllllllllllang", "## lang getDefault :: " + languageDef);


            if (languageDef.startsWith("ar"))
                editor.putString("language", "Arabic");
            else if (languageDef.startsWith("en"))
                editor.putString("language", "English");
            else
                editor.putString("language", "Arabic");

            editor.commit();
            editor.apply();
        } else{
            rel_topbar.setVisibility(View.VISIBLE);
            navigationView.setVisibility(View.VISIBLE);
            bottom_navigation_view.setVisibility(View.VISIBLE);
        }
    }

    private void setNewLocale(String language, boolean restartProcess) {
        AppController.localeManager.setNewLocale(mContext, language);

        Intent i = new Intent(mContext, HomeActivity.class);
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
//        overridePendingTransition(R.anim.enter_from_right, R.anim.no_change);
        if (restartProcess)
            System.exit(0);
    }

    @Override
    public void onClick(View view) {
        SharedPreferences.Editor editor = preferences.edit();
        switch (view.getId()) {
            case R.id.arabic_img:
                Log.e("LA", "## pref click ar ::: " + preferences.getString("language", ""));
                if (preferences.getString("language", "").equals("English")) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (!preferences_Login_Data.getString(PreferenceKeys.UserID, "").equals(""))
                        changeLanguage("Arabic");
                    else {
                        editor.putString("language", "Arabic");
                        editor.commit();
                        editor.apply();
                        setNewLocale("Arabic", true);
                    }
                } else {
                    startActivity(new Intent(LanguageActivity.this, HomeActivity.class));
                }
                /*Consts.getInstance().chk_lang="0";
                editor.putString("language", "Arabic");
                startActivity(new Intent(mContext, HomeActivity.class));*/
                break;
            case R.id.english_img:
                Log.e("LA", "## pref click en ::: " + preferences.getString("language", ""));
                if (preferences.getString("language", "").equals("Arabic")) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (!preferences_Login_Data.getString(PreferenceKeys.UserID, "").equals(""))
                        changeLanguage("English");
                    else {
                        editor.putString("language", "English");
                        editor.commit();
                        editor.apply();
                        setNewLocale("English", true);
                    }
                } else {
                    startActivity(new Intent(LanguageActivity.this, HomeActivity.class));
                }
                /*-onsts.getInstance().chk_lang="0";
                editor.putString("language", "English");
                startActivity(new Intent(mContext, HomeActivity.class));*/
                break;
            case R.id.Rel_Policy:
                startActivity(new Intent(mContext, AgreementPolicyActivity.class));
                break;
            case R.id.Rel_terms:
                startActivity(new Intent(mContext, TermsConditionsActivity.class));
                break;
            case R.id.Rel_promises:
                startActivity(new Intent(mContext, OurPromisesActivity.class));
                break;
        }
        editor.commit();
    }

    private void changeLanguage(String selectedLang) {
        Log.e("params", "## changeLanguage :: " + selectedLang);

        if (Utils.getInstance().isInternetAvailable(mContext)) {
            progress.setVisibility(View.VISIBLE);

            HashMap<String, String> params = new HashMap<>();
            params.put("language", selectedLang);
            params.put("UserID", preferences_Login_Data.getString("UserID", ""));

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
                                Toast.makeText(mContext, "" + updateLanguage.getMassege(), Toast.LENGTH_SHORT).show();

                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("language", selectedLang);
                                editor.commit();
                                editor.apply();
                                setNewLocale(selectedLang, true);
                            } else {
                                Toast.makeText(mContext, "" + updateLanguage.getMassege(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception ee) {
                        ee.printStackTrace();
                        progress.setVisibility(View.GONE);
                        Toast.makeText(mContext, "" + getResources().getString(R.string.somethingWrong), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UpdateLanguage> call, @NonNull Throwable t) {
                    progress.setVisibility(View.GONE);
                    t.printStackTrace();
                    Toast.makeText(mContext, "" + getResources().getString(R.string.somethingWrong), Toast.LENGTH_SHORT).show();
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
}
