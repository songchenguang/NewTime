package com.tencent.newtime.module.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.tencent.newtime.R;
import com.tencent.newtime.module.login.LoginActivity;
import com.tencent.newtime.module.shop_detail.WebBannerActivity;
import com.tencent.newtime.util.LogUtils;

public class ChoiceActivity extends CheckPermissionsActivity {

    LinearLayout guestButton;
    LinearLayout hostButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        guestButton = (LinearLayout) findViewById(R.id.button_guest);
        hostButton = (LinearLayout) findViewById(R.id.button_host);
        guestButton.setOnClickListener(listener);
        hostButton.setOnClickListener(listener);

    }
    LinearLayout.OnClickListener listener = new LinearLayout.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()){
                case R.id.button_guest:
                    Intent intentGuest = new Intent(ChoiceActivity.this, LoginActivity.class);
                    intentGuest.putExtra("identity", "guest");
                    startActivity(intentGuest);
//                    finish();
                    break;
                case R.id.button_host:
                    Intent intentHost = new Intent(ChoiceActivity.this, LoginActivity.class);
                    intentHost.putExtra("identity", "host");
                    startActivity(intentHost);
//                    finish();
                    break;
            }
        }

    };
}
