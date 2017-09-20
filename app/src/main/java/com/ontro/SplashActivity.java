package com.ontro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.ontro.utils.PreferenceHelper;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends Activity {
    private PreferenceHelper preferenceHelper;
    private boolean islogged = false;
    private String isProfile = "0", isOtp = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        Fabric.with(this, new Crashlytics());

        preferenceHelper = new PreferenceHelper(this, Constants.APP_NAME, 0);
        islogged = preferenceHelper.getBoolean("is_logged", false);
        isProfile = preferenceHelper.getString("is_profile", "0");
        isOtp = preferenceHelper.getString("is_phone", "0");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("player_id")) {
            Constants.notificationview = 1;
            Constants.invitecountindicator = 1;
        }else if(bundle != null && bundle.containsKey("payment_status")){
            Constants.notificationview = 1;
        }
        Thread background = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (!islogged) {
                        IntroScreenDataBaseHelper databaseHandler = new IntroScreenDataBaseHelper(SplashActivity.this);
                        String status = databaseHandler.getIntroScreenStatus();
                        if (!status.equals("")) {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SplashActivity.this, IntroScreen.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        if (isProfile.equals("0") || isOtp.equals("0")) {
                            Intent intent = new Intent(SplashActivity.this, ProfileCompletionActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }
        };
        background.start();
    }
}
