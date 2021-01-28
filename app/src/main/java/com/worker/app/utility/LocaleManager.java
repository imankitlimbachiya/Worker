package com.worker.app.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.Log;

import java.util.Locale;

import static android.os.Build.VERSION_CODES.N;

public class LocaleManager {

    public static final String LANGUAGE_ENGLISH = "English";
    public static final String LANGUAGE_ARABIC = "Arabic";
    private static final String LANGUAGE_KEY = "language";
    SharedPreferences preferences;

    public LocaleManager(Context context) {
        preferences = context.getSharedPreferences("Language", context.MODE_PRIVATE);
    }

    public Context setLocale(Context c) {
        return updateResources(c, getLanguage());
    }

    public Context setNewLocale(Context c, String language) {
        Log.e("LM", "## setNewLocale : " + language);
        persistLanguage(language);
        return updateResources(c, language);
    }

    public String getLanguage() {
        return preferences.getString(LANGUAGE_KEY, LANGUAGE_ENGLISH);
    }

    @SuppressLint("ApplySharedPref")
    private void persistLanguage(String language) {
        Log.e("LM", "## persistLanguage : " + language);
        // use commit() instead of apply(), because sometimes we kill the application process immediately
        // which will prevent apply() to finish
        preferences.edit().putString(LANGUAGE_KEY, language).commit();
    }

    private Context updateResources(Context context, String language) {
        Log.e("App", "## updateResources setDefault : " + language);

        String langStr = "";
        if (language.equals("Arabic"))
            langStr = "ar";
        else
            langStr = "en";
        Locale locale = new Locale(langStr);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.e("App", "## updateResources  >= Build.VERSION_CODES.N ");
            config.setLocale(locale);
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context = context.createConfigurationContext(config);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Log.e("App", "## updateResources >= Build.VERSION_CODES.JELLY_BEAN_MR1");
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            Log.e("App", "## updateResources else");
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());

        return context;
    }

    public static boolean isAtLeastVersion(int version) {
        return Build.VERSION.SDK_INT >= version;
    }

    public static Locale getLocale(Resources res) {
        Configuration config = res.getConfiguration();
        return isAtLeastVersion(N) ? config.getLocales().get(0) : config.locale;
    }
}
