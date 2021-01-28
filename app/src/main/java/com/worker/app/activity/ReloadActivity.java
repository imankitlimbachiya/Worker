package com.worker.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.worker.app.HomeActivity;
import com.worker.app.R;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyButton;

public class ReloadActivity extends AppCompatActivity implements View.OnClickListener {

    Context mContext;
    MyButton reload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reload);

        getSupportActionBar().hide();

        mContext = this;

        init();
    }

    public void init() {
        reload = findViewById(R.id.reload);
        reload.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reload:
                switch (Consts.getInstance().Act_vity) {
                    case "Home":
                        startActivity(new Intent(ReloadActivity.this, HomeActivity.class));
                        break;
                    case "Createstep2":
                        startActivity(new Intent(ReloadActivity.this, Create_Request_Step_2.class));
                        break;
                    case "paymentactivity":
                        startActivity(new Intent(ReloadActivity.this, PaymentActivity.class));
                        break;
                    case "about":
                        startActivity(new Intent(ReloadActivity.this, AboutActivity.class));
                        break;
                    case "agreement":
                        startActivity(new Intent(ReloadActivity.this, AgreementPolicyActivity.class));
                        break;
                    case "annoucement":
                        startActivity(new Intent(ReloadActivity.this, AnnouncementActivity.class));
                        break;
                    case "Contactus":
                        startActivity(new Intent(ReloadActivity.this, ContactUsActivity.class));
                        break;
                    case "Createstep1":
                        startActivity(new Intent(ReloadActivity.this, Create_request_step_1.class));
                        break;
                    case "Createstep3":
                        startActivity(new Intent(ReloadActivity.this, CreateRequestActivity_3.class));
                        break;
                    case "login":
                        startActivity(new Intent(ReloadActivity.this, LoginActivity.class));
                        break;
                    case "matchingrequest":
                        startActivity(new Intent(ReloadActivity.this, MatchingRequestActivity.class));
                        break;
                    case "Orderlisting":
                        startActivity(new Intent(ReloadActivity.this, MyOrderListingActivity.class));
                        break;
                    case "Profile":
                        startActivity(new Intent(ReloadActivity.this, MyProfileActivity.class));
                        break;
                    case "Myrequest":
                        startActivity(new Intent(ReloadActivity.this, MyRequestActivity.class));
                        break;
                    case "Myrequestlisting":
                        startActivity(new Intent(ReloadActivity.this, MyRequestListingActivity.class));
                        break;
                    case "MyWorker":
                        startActivity(new Intent(ReloadActivity.this, MyWorkersActivity.class));
                        break;
                    case "Notification":
                        startActivity(new Intent(ReloadActivity.this, NotificationActivity.class));
                        break;
                    case "Ordersummery":
                        startActivity(new Intent(ReloadActivity.this, OrderSummeryActivity.class));
                        break;
                    case "Ourpromise":
                        startActivity(new Intent(ReloadActivity.this, OurPromisesActivity.class));
                        break;
                    case "Setting":
                        startActivity(new Intent(ReloadActivity.this, SettingActivity.class));
                        break;
                    case "showallcat":
                        startActivity(new Intent(ReloadActivity.this, ShowAllCategoryActivity.class));
                        break;
                    case "Showallcountry":
                        startActivity(new Intent(ReloadActivity.this, ShowAllCountryActivity.class));
                        break;
                    case "Signupcorporate":
                        startActivity(new Intent(ReloadActivity.this, SignUpCorporateActivity.class));
                        break;
                    case "SignupIndividual":
                        startActivity(new Intent(ReloadActivity.this, SignUpIndividualActivity.class));
                        break;
                    case "Subcat":
                        startActivity(new Intent(ReloadActivity.this, SubCategoryActivity.class));
                        break;
                    case "term":
                        startActivity(new Intent(ReloadActivity.this, TermsConditionsActivity.class));
                        break;
                    case "Vieworder":
                        startActivity(new Intent(ReloadActivity.this, ViewOrdreDeliveryActivity.class));
                        break;
                    case "Viewrequestdetail":
                        startActivity(new Intent(ReloadActivity.this, ViewRequestDeatailActivity.class));
                        break;
                    case "VisaDetail":
                        startActivity(new Intent(ReloadActivity.this, VisaDetailActivity.class));
                        break;
                    case "Wallet":
                        startActivity(new Intent(ReloadActivity.this, WalletActivity.class));
                        break;
                    case "Workerprofile":
                        startActivity(new Intent(ReloadActivity.this, WorkerProfileActivity.class));
                        break;
                    default:
                        finish();
                        break;
                }
                break;
        }
    }
}
