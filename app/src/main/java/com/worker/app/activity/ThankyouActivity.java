package com.worker.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.worker.app.BaseActivity;
import com.worker.app.HomeActivity;
import com.worker.app.R;
import com.worker.app.utility.MyButton;

public class ThankyouActivity extends BaseActivity implements View.OnClickListener {

    MyButton btn_back;
    ImageView plus;
    Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou);

        mContext = this;

        Init();
    }

    public void Init() {
        plus = findViewById(R.id.plus);
        plus.setVisibility(View.GONE);
        btn_back = findViewById(R.id.btn_back);

        btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                Intent intent = new Intent(mContext, HomeActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(mContext, HomeActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
