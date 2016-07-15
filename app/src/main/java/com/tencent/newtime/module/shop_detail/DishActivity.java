package com.tencent.newtime.module.shop_detail;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.tencent.newtime.R;
import com.tencent.newtime.module.main_host.HostMainActivity;
import com.tencent.newtime.util.OkHttpUtils;
import com.tencent.newtime.util.StrUtils;
import com.tencent.newtime.util.UriToFilePath;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.tencent.newtime.util.UriToFilePath.calculateInSampleSize;

public class DishActivity extends AppCompatActivity {
    private final  String TAG="DishActivity";
    private final int RESULT=1;
    private EditText dishNameEditText;
    private EditText dishDescriptionEditText;
    private EditText dishPriceEditText;
    private ImageView dishPhotoImageView;
    private Button dishSubmitButton;
    private Bitmap mBitmap;
    private Handler mHandler;
    private final int TYPE_FOOD_PHOTO=12;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish);
        dishNameEditText=(EditText)this.findViewById(R.id.dish_name_editText);
        dishDescriptionEditText=(EditText)this.findViewById(R.id.dish_description_editText);
        dishPriceEditText=(EditText)this.findViewById(R.id.dish_price_editText);
        dishPhotoImageView=(ImageView) this.findViewById(R.id.dish_photo_imageView);
        dishSubmitButton=(Button) this.findViewById(R.id.dish_submit_button);
        mHandler=new Handler();
        dishPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT);
            }
        });
        dishSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check()){
                    SharedPreferences sp=DishActivity.this.getSharedPreferences(StrUtils.SP_USER,MODE_PRIVATE);
                    final String token=sp.getString(StrUtils.SP_USER_TOKEN,"");
                    final String userId=sp.getString(StrUtils.SP_USER_ID,"");
                    if(token.equals("")||userId.equals("")){
                        Log.d(TAG,"获取token或者userID失败"+"token:"+token+"\tuserId:"+userId);
                        return ;
                    }
                    final Map map=new HashMap();
                    map.put("token",token);
                    map.put("foodName",dishNameEditText.getText().toString());
                    map.put("description",dishDescriptionEditText.getText().toString());
                    map.put("price",dishPriceEditText.getText().toString());
                    OkHttpUtils.post(getString(R.string.baseUrl_z)+getString(R.string.commitDish_z),map,TAG,new OkHttpUtils.OkCallBack() {
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
                                Toast.makeText(DishActivity.this, "返回数据解析失败", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String state = j.optString("state", "");
                            if (state.equals("successful")) {
                                Toast.makeText(DishActivity.this, "上传菜品成功", Toast.LENGTH_SHORT).show();
                                final String foodId=j.optString("foodId","");
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //继续上传菜品图片。
                                        Map mapPhoto=new HashMap();
                                        mapPhoto.put("token",token);
                                        mapPhoto.put("number",foodId);
                                        uploadPhoto(TYPE_FOOD_PHOTO,mapPhoto,mBitmap,mHandler);

                                    }
                                });
                            } else {
                                String reason = j.optString("reason");
                                Toast.makeText(DishActivity.this, reason, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean check(){
        if(dishNameEditText.getText().toString().equals("")){
            Toast.makeText(DishActivity.this,"请输入菜名",Toast.LENGTH_LONG).show();
            return false;
        }
        if(!priceCheck()){
            return false;
        }
        if(mBitmap==null){
            Toast.makeText(DishActivity.this,"请添加图片",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }


    private boolean priceCheck(){

        try{
            Float.valueOf(dishPriceEditText.getText().toString());
        }catch (NumberFormatException e){
            Toast.makeText(DishActivity.this,"价格格式错误，请输入：数字，如：12.23",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null&&requestCode==RESULT){
            ImageView view=null;
            Uri selectedImage = data.getData();
            String filePath= UriToFilePath.getImageAbsolutePath(this,selectedImage);
//            Toast.makeText(DishActivity.this,selectedImage.toString()+"\n文件路径："+filePath,Toast.LENGTH_LONG).show();
            showPhoto(dishPhotoImageView,filePath);
        }
    }
    private void showPhoto(ImageView photo,String picturePath){
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
        mBitmap = BitmapFactory.decodeFile(picturePath, options);
        // bitmap=BitmapFactory.decodeFile(picturePath);
        photo.setImageBitmap(mBitmap);
        photo.setMaxHeight(350);

    }
    private void uploadPhoto(final int type, final Map<String,String> map, Bitmap bitmap, final Handler mHandler) {
        map.put("type",String.valueOf(type));
        OkHttpUtils.uploadBitmap(getString(R.string.imgBaseUrl_z) + getString(R.string.imageUpload_z), map,bitmap
                , null, TAG, new OkHttpUtils.OkCallBack() {
                    @Override
                    public void onFailure(IOException e) {
                        Toast.makeText(DishActivity.this,"图片上传失败",Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String res) {
                        //Toast.makeText(AuthenticationActivity.this,"图片上传成功",Toast.LENGTH_LONG).show();
                        Log.d(TAG,"图片"+type+"上传成功");
                        map.remove("type");

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(DishActivity.this, HostMainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                });
    }
}
