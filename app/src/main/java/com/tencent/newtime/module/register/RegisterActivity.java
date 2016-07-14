package com.tencent.newtime.module.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.newtime.R;
import com.tencent.newtime.base.BaseActivity;
import com.tencent.newtime.module.main_guest.GuestMainActivity;
import com.tencent.newtime.module.main_host.HostMainActivity;
import com.tencent.newtime.util.LogUtils;
import com.tencent.newtime.util.OkHttpUtils;
import com.tencent.newtime.util.StrUtils;
import com.tencent.newtime.widget.CountDownButton;

import org.json.JSONObject;

import java.util.regex.Pattern;

public class RegisterActivity extends BaseActivity {

    private static final String TAG = "AtyRegister";

    Pattern phone = Pattern.compile(StrUtils.PHONE_PATTERN);

    EditText etName;
    EditText etPass;
    EditText etPass2;
    EditText etCode;
    Button btnCode;
    TextView tvRegister;
    TextView tvError;

    CountDownButton mCountDown;
    ProgressDialog progressDialog;
    private String identity;


    TextWatcher mTextWatcher;
    View.OnClickListener mListener;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        identity = getIntent().getStringExtra("identity");
        etName = (EditText) findViewById(R.id.phone);
        etPass = (EditText) findViewById(R.id.login_password);
        etPass2 = (EditText) findViewById(R.id.login_copy_password);
        tvRegister = (TextView) findViewById(R.id.register);
        tvError = (TextView) findViewById(R.id.aty_register_error);
        etCode = (EditText) findViewById(R.id.verification_code);
        btnCode = (Button) findViewById(R.id.gain_verification_code);

        mCountDown = new CountDownButton(btnCode,btnCode.getText().toString(),60,1);

        btnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode();
                mCountDown.start();
            }
        });


        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                checkText();
            }
        };

        mListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        };

        etName.addTextChangedListener(mTextWatcher);
        etPass.addTextChangedListener(mTextWatcher);
        etPass2.addTextChangedListener(mTextWatcher);
        etCode.addTextChangedListener(mTextWatcher);
        tvRegister.setOnClickListener(mListener);

    }

    private void sendCode(){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("phone",etName.getText().toString());
        param.put("type","1");
        String post = StrUtils.SEND_CODE;
        if (identity.equals("host")){
            post = StrUtils.SEND_CODE;
        }else if(identity.equals("guest")){
            post = StrUtils.SEND_CODE_CUS;
        }
        OkHttpUtils.post(post, param, TAG,new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.d(TAG,s);
                JSONObject j = OkHttpUtils.parseJSON(RegisterActivity.this,s);
                if(j != null){
                    Toast.makeText(RegisterActivity.this,R.string.send_code_complete,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkText(){
        if(!phone.matcher(etName.getText()).matches()){
            tvRegister.setEnabled(false);
            tvError.setText(R.string.please_input_phone);
            btnCode.setEnabled(false);
            return;
        }else{
            btnCode.setEnabled(true);
        }
        if(etCode.getText().length()==0){
            tvRegister.setEnabled(false);
            tvError.setText(R.string.code_length);
            return;
        }
        if(etPass.getText().length()<6){
            tvRegister.setEnabled(false);
            tvError.setText(R.string.password_long_6);
            return;
        }
        if(!etPass.getText().toString().equals(etPass2.getText().toString())){
            tvRegister.setEnabled(false);
            tvError.setText(R.string.password_not_equal);
            return;
        }
        tvRegister.setEnabled(true);
        tvError.setText("");
    }


    private void register(){
        String name = etName.getText().toString();
        String passMD5 = StrUtils.md5(etPass.getText().toString());
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("phone",name);
        param.put("password", passMD5);
        param.put("code",etCode.getText().toString());
        progressDialog = ProgressDialog.show(RegisterActivity.this,null,"正在注册");
        String post = StrUtils.REGISTER_PHONE;
        if (identity.equals("host")){
            post = StrUtils.REGISTER_PHONE;
        }else if(identity.equals("guest")){
            post = StrUtils.REGISTER_PHONE_CUS;
        }
        OkHttpUtils.post(post,param,TAG,new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG,s);
                final JSONObject j = OkHttpUtils.parseJSON(RegisterActivity.this, s);
                if(j == null){
                    progressDialog.dismiss();
                    return;
                }
                final String id = j.optString("id");
                final String token = j.optString("token");
                SharedPreferences sp = getSharedPreferences(StrUtils.SP_USER, MODE_PRIVATE);
                sp.edit().putString(StrUtils.SP_USER_ID, id)
                        .putString(StrUtils.SP_USER_TOKEN, token).apply();
                progressDialog.setMessage("注册成功");
                Handler handler = new Handler(getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (identity.equals("host")){
                            Intent i = new Intent(RegisterActivity.this, HostMainActivity.class);
                            startActivity(i);
                            finish();
                        }else if(identity.equals("guest")){
                            Intent i = new Intent(RegisterActivity.this, GuestMainActivity.class);
                            startActivity(i);
                            finish();
                        }

                    }
                }, 1000);
            }
        });
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
