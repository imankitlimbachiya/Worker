package com.worker.app.utility;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.lang.ref.WeakReference;

import com.worker.app.R;

public class ForceUpgradeManager implements LifecycleObserver {
    private static final String KEY_UPDATE_REQUIRED = "force_update_required";
    private static final String KEY_CURRENT_VERSION = "force_update_current_version";
    private static final String TAG = "ForceUpgradeManager";
    private final Context context;

    private WeakReference<Activity> activityWeakReference;

    public ForceUpgradeManager(AppController application) {
        Log.e(TAG,"## ForceUpgradeManager");
        this.context = application.getApplicationContext();
        application.registerActivityLifecycleCallbacks(callbacks);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void appStarted() {
        Log.e(TAG,"## appStarted");
        checkForceUpdateNeeded();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void appStopped() {
        Log.e(TAG,"## appStopped");
        if (activityWeakReference != null) {
            activityWeakReference.clear();
        }
    }

    private Activity getCurrentActivity() {
        Log.e(TAG,"## getCurrentActivity");
        return activityWeakReference != null && activityWeakReference.get() != null
                ? activityWeakReference.get() : null;
    }
    private final Application.ActivityLifecycleCallbacks callbacks =
            new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(@NonNull Activity activity, Bundle bundle) {
                }
                @Override
                public void onActivityStarted(@NonNull Activity activity) {
                    Log.e(TAG,"## onActivityStarted");
                    ForceUpgradeManager.this.activityWeakReference = new WeakReference<>(activity);
                }
                @Override
                public void onActivityResumed(@NonNull Activity activity) {
                }
                @Override
                public void onActivityPaused(@NonNull Activity activity) {
                }
                @Override
                public void onActivityStopped(@NonNull Activity activity) {
                }
                @Override
                public void onActivitySaveInstanceState(@NonNull Activity activity,
                                                        @NonNull Bundle bundle) {
                }
                @Override
                public void onActivityDestroyed(@NonNull Activity activity) {
                }
            };
    /**
     * Gets update alert.
     */
    private void onUpdateNeeded() {
        Log.e(TAG,"## onUpdateNeeded");
        Activity temp = getCurrentActivity();
        if (temp != null) {
            AlertDialog dialog = new AlertDialog.Builder(temp)
                    .setCancelable(false)
                    .setTitle(context.getResources().getString(R.string.newVersion))
                    .setMessage(context.getResources().getString(R.string.plzUpdate))
                    .setPositiveButton(context.getResources().getString(R.string.cont),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    redirectStore();
                                }
                            }).create();
            dialog.show();
        }
    }
    /**
     * Redirect to play store
     */
    private void redirectStore() {
        Log.e(TAG,"## redirectStore");
        Uri updateUrl = Uri.parse("market://details?id=" + context.getPackageName());
        final Intent intent = new Intent(Intent.ACTION_VIEW, updateUrl);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    private void checkForceUpdateNeeded() {
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        
        Log.e(TAG,"## checkForceUpdateNeeded");

//        remoteConfig.setDefaultsAsync(R.xml.remote_config_details);
        // long cacheExpiration = 12 * 60 * 60; // fetch every 12 hours
        // set in-app defaults
        remoteConfig.fetch(0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "## remote config is fetched.");
                            remoteConfig.activateFetched();
                        }
                        if (remoteConfig.getBoolean(KEY_UPDATE_REQUIRED)) {
                            String currentVersion = remoteConfig.getString(KEY_CURRENT_VERSION);
                            String appVersion = getAppVersion(context);

                            Log.e(TAG, "## chk appVersion : "+appVersion);
                            Log.e(TAG, "## chk currentVersion : "+currentVersion);
                            if (!TextUtils.equals(currentVersion, appVersion)) {
                                onUpdateNeeded();
                            }
                        }
                    }
                });
    }
    private String getAppVersion(Context context) {
        String result = "";
        try {
            result = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "## "+e.getMessage());
        }
        return result;
    }
}
