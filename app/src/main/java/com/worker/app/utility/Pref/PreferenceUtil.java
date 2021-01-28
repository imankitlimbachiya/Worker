package com.worker.app.utility.Pref;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {
    private static final String PREFERENCE_FILE = "WorkerApp";
    private static final String PASSWORD = "MyWorkerApp7385";
    private static SharedPreferences preferences;

    public synchronized static SharedPreferences getPref(Context context) {
        if (preferences == null)
            preferences = new ObscuredSharedPreferences(context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE), PASSWORD);
        return preferences;
    }
}