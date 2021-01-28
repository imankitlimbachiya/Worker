package com.worker.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.worker.app.R;
import com.worker.app.utility.Consts;

public class VideoViewActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout linearVideoDialog;
    private VideoView videoView;
    private ProgressBar progressBar;
    private MediaController mediacontroller;
    private LinearLayout linearHideVideo;
    private long lastClickTime = 0;
    Context mContext;
    private String videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        mContext = this;

        init();

        if(getIntent()!=null) {
            if (getIntent().hasExtra("videoUri")) {
                videoUri=getIntent().getStringExtra("videoUri");
                if(!videoUri.equals("")){
                    startVideo();
                }else
                    finish();
            } else {
                finish();
            }
        }else
            finish();
    }

    private void startVideo() {
        try {
            //play video file in pop up
            mediacontroller = new MediaController(mContext);
            mediacontroller.setAnchorView(videoView);

            linearVideoDialog.setVisibility(View.VISIBLE);
            videoView.setVideoURI(Uri.parse(videoUri));
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
                    Log.e("WPA", "## videoView onPrepared");
                    progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }catch (Exception ee){
            Log.e("Video",""+ee.getMessage());
            ee.printStackTrace();
        }
    }

    private void init() {
        progressBar = findViewById(R.id.progressBar);
        linearHideVideo = findViewById(R.id.linearHideVideo);
        linearHideVideo.setOnClickListener(this);
        linearVideoDialog = findViewById(R.id.linearVideoDialog);
        videoView = findViewById(R.id.videoView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearHideVideo:
                if (SystemClock.elapsedRealtime() - lastClickTime < Consts.getInstance().ClickTimeSeconds) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();

//                linearVideoDialog.setVisibility(View.GONE);
                finish();
                break;
        }
    }
}
