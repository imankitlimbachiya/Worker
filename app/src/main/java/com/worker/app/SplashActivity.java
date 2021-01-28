package com.worker.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.worker.app.activity.IntroActivity;
import com.worker.app.activity.LanguageActivity;
import com.worker.app.utility.Pref.PreferenceKeys;
import com.worker.app.utility.Pref.PreferenceUtil;
import com.worker.app.utility.Utils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actiivty_splash);

        getSupportActionBar().hide();

        Utils.getInstance().hideStatusBar(SplashActivity.this);
        int SPLASH_TIME_OUT = 800;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i;
                Log.e("SA","## IS_INTRO :: "+PreferenceUtil.getPref(SplashActivity.this).getBoolean(PreferenceKeys.IS_INTRO, false));
                if (PreferenceUtil.getPref(SplashActivity.this).getBoolean(PreferenceKeys.IS_INTRO, false)) {
                    SharedPreferences preferences = getSharedPreferences("Language",MODE_PRIVATE);
                    String language = preferences.getString("language","");
                    Log.e("SA","## language :: "+language);
                    if (language.equals("")){
                        i = new Intent(SplashActivity.this, LanguageActivity.class);
                    } else {
                        i = new Intent(SplashActivity.this, HomeActivity.class);
                    }

                } else {
                    i = new Intent(SplashActivity.this, IntroActivity.class);
                }
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
