package com.tencent.newtime.module.main_host;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.newtime.APP;
import com.tencent.newtime.R;
import com.tencent.newtime.base.BaseFragment;
import com.tencent.newtime.module.register.AuthenticationActivity;
import com.tencent.newtime.module.splash.ChoiceActivity;
import com.tencent.newtime.util.OkHttpUtils;
import com.tencent.newtime.util.StrUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 晨光 on 2016-07-11.
 */
public class HostMeFragment extends BaseFragment {

    private static final String TAG = "HostMeFragment";

    TextView auth;
    Button logout;
    SimpleDraweeView userPhotoHost;
    TextView userNameMeHost;
    private String uriStr;
    private Handler mHandler;
    private String confirm;
    private boolean isConfirm=true;
    private String sellerName ="食光";
    private String token;
    private void getDataFromServer(Map map, final Handler mHandler){
        OkHttpUtils.post(getString(R.string.baseUrl_z) + getString(R.string.seller_z), map, TAG, new OkHttpUtils.OkCallBack() {
            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
                Log.d(TAG,"获取商家个人信息失败!");
            }
            @Override
            public void onResponse(String res) {
                JSONObject j;
                try {
                    j = new JSONObject(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "返回数据解析失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                String state = j.optString("state", "");
                if (state.equals("successful")) {
                    Toast.makeText(getActivity(), "验证成功", Toast.LENGTH_SHORT).show();
                    uriStr=j.optString("headImg","");
                    confirm=j.optString("confirm","");
                    sellerName =j.optString("sellerName","");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            refresh();
                        }
                    });
                } else {
                    String reason = j.optString("reason");
                    Toast.makeText(getActivity(), reason, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void refresh(){
        if(uriStr!=null&&!uriStr.equals("")){
            userPhotoHost.setImageURI(Uri.parse(uriStr));
        }
        if(confirm!=null&&!confirm.equals("1")){    //已认证
            isConfirm=true;
        }
        if(sellerName !=null&&!sellerName.equals("")){
            userNameMeHost.setText(sellerName);
        }

    }

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
        userPhotoHost=(SimpleDraweeView)rootView.findViewById(R.id.user_photo_me_host);
        userNameMeHost=(TextView)rootView.findViewById(R.id.user_name_me_host);
        logout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                logout();
            }
        });
        auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(isConfirm){
                   Intent intent = new Intent(getActivity(), AuthenticationActivity.class);
                   startActivity(intent);
               }else{
                   Toast.makeText(getActivity(),"您已通过认证",Toast.LENGTH_LONG).show();
               }
            }
        });
        token=StrUtils.token();
        mHandler=new Handler();
        Map map=new HashMap();
        map.put("token",token);
        getDataFromServer(map,mHandler);
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
