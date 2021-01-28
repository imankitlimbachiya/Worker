package com.worker.app.libraries.viewpager_dots_indicator;

import android.content.Context;
import android.util.TypedValue;

import com.worker.app.R;


public class UiUtils {
  public static int getThemePrimaryColor(final Context context) {
    final TypedValue value = new TypedValue();
    context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
    return value.data;
  }
}
