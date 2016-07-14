package com.tencent.newtime.module.register;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.param.Address2GeoParam;
import com.tencent.lbssearch.object.result.Address2GeoResultObject;
import com.tencent.newtime.R;
import com.tencent.newtime.util.OkHttpUtils;
import com.tencent.newtime.util.StrUtils;
import com.tencent.newtime.util.UriToFilePath;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.tencent.newtime.util.UriToFilePath.calculateInSampleSize;

public class AuthenticationActivity extends AppCompatActivity {
    private final String TAG="AuthenticationActivity";
    private final  int RESULT_KITCHEN=1;   //商家头像
    private final  int RESULT_ENVIRONMENT=2;    //homeImage
    private final  int RESULT_SPECIALITY=3;     //拿手菜
    private ImageView authenticationKitchenImageView;
    private ImageView authenticationKitchenEnvironmentImageView;
    private ImageView authenticationSpecialtyImageView;
    private EditText authNameEditText;
    private EditText authAddressEditText;
    private EditText authIdentificationEditText;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private Button submitButton;
    private String token=null;
    private String userId=null;
    private Handler mHandler;
    private int successNum=0;   //记录图片上传成功的数量。

    //存储各种图片,便于上传.  目前此处只传一张。

    //type: 10      number :userId   商家头像
    //type: 13      number:userId   商家环境照片homeImage
    //type:15       number:3        拿手菜
    //requestCode与bitmapIndex与上传type需要一种联系.便于处理，目前只能枚举.
    private Bitmap[] bitmap=new Bitmap[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        mHandler=new Handler();
        authNameEditText=(EditText)findViewById(R.id.auth_name_editText);
        authAddressEditText=(EditText)findViewById(R.id.auth_identification_editText);
        authIdentificationEditText=(EditText)findViewById(R.id.auth_identification_editText);
        maleRadioButton=(RadioButton)findViewById(R.id.auth_male_radioButton);
        femaleRadioButton=(RadioButton)findViewById(R.id.auth_female_radioButton);
        submitButton=(Button)findViewById(R.id.auth_submit_button);
        authenticationSpecialtyImageView=(ImageView)findViewById(R.id.authentication_specialty_imageView);
        authenticationKitchenImageView =(ImageView)findViewById(R.id.authentication_kitchen_imageView);
        authenticationKitchenEnvironmentImageView = (ImageView) findViewById(R.id.authentication_kitchen_environment_imageView);
        authenticationKitchenEnvironmentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_ENVIRONMENT);
            }
        });
        authenticationSpecialtyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_SPECIALITY);
            }
        });
        authenticationKitchenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_KITCHEN);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"提交信息");
                boolean flag=false;
                SharedPreferences sp = AuthenticationActivity.this.getSharedPreferences(StrUtils.SP_USER, MODE_PRIVATE);
                token=sp.getString(StrUtils.SP_USER_TOKEN,"");
                userId=sp.getString(StrUtils.SP_USER_ID,"");
                if(token.equals("")||userId.equals("")){
                    Log.d(TAG,"获取token或者userID失败"+"token:"+token+"\tuserId:"+userId);
                    return ;
                }
                for(int i=0;i<bitmap.length;i++){
                    if(bitmap[i]==null){
                        Toast.makeText(AuthenticationActivity.this,"请上传三张图片",Toast.LENGTH_LONG).show();
                        return ;
                    }
                }
                if(personIdValidation(authIdentificationEditText.getText().toString())){
                    Map map= new HashMap<>();
                    map.put("token",token);
                    map.put("username",authNameEditText.getText());
                    String gender;
                    if(maleRadioButton.isChecked()){
                        gender="M";
                    }else{
                        gender="F";
                    }
                    map.put("userSex",gender);
                    map.put("userIdentity",authIdentificationEditText.getText().toString());
                    map.put("userLocation",authAddressEditText.getText().toString());
                    //还差经纬度;需要添加
                    // MapUtils.search(AuthenticationActivity.this);
                    submit(AuthenticationActivity.this,authAddressEditText.getText().toString(),mHandler,map);
                }else {
                    Toast.makeText(AuthenticationActivity.this,authIdentificationEditText.getText().toString()+"   非法身份证号！",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void uploadPhoto(final Map<String,String> map,int bitmapIndex,Handler mHandler) {
        OkHttpUtils.uploadBitmap(getString(R.string.imgBaseUrl_z) + getString(R.string.imageUpload_z), map,bitmap[bitmapIndex]
                , null, TAG, new OkHttpUtils.OkCallBack() {
                    @Override
                    public void onFailure(IOException e) {
                        Toast.makeText(AuthenticationActivity.this,"图片上传失败",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String res) {
                        //Toast.makeText(AuthenticationActivity.this,"图片上传成功",Toast.LENGTH_LONG).show();
                        Log.d(TAG,"图片上传成功");
                        successNum++;
                        if(successNum==3){
                            Log.d(TAG,"图片完全上传成功");
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            ImageView view=null;
            int bitmapIndex=0;
            switch (requestCode){
                case RESULT_KITCHEN:{
                    view= authenticationKitchenImageView;
                    bitmapIndex=0;
                    break;
                }
                case RESULT_ENVIRONMENT:{
                    view= authenticationKitchenEnvironmentImageView;
                    bitmapIndex=1;
                    break;
                }
                case RESULT_SPECIALITY:{
                    view=authenticationSpecialtyImageView;
                    bitmapIndex=2;
                    break;
                }
                default:{
                    Log.d(TAG,"返回的请求码错误：为"+requestCode);
                }
            }
            Uri selectedImage = data.getData();
            String filePath= UriToFilePath.getImageAbsolutePath(this,selectedImage);
            Toast.makeText(AuthenticationActivity.this,selectedImage.toString()+"\n文件路径："+filePath,Toast.LENGTH_LONG).show();
            showPhoto(view,filePath,bitmapIndex);
        }
    }


    private void showPhoto(ImageView photo,String picturePath,int bitmapIndex){
        if(picturePath.equals("")||photo==null)
            return;
        // Bitmap bitmap;
        // 缩放图片, width, height 按相同比例缩放图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        // options 设为true时，构造出的bitmap没有图片，只有一些长宽等配置信息，但比较快，设为false时，才有图片
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, options);
        options.inSampleSize = calculateInSampleSize(options, 600, 400);  //600:400：转换后的宽和高，具体值会有些出入
        options.inJustDecodeBounds = false;
        bitmap[bitmapIndex] = BitmapFactory.decodeFile(picturePath, options);
        // bitmap=BitmapFactory.decodeFile(picturePath);
        photo.setImageBitmap(bitmap[bitmapIndex]);
        photo.setMaxHeight(350);
    }

    public  void submit(Activity context, final String address, final Handler mHandler, final Map map){
        Log.d(TAG,"进入search()");
        //  final String address="深圳市南山区湾厦村招商路爱榕路2号";//"北京市海淀区彩和坊路海淀西大街74号";
        final String region=null;
        TencentSearch tencentSearch = new TencentSearch(context);
        Address2GeoParam param = new Address2GeoParam().
                address(address).
                region(region);
        tencentSearch.address2geo(param, new HttpResponseListener() {
            //如果成功会调用这个方法，用户需要在这里获取检索结果，调用自己的业务逻辑
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers,
                                  BaseObject object) {
                // TODO Auto-generated method stub
                if(object != null){
                    //这里的object是所有检索结果的父类
                    //用户需要将其转换为其实际类型以获取检索内容
                    //这里将其转换为Address2GeoResultObject

                    final Address2GeoResultObject oj =
                            (Address2GeoResultObject)object;
                    String result = "地址转坐标：地址:"+ address+
                            "  region:"+region + "\n\n";
                    if(oj.result != null){
                        Log.v("demo","location:" +
                                oj.result.location.toString());
                        result += oj.result.location.toString();
                    }
                    Log.d(TAG,result);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            map.put("longitude",String.valueOf(oj.result.location.lng));
                            map.put("latitude",String.valueOf(oj.result.location.lat));
                            submitIdentification(map,mHandler);
                        }
                    });
                }
            }
            @Override
            public void onFailure(int i, org.apache.http.Header[] headers,
                                  String s, Throwable throwable) {
                Log.d(TAG,s);
                Toast.makeText(AuthenticationActivity.this,"请输入详细地址",Toast.LENGTH_LONG).show();
            }
            //如果失败，会调用这个方法，可以在这里进行错误处理
        });
    }
    private void submitIdentification(final Map map, final Handler mHandler){
        OkHttpUtils.post(getString(R.string.baseUrl_z)+getString(R.string.auth_z),map,TAG,new OkHttpUtils.OkCallBack() {
            @Override
            public void onFailure(IOException e) {
                Log.d(TAG,"身份信息上传失败");
            }
            @Override
            public void onResponse(String res) {
                JSONObject j;
                try {
                    j = new JSONObject(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AuthenticationActivity.this, "返回数据解析失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                String state = j.optString("state", "");
                if (state.equals("successful")) {
                    Toast.makeText(AuthenticationActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            submitPhotos(mHandler);
                        }
                    });
                } else {
                    String reason = j.optString("reason");
                    Toast.makeText(AuthenticationActivity.this, reason, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void submitPhotos(Handler mHandler){
        Map map=new HashMap();
        map.put("token",token);
        map.put("type",10);
        map.put("number",userId);
        uploadPhoto(map,0,mHandler); //上传第一张图片.
        map.put("type",13);
        map.put("number",userId);
        uploadPhoto(map,1,mHandler);
        map.put("type",15);
        map.put("number",3);
        uploadPhoto(map,2,mHandler);
    }

   /* private void submitPhoto(Map map,int bitmapIndex){
        //上传图片;
        map.clear();
        map.put("token",token);
        map.put("number",userId); //这里上传userID,应该从sharepreference中或者intent中获取.
        uploadPhoto(map,bitmapIndex);
        //这里需要判断是否都上传成功了；
        //跳转到菜单界面
       // startActivity(new Intent(AuthenticationActivity.this, AuthenticationActivity.class));
    }*/


    /**
     * 验证身份证号是否符合规则
     * @param text 身份证号
     * @return
     */
    public boolean personIdValidation(String text) {
        String regx = "[0-9]{17}x";
        String reg1 = "[0-9]{15}";
        String regex = "[0-9]{18}";
        //  return text.matches(regx) || text.matches(reg1) || text.matches(regex);
        return true;
    }
}


/*
//压缩图片并将Bitmap保存到本地
FileOutputStream out = new FileOutputStream(new File(filePath));
saveBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);   //60代表压缩40%
* */

/*  Toast.makeText(AuthenticationActivity.this,uri.toString(),Toast.LENGTH_SHORT).show();
            Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            String img_path = actualimagecursor.getString(actual_image_column_index);*/