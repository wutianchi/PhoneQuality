package com.cattsoft.phone.quality;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import roboguice.activity.RoboSplashActivity;

/**
 * Created by Xiaohong on 2014/5/12.
 */
public class SplashActivity extends RoboSplashActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        minDisplayMs = 2 * 1000;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_splash);
    }

    @Override
    protected void doStuffInBackground(Application app) {
        super.doStuffInBackground(app);
    }

    @Override
    protected void startNextActivity() {
        startActivity(new Intent(getApplicationContext(), PhoneQualityActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
