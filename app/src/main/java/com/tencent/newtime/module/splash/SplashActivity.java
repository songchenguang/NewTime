package com.tencent.newtime.module.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;


import com.tencent.newtime.R;
import com.tencent.newtime.module.login.LoginActivity;
import com.tencent.newtime.module.main_guest.GuestMainActivity;
import com.tencent.newtime.module.main_host.HostMainActivity;
import com.tencent.newtime.util.*;

/**
 * Created by carlcgsong on 2016/07/09
 */
public class SplashActivity extends AppCompatActivity {

    private static final int DELAY_MILLIS = 1000;
    private static final String TAG = "SplashActivity";
    private View mContentView;
    private View mControlsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        mControlsView.setVisibility(View.GONE);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {SharedPreferences sp = getSharedPreferences(StrUtils.SP_USER, MODE_PRIVATE);
                String token = sp.getString(StrUtils.SP_USER_TOKEN, "");
                String identity = sp.getString(StrUtils.SP_USER_IDENTITY, "");
                Log.i(TAG, "identity" + identity);
                if (token.equals("")) {
                    choice();
                    overridePendingTransition(0,0);
                }else if(!token.equals("") && identity.equals("guest")){
                    mainGuest();
                    overridePendingTransition(0,0);
                }else if(!token.equals("") && identity.equals("host")){
                    mainHost();
                    overridePendingTransition(0,0);
                }
                finish();
            }
        },DELAY_MILLIS);
}

    private void choice(){
        Intent intent = new Intent(SplashActivity.this, ChoiceActivity.class);
        startActivity(intent);
    }

    private void mainGuest(){
        Intent intent = new Intent(SplashActivity.this, GuestMainActivity.class);
        startActivity(intent);
    }

    private void mainHost(){
        Intent intent = new Intent(SplashActivity.this, HostMainActivity.class);
        startActivity(intent);
    }
}
