package com.worker.app.utility;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.worker.app.R;


/**
 * Created by SHRUTI on 6/30/2017.
 */

public class MyEditText extends androidx.appcompat.widget.AppCompatEditText {

    private boolean font_light = false;
    private boolean font_regular = false;
    private boolean font_semibold = false;
    private boolean font_bold = false;
    private boolean font_italic = false;

    public MyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyTextView, 0, 0);
        try {
            font_light = a.getBoolean(R.styleable.MyTextView_font_light, false);
            font_regular = a.getBoolean(R.styleable.MyTextView_font_regular, false);
            font_semibold = a.getBoolean(R.styleable.MyTextView_font_semibold, false);
            font_bold = a.getBoolean(R.styleable.MyTextView_font_bold, false);
            font_italic = a.getBoolean(R.styleable.MyTextView_font_italic, false);
        } finally {
            a.recycle();
        }
        init();
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.MyTextView, 0, 0);
        try {
            font_light = a.getBoolean(R.styleable.MyTextView_font_light, false);
            font_regular = a.getBoolean(R.styleable.MyTextView_font_regular, false);
            font_semibold = a.getBoolean(R.styleable.MyTextView_font_semibold, false);
            font_bold = a.getBoolean(R.styleable.MyTextView_font_bold, false);
            font_italic = a.getBoolean(R.styleable.MyTextView_font_italic, false);
        } finally {
            a.recycle();
        }
        init();
    }

    public MyEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = null;
        if (font_light) {
            tf = Typeface.createFromAsset(getContext().getAssets(), "aller_light.ttf");
        } else if (font_regular) {
            tf = Typeface.createFromAsset(getContext().getAssets(), "aller_regular.ttf");
        } else if (font_semibold) {
            tf = Typeface.createFromAsset(getContext().getAssets(), "aller_semibold.ttf");
        } else if (font_bold) {
            tf = Typeface.createFromAsset(getContext().getAssets(), "aller_bold.ttf");
        }else if (font_italic) {
            tf = Typeface.createFromAsset(getContext().getAssets(), "aller_italic.ttf");
        }else {
            tf = Typeface.createFromAsset(getContext().getAssets(), "aller_regular.ttf");
        }
        setTypeface(tf);
    }

}
