package com.worker.app.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.worker.app.utility.Pref.PreferenceKeys;
import com.worker.app.utility.Pref.PreferenceUtil;

public class Utils {

    private static Utils mInstance;

    public synchronized static Utils getInstance() {
        if (mInstance == null)
            mInstance = new Utils();
        return mInstance;
    }

    public void hideStatusBar(Activity context) {
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void showStatusBar(Activity context) {
        context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void changeImageViewColor(ImageView imgCenter, String myMood) {
        int color = Color.parseColor(myMood);
        imgCenter.setColorFilter(color);
    }

    public void changeImageViewColor(ImageView imgCenter, int myMood) {
        imgCenter.setColorFilter(myMood);
    }

    public boolean isArabicLanguage(Context mContext) { //1=urdu , 0= eng
        if (PreferenceUtil.getPref(mContext).getString(PreferenceKeys.PrefLanguage, "0").equals("1"))
            return true;
        else
            return false;
    }

    public void showSnackBar(View view, String message) {
        try {
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.parseColor("#AB8BFF"));
            TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isInternetAvailable(Context context) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            } else
                connected = false;
        }
        return connected;
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean checkDiscountValueNotNull(String discount) {
        if (discount == null ||
                discount.equals("0") ||
                discount.equals("0.00") ||
                discount.equals("")) {
            return true;
        } else
            return false;
    }

    public float giveDiffrence(String price, String discountedPrice) {
        if (price.equals("") || discountedPrice.equals("")) {
            return 0;
        }
        return Float.parseFloat(price.replace(",", "")) -
                Float.parseFloat(discountedPrice.replace(",", ""));
    }

    public float giveDiffrence(Float price, Float discountedPrice) {
        if (price==0 || discountedPrice==0) {
            return 0;
        }
        return price - discountedPrice;
    }

    public float giveAddition(String price, String discountedPrice) {
        if (price.equals("") || discountedPrice.equals("")) {
            return 0;
        }
        return Float.parseFloat(price.replace(",", "")) +
                Float.parseFloat(discountedPrice.replace(",", ""));
    }

    public float convertTowDecimal(double d) {
        int i;
        d += 0.005;
        i = (int) (d * 100);
        float b = i / 100;
        return b;
    }

    public String formatDate(String date, String initDateFormat, String endDateFormat) throws ParseException {
        Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = formatter.format(initDate);
        return parsedDate;
    }
}
