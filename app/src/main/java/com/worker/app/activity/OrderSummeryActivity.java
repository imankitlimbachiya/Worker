package com.worker.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.libraries.places.api.Places;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.PlaceBuffer;
//import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.worker.app.BaseActivity;
import com.worker.app.R;
import com.worker.app.adapter.PlaceArrayAdapter;
import com.worker.app.model.FacilityModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyButton;
import com.worker.app.utility.MyEditText;
import com.worker.app.utility.MyTextView;
import com.worker.app.utility.Utils;
import com.google.android.libraries.places.api.net.PlacesClient;

public class OrderSummeryActivity extends BaseActivity implements View.OnClickListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnMapLongClickListener,
        LocationListener {

    private long lastClickTime = 0;
    public static SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private Geocoder geocoder;
    private AutoCompleteTextView autoCompleteSearch;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private GoogleApiClient googleApiClient;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 10;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private FusedLocationProviderClient mFusedLocationClient;
    Double WorkerContractTotal, TaxPer, TaxAmount, Total, addOnTotal = 0.0, Discount = 0.0, REQUEST_CODE = 0.0, REQUEST_LOCATION = 199.0;
    int WorkerCount;
    Double TotalWalletAmount = 0.0, UsedWalletAmount = 0.0;
    double latitude = 0.0, longitude = 0.0;
    double worklat = 0.0, worklong = 0.0;
    GoogleMap.OnCameraIdleListener onCameraIdleListener;
    RelativeLayout rel_forgot, rel_drop_location, rel_work_location;
    ImageView image_gone;
    MyButton btnClose;
    MyTextView txt_total, txt_tax, txt_discount, addOnService, contract_total, txt_WalletAmount, txt_totalwalletamount, txt_description, txt_price, txt_title, btn_coupon;
    String Type = "", country = "", CountryCode, PostalCode = "", locality, OrderID, CouponID = "", coupon = "", City, District, State;
    ArrayList<FacilityModel> listFacility = new ArrayList<FacilityModel>();
    RecyclerView recycler_facility;
    ProgressBar progress;
    SharedPreferences preferences, preferences_Login_Data;
    JSONArray array_AddOnFacility;
    ArrayList<String> check = new ArrayList<>();
    MyButton btn_pay, btn_ok;
    ImageView plus, img_xml_center_marker;
    LinearLayout lin_map, relativeMapParent, lin_apply, lin_work_map;
    Context mContext;
    ScrollView scroll_main;
    MyEditText edt_apply, edt_coupon;
    private MyTextView edt_work_address, edt_address;
    //    GPSTrackernew gps;
    LatLng newCurrentLatLong;

    //new
    private boolean isCheckGPS = false;
    private AlertDialog dialogGps;
    private final int PERMISSION_ALL = 15;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 280000;  /* 280 secs */
    private long FASTEST_INTERVAL = 120000; /* 120 secs */
    //    private Location mLocation;
    private LocationCallback mLocationCallback;
    private SettingsClient mSettingsClient;
    private Location mCurrentLocation;
    protected LocationSettingsRequest mLocationSettingsRequest;
    private static final int REQUEST_CHECK_SETTINGS = 214;
    private static final int REQUEST_ENABLE_GPS = 516;
    private boolean isFirstTime = false;
    private PlacesClient placesClient;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        mContext = this;

        String apiKey = getString(R.string.googleAPIKey);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Retrieve a PlacesClient (previously initialized - see MainActivity)
        placesClient = Places.createClient(this);

        Init();

        geocoder = new Geocoder(mContext);
        // gps = new GPSTrackernew(mContext);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Consts.getInstance().isNetworkAvailable(mContext)) {

            InitializeMap();

            bindPlacesAdapter();

            String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            if (hasPermissions(this, PERMISSIONS)) {
                Log.e("DLS", "## if displayLastLocationIfSet");
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            } else {
                buildGoogleApiClient();
            }

            setUserLocation();

            order_step_3_list();

        } else {
            Consts.getInstance().Act_vity = "OrderSummery";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
    }

    private void bindPlacesAdapter() {
        mPlaceArrayAdapter = new PlaceArrayAdapter(OrderSummeryActivity.this, android.R.layout.simple_list_item_1);
        autoCompleteSearch.setAdapter(mPlaceArrayAdapter);
        autoCompleteSearch.setOnItemClickListener(mAutocompleteClickListener);
        autoCompleteSearch.setThreshold(2);
        /*mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1, BOUNDS_MOUNTAIN_VIEW, null);
        autoCompleteSearch.setAdapter(mPlaceArrayAdapter);
        newCurrentLatLong = getLatLongFromPlace(autoCompleteSearch.getText().toString());
        autoCompleteSearch.setThreshold(2);
        autoCompleteSearch.setOnItemClickListener(mAutocompleteClickListener);*/
    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
        }
        return false;
    }

    private void Init() {
        plus = findViewById(R.id.plus);
        plus.setVisibility(View.GONE);
        btn_pay = findViewById(R.id.btn_pay);
        btn_ok = findViewById(R.id.btn_ok);
        btn_coupon = findViewById(R.id.btn_coupon);
        lin_map = findViewById(R.id.lin_map);
        lin_work_map = findViewById(R.id.lin_work_map);
        edt_address = findViewById(R.id.edt_address);
        edt_work_address = findViewById(R.id.edt_work_address);
        img_xml_center_marker = findViewById(R.id.img_xml_center_marker);
        scroll_main = findViewById(R.id.scroll_main);
        relativeMapParent = findViewById(R.id.relativeMapParent);
        edt_coupon = findViewById(R.id.edt_coupon);
        btn_pay.setOnClickListener(this);
        lin_map.setOnClickListener(this);
        lin_work_map.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_coupon.setOnClickListener(this);
        recycler_facility = findViewById(R.id.recycler_facility);
        progress = findViewById(R.id.progress);
        addOnService = findViewById(R.id.addOnService);
        contract_total = findViewById(R.id.contract_total);
        txt_discount = findViewById(R.id.txt_discount);
        txt_tax = findViewById(R.id.txt_tax);
        txt_total = findViewById(R.id.txt_total);
        txt_WalletAmount = findViewById(R.id.txt_WalletAmount);
        edt_apply = findViewById(R.id.edt_apply);
        lin_apply = findViewById(R.id.lin_apply);
        txt_totalwalletamount = findViewById(R.id.txt_totalwalletamount);
        lin_apply.setOnClickListener(this);
        scroll_main.setVisibility(View.VISIBLE);
        btn_pay.setVisibility(View.GONE);
        autoCompleteSearch = findViewById(R.id.autoCompleteSearch);
        /*autoCompleteSearch.setThreshold(2);
        autoCompleteSearch.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1, BOUNDS_MOUNTAIN_VIEW, null);
        autoCompleteSearch.setAdapter(mPlaceArrayAdapter);
        newCurrentLatLong = getLatLongFromPlace(autoCompleteSearch.getText().toString());*/

        txt_discount.setText(String.format(Locale.ENGLISH, "%.2f", Discount));
        txt_WalletAmount.setText(String.format(Locale.ENGLISH, "%.2f", UsedWalletAmount));
        txt_description = findViewById(R.id.txt_description);
        image_gone = findViewById(R.id.image_gone);
        rel_forgot = findViewById(R.id.rel_forgot);
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);
        txt_price = findViewById(R.id.txt_price);
        txt_title = findViewById(R.id.txt_title);
        rel_drop_location = findViewById(R.id.rel_drop_location);
        rel_work_location = findViewById(R.id.rel_work_location);
        rel_drop_location.setVisibility(View.GONE);

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);
        OrderID = getIntent().getExtras().getString("OrderID");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pay:
                //to avoid multiple clicks
                if (SystemClock.elapsedRealtime() - lastClickTime < Consts.getInstance().ClickTimeSeconds) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();

                if (check.contains("1")) {
                    if (edt_address.getText().toString().equals("") || edt_address.getText().toString().equals("null") || edt_address.getText().toString().equals(null) || edt_address.getText().toString() == null) {
                        Toast.makeText(mContext, R.string.validaddress, Toast.LENGTH_LONG).show();
                    } else if (edt_work_address.getText().toString().equals("") || edt_work_address.getText().toString().equals("null") || edt_work_address.getText().toString().equals(null) || edt_work_address.getText().toString() == null) {
                        Toast.makeText(mContext, R.string.validworkaddress, Toast.LENGTH_LONG).show();
                    } else {
                        if (Consts.getInstance().isNetworkAvailable(mContext)) {
                            updateOrderApi();
                        } else {
                            Consts.getInstance().Act_vity = "Ordersummery";
                            Intent intent = new Intent(mContext, ReloadActivity.class);
                            startActivity(intent);
                        }
                    }
                } else {
                    latitude = 0.0;
                    longitude = 0.0;
                    edt_address.setText("");
                    if (edt_work_address.getText().toString().equals("") || edt_work_address.getText().toString().equals("null") || edt_work_address.getText().toString().equals(null) || edt_work_address.getText().toString() == null) {
                        Toast.makeText(OrderSummeryActivity.this, R.string.validworkaddress, Toast.LENGTH_LONG).show();
                    } else {
                        if (Consts.getInstance().isNetworkAvailable(mContext)) {
                            updateOrderApi();
                        } else {
                            Consts.getInstance().Act_vity = "Ordersummery";
                            Intent intent = new Intent(mContext, ReloadActivity.class);
                            startActivity(intent);
                        }
                    }
                }
                break;
            case R.id.lin_map:
                img_xml_center_marker.setVisibility(View.VISIBLE);
                scroll_main.setVisibility(View.GONE);
                btn_pay.setVisibility(View.GONE);
                relativeMapParent.setVisibility(View.VISIBLE);
                btn_ok.setVisibility(View.VISIBLE);
                edt_address.setText("");
                Type = "1";
                /*if (gps.canGetLocation()) {
                    Log.e("** Ordersum", "** canGetLocation");
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    setSearchLocation(new LatLng(latitude, longitude));
                    getAddressDetails(new LatLng(latitude, longitude));
                }*/
                break;
            case R.id.lin_work_map:
