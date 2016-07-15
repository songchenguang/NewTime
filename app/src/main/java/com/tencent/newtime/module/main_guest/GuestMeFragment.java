package com.tencent.newtime.module.main_guest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.logging.LoggingDelegate;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.lbssearch.object.result.TransitResultObject;
import com.tencent.newtime.APP;
import com.tencent.newtime.R;
import com.tencent.newtime.base.BaseFragment;
import com.tencent.newtime.module.login.LoginActivity;
import com.tencent.newtime.module.splash.ChoiceActivity;
import com.tencent.newtime.util.LogUtils;
import com.tencent.newtime.util.OkHttpUtils;
import com.tencent.newtime.util.StrUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    String creditworthiness="90";
    String kind="90";
    String zeal="90";
    private Handler mHandler;

    private void getDataFromServer(Map<String,String> params, final Handler mHandler){
        OkHttpUtils.post(getString(R.string.baseUrl_z) + getString(R.string.person_z), params, TAG, new OkHttpUtils.OkCallBack() {
            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
                Log.d(TAG,"获取个人信息失败!");
            }
            @Override
            public void onResponse(String res) {
                JSONObject j;
                try {
                    j = new JSONObject(res);
                    LogUtils.d(TAG, "json"+j.toString());
                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(getActivity(), "返回数据解析失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                String state = j.optString("state", "");
                Uri uri = Uri.parse("http://119.29.233.72:3001/uploadfiles/shiguang/top/top5.jpg");
                userPhoto.setImageURI(uri);
                if (state.equals("successful")) {
//                    Toast.makeText(getActivity(), "验证成功", Toast.LENGTH_SHORT).show();
                    creditworthiness=j.optString("honesty","80");
                    kind=j.optString("friendly","80");
                    zeal=j.optString("passion","80");
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


    public static GuestMeFragment newInstance() {
        Bundle args = new Bundle();
        GuestMeFragment fragment = new GuestMeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private void refresh(){
        zealNum.setText(zeal);
        kindNum.setText(kind);
        creditworthinessNum.setText(creditworthiness);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_me_guest, container, false);
        userName = (TextView) rootView.findViewById(R.id.user_name_me_guest);
        userName.setVisibility(View.INVISIBLE);
        userPhoto = (SimpleDraweeView) rootView.findViewById(R.id.user_photo_me_guest);
        creditworthinessNum = (TextView) rootView.findViewById(R.id.creditworthiness_num);
        kindNum = (TextView) rootView.findViewById(R.id.kind_num);
        zealNum = (TextView) rootView.findViewById(R.id.zeal_num);
        aboutNewTime = (TextView) rootView.findViewById(R.id.about_new_time_guest);
        logoutButton = (Button) rootView.findViewById(R.id.logout_button_guest);
        aboutNewTime.setOnClickListener(listener);
        logoutButton.setOnClickListener(listener);
        mHandler=new Handler();
        String token=StrUtils.token();
//        Map map=new HashMap();
        ArrayMap<String,String> params = new ArrayMap<>(3);
        params.put("token", StrUtils.token());
//        map.put("token",token);
        getDataFromServer(params,mHandler);


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