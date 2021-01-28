package com.worker.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.worker.app.R;
import com.worker.app.libraries.viewpager_dots_indicator.DotsIndicator;
import com.worker.app.utility.MyTextView;
import com.worker.app.utility.Pref.PreferenceKeys;
import com.worker.app.utility.Pref.PreferenceUtil;
import com.worker.app.utility.Utils;

public class IntroActivity extends AppCompatActivity implements View.OnTouchListener {

    private ViewPager view_pager;
    private DotsIndicator dots_indicator;
    private ArrayList<Integer> listIntro = new ArrayList<>();
    private Timer timer;
    private int page = 1;
    int SPLASH_TIME_OUT = 1500;
    final long DELAY_MS = 0;
    private MyTextView txtSkip, txtNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        getSupportActionBar().hide();

        Utils.getInstance().hideStatusBar(IntroActivity.this);

        initViews();

        listIntro.add(R.drawable.intro1);
        listIntro.add(R.drawable.intro2);
        listIntro.add(R.drawable.intro3);

        view_pager.setAdapter(new IntroPagerAdapter(this, listIntro));
        dots_indicator.setViewPager(view_pager);

        view_pager.setOnTouchListener(this);
    }

    public void startSliding(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pageSwitcher(SPLASH_TIME_OUT);
            }
        }, SPLASH_TIME_OUT);
    }

    private void initViews() {
        view_pager = findViewById(R.id.view_pager);
        dots_indicator = findViewById(R.id.dots_indicator);
        txtSkip = findViewById(R.id.txtSkip);
        txtNext = findViewById(R.id.txtNext);
        txtSkip.setVisibility(View.VISIBLE);
        txtNext.setVisibility(View.GONE);

        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHomeActivity();
            }
        });

        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHomeActivity();
            }
        });
    }

    public void startHomeActivity() {
        PreferenceUtil.getPref(this).edit().putBoolean(PreferenceKeys.IS_INTRO, true).apply();

        SharedPreferences preferences = getSharedPreferences("Language", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("chk_lang", "1");
        editor.apply();

        Intent intentLang =new Intent(IntroActivity.this, LanguageActivity.class);
        intentLang.putExtra("fromWhere","Intro");
        startActivity(intentLang);
        finish();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (timer != null)
                    return false;
        }
        return false;
    }

    class RemindTask extends TimerTask {
        @Override
        public void run() {
            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            runOnUiThread(new Runnable() {
                public void run() {
                    if (page == listIntro.size()) { // In my case the number of pages are 5
                        timer.cancel();
                    } else {
                        view_pager.setCurrentItem(page++, true);
                    }
                }
            });
        }
    }

    public void pageSwitcher(int seconds) {
        timer = new Timer(); // At this line a new Thread will be created
        timer.schedule(new RemindTask(), DELAY_MS, seconds); // delay in milliseconds
    }

    public class IntroPagerAdapter extends PagerAdapter {

        private Context mContext;
        private ArrayList<Integer> listIntro;

        public IntroPagerAdapter(Context context, ArrayList<Integer> listIntro) {
            mContext = context;
            this.listIntro = listIntro;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.row_intro, collection, false);

            ImageView imgView = layout.findViewById(R.id.imgView);
            imgView.setImageResource(listIntro.get(position));

            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            int j = view_pager.getCurrentItem();
            if (j == 2) {
                txtSkip.setVisibility(View.GONE);
                txtNext.setVisibility(View.VISIBLE);
            } else {
                txtSkip.setVisibility(View.VISIBLE);
                txtNext.setVisibility(View.GONE);
            }
            return listIntro.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}