//                canSetAddress = true;
                img_xml_center_marker.setVisibility(View.VISIBLE);
                scroll_main.setVisibility(View.GONE);
                btn_pay.setVisibility(View.GONE);
                relativeMapParent.setVisibility(View.VISIBLE);
                btn_ok.setVisibility(View.VISIBLE);
//                edt_work_address.setText("");
                Type = "0";
                /*if (gps.canGetLocation()) {
                    Log.e("** Ordersum", "** canGetLocation");
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    setSearchLocation(new LatLng(latitude, longitude));
                    getAddressDetails(new LatLng(latitude, longitude));
                }*/
                break;
            case R.id.btn_ok:
                img_xml_center_marker.setVisibility(View.GONE);
                scroll_main.setVisibility(View.VISIBLE);
                btn_pay.setVisibility(View.VISIBLE);
                relativeMapParent.setVisibility(View.GONE);
                btn_ok.setVisibility(View.GONE);
                if (Type.equals("1")) {
                    edt_address.setText(locality);
                } else {
                    edt_work_address.setText(locality);
                }
//                autoCompleteSearch.setText(locality);
                break;
            case R.id.lin_apply:
                if (edt_apply.getText().toString().equals("") || edt_apply.getText().toString().equals("null")) {
                    Toast.makeText(mContext, R.string.usedwalletamount, Toast.LENGTH_LONG).show();
                } else if (edt_apply.getText().toString().equals(".")) {
                    Toast.makeText(mContext, R.string.walValidation, Toast.LENGTH_LONG).show();
                } else if (Double.parseDouble(edt_apply.getText().toString()) > Total) {
                    Toast.makeText(mContext, R.string.walValidationM, Toast.LENGTH_LONG).show();
                } else {
                    Utils.getInstance().hideKeyboard(OrderSummeryActivity.this);
                    walletAmount();
                }
                break;
            case R.id.btnClose:
                rel_forgot.setVisibility(View.GONE);
                break;
            case R.id.btn_coupon:
                if (btn_coupon.getText().toString().equals(getResources().getString(R.string.couponRemove))) {
                    btn_coupon.setText(getResources().getString(R.string.apply));

                    coupon = "";
                    CouponID = "";
                    Discount = 0.0;

                    reCalculateAmount("");

                    edt_coupon.setText("");
                    edt_coupon.setEnabled(true);
                    Toast.makeText(mContext, R.string.removeCoupon, Toast.LENGTH_LONG).show();
                } else {
                    coupon = edt_coupon.getText().toString();
                    if (coupon.equals("") || coupon.equals("null") || coupon.equals(null) || coupon == null) {
                        Toast.makeText(mContext, R.string.applycoupon, Toast.LENGTH_LONG).show();
                    } else {
                        if (UsedWalletAmount != 0.0) {//wallet is applied
                            if (WorkerContractTotal - UsedWalletAmount > 0) {
                                applyCouponApi();
                            } else {
                                Toast.makeText(mContext, getString(R.string.cpn1), Toast.LENGTH_LONG).show();
                            }
                        } else
                            applyCouponApi();
                    }
                }
                break;
        }
    }

    private void reCalculateAmount(String messsge) {
        TaxAmount = WorkerContractTotal * TaxPer / 100; //calculate tax from tax percentage
        txt_tax.setText(String.valueOf(TaxAmount));

        Log.e("OSA", "## reCalculateAmount  coupon : " + coupon);
        //check if wallet is applied
        if (UsedWalletAmount != 0.0) {//wallet is applied
            //check if coupon is added
            if (!coupon.equals("")) {
                if (Discount != 0.0) {
                    Log.e("OSA", "## Discount < WorkerContractTotal");
                    applyCouponAmount(messsge, false);
                }
            } else {
                Total = (WorkerContractTotal + TaxAmount + addOnTotal) - UsedWalletAmount;
                contract_total.setText(String.valueOf(WorkerContractTotal));
                txt_tax.setText(String.valueOf(TaxAmount));
                txt_total.setText(String.format(Locale.ENGLISH, "%.2f", Total));
                txt_discount.setText("00.0");
            }
        } else {
            //check if coupon is added
            if (!coupon.equals("")) {
                if (Discount != 0.0) {
                    Log.e("OSA", "## Discount < WorkerContractTotal");
                    applyCouponAmount(messsge, false);
                    /*if (Discount < WorkerContractTotal) {
                        Log.e("OSA", "## Discount < WorkerContractTotal");
                        applyCouponAmount(messsge,false);
                    } else {
                        btn_coupon.setEnabled(true);
                        Toast.makeText(mContext, R.string.couponalert, Toast.LENGTH_LONG).show();
                    }*/
                }
            } else {
                Total = WorkerContractTotal + TaxAmount + addOnTotal;
                contract_total.setText(String.valueOf(WorkerContractTotal));
                txt_tax.setText(String.valueOf(TaxAmount));
                txt_total.setText(String.format(Locale.ENGLISH, "%.2f", Total));
                txt_discount.setText("00.0");
            }
        }
    }

    private void applyCouponAmount(String message, boolean fromWallet) {
        if (!fromWallet) {
            edt_coupon.setEnabled(false);
            txt_discount.setText(String.format(Locale.ENGLISH, "%.2f", Discount));
            btn_coupon.setText(getResources().getString(R.string.couponRemove));
        }
        Log.e("OSA", "## WorkerContractTotal : " + WorkerContractTotal);
        Log.e("OSA", "## Discount : " + Discount);
        Log.e("OSA", "## TaxAmount : " + TaxAmount);
        Log.e("OSA", "## addOnTotal : " + addOnTotal);
        Log.e("OSA", "## UsedWalletAmount : " + UsedWalletAmount);
        //total till now
        if (Discount < WorkerContractTotal) {
            Total = ((WorkerContractTotal - Discount) + TaxAmount + addOnTotal) - UsedWalletAmount;
        } else
            Total = (TaxAmount + addOnTotal) - UsedWalletAmount;

        txt_total.setText(String.format(Locale.ENGLISH, "%.2f", Total));

        if (!message.equals("")) {
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }
    }

    public void walletAmount() {
        //check for wallet
        double applyAmount;
        String apply = edt_apply.getText().toString();
        if (apply.equals("")) {
            //no wallet applied
            txt_total.setText(String.format(Locale.ENGLISH, "%.2f", Total));
        } else {
            applyAmount = Double.parseDouble(apply);
            if (applyAmount > TotalWalletAmount) {
                Toast.makeText(mContext, R.string.walMsg, Toast.LENGTH_LONG).show();
            } else if (applyAmount > Total) {
                Toast.makeText(mContext, R.string.walMsg1, Toast.LENGTH_LONG).show();
            } else {
                //apply wallet amounts
                UsedWalletAmount = UsedWalletAmount + applyAmount;
                TotalWalletAmount = TotalWalletAmount - applyAmount;

                txt_WalletAmount.setText("-" + String.format(Locale.ENGLISH, "%.2f", UsedWalletAmount));
                edt_apply.setText("");
                txt_totalwalletamount.setText("(" + String.format(Locale.ENGLISH, "%.2f", TotalWalletAmount) + " SAR)");

//                    Toast.makeText(mContext, R.string.wallet_success, Toast.LENGTH_LONG).show();

                applyCouponAmount(getString(R.string.wallet_success), true);
            }
        }
    }

    private void applyCouponApi() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().APPLY_COUPON, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        try {
                            JSONObject json_main = new JSONObject(response);
                            Log.e("** response", "" + Consts.getInstance().APPLY_COUPON + json_main);
                            if (json_main.getString("success").equalsIgnoreCase("True")) {
                                progress.setVisibility(View.GONE);
                                JSONObject object_Coupon = json_main.getJSONObject("Coupon");
                                CouponID = object_Coupon.getString("CouponID");
                                coupon = object_Coupon.getString("CouponCode");
                                String DiscountType = object_Coupon.getString("DiscountType");
//                                String DiscountTypeName = object_Coupon.getString("DiscountTypeName");
                                Discount = Double.parseDouble(object_Coupon.getString("Discount"));
                                if (DiscountType.equals("1")) {
                                    reCalculateAmount(json_main.getString("message"));
                                } else {
                                    Toast.makeText(mContext, json_main.getString("message"), Toast.LENGTH_LONG).show();
                                    Discount = (double) ((WorkerContractTotal - UsedWalletAmount) * Discount) / 100;

                                    reCalculateAmount(json_main.getString("message"));
                                }
                            } else {
                                progress.setVisibility(View.GONE);
                                btn_coupon.setEnabled(true);
                                Toast.makeText(mContext, json_main.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            progress.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                });

            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e("** error", "" + error.toString());
                progress.setVisibility(View.GONE);
            }
        }) {
            protected Map<String, String> getParams() {   //Post
                Map<String, String> params = new HashMap<String, String>();///params put, not used in get method
                params.put("language", preferences.getString("language", ""));
                params.put("CouponCode", edt_coupon.getText().toString());
                params.put("UserID", preferences_Login_Data.getString("UserID", ""));
                Log.e("** params", Consts.getInstance().APPLY_COUPON + params);
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
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().APPLY_COUPON);///cache remove
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);//manage request
    }

    private void order_step_3_list() {
        String tag_string_req = "req";
        listFacility.clear();
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().order_step_3, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                Log.e("** response", "" + Consts.getInstance().order_step_3 + response);
                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            btn_pay.setVisibility(View.VISIBLE);
                            JSONObject json_main = new JSONObject(response);
                            if (json_main.getString("success").equalsIgnoreCase("True")) {
                                WorkerContractTotal = Double.parseDouble(json_main.getString("WorkerContractTotal"));
                                WorkerCount = Integer.parseInt(json_main.getString("WorkerCount"));
                                TaxPer = Double.parseDouble(json_main.getString("Tax"));

                                TotalWalletAmount = Double.parseDouble(json_main.getString("WalletAmount"));
                                txt_totalwalletamount.setText("(" + String.format(Locale.ENGLISH, "%.2f", TotalWalletAmount) + " SAR)");
                                reCalculateAmount("");

                                array_AddOnFacility = json_main.getJSONArray("AddOnFacility");
                                for (int i = 0; i < array_AddOnFacility.length(); i++) {
                                    int cost = Integer.parseInt(array_AddOnFacility.getJSONObject(i).getString("Cost")) * WorkerCount;
                                    FacilityModel model = new FacilityModel();
                                    model.setTitle(array_AddOnFacility.getJSONObject(i).getString("Title"));
                                    model.setCost(String.valueOf(cost));
                                    model.setFacilityID(array_AddOnFacility.getJSONObject(i).getString("AddOnFacilityID"));
                                    model.setDescription(array_AddOnFacility.getJSONObject(i).getString("Description"));
                                    listFacility.add(model);
                                }
                                if (listFacility.size() > 0) {
                                    ListFacilityAdapter mAdapter = new ListFacilityAdapter(OrderSummeryActivity.this, listFacility);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                                    recycler_facility.setLayoutManager(mLayoutManager);
                                    recycler_facility.setItemAnimator(new DefaultItemAnimator());
                                    recycler_facility.setAdapter(mAdapter);
                                }
                            }
                        } catch (Exception e) {
                            progress.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e("** error", "" + error.toString());
                progress.setVisibility(View.GONE);
            }
        }) {
            protected Map<String, String> getParams() {   //Post
                Map<String, String> params = new HashMap<String, String>();///params put, not used in get method
                params.put("language", preferences.getString("language", ""));
                params.put("OrderID", OrderID);
                params.put("UserID", preferences_Login_Data.getString("UserID", ""));
                Log.e("** params", Consts.getInstance().order_step_3 + params);
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
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().order_step_3);///cache remove
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);//manage request
    }

    private void updateOrderApi() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().UPDATE_ORDER, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.setVisibility(View.GONE);
                            JSONObject json_main = new JSONObject(response);
                            Log.e("** response", "" + Consts.getInstance().UPDATE_ORDER + json_main);
                            if (json_main.getString("success").equalsIgnoreCase("True")) {
                                Intent intent = new Intent(mContext, PaymentActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("merchantInvoiceId", json_main.getString("uniq_code"));
                                intent.putExtra("City", City);
                                intent.putExtra("country", country);
                                intent.putExtra("locality", locality);
                                intent.putExtra("PostalCode", PostalCode);
                                intent.putExtra("CountryCode", CountryCode);
                                intent.putExtra("District", District);
                                intent.putExtra("State", State);
                                intent.putExtra("amount", String.valueOf(Total));
                                intent.putExtra("OrderID", OrderID);
                                intent.putExtra("TotalWalletAmount", String.valueOf(TotalWalletAmount));
                                intent.putExtra("UsedWalletAmount", String.valueOf(UsedWalletAmount));
                                mContext.startActivity(intent);
                            }
                        } catch (Exception e) {
                            progress.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                });

            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e("** error", "" + error.toString());
                progress.setVisibility(View.GONE);
            }
        }) {
            protected Map<String, String> getParams() {   //Post
                String AddOnFacility = "";
                for (int i = 0; i < check.size(); i++) {
                    if (AddOnFacility.equals("")) {
                        AddOnFacility = check.get(i);
                    } else {
                        AddOnFacility = AddOnFacility + "," + check.get(i);
                    }
                }
                Map<String, String> params = new HashMap<String, String>();///params put, not used in get method
                params.put("language", preferences.getString("language", ""));
                params.put("OrderID", OrderID);
                params.put("ContractFees", String.valueOf(WorkerContractTotal));
                params.put("Address", edt_address.getText().toString());
                params.put("Long", String.valueOf(longitude));
                params.put("Lat", String.valueOf(latitude));
                params.put("Total", String.valueOf(Total));
                params.put("Discount", String.valueOf(Discount));
                params.put("AddOnsTotal", String.valueOf(addOnTotal));
                params.put("CouponID", CouponID);
                params.put("CouponCode", coupon);
                params.put("Tax", String.valueOf(TaxAmount));
                params.put("AddOnFacilityID", AddOnFacility);
                params.put("WorkLocationAddress", edt_work_address.getText().toString());
                params.put("WorkLocationLat", String.valueOf(worklat));
                params.put("WorkLocationLong", String.valueOf(worklong));
                Log.e("** params", Consts.getInstance().UPDATE_ORDER + params);
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
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().UPDATE_ORDER);///cache remove
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);//manage request
    }

    public class ListFacilityAdapter extends RecyclerView.Adapter<ListFacilityAdapter.MyViewHolder> {

        private List<FacilityModel> listSlider;
        private Context mContext;
        int sum;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public ImageView radio_btn;
            private MyTextView facilityTiltle, txt_cost, txt_moreInfo;
            RelativeLayout linear_title;

            public MyViewHolder(View view) {
                super(view);

                radio_btn = view.findViewById(R.id.radio_btn);
                facilityTiltle = view.findViewById(R.id.facilityTiltle);
                txt_cost = view.findViewById(R.id.txt_cost);
                linear_title = view.findViewById(R.id.linear_title);
                txt_moreInfo = view.findViewById(R.id.txt_moreInfo);
            }
        }

        public ListFacilityAdapter(Context mContext, List<FacilityModel> listSlider) {
            this.mContext = mContext;
            this.listSlider = listSlider;

        }

        @Override
        public ListFacilityAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_add_facilities, parent, false);

            return new ListFacilityAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            FacilityModel model = listSlider.get(position);
            if (model.getTitle().equals("") || model.getTitle().equals("null") || model.getTitle().equals(null) || model.getTitle() == null) {
                holder.facilityTiltle.setText("-");
            } else {
                holder.facilityTiltle.setText(model.getTitle());
            }
            if (model.getCost().equals("") || model.getCost().equals("null") || model.getCost().equals(null) || model.getCost() == null) {
                holder.txt_cost.setText("-");
            } else {
                holder.txt_cost.setText(model.getCost());
            }

            if (check.contains(model.getFacilityID())) {
                holder.radio_btn.setBackgroundResource(R.drawable.round);
            } else {
                holder.radio_btn.setBackgroundResource(R.drawable.round_unselect);
            }

            holder.txt_moreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txt_title.setText(model.getTitle());
                    txt_description.setText(model.getDescription());
                    txt_price.setText(model.getCost() + " SAR");
                    String text = "<font color='#767877'>" + model.getDescription() + "</font>" + "<b><font color='#2969cb'> " + "</b></font>";
                    txt_description.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
                    rel_forgot.setVisibility(View.VISIBLE);
                }
            });

            holder.linear_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sum = 0;
                    if (check.contains(model.getFacilityID())) {
                        check.remove(model.getFacilityID());
                        addOnTotal = addOnTotal - Integer.parseInt(model.getCost());
                        Total = Total - Integer.parseInt(model.getCost());
                        holder.radio_btn.setBackgroundResource(R.drawable.round_unselect);

                    } else {
                        Log.e("OSa", "## model.getCost() :: " + model.getCost());
                        Log.e("OSa", "## addOnTotal :: " + addOnTotal);
                        check.add(model.getFacilityID());
                        addOnTotal = addOnTotal + Integer.parseInt(model.getCost());
                        Total = Total + Integer.parseInt(model.getCost());
                        Log.e("OSa", "## Total :: " + Total);
                        holder.radio_btn.setBackgroundResource(R.drawable.round);
                    }

                    if (check.contains("1")) {
                        rel_drop_location.setVisibility(View.VISIBLE);
                    } else {
                        rel_drop_location.setVisibility(View.GONE);
                    }
                    addOnService.setText(String.valueOf(addOnTotal));
                    txt_total.setText(String.format(Locale.ENGLISH, "%.2f", Total));
                }
            });
        }

        @Override
        public int getItemCount() {
            return listSlider.size();
        }
    }

    public void showSettingsDialog() {
        Log.e("** order", "** showSettingsDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getResources().getString(R.string.gps));
        builder.setMessage(getResources().getString(R.string.gpsalert));
        builder.setPositiveButton(getResources().getString(R.string.gpsetting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do Something
                // dialog.cancel();
                isCheckGPS = true;
                // openSettings(context);
                openSettingsNew();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.gpcancel), null);
        builder.setCancelable(false);
        dialogGps = builder.create();
        dialogGps.show();
        // dialogGps.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
        // dialogGps.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
    }

    public void openSettingsNew() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        // startActivity(getIntent());
    }

    private void InitializeMap() {
        try {
            mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment));
            mapFragment.getMapAsync(this);
            // configureCameraIdle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(map.MAP_TYPE_TERRAIN);
        if (newCurrentLatLong != null) {
            /*googleMap.addMarker(new MarkerOptions()
                    .position(newCurrentLatLong)
                    .title(txt_map_location.getText().toString()));*/
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newCurrentLatLong, 16));
        }
        /*if (ActivityCompat.checkSelfPermission(OrderSummeryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OrderSummeryActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }*/
        googleMap.setOnCameraIdleListener(this);
        // checkLocationPermission();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // mPlaceArrayAdapter.setGoogleApiClient(googleApiClient);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();

        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.e(TAG, "GPS Success");
                        requestLocationUpdate();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException rae = (ResolvableApiException) e;
                            rae.startResolutionForResult(OrderSummeryActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sie) {
                            Log.e(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e(TAG, "Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
                }
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Log.e(TAG, "checkLocationSettings -> onCanceled");
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (!isFirstTime) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (mCurrentLocation != null) {
                getAddressDetails(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                // latLng.setText("Latitude : " + mLocation.getLatitude() + " , Longitude : " + mLocation.getLongitude());
            }
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // mPlaceArrayAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onCameraIdle() {
        Log.e("** act", "** onCameraIdle latitude : " + latitude);
        if (latitude != 0.0 && longitude != 0.0) {
            LatLng latLng = googleMap.getCameraPosition().target;
            try {
                Log.e("** act", "** onCameraIdle");
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                if (addressList != null && addressList.size() > 0) {
                    locality = addressList.get(0).getAddressLine(0);
                    PostalCode = addressList.get(0).getPostalCode();
                    country = addressList.get(0).getCountryName();
                    CountryCode = addressList.get(0).getCountryCode();
                    City = addressList.get(0).getLocality();
                    District = addressList.get(0).getSubAdminArea();
                    State = addressList.get(0).getAdminArea();
                    Log.e("** PostalCode", "" + PostalCode);
                    Log.e("** country", "" + country);
                    Log.e("** CountryCode", "" + CountryCode);
                    Log.e("** City", "" + City);
                    Log.e("** District", "" + addressList.get(0).getSubAdminArea());
                    Log.e("** State", "" + addressList.get(0).getAdminArea());
                    if (Type.equals("1")) {
                        edt_address.setText(locality);
                    } else {
                        edt_work_address.setText(locality);
                    }
                    if (locality != null && country != null) {
                        if (!locality.isEmpty() && !country.isEmpty()) {
                            autoCompleteSearch.setText(locality);
                        }
                    }
                }
            } catch (IOException e) {
                Log.e("** City", "** IOException  " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Enable Permissions", Toast.LENGTH_LONG).show();
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback, Looper.myLooper());
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        googleMap.clear();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 16));
        Geocoder geocoder = new Geocoder(OrderSummeryActivity.this);
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                String locality = addressList.get(0).getAddressLine(0);
                String country = addressList.get(0).getCountryName();
                if (!locality.isEmpty() && !country.isEmpty()) {
                    Log.e("** locality", "** bff onMapLongClick autocomplet " + locality);
                    autoCompleteSearch.setText(locality);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "permission granted", Toast.LENGTH_LONG).show();

                Log.e("** order", "** PERMISSION_ALL granted ");
                buildGoogleApiClient();
                displayLastLocationIfSet();
            } else {
                Log.e("** order", "** RESULT_CANCELED dialogGps ");
                if (dialogGps == null) {
                    showSettingsDialog();
                }
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        /*.addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)*/
//                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
        /*googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();*/
        connectGoogleClient();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
//                Log.e(TAG, "## onLocationResult isFirstTime :: " + isFirstTime);
                if (!isFirstTime) {

                    mCurrentLocation = locationResult.getLastLocation();

                    getAddressDetails(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                    onLocationChanged(mCurrentLocation);
                }
            }
        };
    }

    private void connectGoogleClient() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS) {
            googleApiClient.connect();
            // Toast.makeText(mContext, "googleApiClient connected", Toast.LENGTH_SHORT).show();
        } else {
            int REQUEST_GOOGLE_PLAY_SERVICE = 988;
            googleAPI.getErrorDialog(this, resultCode, REQUEST_GOOGLE_PLAY_SERVICE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            String placeId = null;
            if (item != null) {
                placeId = String.valueOf(item.placeId);
            }
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
            Task<FetchPlaceResponse> placeTask = placesClient.fetchPlace(request);
            placeTask.addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                @Override
                public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                    Utils.getInstance().hideKeyboard(OrderSummeryActivity.this);
                    // Utils.getInstance().showSnackBar(btnSelectAddress,""+fetchPlaceResponse.getPlace().getName());
                    Log.e(TAG, "## onSuccess getName" + fetchPlaceResponse.getPlace().getName());
                    Log.e(TAG, "## onSuccess getAddress" + fetchPlaceResponse.getPlace().getAddress());
                    Place placeResult = fetchPlaceResponse.getPlace();
                    if (placeResult.getLatLng() != null) {
                        Utils.getInstance().hideKeyboard(OrderSummeryActivity.this);
                        setSearchLocation(placeResult.getLatLng());
                    } else {
                        Toast.makeText(OrderSummeryActivity.this, "getting lat and long null", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "## onSuccess placeResult.getLatLng() is nulllllllllllllllllllllllllll");
                    }
                }
            });
            placeTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utils.getInstance().hideKeyboard(OrderSummeryActivity.this);
                    e.printStackTrace();
                    Log.e(TAG, "## onFailure : " + e.getMessage());
                    Utils.getInstance().showSnackBar(btn_coupon, "" + e.getMessage());
                }
            });
            /*String placeId = null;
            if (item != null) {
                placeId = String.valueOf(item.placeId);
            }
            if (item != null) {
                Log.i("CREATE", "Selected: " + item.description);
            }
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(googleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            if (item != null) {
                Log.i("CREATE", "Fetching details for ID: " + item.placeId);
            }*/
        }
    };

    /*private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            Utils.getInstance().hideKeyboard(OrderSummeryActivity.this);
            setSearchLocation(place.getLatLng());
        }
    };*/

    public LatLng getLatLongFromPlace(String place) {
        try {
            Geocoder selected_place_geocoder = new Geocoder(mContext);
            List<Address> address;
            address = selected_place_geocoder.getFromLocationName(place, 5);

            if (address == null) {
            } else {
                Address location = address.get(0);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                return latLng;
            }
        } catch (Exception e) {
            Log.e("** EEE ", "*** e.getMessage() :: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void setSearchLocation(LatLng location) {
        if (googleMap != null) {
            googleMap.clear();
            if (location != null) {

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(location, 16);
                googleMap.animateCamera(yourLocation);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdate() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("** Ordersum", "** onLocationChanged");
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        setUserLocation();

        if (latitude == 0.0 && longitude == 0.0) {
            requestLocationUpdate();
        } else {
            Log.e("DLS", "## location.getLatitude() " + location.getLatitude());
            Log.e("DLS", "## location.getLatitude() " + location.getLongitude());
            getAddressDetails(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    protected void onResume() {
        Log.e("** order", "** onResume isCheckGPS :::: " + isCheckGPS);
        if (isCheckGPS) {
            dialogGps.dismiss();

            String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            if (hasPermissions(this, PERMISSIONS)) {
                Log.e("DLS", "## if displayLastLocationIfSet");
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            } else {
                buildGoogleApiClient();
            }
            displayLastLocationIfSet();
            // setUserLocation();
        }
        super.onResume();
    }

    /*private void openGpsEnableSetting() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, REQUEST_ENABLE_GPS);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("** order", "** onActivityResult REQUEST_LOCATION");
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.e(TAG, "User allow to access location. Request Location Update");
                    buildGoogleApiClient();
                    requestLocationUpdate();
                    break;
                case Activity.RESULT_CANCELED:
                    Log.e(TAG, "User denied to access location.");
                    showSettingsDialog();
                    break;
            }
        } else if (requestCode == REQUEST_ENABLE_GPS) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGpsEnabled) {
                showSettingsDialog();
            } else {
                requestLocationUpdate();
            }
        }
        /*if (requestCode == REQUEST_LOCATION)
            switch (resultCode) {
                case Activity.RESULT_OK: {
                    // All required changes were successfully made
                    buildGoogleApiClient();
                    setAPI();
                    gps = new GPSTrackernew(mContext);
                    if (gps.canGetLocation()) {
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                        setSearchLocation(new LatLng(latitude, longitude));
                        getAddressDetails(new LatLng(latitude, longitude));
                    }

                    setUserLocation();
                    break;
                }
                case Activity.RESULT_CANCELED: {
                    Log.e("** order", "** RESULT_CANCELED ");
                    if (dialogGps == null) {
                        Log.e("** order", "** RESULT_CANCELED dialogGps ");
                        showSettingsDialog();
                    }
                    break;
                }
                default: {
                    break;
                }
            }*/
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getAddressDetails(LatLng latLng) {
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        Geocoder geocoder = new Geocoder(OrderSummeryActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Log.e("** Ordersum", "** getAddressDetails addresses :: " + addresses.get(0).getAddressLine(0));
                locality = addresses.get(0).getAddressLine(0);
                PostalCode = addresses.get(0).getPostalCode();
                country = addresses.get(0).getCountryName();
                CountryCode = addresses.get(0).getCountryCode();
                City = addresses.get(0).getLocality();
                District = addresses.get(0).getSubAdminArea();
                State = addresses.get(0).getAdminArea();
                Log.e("** PostalCode", "" + PostalCode);
                Log.e("** country", "" + country);
                Log.e("** CountryCode", "" + CountryCode);
                Log.e("** City", "" + City);
                Log.e("** District", "" + addresses.get(0).getSubAdminArea());
                Log.e("** State", "" + addresses.get(0).getAdminArea());
                if (Type.equals("1")) {
                    edt_address.setText(locality);
                } else {
                    edt_work_address.setText(locality);
                }
                if (autoCompleteSearch.getText().toString().equals("")) {
                    Log.e("** Ordersum", "** autoCompleteSearch.getText().toString() " + autoCompleteSearch.getText().toString());
                    autoCompleteSearch.setText(locality);
                }
            }
            //String country = addresses.get(0).getAddressLine(2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayLastLocationIfSet() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.e("DLS", "** onSuccess getLastLocation ");
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {
                            Log.e("DLS", "** location.getLatitude() " + location.getLatitude());
                            Log.e("DLS", "** location.getLatitude() " + location.getLongitude());
                            LatLng rochester = new LatLng(location.getLatitude(), location.getLongitude());

                            //MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.style_silver_mapr);
                            //mMap.setMapStyle(mapStyleOptions);
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(rochester));
                            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(rochester, 14);
                            googleMap.animateCamera(yourLocation);

                            getAddressDetails(rochester);
//                            mapAddressModel.setLat(String.valueOf(location.getLatitude()));
//                            mapAddressModel.setLong(String.valueOf(location.getLongitude()));
                        }
                    }
                });
    }

    private void setUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onSuccess(Location location) {
                        Log.e("** onSuccess", "*** onSuccess");
                        googleMap.clear();
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Geocoder geocoder = new Geocoder(OrderSummeryActivity.this);
                            try {
                                List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                if (addressList != null && addressList.size() > 0) {
                                    String locality = addressList.get(0).getAddressLine(0);
                                    String country = addressList.get(0).getCountryName();
                                    if (!locality.isEmpty() && !country.isEmpty()) {
                                        Log.e("** order", "** if getLastLocation locality not empty");
                                        autoCompleteSearch.setText("");
                                        if (!autoCompleteSearch.getText().toString().equals(""))
                                            autoCompleteSearch.setText(locality);
                                    }
                                }
                            } catch (IOException e) {
                                Log.e("** onSuccess", "*** IOException :: " + e.getMessage());
                                e.printStackTrace();
                            }
                            LatLng rochester = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(rochester));
                            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(rochester, 16);
                            googleMap.animateCamera(yourLocation);
                        }
                    }
                });
    }


    private void setAPI() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10);
        mLocationRequest.setSmallestDisplacement(10);
        mLocationRequest.setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new
                LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                resolvable.startResolutionForResult(OrderSummeryActivity.this, 101);
                            } catch (IntentSender.SendIntentException e) {
                            } catch (ClassCastException e) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }
                }
            }
        });
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    public boolean checkLocationPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                   /* if (dialogGps == null) {
                        Log.e("** order", "** checkLocationPermission ");
                        showSettingsDialog();
                    }*/
                } else {
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /*public boolean checkLocationPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Location Permission is necessary!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }*/

    private void enableLoc() {

         /*public void set() {
        try {
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
                enableLoc();
            }

            Log.e("** order", "** checkLocationPermission ");
            if (checkLocationPermission()) {
                buildGoogleApiClient();
                setAPI();
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }*/

/* @Override
    public void onMarkerDragEnd(Marker marker) {
        googleMap.clear();

        *//*googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude))
                .title(txt_map_location.getText().toString()).draggable(true));*//*

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), 14));

        Geocoder geocoder = new Geocoder(OrderSummeryActivity.this);
        try {
            List<Address> addressList = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                String locality = addressList.get(0).getAddressLine(0);
                String country = addressList.get(0).getCountryName();
                if (!locality.isEmpty() && !country.isEmpty()) {
                    Log.e("** locality", "** bff onMarkerDragEnd autocomplet " + locality);
                    autoCompleteSearch.setText(locality);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    /*@SuppressLint("LongLogTag")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e("** order", "** onRequestPermissionsResult ");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                Log.e("** order", "** onRequestPermissionsResult MY_PERMISSIONS_REQUEST_LOCATION if");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.e("** order", "** onRequestPermissionsResult if if granted");
                        set();

                        initilizeMap();

                        currentLatLong();

                        setUserLocation();
                    }
                } else {
                    Log.e("** order", "** RESULT_CANCELED dialogGps ");
                    if (dialogGps == null) {
                        showSettingsDialog();
                    }
                }
                // Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                break;
        }
    }*/
        /*if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        }
                    }).build();
            googleApiClient.connect();*/

            /*LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(OrderSummeryActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }*/
    }

    /*public void currentLatLong() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gps.canGetLocation()) {
                    Log.e("** Ordersum", "** canGetLocation currentLatLong");
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    setSearchLocation(new LatLng(latitude, longitude));
                    getAddressDetails(new LatLng(latitude, longitude));
                } else {
                    //this method is calling twice, so we need to check if it is already shown
                    *//*if (dialogGps == null) {
                        Log.e("** order", "** checkSelfPermission if else open showSettingsDialog :: ");
                        showSettingsDialog();
                    }*//*
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }
        } else {
            if (gps.canGetLocation()) {
                Log.e("** Ordersum", "** canGetLocation currentLatLong else");
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                setSearchLocation(new LatLng(latitude, longitude));
                getAddressDetails(new LatLng(latitude, longitude));
            } else {
                Log.e("** order", "** checkSelfPermission else os 6 showSettingsDialog");
                // gps.showSettingsAlert();
            }
        }
    }



    public void openSettings(Context context) {
        Activity myActivity = (Activity) context;
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        myActivity.startActivityForResult(intent, 101);
    }*/
}
