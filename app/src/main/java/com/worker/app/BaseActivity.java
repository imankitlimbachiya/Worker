package com.worker.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.worker.app.activity.AboutActivity;
import com.worker.app.activity.AgreementPolicyActivity;
import com.worker.app.activity.AnnouncementActivity;
import com.worker.app.activity.ContactUsActivity;
import com.worker.app.activity.Create_request_step_1;
import com.worker.app.activity.LanguageActivity;
import com.worker.app.activity.LoginActivity;
import com.worker.app.activity.MyOrderListingActivity;
import com.worker.app.activity.MyProfileActivity;
import com.worker.app.activity.MyRequestActivity;
import com.worker.app.activity.MyRequestListingActivity;
import com.worker.app.activity.MyWorkersActivity;
import com.worker.app.activity.NotificationActivity;
import com.worker.app.activity.SettingActivity;
import com.worker.app.activity.TermsConditionsActivity;
import com.worker.app.activity.WalletActivity;
import com.worker.app.adapter.DrawerAdapter;
import com.worker.app.model.DrawerModel;
import com.worker.app.network.network.APIService;
import com.worker.app.network.network.ApiClient;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyTextView;
import com.worker.app.utility.Pref.PreferenceKeys;
import com.worker.app.utility.Pref.PreferenceUtil;
import com.worker.app.utility.Utils;

public class BaseActivity extends AppCompatActivity implements DrawerAdapter.ClickListener {

    public FirebaseAnalytics mFirebaseAnylytics;
    public final String TAG = BaseActivity.this.getClass().getSimpleName();
    public APIService apiService = ApiClient.getClient().create(APIService.class);
    DrawerLayout drawer;
    private FrameLayout activity_content;
    private Context mContext;
    public Toolbar toolbar;
    RelativeLayout rel_topbar;
    ImageView plus;
    private MenuItem mMenuItem;
    public TextView txt_cartItem;
    public ImageView imgNaamLogo;
    public MyTextView txtNaamTitle;
    private ImageView imgProfilePic;
    private boolean is_login;
    private MyTextView text_userName, textEmailAddress;
    private RecyclerView recyclerViewDrawer;
    private DrawerAdapter mAdapter;
    private ArrayList<DrawerModel> listDrawer = new ArrayList<>();
    public ImageView imgInvisible;
    public BottomNavigationView bottom_navigation_view;
    SharedPreferences preferences, preferences_Login_Data;
    String UserID = "", Name = "";
    private LinearLayout linearProfileName;
    private NavigationView nav_view_slider;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(AppController.localeManager.setLocale(base));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void setContentView(int layoutResID) {

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);

        drawer = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);

        mContext = this;

        activity_content = drawer.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activity_content, true);
        super.setContentView(drawer);

        //firebase analytics variable set up for log events like banner click...
        mFirebaseAnylytics = FirebaseAnalytics.getInstance(this);

        initViews();

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, Create_request_step_1.class);
                startActivity(intent);
            }
        });

        bottom_navigation_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom1:
                        // TODO
                        if (!(mContext instanceof AnnouncementActivity)) {
                            mContext.startActivity(new Intent(mContext, AnnouncementActivity.class));
                        }
                                /*Intent intent = new Intent(BaseActivity.this, AnnouncementActivity.class);
                                startActivity(intent);*/
                        return true;
                    case R.id.bottom2:
                        // TODO
                        if (!(mContext instanceof MyRequestActivity)) {
                            mContext.startActivity(new Intent(mContext, MyRequestActivity.class));

                        }
                                /*Intent intent1 = new Intent(BaseActivity.this, MyRequestActivity.class);
                                startActivity(intent1);*/
                        return true;
                    case R.id.bottom3:
                        // TODO
                        if (!(mContext instanceof HomeActivity)) {
                            mContext.startActivity(new Intent(mContext, HomeActivity.class));

                        }
                                /*Intent intent3 = new Intent(BaseActivity.this, HomeActivity.class);
                                startActivity(intent3);*/
                        return true;
                    case R.id.bottom4:
                        // TODO
                        if (!(mContext instanceof NotificationActivity)) {
                            mContext.startActivity(new Intent(mContext, NotificationActivity.class));

                        }
                                /*Intent intent4 = new Intent(BaseActivity.this, NotificationActivity.class);
                                startActivity(intent4);*/
                        return true;
                    case R.id.bottom5:
                        // TODO
                        if (!(mContext instanceof MyProfileActivity)) {
                            mContext.startActivity(new Intent(mContext, MyProfileActivity.class));

                        }
                                /*Intent intent5 = new Intent(BaseActivity.this, MyProfileActivity.class);
                                startActivity(intent5);*/
                        return true;
                }
                return true;
            }
        });

        if (useToolbar()) {
            setSupportActionBar(toolbar);
            setTitle("");
        } else {
            toolbar.setVisibility(View.GONE);
        }

        if (useNavigationDrawer()) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, 0, 0) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    Utils.getInstance().hideKeyboard(BaseActivity.this);
                }
            };
            //  ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
            drawer.addDrawerListener(toggle);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.setDrawerIndicatorEnabled(false);
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawer.openDrawer(GravityCompat.START);
                }
            });
            toggle.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_menu));
            preferences = getSharedPreferences("Language", MODE_PRIVATE);
            Log.e("base", "## language :: " + preferences.getString("language", ""));
            if (!preferences.getString("language", "").equals("English"))
                Objects.requireNonNull(toolbar.getNavigationIcon()).setAutoMirrored(true);
            toggle.syncState();
        } else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        setDrawerRecyclerList();

        tokenApi();

