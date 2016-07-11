package com.tencent.newtime.module.splash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tencent.newtime.R;
import com.tencent.newtime.module.main_guest.GuestMainActivity;
import com.tencent.newtime.module.main_host.HostMainActivity;

public class ChoiceActivity extends AppCompatActivity {

    Button guestButton;
    Button hostButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        guestButton = (Button) findViewById(R.id.button_guest);
        hostButton = (Button) findViewById(R.id.button_host);
        guestButton.setOnClickListener(listener);
        hostButton.setOnClickListener(listener);

    }
    Button.OnClickListener listener = new Button.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()){
                case R.id.button_guest:
                    Intent intentGuest = new Intent(ChoiceActivity.this, GuestMainActivity.class);
                    startActivity(intentGuest);
                    break;
                case R.id.button_host:
                    Intent intentHost = new Intent(ChoiceActivity.this, HostMainActivity.class);
                    startActivity(intentHost);

                    break;
            }
        }

    };
}
