package com.worker.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.worker.app.BaseActivity;
import com.worker.app.R;


public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext;
    private RelativeLayout Rel_indi_signup, Rel_cor_signup, Rel_promises, Rel_terms, Rel_agreement;
    ImageView plus;
    private LinearLayout Lin_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext = this;

        init();
    }

    public void init() {
        Rel_cor_signup = findViewById(R.id.Rel_cor_signup);
        Rel_indi_signup = findViewById(R.id.Rel_indi_signup);
        Rel_promises = findViewById(R.id.Rel_promises);
        Rel_terms = findViewById(R.id.Rel_terms);
        Rel_agreement = findViewById(R.id.Rel_agreement);
        Lin_back = findViewById(R.id.Lin_back);
        plus = findViewById(R.id.plus);
        plus.setVisibility(View.GONE);

        Rel_indi_signup.setOnClickListener(this);
        Rel_cor_signup.setOnClickListener(this);
        Lin_back.setOnClickListener(this);
        Rel_promises.setOnClickListener(this);
        Rel_terms.setOnClickListener(this);
        Rel_agreement.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Rel_indi_signup:
                Intent intent = new Intent(mContext, SignUpIndividualActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.Rel_cor_signup:
                Intent i = new Intent(mContext, SignUpCorporateActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.Lin_back:
                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
                break;
            case R.id.Rel_promises:
                Intent intent2 = new Intent(mContext, OurPromisesActivity.class);
                startActivity(intent2);
                break;
            case R.id.Rel_terms:
                Intent intent3 = new Intent(mContext, TermsConditionsActivity.class);
                startActivity(intent3);
                break;
            case R.id.Rel_agreement:
                Intent intent4 = new Intent(mContext, AgreementPolicyActivity.class);
                startActivity(intent4);
                break;
        }
    }
}

