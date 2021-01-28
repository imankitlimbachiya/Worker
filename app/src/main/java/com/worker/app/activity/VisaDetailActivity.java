package com.worker.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.worker.app.BaseActivity;
import com.worker.app.R;
import com.worker.app.libraries.PickiT.PickiT;
import com.worker.app.libraries.PickiT.PickiTCallbacks;
import com.worker.app.model.DocumentUploadResponse;
import com.worker.app.model.VisaDetailModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyButton;
import com.worker.app.utility.MyEditText;
import com.worker.app.utility.MyTextView;
import com.worker.app.utility.UriHelper;
import com.worker.app.utility.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class VisaDetailActivity extends BaseActivity implements View.OnClickListener, PickiTCallbacks {

    SharedPreferences preferences, preferences_Login_Data;
    private long lastClickTime = 0;
    ProgressBar progress;
    RecyclerView recycle_workerDetail;
    ArrayList<VisaDetailModel> listWorkerDetail = new ArrayList<>();
    MyButton btn_next;
    LinearLayout lin_upload;
    ImageView plus, img_profile, image_remove, view_image, image_close;
    Context mContext;
    RelativeLayout rel_view;
    ScrollView scroll_main;
    MyEditText edt_idNumber;
    LinearLayout lin_uper;
    private final int PERMISSION_ALL = 165;
    private final int PERMISSION_CAMERA = 166;
    String OrderID;
    private int requestIDProof = 63;
    private boolean CountryVisa = false;
    String UserIdentificationAttachment = "", UserIdentificationAttachmentType, Visa_Identity;
    String CountryID;
    boolean firstTime = true;
    boolean checkCountryVisa = true;
    private MyTextView txtUseCameraUpload;
    /*Bitmap mBitmap;
    String selectedPath = "", mPath = "";
    private static final int SELECT_IMAGE = 4;
    private int MY_REQUEST_CODE, REQUEST_CODE;
    File photo;
    private ArrayList<Bitmap> mTempBitmapArray = new ArrayList<Bitmap>();
    String OrderID, UserIdentificationAttachment="", UserIdentificationAttachmentType, Visa_Identity;
    */
    //Declare PickiT
    PickiT pickiT;
    private ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visa_detail);

        mContext = this;

        progressDialog = new ProgressDialog(this);

        //Initialize PickiT
        pickiT = new PickiT(this, this);

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);
        OrderID = getIntent().getExtras().getString("OrderID");
        Log.e("OrderID", "** OrderID OrderID OrderID ::: " + OrderID);

        Init();

        if (Consts.isNetworkAvailable(VisaDetailActivity.this)) {
            String UserID = preferences_Login_Data.getString("UserID", "");
            if (UserID.equals("")) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            } else {
                visaDetailApi();
            }
        } else {
            Consts.getInstance().Act_vity = "VisaDetail";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
    }

    public void Init() {
        txtUseCameraUpload = findViewById(R.id.txtUseCameraUpload);
        plus = findViewById(R.id.plus);
        img_profile = findViewById(R.id.img_profile);
        plus.setVisibility(View.INVISIBLE);
        lin_upload = findViewById(R.id.lin_upload);
        recycle_workerDetail = findViewById(R.id.recycle_workerDetail);
        btn_next = findViewById(R.id.btn_next);
        progress = findViewById(R.id.progress);
        view_image = findViewById(R.id.view_image);
        image_remove = findViewById(R.id.image_remove);
        rel_view = findViewById(R.id.rel_view);
        scroll_main = findViewById(R.id.scroll_main);
        image_close = findViewById(R.id.image_close);
        edt_idNumber = findViewById(R.id.edt_idNumber);
        lin_uper = findViewById(R.id.lin_uper);

        btn_next.setOnClickListener(this);
        lin_upload.setOnClickListener(this);
        image_remove.setOnClickListener(this);
        image_close.setOnClickListener(this);


//        btn_next.setVisibility(View.GONE);
//        rel_view.setVisibility(View.GONE);
//        checkCountryVisa = "";
//        firstTime = "1";
    }

    private boolean isAllVisaPicsUploaded() {
        for (int i = 0; i < listWorkerDetail.size(); i++) {
            if (listWorkerDetail.get(i).getVisa_Identity().equals("")) {
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                //to avoid multiple clicks
                if (SystemClock.elapsedRealtime() - lastClickTime < Consts.getInstance().ClickTimeSeconds) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();

                if (edt_idNumber.getText().toString().equals("")) {
                    Toast.makeText(mContext, R.string.checkid, Toast.LENGTH_SHORT).show();
                } else if (UserIdentificationAttachment.equals("") || UserIdentificationAttachment.equals("null")) {
                    Toast.makeText(mContext, R.string.checkidproof, Toast.LENGTH_SHORT).show();
                } else if (!isAllVisaPicsUploaded()) {
                    Toast.makeText(mContext, R.string.checkcountryvisaproof, Toast.LENGTH_SHORT).show();
                } else {
                    if (Consts.getInstance().isNetworkAvailable(mContext)) {
                        checkVisaDetailApi();
                    } else {
                        Consts.getInstance().Act_vity = "VisaDetail";
                        Intent intent = new Intent(mContext, ReloadActivity.class);
                        startActivity(intent);
                    }
                }
                /*else if (!checkCountryVisa) {
                Toast.makeText(mContext, R.string.checkcountryvisaproof, Toast.LENGTH_SHORT).show();
            }*/
                break;
            case R.id.lin_upload:
                CountryVisa = false;
                if (UserIdentificationAttachment.equals("")) {
                    selectImage();

                } else {
                    if (UserIdentificationAttachmentType.equals("doc")) {
                        if (!UserIdentificationAttachment.equals("")) {
                            openFile(UserIdentificationAttachment);
                        }
                    } else {
                        scroll_main.setVisibility(View.GONE);
                        btn_next.setVisibility(View.GONE);
                        rel_view.setVisibility(View.VISIBLE);
                        image_close.setVisibility(View.VISIBLE);
                        Glide.with(mContext).load(UserIdentificationAttachment).into(view_image);
                    }
                }
                break;
            case R.id.image_close:
                scroll_main.setVisibility(View.VISIBLE);
                btn_next.setVisibility(View.VISIBLE);
                rel_view.setVisibility(View.GONE);
                image_close.setVisibility(View.GONE);
                break;
            case R.id.image_remove:
                PopupMenu popup = new PopupMenu(mContext, view);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.poupup_visa, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (Consts.isNetworkAvailable(mContext)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(R.string.removedocument);
                            builder.setMessage(R.string.alertdeletedocument);
                            builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (Consts.getInstance().isNetworkAvailable(mContext)) {
                                        removeUserOrderIdentityAPI();
                                    } else {
                                        Consts.getInstance().Act_vity = "VisaDetail";
                                        Intent intent = new Intent(mContext, ReloadActivity.class);
                                        startActivity(intent);
                                    }
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                            //     alert.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);
                            //     alert.getButton(DialogInterface.BUTTON_NEGATIVE).setAllCaps(false);
                        } else {
                            Toast.makeText(mContext, R.string.networkcheck, Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
                popup.show();//showing popup menu
                break;
        }
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

    private Intent getFileChooserIntent() {

        String[] mimeTypes = {"image/*", "application/pdf"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType("image/*|application/pdf");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        //newly added lines
        intent.putExtra("return-data", true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e(TAG, "** onActivityResult");
        if (requestCode == requestIDProof) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    pickiT.getPath(data.getData(), Build.VERSION.SDK_INT);
                    /*Uri uri = data.getData();
                    Log.e(TAG, "** uri camera uri : " + uri);
                    Log.e(TAG, "** uri camera toString : " + uri.toString());
                    Log.e(TAG, "** uri camera getPath : " + uri.getPath());
                    // Get the path
                    String pathReal = "";
                    pathReal = UriHelper.getPath(mContext, uri);
                    Log.e(TAG, "** File Path: " + pathReal);

                    //check if path exists
                    if (pathReal != null && !pathReal.equals("")) {

                        File fileObj = new File(pathReal);
                        Log.e(TAG, "** fileObj.getName()  " + fileObj.getName());
                        Log.e(TAG, "** fileObj.getPath()  " + fileObj.getPath());
                        Log.e(TAG, "** fileObj.getAbsolutePath()  " + fileObj.getAbsolutePath());

                        //check file size
                        // Get length of file in bytes
                        long fileSizeInBytes = fileObj.length();
                        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                        long fileSizeInKB = fileSizeInBytes / 1024;
                        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                        long fileSizeInMB = fileSizeInKB / 1024;

                        Log.e(TAG, "** fileSizeInMB  " + fileSizeInMB);
                        if (fileSizeInMB > 15) {
                            isImageProcessing=false;
                            Toast.makeText(VisaDetailActivity.this, R.string.fileSizeCheck, Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            if (Consts.isNetworkAvailable(VisaDetailActivity.this)) {
                                Log.e(TAG, "** CountryVisa  " + CountryVisa);

                                if (!CountryVisa) {
                                    uploadUserId(fileObj);
                                } else {
                                    uploadVisaDetails(fileObj);
                                }
                            } else {
                                isImageProcessing=false;
                                Toast.makeText(VisaDetailActivity.this, R.string.networkcheck, Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        isImageProcessing=false;
                        Toast.makeText(VisaDetailActivity.this, "getting blank file path", Toast.LENGTH_LONG).show();
                    }*/
                } else {
//                    isImageProcessing = false;
                }
            } else {
//                isImageProcessing = false;
            }
        } else if (requestCode == 12) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    Uri uri = UriHelper.writeToTempImageAndGetPathUri(getApplicationContext(), photo);
                    String pathReal = "";
                    Log.e(TAG, "** uri camera uri : " + uri);
                    Log.e(TAG, "** uri camera toString : " + uri.toString());
                    Log.e(TAG, "** uri camera getPath : " + uri.getPath());

                    pathReal = UriHelper.getPath(mContext, uri);
                    Log.e(TAG, "** File Path: " + pathReal);

                    if (pathReal != null && !pathReal.equals("")) {

                        File fileObj = new File(pathReal);
                        Log.e(TAG, "** fileObj.getName()  " + fileObj.getName());
                        Log.e(TAG, "** fileObj.getPath()  " + fileObj.getPath());
                        Log.e(TAG, "** fileObj.getAbsolutePath()  " + fileObj.getAbsolutePath());

                        //check file size
                        // Get length of file in bytes
                        long fileSizeInBytes = fileObj.length();
                        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                        long fileSizeInKB = fileSizeInBytes / 1024;
                        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                        long fileSizeInMB = fileSizeInKB / 1024;

                        Log.e(TAG, "** fileSizeInMB  " + fileSizeInMB);
                        if (fileSizeInMB > 15) {
//                            isImageProcessing = false;
                            Toast.makeText(VisaDetailActivity.this, R.string.fileSizeCheck, Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            if (Consts.isNetworkAvailable(VisaDetailActivity.this)) {
                                Log.e(TAG, "** CountryVisa  " + CountryVisa);

                                if (fileObj != null) {
                                    if (!CountryVisa) {
                                        uploadUserId(fileObj);
                                    } else {
                                        uploadVisaDetails(fileObj);
                                    }
                                }
                            } else {
                                Toast.makeText(VisaDetailActivity.this, R.string.networkcheck, Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
//                        isImageProcessing = false;
                    }
                } else {
//                    isImageProcessing = false;
                }
            } else {
//                isImageProcessing = false;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.ImageColumns.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e("FAc", "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void uploadVisaDetails(File fileObj) {
        Log.e(TAG, "** uploadUserId  ");
        if (Utils.getInstance().isInternetAvailable(mContext)) {
//            progress.setVisibility(View.VISIBLE);
            showProgressDialogWithTitle("Uploading Document");

            Log.e("VDA", "** lang " + preferences.getString("language", ""));
            Log.e("VDA", "** UserID " + preferences_Login_Data.getString("UserID", ""));
            Log.e("VDA", "** OrderID " + OrderID);
            Log.e("VDA", "** fileObj name :::  " + fileObj.getName());

            RequestBody language = RequestBody.create(MediaType.parse("text/plain"), preferences.getString("language", ""));
            RequestBody MyCountryID = RequestBody.create(MediaType.parse("text/plain"), CountryID);
            RequestBody MyOrderID = RequestBody.create(MediaType.parse("text/plain"), OrderID);

            MultipartBody.Part fileToUpload = null;
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), fileObj);
            fileToUpload = MultipartBody.Part.createFormData("Upload", fileObj.getName(), requestBody);

            Log.e("VDA", "** fileToUpload.body().toString() :::  " + fileToUpload.body().toString());
            Call<DocumentUploadResponse> resultCall = apiService.uploadVisaProof(language, MyOrderID, MyCountryID, fileToUpload);
//            final String finalImagePathReal = imagePathReal;
            resultCall.enqueue(new Callback<DocumentUploadResponse>() {
                @Override
                public void onResponse(Call<DocumentUploadResponse> call, retrofit2.Response<DocumentUploadResponse> response) {
//                    progress.setVisibility(View.GONE);
                    hideProgressDialogWithTitle();

                    if (response.isSuccessful()) {
                        DocumentUploadResponse updateProfileResponse = response.body();
                        if (updateProfileResponse.getSuccess().equals("True")) {
//                            Toast.makeText(VisaDetailActivity.this, updateProfileResponse.getMassege(), Toast.LENGTH_LONG).show();
//                            Log.e("##Response-", "** ## getError_msg errorBody:: " + response.errorBody());
                            visaDetailApi();
                        } else {
                            Toast.makeText(VisaDetailActivity.this, updateProfileResponse.getMassege(), Toast.LENGTH_LONG).show();
                            Log.e("##Response-", "** ## getError_msg updateProfile:: " + updateProfileResponse.getMassege());
                            Log.e("##Response-", "** ## getError_msg errorBody:: " + response.errorBody());
                            Log.e("##Response-", "** ## getError_msg message:: " + response.message());
                            Log.e("##Response-", "** ## getError_msg raw:: " + response.raw().toString());
                            Log.e("##Response-", "** ## response isSuccessful:: " + response.isSuccessful());
                        }
                    }
                }

                @Override
                public void onFailure(Call<DocumentUploadResponse> call, Throwable t) {
                    Log.e("##Response-", "** onFailure updateProfile getMessage :: " + t.getMessage());
                    Log.e("##Response-", "** onFailure updateProfile getMessage :: " + t.getLocalizedMessage());
                    Log.e("##Response-", "** onFailure updateProfile getMessage :: " + t.getCause());
//                    progress.setVisibility(View.GONE);
                    hideProgressDialogWithTitle();
                    t.printStackTrace();
                    Toast.makeText(VisaDetailActivity.this, R.string.somethingWrong, Toast.LENGTH_LONG).show();
//                    Utils.getInstance().showSnackBar(btnUpdate, getResources().getString(R.string.somethingWrong));
                }
            });
        } else {
            Consts.getInstance().Act_vity = "VisaDetail";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
    }

    private void uploadUserId(File fileObj) {
        Log.e(TAG, "** uploadUserId  ");
        if (Utils.getInstance().isInternetAvailable(mContext)) {
//            progress.setVisibility(View.VISIBLE);
            showProgressDialogWithTitle("Uploading Document");
            Log.e("VDA", "** lang " + preferences.getString("language", ""));
            Log.e("VDA", "** UserID " + preferences_Login_Data.getString("UserID", ""));
            Log.e("VDA", "** OrderID " + OrderID);
            Log.e("VDA", "** fileObj name :::  " + fileObj.getName());

            RequestBody language = RequestBody.create(MediaType.parse("text/plain"), preferences.getString("language", ""));
            RequestBody UserID = RequestBody.create(MediaType.parse("text/plain"), preferences_Login_Data.getString("UserID", ""));
            RequestBody MyOrderID = RequestBody.create(MediaType.parse("text/plain"), OrderID);

            MultipartBody.Part fileToUpload = null;
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), fileObj);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.ENGLISH).format(new
                    Date());
            String newFileName = "User_" + preferences_Login_Data.getString("UserID", "") + timeStamp +
                    fileObj.getName().substring(fileObj.getName().lastIndexOf("0") + 1);
            fileToUpload = MultipartBody.Part.createFormData("UserIdentificationAttachment", newFileName, requestBody);

            Log.e("VDA", "** newFileName :::  " + newFileName);
            Call<DocumentUploadResponse> resultCall = apiService.uploadUserIdProof(language, UserID, MyOrderID, fileToUpload);
//            final String finalImagePathReal = imagePathReal;
            resultCall.enqueue(new Callback<DocumentUploadResponse>() {
                @Override
                public void onResponse(Call<DocumentUploadResponse> call, retrofit2.Response<DocumentUploadResponse> response) {
//                    progress.setVisibility(View.GONE);
                    hideProgressDialogWithTitle();

                    if (response.isSuccessful()) {
                        DocumentUploadResponse updateProfileResponse = response.body();
                        if (updateProfileResponse.getSuccess().equals("True")) {
//                            Toast.makeText(VisaDetailActivity.this, updateProfileResponse.getMassege(), Toast.LENGTH_LONG).show();
//                            Log.e("##Response-", "** ## getError_msg errorBody:: " + response.errorBody());
                            visaDetailApi();
                        } else {
//                            Toast.makeText(VisaDetailActivity.this, updateProfileResponse.getMassege(), Toast.LENGTH_LONG).show();
                            Log.e("##Response-", "** ## getError_msg updateProfile:: " + updateProfileResponse.getMassege());
                            Log.e("##Response-", "** ## getError_msg errorBody:: " + response.errorBody());
                            Log.e("##Response-", "** ## getError_msg message:: " + response.message());
                            Log.e("##Response-", "** ## getError_msg raw:: " + response.raw().toString());
                            Log.e("##Response-", "** ## response isSuccessful:: " + response.isSuccessful());
                        }
                    }
                }

                @Override
                public void onFailure(Call<DocumentUploadResponse> call, Throwable t) {
                    Log.e("##Response-", "** onFailure updateProfile getMessage :: " + t.getMessage());
                    Log.e("##Response-", "** onFailure updateProfile getMessage :: " + t.getLocalizedMessage());
                    Log.e("##Response-", "** onFailure updateProfile getMessage :: " + t.getCause());
//                    progress.setVisibility(View.GONE);
                    hideProgressDialogWithTitle();
                    t.printStackTrace();
                    Toast.makeText(VisaDetailActivity.this, R.string.somethingWrong, Toast.LENGTH_LONG).show();
//                    Utils.getInstance().showSnackBar(btnUpdate, getResources().getString(R.string.somethingWrong));
                }
            });
        } else {
            Consts.getInstance().Act_vity = "VisaDetail";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
    }

  /*  public String getPath(Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }*/

    private void visaDetailApi() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        listWorkerDetail.clear();
        StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().VISA_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            try {
                                progress.setVisibility(View.GONE);
                                JSONObject json_main = new JSONObject(response);
                                Log.e("response", "** " + Consts.getInstance().VISA_DETAIL + response);
                                if (json_main.getString("success").equalsIgnoreCase("True")) {
//                                    isImageProcessing = false;

                                    Glide.with(mContext).load(R.drawable.user).into(img_profile);
//                                    img_profile.setPadding(30, 30, 30, 30);

                                    UserIdentificationAttachment = json_main.getString("UserIdentificationAttachment");
                                    UserIdentificationAttachmentType = json_main.getString("Type");

//                                    Toast.makeText(mContext, "" + UserIdentificationAttachment, Toast.LENGTH_SHORT).show();
                                    //check document type
                                    Log.e("response", "** got file  " + UserIdentificationAttachment);
                                    if (UserIdentificationAttachmentType.equals("doc")) {
                                        Glide.with(mContext)
                                                .load(getResources().getDrawable(R.drawable.pdf_icon))
                                                .into(img_profile);
//                                        img_profile.setPadding(0, 0, 0, 0);
                                    } else {
                                        if (!UserIdentificationAttachment.equals("")) {
                                            Glide.with(mContext).load(UserIdentificationAttachment).into(img_profile);
//                                            img_profile.setPadding(0, 0, 0, 0);
                                        }
                                    }

                                    if (firstTime) {
                                        edt_idNumber.setText(json_main.getString("UserIdentificationNumber"));
                                        firstTime = false;
                                    }
                                    JSONArray array_Nationality = json_main.getJSONArray("Nationality");
                                    btn_next.setVisibility(View.VISIBLE);
                                    lin_uper.setVisibility(View.VISIBLE);
                                    for (int i = 0; i < array_Nationality.length(); i++) {
                                        VisaDetailModel visaDetailModel = new VisaDetailModel();
                                        visaDetailModel.setCountryID(array_Nationality.getJSONObject(i).getString("CountryID"));
                                        visaDetailModel.setCountryName(array_Nationality.getJSONObject(i).getString("CountryName"));
                                        visaDetailModel.setWorkersCount(array_Nationality.getJSONObject(i).getString("WorkersCount"));
                                        visaDetailModel.setVisa_Identity(array_Nationality.getJSONObject(i).getString("Visa_Identity"));
                                        visaDetailModel.setVisaType(array_Nationality.getJSONObject(i).getString("Type"));
                                        String VisaIdTemp = array_Nationality.getJSONObject(i).getString("Visa_Identity");
                                        if (VisaIdTemp.equals("") || VisaIdTemp.equals("null")) {
                                            checkCountryVisa = false;
                                        }
                                        listWorkerDetail.add(visaDetailModel);
                                    }
                                    ListVisaDetailAdapter mAdapter = new ListVisaDetailAdapter(VisaDetailActivity.this, listWorkerDetail);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                                    recycle_workerDetail.setLayoutManager(mLayoutManager);
                                    recycle_workerDetail.setAdapter(mAdapter);
                                }
                            } catch (Exception e) {
                                progress.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                        } else {
                            progress.setVisibility(View.GONE);
                        }
                        progress.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "error => " + error.toString());
                        progress.setVisibility(View.GONE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("language", preferences.getString("language", ""));
                params.put("UserID", preferences_Login_Data.getString("UserID", ""));
                params.put("OrderID", OrderID);
                Log.e("params", "" + Consts.getInstance().VISA_DETAIL + params);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().VISA_DETAIL);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("FMA", "## onRequestPermissionsResult requestCode :: " + requestCode);
        if (requestCode == PERMISSION_ALL) {
            Log.e("FMA", "## requestCode == PERMISSION_ALL");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("FMA", "## grantResults.length > 0");
                startActivityForResult(getFileChooserIntent(), requestIDProof);
            } else {
                Log.e("FMA", "## grantResults.length > 0 elseeeeeeeeeeeeee");
            }
        } else if (requestCode == PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("FMA", "## grantResults.length > 0");
                photocamera();
            } else {
                Log.e("FMA", "## grantResults.length > 0 elseeeeeeeeeeeeee");
            }
        }
    }

    public class ListVisaDetailAdapter extends RecyclerView.Adapter<ListVisaDetailAdapter.MyViewHolder> {

        private List<VisaDetailModel> arraylist;
        private Context mContext;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private MyTextView txt_WorkersNo, txt_countryName;
            private ImageView image_remove, image_country;
            LinearLayout lin_upload;

            public MyViewHolder(View view) {
                super(view);
                txt_countryName = view.findViewById(R.id.txt_countryName);
                txt_WorkersNo = view.findViewById(R.id.txt_WorkersNo);
                image_remove = view.findViewById(R.id.image_remove);
                lin_upload = view.findViewById(R.id.lin_upload);
                image_country = view.findViewById(R.id.image_country);
            }
        }

        public ListVisaDetailAdapter(Context mContext, List<VisaDetailModel> arraylist) {
            this.mContext = mContext;
            this.arraylist = arraylist;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_visa_detail, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            VisaDetailModel visaDetailModel = arraylist.get(position);
            holder.txt_countryName.setText(visaDetailModel.getCountryName());
            holder.txt_WorkersNo.setText(visaDetailModel.getWorkersCount());
            Glide.with(mContext).load(R.drawable.file).into(holder.image_country);
            Visa_Identity = visaDetailModel.getVisa_Identity();
            String docType = visaDetailModel.getVisaType();

            if (Visa_Identity.equals("") || Visa_Identity.equals("null")) {
            } else {
                if (docType.equals("doc"))
                    Glide.with(mContext).load(R.drawable.pdf_icon).into(holder.image_country);
                else
                    Glide.with(mContext).load(Visa_Identity).into(holder.image_country);
            }

            CountryID = visaDetailModel.getCountryID();

            holder.lin_upload.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    CountryVisa = true;
                    CountryID = visaDetailModel.getCountryID();
                    if (Visa_Identity.equals("") || Visa_Identity.equals("null")) {
                        selectImage();
                    } else {
                        if (docType.equals("doc")) {
                            openFile(visaDetailModel.getVisa_Identity());
                        } else {
                            scroll_main.setVisibility(View.GONE);
                            btn_next.setVisibility(View.GONE);
                            rel_view.setVisibility(View.VISIBLE);
                            image_close.setVisibility(View.VISIBLE);
                            Glide.with(VisaDetailActivity.this).load(Visa_Identity).into(view_image);
                        }
                    }
                }
            });

            holder.image_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    CountryID = visaDetailModel.getCountryID();
                    PopupMenu popup = new PopupMenu(mContext, view);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.poupup_visa, popup.getMenu());
                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (Consts.isNetworkAvailable(mContext)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle(R.string.removedocument);
                                builder.setMessage(R.string.alertdeletedocument);
                                builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (Consts.getInstance().isNetworkAvailable(mContext)) {
                                            removeCountryVisaDetailApi();
                                        } else {
                                            Consts.getInstance().Act_vity = "VisaDetail";
                                            Intent intent = new Intent(mContext, ReloadActivity.class);
                                            startActivity(intent);
                                        }
                                        dialog.dismiss();
                                    }
                                });
                                builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                                // alert.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);
                                //alert.getButton(DialogInterface.BUTTON_NEGATIVE).setAllCaps(false);
                            } else {
                                Toast.makeText(mContext, R.string.networkcheck, Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });
                    popup.show();//showing popup menu
                }
            });
        }

        @Override
        public int getItemCount() {
            return arraylist.size();
        }

    }

    private void openFile(String pathFile) {
        Log.e("VDA","## openFile pathFile :: "+pathFile);
        try {
            Uri uri = Uri.parse(pathFile);
            Log.e("uri", "" + uri);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(VisaDetailActivity.this, "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeUserOrderIdentityAPI() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().REMOVE_USER_ORDER_IDENTITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            try {
                                progress.setVisibility(View.GONE);
                                JSONObject json_main = new JSONObject(response);
                                Log.e("response", "" + Consts.getInstance().REMOVE_USER_ORDER_IDENTITY + response);
                                if (json_main.getString("success").equalsIgnoreCase("True")) {
                                    visaDetailApi();
                                } else {
                                    Toast.makeText(mContext, json_main.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                progress.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                        } else {
                            progress.setVisibility(View.GONE);
                        }
                        progress.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "error => " + error.toString());
                        progress.setVisibility(View.GONE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("language", preferences.getString("language", ""));
                params.put("OrderID", OrderID);
                params.put("UserID", preferences_Login_Data.getString("UserID", ""));
                Log.e("params", "" + Consts.getInstance().REMOVE_USER_ORDER_IDENTITY + params);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().REMOVE_USER_ORDER_IDENTITY);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void removeCountryVisaDetailApi() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().REMOVE_COUNTRY_VISA_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            try {
                                progress.setVisibility(View.GONE);
                                JSONObject json_main = new JSONObject(response);
                                Log.e("response", "" + json_main + response);
                                if (json_main.getString("success").equalsIgnoreCase("True")) {
                                    visaDetailApi();
                                } else {
                                    Toast.makeText(mContext, json_main.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.d("ERROR", "## eexeptipn => " + e.getMessage());
                                progress.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                        } else {
                            Log.d("ERROR", "## response null ");
                            Toast.makeText(mContext, getResources().getString(R.string.somethingWrong), Toast.LENGTH_SHORT).show();
                            progress.setVisibility(View.GONE);
                        }
                        progress.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, getResources().getString(R.string.somethingWrong), Toast.LENGTH_SHORT).show();
                        Log.d("ERROR", "## error => " + error.toString());
                        progress.setVisibility(View.GONE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("language", preferences.getString("language", ""));
                params.put("OrderID", OrderID);
                params.put("CountryID", CountryID);
                Log.e("params", "" + Consts.getInstance().REMOVE_COUNTRY_VISA_DETAIL + params);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().REMOVE_COUNTRY_VISA_DETAIL);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void checkVisaDetailApi() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().CHECK_VISA_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            try {
                                progress.setVisibility(View.GONE);
                                JSONObject json_main = new JSONObject(response);
                                Log.e("response", "" + Consts.getInstance().CHECK_VISA_DETAIL + response);
                                if (json_main.getString("success").equalsIgnoreCase("True")) {
                                    Intent intent = new Intent(mContext, OrderSummeryActivity.class);
                                    intent.putExtra("OrderID", OrderID);
                                    mContext.startActivity(intent);
                                } else {
                                    Toast.makeText(mContext, json_main.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                progress.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                        } else {
                            progress.setVisibility(View.GONE);
                        }
                        progress.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "error => " + error.toString());
                        progress.setVisibility(View.GONE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("language", preferences.getString("language", ""));
                params.put("OrderID", OrderID);
                params.put("UserIdentificationNumber", edt_idNumber.getText().toString());
                Log.e("params", "" + Consts.getInstance().CHECK_VISA_DETAIL + params);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().CHECK_VISA_DETAIL);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void selectImage() {
        String str[] = {getResources().getString(R.string.choosefromgallery), getResources().getString(R.string.opencamera), getResources().getString(R.string.gpcancel)};
        AlertDialog.Builder alert = new AlertDialog.Builder(VisaDetailActivity.this);
        alert.setItems(str,
                new DialogInterface.OnClickListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if (which == 0) {
                            //took permission of storage first
                            String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

                            if (hasPermissions(mContext, PERMISSIONS)) {
                                Log.e("DLS", "## if hasPermissions PERMISSION_ALL");
                                requestPermissions(PERMISSIONS, PERMISSION_ALL);
                            } else {
                                startActivityForResult(getFileChooserIntent(), requestIDProof);
                            }
                        } else if (which == 1) {
                            // camera
                            String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

                            if (hasPermissions(mContext, PERMISSIONS)) {
                                Log.e("DLS", "## if hasPermissions PERMISSION_ALL");
                                requestPermissions(PERMISSIONS, PERMISSION_CAMERA);
                            } else {
                                photocamera();
                            }
                        } else {
//                            isImageProcessing = false;
                        }
                    }
                });
        alert.show();
    }

    @SuppressLint("NewApi")
    private void photocamera_perm() {
        /*REQUEST_CODE = 50;
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            photocamera();
        } else {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }*/
    }

    private void photogallery() {
        // TODO Auto-generated method stub
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("image/jpeg");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Image"), 11);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setType("image/jpeg");
            startActivityForResult(galleryIntent, 33);
        }
    }

    private void photocamera() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 12);
    }

    @Override
    public void PickiTonStartListener() {

    }

    @Override
    public void PickiTonProgressUpdate(int progress) {

    }

    @Override
    public void PickiTonCompleteListener(String pathReal, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason) {
        Log.e(TAG, "** PickiTonCompleteListener wasSuccessful : " + wasSuccessful);
        //  Chick if it was successful
        if (wasSuccessful) {
            Log.e(TAG, "** pathReal : " + pathReal);

            if (pathReal != null && !pathReal.equals("")) {

                File fileObj = new File(pathReal);
                Log.e(TAG, "** fileObj.getName()  " + fileObj.getName());
                Log.e(TAG, "** fileObj.getPath()  " + fileObj.getPath());
                Log.e(TAG, "** fileObj.getAbsolutePath()  " + fileObj.getAbsolutePath());

                //check file size
                // Get length of file in bytes
                long fileSizeInBytes = fileObj.length();
                // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                long fileSizeInKB = fileSizeInBytes / 1024;
                // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                long fileSizeInMB = fileSizeInKB / 1024;

                Log.e(TAG, "** fileSizeInMB  " + fileSizeInMB);
                if (fileSizeInMB > 15) {
//                    isImageProcessing = false;
                    Toast.makeText(VisaDetailActivity.this, R.string.fileSizeCheck, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    if (Consts.isNetworkAvailable(VisaDetailActivity.this)) {
                        Log.e(TAG, "** CountryVisa  " + CountryVisa);

                        if (fileObj != null) {
                            if (!CountryVisa) {
                                uploadUserId(fileObj);
                            } else {
                                uploadVisaDetails(fileObj);
                            }
                        }
                    } else {
                        Toast.makeText(VisaDetailActivity.this, R.string.networkcheck, Toast.LENGTH_LONG).show();
                    }
                }
            } else {
//                isImageProcessing = false;
            }
        }
    }

    private void showProgressDialogWithTitle(String substring) {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //Without this user can hide loader by tapping outside screen
        progressDialog.setCancelable(false);
        progressDialog.setMessage(substring);
        progressDialog.show();
    }

    private void hideProgressDialogWithTitle() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.dismiss();
    }
}

