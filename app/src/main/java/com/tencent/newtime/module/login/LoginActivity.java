package com.tencent.newtime.module.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.newtime.APP;
import com.tencent.newtime.R;
import com.tencent.newtime.module.main_guest.GuestMainActivity;
import com.tencent.newtime.module.main_host.HostDishFragment;
import com.tencent.newtime.module.main_host.HostMainActivity;
import com.tencent.newtime.module.register.RegisterActivity;
import com.tencent.newtime.util.LogUtils;
import com.tencent.newtime.util.OkHttpUtils;
import com.tencent.newtime.util.StrUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{

    // UI references.
    private AutoCompleteTextView mMobileView;
    private EditText mPasswordView;
    Button mSignInButton;

    private final String TAG = "LoginActivity";
    private String identity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        identity = getIntent().getStringExtra("identity");
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mMobileView = (AutoCompleteTextView) findViewById(R.id.mobile);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mSignInButton.setOnClickListener(listener);
        TextView mForgetPassword = (TextView) findViewById(R.id.login_text_view_forget_password);
        mForgetPassword.setOnClickListener(listener);
        TextView mRegister = (TextView) findViewById(R.id.login_text_view_register);
        mRegister.setOnClickListener(listener);
    }
    Button.OnClickListener listener = new Button.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.email_sign_in_button:
                    attemptLogin();
                    break;
                case R.id.login_text_view_register:
                    register();
                    break;
                case R.id.login_text_view_forget_password:
                    attemptLogin();
                    break;
            }
        }

    };
    private void attemptLogin() {

        // Reset errors.
        mMobileView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String mobile = mMobileView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid mobile address.
        if (TextUtils.isEmpty(mobile)) {
            mMobileView.setError(getString(R.string.error_field_required));
            focusView = mMobileView;
            cancel = true;
        } else if (!isMobileValid(mobile)) {
            mMobileView.setError(getString(R.string.error_invalid_email));
            focusView = mMobileView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            String passMd5 = StrUtils.md5(password);
            Map<String,String> map = new HashMap<>();
            map.put("username", mobile);
            map.put("password",passMd5);
            String post = StrUtils.LOGIN_URL;
            if(identity.equals("host")){
                post = StrUtils.LOGIN_URL;
            }else if(identity.equals("guest")){
                post = StrUtils.LOGIN_URL_CUS;
            }

            OkHttpUtils.post(post, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
                @Override
                public void onResponse(String s) {
                    LogUtils.i(TAG, s);
                    JSONObject j;
                    try {
                        j = new JSONObject(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                    String state = j.optString("state", "");
                    if (state.equals("successful")) {
                        String token = j.optString("token", "");
                        String id = j.optString("id", "");
                        String gender = j.optString("gender","");
                        SharedPreferences sp = APP.context().getSharedPreferences(StrUtils.SP_USER, MODE_PRIVATE);
                        sp.edit().putString(StrUtils.SP_USER_TOKEN, token)
                                .putString(StrUtils.SP_USER_ID, id)
                                .putString(StrUtils.SP_USER_IDENTITY, identity)
                                .putString(StrUtils.SP_USER_GENDER,gender).apply();
                        if (identity.equals("guest")){
                            startActivity(new Intent(LoginActivity.this, GuestMainActivity.class));
                        }else if(identity.equals("host")){
                            startActivity(new Intent(LoginActivity.this, HostMainActivity.class));
                        }
                        finish();
                    } else {
                        String reason = j.optString("reason");
                        Toast.makeText(LoginActivity.this, reason, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });

        }
    }

    public void register(){
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        registerIntent.putExtra("identity", identity);
        startActivity(registerIntent);
    }

    private boolean isMobileValid(String mobile) {

        Pattern p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
        Matcher m = p.matcher(mobile);
        boolean b = m.matches();
        return b;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


}