//        nav_view_slider.getBackground().setAlpha(230);
    }

    private void initViews() {
        plus = findViewById(R.id.plus);
        bottom_navigation_view = findViewById(R.id.bottom_navigation_view);
        bottom_navigation_view.setItemIconTintList(null); // for home icon color (it should not change)
        bottom_navigation_view.setSelectedItemId(R.id.bottom3);
        text_userName = findViewById(R.id.text_userName);
        toolbar = findViewById(R.id.toolbar);
        imgNaamLogo = findViewById(R.id.imgNaamLogo);
        txtNaamTitle = findViewById(R.id.txtNaamTitle);
        linearProfileName = findViewById(R.id.linearProfileName);
        nav_view_slider = findViewById(R.id.nav_view_slider);
    }

    private void setDrawerRecyclerList() {
        recyclerViewDrawer = findViewById(R.id.recyclerViewDrawer);
        addDrawerItems();
        mAdapter = new DrawerAdapter(listDrawer, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerViewDrawer.setLayoutManager(mLayoutManager);
        recyclerViewDrawer.setNestedScrollingEnabled(false);
        recyclerViewDrawer.setItemAnimator(new DefaultItemAnimator());
        recyclerViewDrawer.setAdapter(mAdapter);
    }

    private void addDrawerItems() {
        DrawerModel drawerModel;

        listDrawer.clear();
        drawerModel = new DrawerModel(getResources().getString(R.string.d1_profile), "0");
        listDrawer.add(drawerModel);

        drawerModel = new DrawerModel(getResources().getString(R.string.d2_orders), "1");
        listDrawer.add(drawerModel);

        drawerModel = new DrawerModel(getResources().getString(R.string.d3_my_requests), "2");
        listDrawer.add(drawerModel);

        drawerModel = new DrawerModel(getResources().getString(R.string.d14_myWorker), "3");
        listDrawer.add(drawerModel);

        drawerModel = new DrawerModel(getResources().getString(R.string.d4_my_wallet), "4");
        listDrawer.add(drawerModel);

        drawerModel = new DrawerModel(getResources().getString(R.string.d5_language), "5");
        listDrawer.add(drawerModel);

        drawerModel = new DrawerModel(getResources().getString(R.string.d6_settings), "6");
        listDrawer.add(drawerModel);

        drawerModel = new DrawerModel(getResources().getString(R.string.d7_about_worker), "7");
        listDrawer.add(drawerModel);

        drawerModel = new DrawerModel(getResources().getString(R.string.d8_contact_us), "8");
        listDrawer.add(drawerModel);

        drawerModel = new DrawerModel(getResources().getString(R.string.d9_privacy_policy), "9");
        listDrawer.add(drawerModel);

        drawerModel = new DrawerModel(getResources().getString(R.string.d10_terms_condition), "10");
        listDrawer.add(drawerModel);

        if (UserID.equals("")) {
            drawerModel = new DrawerModel(getResources().getString(R.string.d12_login), "11");
            listDrawer.add(drawerModel);
        } else {
            drawerModel = new DrawerModel(getResources().getString(R.string.d11_logout), "12");
            listDrawer.add(drawerModel);
        }
    }

    protected boolean useToolbar() {
        return true;
    }

    protected boolean useNavigationDrawer() {
        return true;
    }

    protected boolean useBottomToolbar() {
        return true;
    }

    public void OpenMenu(View v) {
        drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer != null)
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                //callHome();
                super.onBackPressed();
            }
        else
            super.onBackPressed();
    }

    public void updateIfLogin() {
        is_login = PreferenceUtil.getPref(mContext).getBoolean(PreferenceKeys.IS_LOGIN, false);
        setUserName();
        listDrawer.clear();
        addDrawerItems();
        mAdapter.notifyDataSetChanged();
    }

    public void callHome() {
        Intent i = new Intent(BaseActivity.this, HomeActivity.class);
        //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private void setUserName() {
        if (is_login) {
            String strUser = getString(R.string.hiii) + ", " + PreferenceUtil.getPref(mContext).getString(PreferenceKeys.UserFirstName, "");

            text_userName.setText(strUser);
            textEmailAddress.setText(PreferenceUtil.getPref(mContext).getString(PreferenceKeys.UserEmail, ""));

            linearProfileName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(BaseActivity.this, MyProfileActivity.class);
                    startActivity(intent);
                    drawer.closeDrawer(GravityCompat.START);
                }
            });
        } else {
            String strGuest = getString(R.string.hiii) + ", " + getString(R.string.guest);
            text_userName.setText(strGuest);

            linearProfileName.setOnClickListener(null);
        }
    }

    @Override
    public void OnClickDrawerItem(DrawerModel model) {
        if (model.getId().equals("0")) {
            Intent intent = new Intent(BaseActivity.this, MyProfileActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (model.getId().equals("1")) {
            Intent intent = new Intent(BaseActivity.this, MyOrderListingActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (model.getId().equals("2")) {
            Intent intent = new Intent(BaseActivity.this, MyRequestListingActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (model.getId().equals("3")) {
            Intent intent = new Intent(BaseActivity.this, MyWorkersActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (model.getId().equals("4")) {
            Intent intent = new Intent(BaseActivity.this, WalletActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (model.getId().equals("5")) {
            Intent intentLang = new Intent(BaseActivity.this, LanguageActivity.class);
            intentLang.putExtra("fromWhere", "Drawer");
            startActivity(intentLang);
        } else if (model.getId().equals("6")) {
            Intent intent = new Intent(BaseActivity.this, SettingActivity.class);
            startActivity(intent);
        } else if (model.getId().equals("7")) {
            Intent intent = new Intent(BaseActivity.this, AboutActivity.class);
            startActivity(intent);
        } else if (model.getId().equals("8")) {
            Intent intent = new Intent(BaseActivity.this, ContactUsActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (model.getId().equals("10")) {
            Intent intent = new Intent(BaseActivity.this, TermsConditionsActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (model.getId().equals("9")) {
            Intent intent = new Intent(BaseActivity.this, AgreementPolicyActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (model.getId().equals("11")) {
            Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        } else if (model.getId().equals("12")) {
            drawer.closeDrawer(GravityCompat.START);
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
            builder.setTitle(R.string.d11_logout);
            builder.setMessage(R.string.alertlogout);
            builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    SharedPreferences preferences = getSharedPreferences("Login_Data", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear().commit();
                    Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    drawer.closeDrawer(GravityCompat.START);
                    // finish();
                }
            });
            builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    drawer.closeDrawer(GravityCompat.START);
                    dialog.dismiss();

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            // alert.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
            // alert.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
        }
    }

    public void requestWorkerCountApi() {
        String tag_string_req = "req";
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottom_navigation_view.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(1);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
        itemView.removeView((BaseActivity.this).findViewById(R.id.notifications_badge));
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().REQUEST_WORKER_COUNT, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json_main = new JSONObject(response);
//                            Log.e("response", "" + Consts.getInstance().REQUEST_WORKER_COUNT + json_main);
                            if (json_main.getString("success").equalsIgnoreCase("true")) {
                                View badge = LayoutInflater.from(BaseActivity.this).inflate(R.layout.notification_base_layout, itemView, true);
                                MyTextView requestCount = badge.findViewById(R.id.notifications_badge);
                                requestCount.setText(json_main.getString("RequestWorkers"));
                                requestCount.bringToFront();
                                String Count = json_main.getString("RequestWorkers");
                                if (Count.equals("0")) {
                                    BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottom_navigation_view.getChildAt(0);
                                    View v = bottomNavigationMenuView.getChildAt(1);
                                    BottomNavigationItemView itemView = (BottomNavigationItemView) v;
                                    itemView.removeView((BaseActivity.this).findViewById(R.id.notifications_badge));
                                }
                            } else {
                                // Toast.makeText(BaseActivity.this, json_main.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            //progress.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                });
            }
        },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        //progress.setVisibility(View.GONE);
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                String OrderID = preferences_Login_Data.getString("OrderID", "");
                if (OrderID.equals("")) {
                    OrderID = "0";
                }

                String UserID = preferences_Login_Data.getString("UserID", "");
                if (UserID.equals("")) {
                    UserID = "0";
                }
                SharedPreferences preferences = getSharedPreferences("Language", MODE_PRIVATE);
                params.put("language", preferences.getString("language", ""));
                params.put("UserID", UserID);
                params.put("OrderID", OrderID);
                Log.e("params", "" + Consts.getInstance().REQUEST_WORKER_COUNT + params);
                Log.e("params", "## REQUEST_WORKER_COUNT :: " + params);

                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().REQUEST_WORKER_COUNT);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void notificationCountApi() {
        String tag_string_req = "req";
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottom_navigation_view.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(3);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
        itemView.removeView((BaseActivity.this).findViewById(R.id.notifications_badge_count));
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().NOTIFICATION_COUNT, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json_main = new JSONObject(response);
//                            Log.e("response", "" + Consts.getInstance().NOTIFICATION_COUNT + json_main);
                            if (json_main.getString("success").equalsIgnoreCase("true")) {
                                View badge = LayoutInflater.from(BaseActivity.this).inflate(R.layout.notification_base_count_layout, itemView, true);
                                MyTextView notificationCount = badge.findViewById(R.id.notifications_badge_count);
                                notificationCount.setText(json_main.getString("NotificationCount"));
                                notificationCount.bringToFront();
                                String Count = json_main.getString("NotificationCount");
                                if (Count.equals("0")) {
                                    BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottom_navigation_view.getChildAt(0);
                                    View v = bottomNavigationMenuView.getChildAt(3);
                                    BottomNavigationItemView itemView = (BottomNavigationItemView) v;
                                    itemView.removeView((BaseActivity.this).findViewById(R.id.notifications_badge_count));
                                }
                            } else {
                                Toast.makeText(BaseActivity.this, json_main.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            //progress.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                });

            }
        },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        //progress.setVisibility(View.GONE);
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences preferences = getSharedPreferences("Language", MODE_PRIVATE);
                params.put("language", preferences.getString("language", ""));
                params.put("UserID", preferences_Login_Data.getString("UserID", ""));
//                Log.e("params", "" + Consts.getInstance().NOTIFICATION_COUNT + params);

                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().NOTIFICATION_COUNT);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void updateDeviceApi() {
        String tag_string_req = "req";
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().UPDATE_DEVICE, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json_main = new JSONObject(response);
//                            Log.e("response", "" + Consts.getInstance().UPDATE_DEVICE + json_main);
                            if (json_main.getString("success").equalsIgnoreCase("true")) {
                            } else {
                                Toast.makeText(BaseActivity.this, json_main.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("Token", PreferenceUtil.getPref(BaseActivity.this).getString(PreferenceKeys.PrefFCMToken, ""));
                SharedPreferences preferences = getSharedPreferences("Language", MODE_PRIVATE);
                params.put("language", preferences.getString("language", ""));
                params.put("UserID", preferences_Login_Data.getString("UserID", ""));
                params.put("DeviceType", "0");
                Log.e("params", "" + Consts.getInstance().UPDATE_DEVICE + params);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().UPDATE_DEVICE);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void tokenApi() {
        String Token = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences preferences_token = getSharedPreferences("TOKEN", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences_token.edit();
        editor.putString("Token", Token);
        editor.commit();
    }

    @Override
    protected void onResume() {
        preferences = getSharedPreferences("Login_Data", MODE_PRIVATE);
        UserID = preferences.getString("UserID", "");
        Name = preferences.getString("Name", "");
        if (UserID.equals("")) {
            String strGuest = getString(R.string.hiii) + ", " + getString(R.string.guest);

            text_userName.setText(strGuest);
            addDrawerItems();
            linearProfileName.setOnClickListener(null);
        } else {
            String strUser = getString(R.string.hiii) + ", " + Name;

            text_userName.setText(strUser);
            addDrawerItems();
            notificationCountApi();
            updateDeviceApi();

            linearProfileName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(BaseActivity.this, MyProfileActivity.class);
                    startActivity(intent);
                    drawer.closeDrawer(GravityCompat.START);
                }
            });
        }
        requestWorkerCountApi();
        setDrawerRecyclerList();
        tokenApi();
        super.onResume();
    }
}
