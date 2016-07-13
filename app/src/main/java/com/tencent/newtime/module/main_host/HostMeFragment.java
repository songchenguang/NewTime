package com.tencent.newtime.module.main_host;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.newtime.APP;
import com.tencent.newtime.R;
import com.tencent.newtime.base.BaseFragment;
import com.tencent.newtime.module.register.AuthenticationActivity;
import com.tencent.newtime.module.splash.ChoiceActivity;
import com.tencent.newtime.util.StrUtils;

/**
 * Created by 晨光 on 2016-07-11.
 */
public class HostMeFragment extends BaseFragment {

    private static final String TAG = "HostMeFragment";

    TextView auth;
    Button logout;

    public static HostMeFragment newInstance() {
        Bundle args = new Bundle();
        HostMeFragment fragment = new HostMeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_me_host, container, false);
        auth = (TextView) rootView.findViewById(R.id.auth_state_host);
        logout = (Button) rootView.findViewById(R.id.logout_button_host);
        logout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                logout();
            }
        });
        auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AuthenticationActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
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
