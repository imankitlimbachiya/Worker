package com.worker.app.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.oppwa.mobile.connect.exception.PaymentError;
import com.oppwa.mobile.connect.exception.PaymentException;
import com.oppwa.mobile.connect.payment.BrandsValidation;
import com.oppwa.mobile.connect.payment.CheckoutInfo;
import com.oppwa.mobile.connect.payment.ImagesRequest;
import com.oppwa.mobile.connect.payment.PaymentParams;
import com.oppwa.mobile.connect.payment.card.CardPaymentParams;
import com.oppwa.mobile.connect.provider.Connect;
import com.oppwa.mobile.connect.provider.ITransactionListener;
import com.oppwa.mobile.connect.provider.Transaction;
import com.oppwa.mobile.connect.provider.TransactionType;
import com.oppwa.mobile.connect.service.ConnectService;
import com.oppwa.mobile.connect.service.IProviderBinder;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.worker.app.BaseActivity;
import com.worker.app.HomeActivity;
import com.worker.app.R;
import com.worker.app.adapter.CardTypeAdapter;
import com.worker.app.libraries.viewpager_dots_indicator.datepickerview.popwindow.MonthPickerPopWin;
import com.worker.app.model.CardTypeModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyButton;
import com.worker.app.utility.MyEditText;
import com.worker.app.utility.MyTextView;

import static com.worker.app.libraries.viewpager_dots_indicator.datepickerview.popwindow.DatePickerPopWinNew.format2LenStr;

public class PaymentActivity extends BaseActivity implements View.OnClickListener, ITransactionListener {

