package com.worker.app.utility;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    public static LocaleManager localeManager;

    private RequestQueue mRequestQueue;
    LruBitmapCache mLruBitmapCache;
    private static AppController mInstance;
    private ForceUpgradeManager forceUpgradeManager;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    @Override
    protected void attachBaseContext(Context base) {
        localeManager = new LocaleManager(base);
        super.attachBaseContext(localeManager.setLocale(base));
        Log.e("App", "## attachBaseContext");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        localeManager.setLocale(this);
        Log.e("App", "## onConfigurationChanged: " + newConfig.locale.getLanguage());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        FirebaseApp.initializeApp(this);

        /*initForceUpgradeManager();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);*/
    }

    public void initForceUpgradeManager() {
        if (forceUpgradeManager == null) {
            forceUpgradeManager = new ForceUpgradeManager(mInstance);
        }
    }
}