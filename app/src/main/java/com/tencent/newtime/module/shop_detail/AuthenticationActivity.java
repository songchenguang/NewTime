package com.tencent.newtime.module.shop_detail;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import com.tencent.newtime.R;
import com.tencent.newtime.util.MapUtils;
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
    private final  int RESULT_KITCHEN=13;
    private final  int RESULT_ENVIRONMENT=2;
    private final  int RESULT_SPECIALITY=3;
    private ImageView authenticationKitchenImageView;
    private ImageView authenticationKitchenEnvironmentImageView;
    private ImageView authenticationSpecialtyImageView;
    private EditText authNameEditText;
    private EditText authAddressEditText;
    private EditText authIdentificationEditText;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private Button submitButton;

    //存储各种图片,便于上传.  目前此处只传一张。
    //
    //requestCode与bitmapIndex与上传type需要一种联系.便于处理，目前只能枚举.
    private Bitmap[] bitmap=new Bitmap[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
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
                MapUtils.search(AuthenticationActivity.this,"南京航空航天大学江宁校区");
                boolean flag=false;
                String token=null;
                SharedPreferences sp = AuthenticationActivity.this.getSharedPreferences(StrUtils.SP_USER, MODE_PRIVATE);
                token=sp.getString(StrUtils.SP_USER_TOKEN,"");
                String userId=sp.getString(StrUtils.SP_USER_ID,"");
                if(token.equals("")||userId.equals("")){
                    Log.d(TAG,"获取token或者userID失败"+"token:"+token+"\tuserId:"+userId);
                    return ;
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

                            } else {
                                String reason = j.optString("reason");
                                Toast.makeText(AuthenticationActivity.this, reason, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                    //上传图片;
                    map.clear();
                    map.put("token",token);
                    map.put("number",userId); //这里上传userID,应该从sharepreference中或者intent中获取.
                    uploadPhoto(RESULT_KITCHEN,map);
                    //这里需要判断是否都上传成功了；
                    //跳转到菜单界面
                    startActivity(new Intent(AuthenticationActivity.this, AuthenticationActivity.class));

                }else {
                    Toast.makeText(AuthenticationActivity.this,authIdentificationEditText.getText().toString()+"   非法身份证号！",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void uploadPhoto(final int type, final Map<String,String> map) {
        map.put("type",String.valueOf(type));
        OkHttpUtils.uploadBitmap(getString(R.string.imgBaseUrl_z) + getString(R.string.imageUpload_z), map,bitmap[0]
                , null, TAG, new OkHttpUtils.OkCallBack() {
                    @Override
                    public void onFailure(IOException e) {
                        Toast.makeText(AuthenticationActivity.this,"图片上传失败",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String res) {
                        //Toast.makeText(AuthenticationActivity.this,"图片上传成功",Toast.LENGTH_LONG).show();
                        Log.d(TAG,"图片"+type+"上传成功");
                        map.remove("type");
                    }
                });
    }

    private void getLocation(String address){
        //返回地址。
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            ImageView view=null;
            int bitmapIndex=0;
            switch (requestCode){
                case RESULT_KITCHEN:{   //
                    //13       //homeImage
                    view= authenticationKitchenImageView;
                    bitmapIndex=0;
                    break;
                }
                case RESULT_ENVIRONMENT:{
                    //       //不要
                    view= authenticationKitchenEnvironmentImageView;
                    break;
                }
                case RESULT_SPECIALITY:{
                    //       //不要
                    view=authenticationSpecialtyImageView;
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

       /* if(bitmapIndex==0){
            Bitmap bitmap1=toRoundBitmap(bitmap[0]);
            photo.setImageBitmap(bitmap1);
        }else {

        }*/
        photo.setImageBitmap(bitmap[bitmapIndex]);
        photo.setMaxHeight(350);
    }



    public Bitmap toRoundBitmap(Bitmap bitmap){
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        int r=0;
        //取最短边做边长
        if(width<height){
            r=width;
        }else{
            r=height;
        }
        //构建一个bitmap
        Bitmap backgroundBm=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas=new Canvas(backgroundBm);
        Paint p=new Paint();
        //设置边缘光滑，去掉锯齿
        p.setAntiAlias(true);
        RectF rect=new RectF(0, 0, r, r);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r/2, r/2, p);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, p);
        return backgroundBm;
    }


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