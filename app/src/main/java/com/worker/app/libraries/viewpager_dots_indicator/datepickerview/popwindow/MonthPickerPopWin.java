package com.worker.app.libraries.viewpager_dots_indicator.datepickerview.popwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.PopupWindow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.worker.app.R;
import com.worker.app.libraries.viewpager_dots_indicator.datepickerview.LoopScrollListener;
import com.worker.app.libraries.viewpager_dots_indicator.datepickerview.LoopView;

public class MonthPickerPopWin extends PopupWindow implements View.OnClickListener {

    private static final int DEFAULT_MIN_YEAR = 1900;
    public Button cancelBtn;
    public Button confirmBtn;
    public LoopView yearLoopView;
    public LoopView monthLoopView;
    public LoopView dayLoopView;
    public View pickerContainerV;
    public View contentView;//root view

    private int minYear; // min year
    private int maxYear; // max year
    private int yearPos = 0;
    private int monthPos = 0;
    private int dayPos = 0;
    private Context mContext;
    private String textCancel;
    private String textConfirm;
    private int colorCancel;
    private int colorConfirm;
    private int btnTextsize;//text btnTextsize of cancel and confirm button
    private int viewTextSize;
    private boolean showDayMonthYear;

    List<String> yearList = new ArrayList();
    List<String> monthList = new ArrayList();
    List<String> dayList = new ArrayList();

    public static class Builder{

        //Required
        private Context context;
        private MonthPickerPopWin.OnDatePickedListener listener;
        public Builder(Context context, MonthPickerPopWin.OnDatePickedListener listener){
            this.context = context;
            this.listener = listener;
        }

        //Option
        private boolean showDayMonthYear = false;
        private int minYear = DEFAULT_MIN_YEAR;
        private int maxYear = Calendar.getInstance().get(Calendar.YEAR)+1;
        private String textCancel = "Cancel";
        private String textConfirm = "Confirm";
        private String dateChose = getStrDate();
        private int colorCancel = Color.parseColor("#999999");
        private int colorConfirm = Color.parseColor("#303F9F");
        private int btnTextSize = 16;//text btnTextsize of cancel and confirm button
        private int viewTextSize = 25;

        public MonthPickerPopWin.Builder minYear(int minYear){
            this.minYear = minYear;
            return this;
        }

        public MonthPickerPopWin.Builder maxYear(int maxYear){
            this.maxYear = maxYear;
            return this;
        }

        public MonthPickerPopWin.Builder textCancel(String textCancel){
            this.textCancel = textCancel;
            return this;
        }

        public MonthPickerPopWin.Builder textConfirm(String textConfirm){
            this.textConfirm = textConfirm;
            return this;
        }

        public MonthPickerPopWin.Builder dateChose(String dateChose){
            this.dateChose = dateChose;
            return this;
        }

        public MonthPickerPopWin.Builder colorCancel(int colorCancel){
            this.colorCancel = colorCancel;
            return this;
        }

        public MonthPickerPopWin.Builder colorConfirm(int colorConfirm){
            this.colorConfirm = colorConfirm;
            return this;
        }

        /**
         * set btn text btnTextSize
         * @param textSize dp
         */
        public MonthPickerPopWin.Builder btnTextSize(int textSize){
            this.btnTextSize = textSize;
            return this;
        }

        public MonthPickerPopWin.Builder viewTextSize(int textSize){
            this.viewTextSize = textSize;
            return this;
        }

        public MonthPickerPopWin build(){
            if(minYear > maxYear){
                throw new IllegalArgumentException();
            }
            return new MonthPickerPopWin(this);
        }

        public MonthPickerPopWin.Builder showDayMonthYear(boolean useDayMonthYear) {
            this.showDayMonthYear = useDayMonthYear;
            return this;
        }
    }

    public MonthPickerPopWin(MonthPickerPopWin.Builder builder){
        this.minYear = builder.minYear;
        this.maxYear = builder.maxYear;
        this.textCancel = builder.textCancel;
        this.textConfirm = builder.textConfirm;
        this.mContext = builder.context;
        this.mListener = builder.listener;
        this.colorCancel = builder.colorCancel;
        this.colorConfirm = builder.colorConfirm;
        this.btnTextsize = builder.btnTextSize;
        this.viewTextSize = builder.viewTextSize;
        this.showDayMonthYear = builder.showDayMonthYear;
        setSelectedDate(builder.dateChose);
        initView();
    }

    private MonthPickerPopWin.OnDatePickedListener mListener;

