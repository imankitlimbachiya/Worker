package com.worker.app.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.worker.app.BaseActivity;
import com.worker.app.R;
import com.worker.app.adapter.WorkerEducationAdapter;
import com.worker.app.adapter.WorkerExperienceAdapter;
import com.worker.app.model.WorkerProfileResponse;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyButton;
import com.worker.app.utility.MyTextView;
import com.worker.app.utility.Utils;
import retrofit2.Call;
import retrofit2.Callback;

public class WorkerProfileActivity extends BaseActivity implements View.OnClickListener {

    private long lastClickTime = 0;
    ImageView back;
    MyButton btnAddToMyRequest;
    ProgressBar progress;
    String WorkerID, ContractFees;
    ImageView imgProfile, img_video, image_cv;
    MyTextView txt_name, txt_nationality, txt_birth, txt_Gender, txt_category, txt_subcategory, txt_categoryName;
    MyTextView txt_salary, txt_Religon, txt_contractFees, txt_language,  txt_subcategoryName;
    SharedPreferences preferences, preferences_Login_Data;
    String videoUrl, cvp;
    LinearLayout language, education, experience;
    Context mContext;
    private RecyclerView recycleEducation, recycleExperience;
    private View viewEducation, viewExp;
    private RelativeLayout linearVideoDialog;
    private VideoView videoView;
    private ProgressBar progressBar;
    private MediaController mediacontroller;
    private LinearLayout linearHideVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_profile);

        mContext = this;

        init();
    }

    private void init() {
        progressBar=findViewById(R.id.progressBar);
        linearHideVideo=findViewById(R.id.linearHideVideo);
        linearHideVideo.setOnClickListener(this);
        linearVideoDialog=findViewById(R.id.linearVideoDialog);
        videoView=findViewById(R.id.videoView);
        viewEducation = findViewById(R.id.viewEducation);
        viewExp = findViewById(R.id.viewExp);
        recycleEducation = findViewById(R.id.recycleEducation);
        recycleExperience = findViewById(R.id.recycleExperience);
        btnAddToMyRequest = findViewById(R.id.btnAddToMyRequest);
        imgProfile = findViewById(R.id.imgProfile);
        txt_name = findViewById(R.id.txt_name);
        txt_nationality = findViewById(R.id.txt_nationality);
        txt_birth = findViewById(R.id.txt_birth);
        txt_Gender = findViewById(R.id.txt_Gender);
        txt_category = findViewById(R.id.txt_category);
        txt_subcategory = findViewById(R.id.txt_subcategory);
        back = findViewById(R.id.back);
        txt_salary = findViewById(R.id.txt_salary);
        txt_Religon = findViewById(R.id.txt_Religon);
        txt_contractFees = findViewById(R.id.txt_contractFees);
        txt_language = findViewById(R.id.txt_language);
//        txt_education = findViewById(R.id.txt_education);
//        txt_experience = findViewById(R.id.txt_experience);
        img_video = findViewById(R.id.img_video);
        image_cv = findViewById(R.id.image_cv);
        txt_categoryName = findViewById(R.id.txt_categoryName);
        progress = findViewById(R.id.progress);
        language = findViewById(R.id.language);
        education = findViewById(R.id.education);
        experience = findViewById(R.id.experience);
        txt_subcategoryName = findViewById(R.id.txt_subcategoryName);

        back.setOnClickListener(this);
        btnAddToMyRequest.setOnClickListener(this);
        img_video.setOnClickListener(this);
        image_cv.setOnClickListener(this);

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);
        WorkerID = getIntent().getExtras().getString("WorkerID");
        boolean isFromListing = getIntent().getExtras().getBoolean("isFromListing", false);
        if (isFromListing)
            btnAddToMyRequest.setVisibility(View.GONE);
        else
            btnAddToMyRequest.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearHideVideo:
                if (SystemClock.elapsedRealtime() - lastClickTime < Consts.getInstance().ClickTimeSeconds) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();

                linearVideoDialog.setVisibility(View.GONE);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.btnAddToMyRequest:
                //to avoid multiple clicks
                if (SystemClock.elapsedRealtime() - lastClickTime < Consts.getInstance().ClickTimeSeconds) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();

                if (Consts.isNetworkAvailable(mContext)) {
                    AddOrderRequestApi();
                } else {
                    Consts.getInstance().Act_vity = "Workerprofile";
                    Intent intent = new Intent(mContext, ReloadActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.img_video:
                if (videoUrl.equals("") || videoUrl.equals(null) || videoUrl.equals("null") || videoUrl == null) {
                    Toast.makeText(mContext, getString(R.string.NoVideo), Toast.LENGTH_LONG).show();
                } else {
                    openVideo(videoUrl);
                }
                break;
            case R.id.image_cv:
                if (cvp.equals("") || cvp.equals(null) || cvp.equals("null") || cvp == null) {
                    Toast.makeText(WorkerProfileActivity.this, "not cv provided", Toast.LENGTH_LONG).show();
                } else {
                    if (cvp.contains(".doc") || cvp.contains(".docx")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        // Word document
                        String docUrl = "https://docs.google.com/gview?embedded=true&url=" + cvp;
                        Uri uri1 = Uri.parse(docUrl);
                        intent.setData(uri1);
                        startActivity(Intent.createChooser(intent, "Choose browser"));
                    } else {
                        openFile(cvp);
                    }
                }
                break;
        }
    }

    private void workerDetailApi() {
        if (Utils.getInstance().isInternetAvailable(mContext)) {
            progress.setVisibility(View.VISIBLE);

            HashMap<String, String> params = new HashMap<>();
            params.put("language", preferences.getString("language", ""));
            params.put("WorkerID", WorkerID);

            Log.e("params", "" + Consts.getInstance().WORKER_DETAIL_URL + params);
            Call<WorkerProfileResponse> resultCall = apiService.viewWorkerProfile(params);
            resultCall.enqueue(new Callback<WorkerProfileResponse>() {
                @Override
                public void onResponse(@NonNull Call<WorkerProfileResponse> call, @NonNull retrofit2.Response<WorkerProfileResponse> response) {
                    progress.setVisibility(View.GONE);
                    try {
                        if (response.isSuccessful()) {
                            WorkerProfileResponse workerProfileResponse = response.body();
                            if (workerProfileResponse.getSuccess().equals("True")) {
                                String Language = workerProfileResponse.getWorkerDetail().get(0).getLanguages();
                                if (Language.equals("") || Language.equals(null) || Language.equals("null") || Language == null) {
                                } else {
                                    language.setVisibility(View.VISIBLE);
                                    txt_language.setVisibility(View.VISIBLE);
                                    txt_language.setText(workerProfileResponse.getWorkerDetail().get(0).getLanguages());
                                }

                                ContractFees = workerProfileResponse.getWorkerDetail().get(0).getContactFees();
                                txt_name.setText(workerProfileResponse.getWorkerDetail().get(0).getWorkerName());
                                txt_nationality.setText(workerProfileResponse.getWorkerDetail().get(0).getNationality());
                                txt_Gender.setText(workerProfileResponse.getWorkerDetail().get(0).getGender());
                                txt_salary.setText(workerProfileResponse.getWorkerDetail().get(0).getMonthlySalary());
                                txt_Religon.setText(workerProfileResponse.getWorkerDetail().get(0).getReligion());
                                txt_contractFees.setText(workerProfileResponse.getWorkerDetail().get(0).getContactFees());
                                txt_categoryName.setText(workerProfileResponse.getWorkerDetail().get(0).getCategoryName());
                                txt_category.setText(workerProfileResponse.getWorkerDetail().get(0).getCategoryName());
                                txt_subcategory.setText(workerProfileResponse.getWorkerDetail().get(0).getSubCategoryName());
                                txt_subcategoryName.setText(workerProfileResponse.getWorkerDetail().get(0).getSubCategoryName());
                                videoUrl = workerProfileResponse.getWorkerDetail().get(0).getVideo();
                                cvp = workerProfileResponse.getWorkerDetail().get(0).getCV();
                                Glide.with(mContext).load(workerProfileResponse.getWorkerDetail().get(0).getWorkerImage()).into(imgProfile);

                                DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
                                DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                                String inputDateStr = workerProfileResponse.getWorkerDetail().get(0).getBirthDate();
                                //Log.e("inputDateStr", "" + inputDateStr);
                                Date date = inputFormat.parse(inputDateStr);
                                String outputDateStr = outputFormat.format(date);
                                //Log.e("outputDateStr", "" + outputDateStr);
                                txt_birth.setText(outputDateStr);

                                /*String Education = workerProfileResponse.getWorkerDetail().get(0).getString("Education");
                            if (Education.equals("") || Education.equals(null) || Education.equals("null") || Education == null) {
                            } else {
                                education.setVisibility(View.VISIBLE);
                                txt_education.setVisibility(View.VISIBLE);

                                Log.e("WP", "## Education :: " + workerProfileResponse.getWorkerDetail().get(0).getString("Education"));
                                txt_education.setText(workerProfileResponse.getWorkerDetail().get(0).getString("Education"));
                            }
                            String Experience = workerProfileResponse.getWorkerDetail().get(0).getString("Experience");

                            Log.e("WP", "## Experience :: " + Experience);
                            if (Experience.equals("") || Experience.equals(null) || Experience.equals("null") || Experience == null) {
                            } else {
                                experience.setVisibility(View.VISIBLE);
                                txt_experience.setVisibility(View.VISIBLE);
                                txt_experience.setText(workerProfileResponse.getWorkerDetail().get(0).getString("Experience"));
                            }*/

                                //set education list
                                if (workerProfileResponse.getWorkerDetail().get(0).getEducation().size() > 0) {
                                    education.setVisibility(View.VISIBLE);
                                    viewEducation.setVisibility(View.VISIBLE);

                                    recycleEducation.setItemAnimator(new DefaultItemAnimator());
                                    WorkerEducationAdapter productAdapter = new WorkerEducationAdapter(
                                            workerProfileResponse.getWorkerDetail().get(0).getEducation());
                                    recycleEducation.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                                    recycleEducation.setAdapter(productAdapter);
                                }


                                //set experiance list
                                if (workerProfileResponse.getWorkerDetail().get(0).getExperience().size() > 0) {
                                    experience.setVisibility(View.VISIBLE);
                                    viewExp.setVisibility(View.VISIBLE);

                                    recycleExperience.setItemAnimator(new DefaultItemAnimator());
                                    WorkerExperienceAdapter ExpAdapter = new WorkerExperienceAdapter(
                                            workerProfileResponse.getWorkerDetail().get(0).getExperience());
                                    recycleExperience.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                                    recycleExperience.setAdapter(ExpAdapter);
                                }
                            } else {
                                progress.setVisibility(View.GONE);
                                Utils.getInstance().showSnackBar(btnAddToMyRequest, getResources().getString(R.string.somethingWrong));
                                Log.e("##Response-", "## get failure");
                            }
                        }
                    } catch (Exception ee) {
                        ee.printStackTrace();
                        progress.setVisibility(View.GONE);
                        Utils.getInstance().showSnackBar(btnAddToMyRequest, getResources().getString(R.string.somethingWrong));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<WorkerProfileResponse> call, @NonNull Throwable t) {
                    progress.setVisibility(View.GONE);
                    t.printStackTrace();
                    Utils.getInstance().showSnackBar(btnAddToMyRequest, getResources().getString(R.string.somethingWrong));
                    Log.e("##Response-", "onFailure");
                }
            });
        } else {
            progress.setVisibility(View.GONE);

            Consts.getInstance().Act_vity = "Workerprofile";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }

       /* String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().WORKER_DETAIL_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                try {
                    progress.setVisibility(View.GONE);
                    JSONObject json_main = new JSONObject(response);
                    Log.e("response", "" + Consts.getInstance().WORKER_DETAIL_URL + json_main);
                    if (json_main.getString("success").equalsIgnoreCase("true")) {
                        JSONArray array_Worker_Detail = json_main.getJSONArray("Worker_Detail");
                        for (int i = 0; i < array_Worker_Detail.length(); i++) {
                            String Language = workerProfileResponse.getWorkerDetail().get(0).getString("Languages");
                            if (Language.equals("") || Language.equals(null) || Language.equals("null") || Language == null) {
                            } else {
                                language.setVisibility(View.VISIBLE);
                                txt_language.setVisibility(View.VISIBLE);
                                txt_language.setText(workerProfileResponse.getWorkerDetail().get(0).getString("Languages"));
                            }
                            String Education = workerProfileResponse.getWorkerDetail().get(0).getString("Education");
                            if (Education.equals("") || Education.equals(null) || Education.equals("null") || Education == null) {
                            } else {
                                education.setVisibility(View.VISIBLE);
                                txt_education.setVisibility(View.VISIBLE);

                                Log.e("WP","## Education :: "+workerProfileResponse.getWorkerDetail().get(0).getString("Education"));
                                txt_education.setText(workerProfileResponse.getWorkerDetail().get(0).getString("Education"));
                            }
                            String Experience = workerProfileResponse.getWorkerDetail().get(0).getString("Experience");

                            Log.e("WP","## Experience :: "+Experience);
                            if (Experience.equals("") || Experience.equals(null) || Experience.equals("null") || Experience == null) {
                            } else {
                                experience.setVisibility(View.VISIBLE);
                                txt_experience.setVisibility(View.VISIBLE);
                                txt_experience.setText(workerProfileResponse.getWorkerDetail().get(0).getString("Experience"));
                            }
                            ContractFees = workerProfileResponse.getWorkerDetail().get(0).getString("ContactFees");
                            txt_name.setText(workerProfileResponse.getWorkerDetail().get(0).getString("WorkerName"));
                            txt_nationality.setText(workerProfileResponse.getWorkerDetail().get(0).getString("Nationality"));
                            txt_Gender.setText(workerProfileResponse.getWorkerDetail().get(0).getString("Gender"));
                            txt_salary.setText(workerProfileResponse.getWorkerDetail().get(0).getString("MonthlySalary"));
                            txt_Religon.setText(workerProfileResponse.getWorkerDetail().get(0).getString("Religion"));
                            txt_contractFees.setText(workerProfileResponse.getWorkerDetail().get(0).getString("ContactFees"));
                            txt_categoryName.setText(workerProfileResponse.getWorkerDetail().get(0).getString("CategoryName"));
                            txt_category.setText(workerProfileResponse.getWorkerDetail().get(0).getString("CategoryName"));
                            txt_subcategory.setText(workerProfileResponse.getWorkerDetail().get(0).getString("SubCategoryName"));
                            txt_subcategoryName.setText(workerProfileResponse.getWorkerDetail().get(0).getString("SubCategoryName"));
                            videoUrl = workerProfileResponse.getWorkerDetail().get(0).getString("Video");
                            cvp=workerProfileResponse.getWorkerDetail().get(0).getString("CV");
                            Glide.with(mContext).load(workerProfileResponse.getWorkerDetail().get(0).getString("WorkerImage")).into(imgProfile);

                            DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
                            DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                            String inputDateStr = workerProfileResponse.getWorkerDetail().get(0).getString("BirthDate");
                            //Log.e("inputDateStr", "" + inputDateStr);
                            Date date = inputFormat.parse(inputDateStr);
                            String outputDateStr = outputFormat.format(date);
                            //Log.e("outputDateStr", "" + outputDateStr);
                            txt_birth.setText(outputDateStr);
                        }
                    }

                } catch (Exception e) {
                    progress.setVisibility(View.GONE);
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        progress.setVisibility(View.GONE);
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("language",preferences.getString("language",""));
                params.put("WorkerID", WorkerID);
                Log.e("params", "" + Consts.getInstance().WORKER_DETAIL_URL + params);
                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().WORKER_DETAIL_URL);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);*/

    }

    private void AddOrderRequestApi() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().ADD_ORDER_REQUEST, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                try {
                    progress.setVisibility(View.GONE);
                    JSONObject json_main = new JSONObject(response);
                    Log.e("response", "" + Consts.getInstance().ADD_ORDER_REQUEST + json_main);
                    String str_msg = json_main.getString("message");
                    if (json_main.getString("success").equalsIgnoreCase("true")) {
                        SharedPreferences preferences = getSharedPreferences("Login_Data", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("OrderID", json_main.getString("OrderID"));
                        editor.commit();
                        Intent intent = new Intent(mContext, MyRequestActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(mContext, str_msg, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    progress.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        progress.setVisibility(View.GONE);
                    }
                }) {
            protected Map<String, String> getParams() {
                String UserID = preferences_Login_Data.getString("UserID", "");
                if (UserID.equals("")) {
                    UserID = "0";
                }
                String OrderID = preferences_Login_Data.getString("OrderID", "");
                if (OrderID.equals("")) {
                    OrderID = "0";
                }
                Map<String, String> params = new HashMap<String, String>();
                params.put("language", preferences.getString("language", ""));
                params.put("WorkerID", WorkerID);
                params.put("WorkerContractFees", ContractFees);
                params.put("UserID", UserID);
                params.put("OrderID", OrderID);
                Log.e("params", "" + Consts.getInstance().ADD_ORDER_REQUEST + params);
                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(100000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().ADD_ORDER_REQUEST);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    protected void onResume() {
        if (Consts.isNetworkAvailable(WorkerProfileActivity.this)) {
            workerDetailApi();
        } else {
            Consts.getInstance().Act_vity = "Workerprofile";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }

    private void openFile(String url) {
        try {
            Uri uri = Uri.parse(url);
            Log.e("uri", "" + uri);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (url.contains(".doc") || url.contains(".docx")) {
                // Word document
                String docUrl = "https://docs.google.com/gview?embedded=true&url=" + url;
                Uri uri1 = Uri.parse(docUrl);
                intent.setDataAndType(uri1, "*/*");
                //intent.setDataAndType(uri, "application/msword");
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (url.toString().contains(".zip")) {
                // ZIP file
                intent.setDataAndType(uri, "application/zip");
            } else if (url.toString().contains(".rar")) {
                // RAR file
                intent.setDataAndType(uri, "application/x-rar-compressed");
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (url.toString().contains(".3gp") || url.toString().contains(".wmv") || url.toString().contains(".mpg") ||
                    url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                intent.setDataAndType(uri, "*/*");
            }
            // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(WorkerProfileActivity.this, "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
    }


    private void openVideo(String url) {
        Intent intentVideo=new Intent(mContext, VideoViewActivity.class);
        intentVideo.putExtra("videoUri",url);
        startActivity(intentVideo);

        /*try {
            //play video file in pop up
            mediacontroller = new MediaController(mContext);
            mediacontroller.setAnchorView(videoView);

            linearVideoDialog.setVisibility(View.VISIBLE);
            videoView.setVideoURI(Uri.parse(url));
            videoView.setMediaController(mediacontroller);
            videoView.start();

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    videoView.start();
                }
            });

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    //close the progress dialog when buffering is done
                    Log.e("WPA","## videoView onPrepared");
                    progressBar=findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.GONE);
                }
            });*/

//            Uri uri = Uri.parse(url);
//            Log.e("uri", "" + uri);
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            if (url.toString().contains(".3gp") || url.toString().contains(".wmv") || url.toString().contains(".mpg") ||
//                    url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
//                intent.setDataAndType(uri, "video/*");
//            } else {
//                intent.setDataAndType(uri, "*/*");
//            }
//            startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(WorkerProfileActivity.this, "No application found which can open the file", Toast.LENGTH_SHORT).show();
//        }
    }
}
