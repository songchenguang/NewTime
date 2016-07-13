package com.tencent.newtime.module.main_guest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.newtime.APP;
import com.tencent.newtime.R;
import com.tencent.newtime.base.BaseFragment;
import com.tencent.newtime.module.login.LoginActivity;
import com.tencent.newtime.module.splash.ChoiceActivity;
import com.tencent.newtime.util.StrUtils;

/**
 * Created by 晨光 on 2016-07-09.
 */
public class GuestMeFragment extends BaseFragment {

    private final String TAG = "GuestMeFragment";

    SimpleDraweeView userPhoto;
    TextView userName;
    TextView creditworthinessNum;
    TextView kindNum;
    TextView zealNum;
    TextView aboutNewTime;
    Button logoutButton;


    public static GuestMeFragment newInstance() {
        Bundle args = new Bundle();
        GuestMeFragment fragment = new GuestMeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_me_guest, container, false);
        userName = (TextView) rootView.findViewById(R.id.user_name_me_guest);
        userPhoto = (SimpleDraweeView) rootView.findViewById(R.id.user_photo_me_guest);
        creditworthinessNum = (TextView) rootView.findViewById(R.id.creditworthiness_num);
        kindNum = (TextView) rootView.findViewById(R.id.kind_num);
        zealNum = (TextView) rootView.findViewById(R.id.zeal_num);
        aboutNewTime = (TextView) rootView.findViewById(R.id.about_new_time_guest);
        logoutButton = (Button) rootView.findViewById(R.id.logout_button_guest);
        aboutNewTime.setOnClickListener(listener);
        logoutButton.setOnClickListener(listener);

        return rootView;
    }

    Button.OnClickListener listener = new Button.OnClickListener(){
        public void onClick(View view){
            switch (view.getId()){
                case R.id.about_new_time_guest:
                    aboutNewTime();
                    break;
                case R.id.logout_button_guest:
                    logout();
                    break;
            }
        }
    };

    public void aboutNewTime(){

    }

    private void logout(){
        SharedPreferences sp = APP.context().getSharedPreferences(StrUtils.SP_USER, Context.MODE_PRIVATE);
        sp.edit().remove(StrUtils.SP_USER_ID).remove(StrUtils.SP_USER_TOKEN).apply();
        Intent i = new Intent(getActivity(), ChoiceActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    protected String tag() {
        return TAG;
    }

}