    private void initView() {
        contentView = LayoutInflater.from(mContext).inflate( R.layout.layout_month_picker , null);
        cancelBtn = (Button) contentView.findViewById(R.id.btn_cancel);
        cancelBtn.setTextColor(colorCancel);
        cancelBtn.setTextSize(btnTextsize);
        confirmBtn = (Button) contentView.findViewById(R.id.btn_confirm);
        confirmBtn.setTextColor(colorConfirm);
        confirmBtn.setTextSize(btnTextsize);
        yearLoopView = (LoopView) contentView.findViewById(R.id.picker_day);
        monthLoopView = (LoopView) contentView.findViewById(R.id.picker_month);
        dayLoopView = (LoopView) contentView.findViewById(R.id.picker_year);
        pickerContainerV = contentView.findViewById(R.id.container_picker);

        //set checked listen
        yearLoopView.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                yearPos = item;
                initDayPickerView();
            }
        });
        monthLoopView.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                monthPos = item;
                initDayPickerView();
            }
        });
        dayLoopView.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                yearPos = item;
            }
        });

        initPickerViews(); // init year and month loop view
        initDayPickerView(); //init day loop view

        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        contentView.setOnClickListener(this);

        if(!TextUtils.isEmpty(textConfirm)){
            confirmBtn.setText(textConfirm);
        }

        if(!TextUtils.isEmpty(textCancel)){
            cancelBtn.setText(textCancel);
        }

        setTouchable(true);
        setFocusable(true);
        // setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
//        setAnimationStyle(R.style.FadeInPopWin);
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * Init year and month loop view,
     * Let the day loop view be handled separately
     */
    private void initPickerViews() {

        int yearCount = maxYear - minYear;

        for (int i = 0; i < yearCount; i++) {
            yearList.add(format2LenStr(minYear + i));
        }

        for (int j = 0; j < 12; j++) {
            monthList.add(format2LenStr(j + 1));
        }

        yearLoopView.setDataList((ArrayList) yearList);
        yearLoopView.setInitPosition(yearPos);

        monthLoopView.setDataList((ArrayList) monthList);
        monthLoopView.setInitPosition(monthPos);
    }

    /**
     * Init day item
     */
    private void initDayPickerView() {
        int dayMaxInMonth;
        Calendar calendar = Calendar.getInstance();
        dayList = new ArrayList<String>();

        calendar.set(Calendar.YEAR, minYear + yearPos);
        calendar.set(Calendar.MONTH, monthPos);

        //get max day in month
        dayMaxInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < dayMaxInMonth; i++) {
            dayList.add(format2LenStr(i + 1));
        }

        dayLoopView.setDataList((ArrayList) dayList);
        dayLoopView.setInitPosition(dayPos);
    }

    /**
     * set selected date position value when initView.
     *
     * @param dateStr
     */
    public void setSelectedDate(String dateStr) {

        if (!TextUtils.isEmpty(dateStr)) {

            long milliseconds = getLongFromyyyyMMdd(dateStr);
            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);

            if (milliseconds != -1) {

                calendar.setTimeInMillis(milliseconds);
                yearPos = calendar.get(Calendar.YEAR) - minYear;
                monthPos = calendar.get(Calendar.MONTH);
                dayPos = calendar.get(Calendar.DAY_OF_MONTH) - 1;
            }
        }
    }

    /**
     * Show date picker popWindow
     *
     * @param activity
     */
    public void showPopWin(Activity activity) {

        if (null != activity) {

            TranslateAnimation trans = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                    0, Animation.RELATIVE_TO_SELF, 1,
                    Animation.RELATIVE_TO_SELF, 0);

            showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM,
                    0, 0);
            trans.setDuration(400);
            trans.setInterpolator(new AccelerateDecelerateInterpolator());

            pickerContainerV.startAnimation(trans);
        }
    }

    /**
     * Dismiss date picker popWindow
     */
    public void dismissPopWin() {

        TranslateAnimation trans = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);

        trans.setDuration(400);
        trans.setInterpolator(new AccelerateInterpolator());
        trans.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                dismiss();
            }
        });

        pickerContainerV.startAnimation(trans);
    }

    @Override
    public void onClick(View v) {
        if (v == contentView || v == cancelBtn) {

            dismissPopWin();
        } else if (v == confirmBtn) {

            if (null != mListener) {

                int year = minYear + yearPos;
                int month = monthPos + 1;
                int day = dayPos + 1;
                StringBuffer sb = new StringBuffer();

                Log.e("Addcard","## format2LenStr(month) : "+format2LenStr(month));
                sb.append(String.valueOf(year));
                sb.append("-");
                sb.append(format2LenStr(month));
                sb.append("-");
                sb.append(format2LenStr(day));
                mListener.onDatePickCompleted(year, month, day, sb.toString());
            }

            dismissPopWin();
        }
    }

    /**
     * get long from yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static long getLongFromyyyyMMdd(String date) {
        SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()); //yyyy-MM-dd
        Date parse = null;
        try {
            parse = mFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (parse != null) {
            return parse.getTime();
        } else {
            return -1;
        }
    }

    public static String getStrDate() {
//        SimpleDateFormat dd = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        return dd.format(new Date());
    }

    /**
     * Transform int to String with prefix "0" if less than 10
     * @param num
     * @return
     */
    public static String format2LenStr(int num) {
        return (num < 10) ? "0" + num : String.valueOf(num);
    }

    public static int spToPx(Context context, int spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public interface OnDatePickedListener {

        /**
         * Listener when date has been checked
         *
         * @param year
         * @param month
         * @param day
         * @param dateDesc  yyyy-MM-dd
         */
        void onDatePickCompleted(int year, int month, int day,
                                 String dateDesc);
    }
}