    boolean isTransactionCompleted = false;
    Context mContext;
    MyButton btn_payment;
    MyEditText edt_cvv, edt_holderName, edt_cardNumber;
    MyTextView edt_date;
    ImageView back;
    ProgressBar progress;
    IProviderBinder providerBinder;
    SharedPreferences preferences, preferences_Login_Data;
    String amount, CountryCode, PostalCode, locality, Country, TotalWalletAmount,
            District, City, State, OrderID, resourcePath = "", checkoutId, UsedWalletAmount;
    private RecyclerView recycler_viewCardType;
    private ArrayList<CardTypeModel> listCard = new ArrayList<>();
    private CardTypeAdapter mAdapter;
    private String selectedCardType = "VISA";
    ArrayList<String> listOfPattern = new ArrayList<String>();
    String a;
    private String merchantInvoiceId = "";
    int keyDel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Log.e("response", "*** onCreate PaymentAct");
        try {
            mContext = this;
            listOfPattern = listOfPattern();

            Init();

            Log.e("amount", "## onCreate amt " + amount);
            if (amount.equals("0.0")) {
                Log.e("amount", "## onCreate ifff amount.equals(0)");

                firebaseEventLoggingStart();

                orderPayApi("", "0.00", new JSONObject(), "");
            } else {
                Log.e("amount", "## onCreate else amount.equals(0)");
                bindCardTypeAdater();

                cardNumberListener();
            }
        } catch (Exception ee) {
            Log.e("response", "*** error onCreate :: " + ee.getMessage());
            ee.printStackTrace();
        }
    }

    private void firebaseEventLoggingStart() {
        //firebase event logging
        Bundle bundle = new Bundle();
        bundle.putString(Consts.getInstance().EVENT_NAME, Consts.getInstance().CHECKOUT_START);
        bundle.putString(Consts.getInstance().FIREBASE_USERID, preferences_Login_Data.getString("UserID", ""));
        bundle.putString(Consts.getInstance().FIREBASE_ORDER_ID, OrderID);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "text");
        mFirebaseAnylytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void cardNumberListener() {
        edt_cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equalsIgnoreCase("")) {
                    Log.e(TAG, "afterTextChanged !s.toString().equals blank" + listOfPattern.size());
                    for (int i = 0; i < listOfPattern.size(); i++) {
                        Log.e(TAG, "afterTextChanged iterate for");
                        if (s.toString().matches(listOfPattern.get(i))) {
                            Log.e(TAG, "afterTextChanged s matches with pattern");
                            for (int j = 0; j < listCard.size(); j++) {
                                listCard.get(j).setSelected(false);
                            }
                            listCard.get(i).setSelected(true);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    public void Init() {
        recycler_viewCardType = findViewById(R.id.recycler_viewCardType);
        btn_payment = findViewById(R.id.btn_payment);
        edt_cvv = findViewById(R.id.edt_cvv);
        edt_date = findViewById(R.id.edt_date);
        back = findViewById(R.id.back);
        edt_holderName = findViewById(R.id.edt_holderName);
        edt_cardNumber = findViewById(R.id.edt_cardNumber);
        progress = findViewById(R.id.progress);

        btn_payment.setOnClickListener(this);
        edt_date.setOnClickListener(this);
        back.setOnClickListener(this);

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);

        Country = getIntent().getExtras().getString("Country");
        locality = getIntent().getExtras().getString("locality");
        PostalCode = getIntent().getExtras().getString("PostalCode");
        CountryCode = getIntent().getExtras().getString("CountryCode");
        amount = getIntent().getExtras().getString("amount");
        District = getIntent().getExtras().getString("District");
        City = getIntent().getExtras().getString("City");
        State = getIntent().getExtras().getString("State");
        TotalWalletAmount = getIntent().getExtras().getString("TotalWalletAmount");
        UsedWalletAmount = getIntent().getExtras().getString("UsedWalletAmount");

        OrderID = getIntent().getExtras().getString("OrderID");
        merchantInvoiceId = getIntent().getExtras().getString("merchantInvoiceId");
        Log.e("OrderID", "*** paymentAct bbb ::: " + OrderID);

        Log.e("Country", "## " + Country);
        Log.e("locality", "## " + locality);
        Log.e("PostalCode", "## " + PostalCode);
        Log.e("CountryCode", "## " + CountryCode);
        Log.e("amount", "## " + amount);
        Log.e("District", "## " + District);
        Log.e("City", "## " + City);
        Log.e("State", "## " + State);
        Log.e("TotalWalletAmount", "## " + TotalWalletAmount);
        Log.e("UsedWalletAmount", "## " + UsedWalletAmount);

        edt_cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean flag = true;
                String myCardNumber = s.toString();
                String eachBlock[] = myCardNumber.split("-");
                for (int i = 0; i < eachBlock.length; i++) {
                    if (eachBlock[i].length() > 4) {
                        flag = false;
                    }
                }
                if (flag) {
                    edt_cardNumber.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_DEL)
                                keyDel = 1;
                            return false;
                        }
                    });

                    if (keyDel == 0) {
                        if (((edt_cardNumber.getText().length() + 1) % 5) == 0) {

                            if (edt_cardNumber.getText().toString().split("-").length <= 3) {
                                String newNumber = s.toString() + "-";
                                edt_cardNumber.setText(newNumber);
                                edt_cardNumber.setSelection(edt_cardNumber.getText().length());
                            }
                        }
                        a = edt_cardNumber.getText().toString();
                    } else {
                        a = edt_cardNumber.getText().toString();
                        keyDel = 0;
                    }

                } else {
                    edt_cardNumber.setText(a);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equalsIgnoreCase("")) {
                    Log.e(TAG, "afterTextChanged !s.toString().equals blank" + listOfPattern.size());
                    for (int i = 0; i < listOfPattern.size(); i++) {
                        Log.e(TAG, "afterTextChanged iterate for");
                        if (s.toString().matches(listOfPattern.get(i))) {
                            Log.e(TAG, "afterTextChanged s matches with pattern");
                            for (int j = 0; j < listCard.size(); j++) {
                                listCard.get(j).setSelected(false);
                            }
                            listCard.get(i).setSelected(true);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    for (int j = 0; j < listCard.size(); j++) {
                        listCard.get(j).setSelected(false);
                    }
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public ArrayList<String> listOfPattern() {
        ArrayList<String> listOfPattern = new ArrayList<String>();
        String ptVisa = "^4[0-9]$";
        listOfPattern.add(ptVisa);
        String ptMasterCard = "^5[1-5]$";
        listOfPattern.add(ptMasterCard);
        String ptDiscover = "^6(?:011|5[0-9])";// "^6(?:011|5[0-9]{2})$";
        listOfPattern.add(ptDiscover);
        String ptAmeExp = "^3[47]$";//"^3[47]$";
        listOfPattern.add(ptAmeExp);
        String ptDinClb = "^(36|38)";// "^3(?:0[0-5]|[68][0-9])[0-9]{4,}$";
        listOfPattern.add(ptDinClb);
        return listOfPattern;
    }

    private void bindCardTypeAdater() {
        CardTypeModel cardTypeModel;

        cardTypeModel = new CardTypeModel(R.drawable.pic_10, "VISA");
        listCard.add(cardTypeModel);

        cardTypeModel = new CardTypeModel(R.drawable.pic_master, "MASTERCARD");
        listCard.add(cardTypeModel);

        cardTypeModel = new CardTypeModel(R.drawable.pic_13, "DISCOVER");
        listCard.add(cardTypeModel);

        cardTypeModel = new CardTypeModel(R.drawable.pic_12, "AMERICAN EXPRESS");
        listCard.add(cardTypeModel);

        cardTypeModel = new CardTypeModel(R.drawable.pic_11, "DINERS CLUB");
        listCard.add(cardTypeModel);

        mAdapter = new CardTypeAdapter(listCard);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        recycler_viewCardType.setLayoutManager(mLayoutManager);
        recycler_viewCardType.setItemAnimator(new DefaultItemAnimator());
        recycler_viewCardType.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_payment:
                if (listCard.size() > 0) {
                    boolean isCardTypeExists = false;
                    for (int i = 0; i < listCard.size(); i++) {
                        if (listCard.get(i).isSelected()) {
                            selectedCardType = listCard.get(i).getCardName();
                            Log.e("FAC", "## getCardName :: " + listCard.get(i).getCardName());
                            isCardTypeExists = true;
                        }
                    }
                    if (isCardTypeExists) {
                        if (edt_holderName.getText().toString().equals("")) {
                            Toast.makeText(mContext, R.string.alertcardholdername, Toast.LENGTH_SHORT).show();
                        } else if (edt_cardNumber.getText().toString().replace("-", "").equals("")) {
                            Toast.makeText(mContext, R.string.alertcardno, Toast.LENGTH_SHORT).show();
                        } else if (edt_cardNumber.getText().toString().replace("-", "").trim().length() <= 14) {
                            Toast.makeText(mContext, R.string.valid_card_number_length, Toast.LENGTH_SHORT).show();
                        } else if (!CardPaymentParams.isNumberValid(edt_cardNumber.getText().toString().replace("-", ""))) {
                            Toast.makeText(mContext, R.string.valid_card_number_length, Toast.LENGTH_SHORT).show();
                        } else if (edt_date.getText().toString().equals("")) {
                            Toast.makeText(mContext, R.string.alertcardexp, Toast.LENGTH_SHORT).show();
                        } else if (edt_cvv.getText().toString().equals("")) {
                            Toast.makeText(mContext, R.string.alertcardcvv, Toast.LENGTH_SHORT).show();
                        } else {
                            if (Consts.getInstance().isNetworkAvailable(mContext)) {
                                firebaseEventLoggingStart();

                                paymentHyperPayApi();
                            } else {
                                Consts.getInstance().Act_vity = "paymentactivity";
                                Intent intent = new Intent(mContext, ReloadActivity.class);
                                startActivity(intent);
                            }
                        }
                    } else {
                        Toast.makeText(mContext, R.string.cardTypeMsg, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.back:
                Intent i = new Intent(PaymentActivity.this, OrderSummeryActivity.class);
                i.putExtra("OrderID", OrderID);
                startActivity(i);
                finish();
                break;
            case R.id.edt_date:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_date.getWindowToken(), 0);
                openBottomSheetCalendar();
                break;
        }
    }

    private void paymentHyperPayApi() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST,
                Consts.getInstance().PAYMENT_HYPER_PAY, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            Log.e("response", "" + Consts.getInstance().PAYMENT_HYPER_PAY + json_main);
                            if (json_main.getString("success").equals("TRUE")) {
                                checkoutId = json_main.getString("checkoutId");
                                requestCheckoutInfo(checkoutId);
                            } else {
                                Toast.makeText(mContext, json_main.getString("error_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("response", "*** error paymentHyperPayApi :: " + e.getMessage());
                            progress.setVisibility(View.GONE);
                            e.printStackTrace();
                            Toast.makeText(mContext, getString(R.string.somethingWrong), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PaymentActivity.this, HomeActivity.class));
                            finish();
                        }
                    }
                });

            }
        },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(mContext, getString(R.string.somethingWrong), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PaymentActivity.this, HomeActivity.class));
                        finish();
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("method", "payment");
                params.put("amount", amount);//amount
                params.put("redirect", "workerspayment");
                params.put("merchantTransactionId", OrderID);
                params.put("merchantInvoiceId", merchantInvoiceId);
                params.put("merchant_customerId", preferences_Login_Data.getString("UserID", ""));
                params.put("billing_street1", locality);
                params.put("billing_street2", "");
                params.put("billing_city", City);
                params.put("billing_state", State);
                params.put("billing_postcode", PostalCode);
                params.put("billing_country", CountryCode);
                params.put("given_name", preferences_Login_Data.getString("Name", ""));
                params.put("surname", preferences_Login_Data.getString("Name", ""));
                params.put("customer_email", preferences_Login_Data.getString("Email", ""));
                Log.e("params first call :: ", "" + Consts.getInstance().PAYMENT_HYPER_PAY + params);
                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().PAYMENT_HYPER_PAY);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void paymentHyperPayReturnApi() {
        Log.e("response", "after onresume paymentHyperPayReturnApi");
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().PAYMENT_HYPER_PAY, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("response", "onResponse ");
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            Log.e("response", "" + Consts.getInstance().PAYMENT_HYPER_PAY + json_main);
                            if (json_main.getString("success").equalsIgnoreCase("TRUE")) {
                                JSONObject object_response = json_main.getJSONObject("response");
                                String TransactionID = object_response.getString("id");
                                String PayAmount = object_response.getString("amount");
                                orderPayApi(TransactionID, PayAmount, object_response, checkoutId);
                            } else {
                                Toast.makeText(mContext, "" + json_main.getString("messege"), Toast.LENGTH_SHORT).show();
//                                Toast.makeText(mContext, getString(R.string.somethingWrong), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(PaymentActivity.this, HomeActivity.class));
                                finish();
                            }
                        } catch (Exception e) {
                            progress.setVisibility(View.GONE);
                            e.printStackTrace();

                            Toast.makeText(mContext, getString(R.string.somethingWrong), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PaymentActivity.this, HomeActivity.class));
                            finish();
                        }
                    }
                });
            }
        },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.e("response", "error :  " + error.getMessage());
                        Log.e("response", "error :  " + error.getLocalizedMessage());
                        Log.e("response", "error :  " + error.getCause());
                        progress.setVisibility(View.GONE);
                        Toast.makeText(mContext, getString(R.string.somethingWrong), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PaymentActivity.this, HomeActivity.class));
                        finish();
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("resourcePath", resourcePath);
                params.put("method", "check_payment");
                Log.e("response params", "" + Consts.getInstance().PAYMENT_HYPER_PAY + params);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().PAYMENT_HYPER_PAY);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void orderPayApi(String TransactionID, String PayAmount, JSONObject object_response, String checkoutId) {
        Log.e("response", "## orderPayApi");
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().ORDER_PAY, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            Log.e("## response", "" + Consts.getInstance().ORDER_PAY + json_main);
                            if (json_main.getString("success").equalsIgnoreCase("True")) {

                                //firebase event logging
                                Bundle bundle = new Bundle();
                                bundle.putString(Consts.getInstance().EVENT_NAME, Consts.getInstance().CHECKOUT);
                                bundle.putString(Consts.getInstance().FIREBASE_USERID, preferences_Login_Data.getString("UserID", ""));
                                bundle.putString(Consts.getInstance().FIREBASE_ORDER_ID, OrderID);
                                bundle.putString(Consts.getInstance().FIREBASE_AMOUNT, PayAmount);
                                bundle.putString(Consts.getInstance().FIREBASE_CHECKOUT_ID, checkoutId);
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "text");
                                mFirebaseAnylytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                                SharedPreferences preferences = mContext.getSharedPreferences("Login_Data", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("OrderID", "0");
                                editor.putString("RequestCount", "0");
                                editor.apply();
                                editor.commit();

                                Intent intent = new Intent(mContext, ThankyouActivity.class);
                                startActivity(intent);
                                finish();

                                edt_cardNumber.setText("");
                                edt_holderName.setText("");
                                edt_cvv.setText("");
                                edt_date.setText("");
                            } else {
                                Toast.makeText(mContext, "" + json_main.getString("massege"), Toast.LENGTH_SHORT).show();
//                                Toast.makeText(mContext, getString(R.string.somethingWrong), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(PaymentActivity.this, HomeActivity.class));
                                finish();
                            }
                        } catch (Exception e) {
                            Log.e("response", "*** error :: " + e.getMessage());
                            progress.setVisibility(View.GONE);
                            e.printStackTrace();

                            Toast.makeText(mContext, getString(R.string.somethingWrong), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PaymentActivity.this, HomeActivity.class));
                            finish();
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.toString());
                progress.setVisibility(View.GONE);

                Toast.makeText(mContext, getString(R.string.somethingWrong), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PaymentActivity.this, HomeActivity.class));
                finish();
            }
        }) {
            protected Map<String, String> getParams() {   //Post
                Map<String, String> params = new HashMap<String, String>();///params put, not used in get method
                params.put("language", preferences.getString("language", ""));
                params.put("OrderID", OrderID);
                params.put("UsedWalletAmount", String.valueOf(UsedWalletAmount));
                params.put("UserID", preferences_Login_Data.getString("UserID", ""));
                params.put("TransactionID", TransactionID);
                params.put("PayAmount", PayAmount);
                params.put("PaymentInfo", object_response.toString());
                params.put("CheckoutID", checkoutId);
                Log.e("params third api : ", "## " + Consts.getInstance().ORDER_PAY + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().ORDER_PAY);///cache remove
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);//manage request
    }

    public void openBottomSheetCalendar() {
        Calendar calCurrent = Calendar.getInstance();
        Calendar calMax = Calendar.getInstance();
        calMax.add(Calendar.YEAR, 20);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentStringDate = sdf.format(new Date());
        MonthPickerPopWin pickerPopWin = new MonthPickerPopWin.Builder(mContext, new MonthPickerPopWin.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                edt_date.setText(format2LenStr(month) + "/" + year);
            }
        }).textConfirm(getResources().getString(R.string.text_confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.text_cancel)) //text of cancel button
                .btnTextSize(18) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(getResources().getColor(R.color.colorWhite)) //color of cancel button
                .colorConfirm(getResources().getColor(R.color.colorWhite))//color of confirm button
                .minYear(calCurrent.get(Calendar.YEAR)) //min year in loop
                .maxYear(calMax.get(Calendar.YEAR)) // max year in loop
                .dateChose(currentStringDate) // date chose when init popwindow
                .build();
        pickerPopWin.showPopWin(PaymentActivity.this);
    }

    @Override
    public void brandsValidationRequestSucceeded(BrandsValidation brandsValidation) {

    }

    @Override
    public void brandsValidationRequestFailed(PaymentError paymentError) {

    }

    @Override
    public void imagesRequestSucceeded(ImagesRequest imagesRequest) {

    }

    @Override
    public void imagesRequestFailed() {

    }

    @Override
    public void paymentConfigRequestSucceeded(CheckoutInfo checkoutInfo) {
        Log.e("MAIN", "### paymentConfigRequestSucceeded");
        if (checkoutInfo == null) {
            return;
        }
        /* Get the resource path from checkout info to request the payment status later. */
        resourcePath = checkoutInfo.getResourcePath();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.e("params first call :: ", "## checkoutInfo.getAmount() :: " + checkoutInfo.getAmount());
                showConfirmationDialog(String.valueOf(checkoutInfo.getAmount()), checkoutInfo.getCurrencyCode());
            }
        });
    }

    @Override
    public void paymentConfigRequestFailed(PaymentError paymentError) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), getString(R.string.cardCredential), Toast.LENGTH_SHORT).show();
            }
        });
        Log.e("MAIN", "### paymentConfigRequestFailed");
        Log.e("MAIN", "### getErrorMessage :: " + paymentError.getErrorMessage());
        Log.e("MAIN", "### getErrorInfo :: " + paymentError.getErrorInfo());
        Log.e("MAIN", "### getErrorCode :: " + paymentError.getErrorCode());
    }

    @Override
    public void transactionCompleted(Transaction transaction) {
        Log.e("TRA", "### transactionCompleted tostring " + transaction.toString());
        Log.e("TRA", "### transactionCompleted " + transaction.getRedirectUrl());
        if (transaction.getRedirectUrl() == null) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), getString(R.string.cardCredential), Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        isTransactionCompleted = true;
        if (transaction.getTransactionType() == TransactionType.SYNC) {
            Log.e("TRA", "### transactionCompleted if");
            //    check the status of synchronous transaction
            Log.e("resourcePath", "" + resourcePath);
//            new GetPAY_STS().execute(resourcePath);
        } else {
            Log.e("MAIN", "### transactionCompleted else");
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(transaction.getRedirectUrl()));
            startActivity(browserIntent);
            Log.e("resourcePath", "" + resourcePath);
        }
    }

    @Override
    public void transactionFailed(Transaction transaction, PaymentError paymentError) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), getString(R.string.cardCredential), Toast.LENGTH_SHORT).show();
            }
        });
        Log.e("MAIN", "### transactionFailed :: " + paymentError.getErrorCode());
        Log.e("MAIN", "### getErrorInfo  " + paymentError.getErrorInfo());
        Log.e("MAIN", "### getErrorMessage " + paymentError.getErrorMessage());
    }

    private class GetPAY_STS extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... voids) {
//            requestPayment_STS(voids[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private void showConfirmationDialog(String amount, String currency) {
        if (!((Activity) mContext).isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(getResources().getString(R.string.paymentpay));
            builder.setMessage(getResources().getString(R.string.payment_sure));
            builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Do Something
                    pay(checkoutId);
                }
            });
            builder.setNegativeButton(getResources().getString(R.string.cancel), null);
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
            //  dialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
            //  dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
        }
    }

    /*private String requestPayment_STS(String resourcePath) {
        Log.e("MAIN", "### requestPayment_STS resourcePath :: " + resourcePath);
        StringBuilder total = new StringBuilder("");
        String paymentStatus = null;
        try {
            URL url = new URL(Consts.getInstance().urlOppwa + resourcePath +
                    "?authentication.userId=" + Consts.getInstance().USERID_PAYMENT +
                    "&authentication.password=" + Consts.getInstance().PASS_PAYMENT +
                    "&authentication.entityId=" + Consts.getInstance().ENTITY_PAYMENT);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            InputStream is;

            if (responseCode >= 400) is = conn.getErrorStream();
            else is = conn.getInputStream();

            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("PAY_STS", "### PAY_STS total :: " + total.toString());
        return paymentStatus;
    }*/

    private void pay(String checkoutId) {
        Log.e("MAIN", "### pay");
        try {
            String[] date = edt_date.getText().toString().split("/");
            PaymentParams paymentParams;
            paymentParams = new CardPaymentParams(
                    checkoutId,
                    selectedCardType,
                    edt_cardNumber.getText().toString().replace("-", ""),
                    edt_holderName.getText().toString().toLowerCase(),
                    date[0],
                    date[1],
                    edt_cvv.getText().toString()
            );
            Log.e("MAIN", "### paymentParams.toString " + paymentParams.toString());
            Transaction transaction = new Transaction(paymentParams);
            providerBinder.submitTransaction(transaction);
        } catch (PaymentException e) {
            Log.e("MAIN", "### PaymentException ::  " + e.getMessage());
            Log.e("MAIN", "### e.getError().getErrorMessage() ::  " + e.getError().getErrorMessage());
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /* we have a connection to the service */
            providerBinder = (IProviderBinder) service;
            providerBinder.addTransactionListener(PaymentActivity.this);

            try {
                providerBinder.initializeProvider(Connect.ProviderMode.LIVE);
            } catch (PaymentException ee) {
                Log.e("MAIN", "### serviceConnection ee " + ee.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            providerBinder = null;
        }
    };

    private void requestCheckoutInfo(String checkoutId) {
        if (providerBinder != null) {
            try {
                Log.e("CDA", "## requestCheckoutInfo checkoutId :: " + checkoutId);
                providerBinder.requestCheckoutInfo(checkoutId);
            } catch (Exception e) {
                Log.e("response", "*** error requestCheckoutInfo :: " + e.getMessage());
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(mContext, ConnectService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        Log.e("MAIN", "### onStop");
        super.onStop();
        unbindService(serviceConnection);
        stopService(new Intent(mContext, ConnectService.class));
    }

    @Override
    protected void onResume() {
        Log.e("response", "*** onResume PaymentAct");
        if (isTransactionCompleted) {
            if (Consts.isNetworkAvailable(mContext)) {
                paymentHyperPayReturnApi();
                isTransactionCompleted = false;
            } else {
                Consts.getInstance().Act_vity = "paymentactivity";
                Intent intent = new Intent(mContext, ReloadActivity.class);
                startActivity(intent);
            }
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(PaymentActivity.this, OrderSummeryActivity.class);
        i.putExtra("OrderID", OrderID);
        startActivity(i);
        finish();
        super.onBackPressed();
    }
}
