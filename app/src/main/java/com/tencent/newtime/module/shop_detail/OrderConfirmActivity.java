package com.tencent.newtime.module.shop_detail;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.newtime.R;
import com.tencent.newtime.model.Kitchen;
import com.tencent.newtime.model.Order;
import com.tencent.newtime.module.main_guest.GuestMainActivity;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

//订单确认2_3确认结果
public class OrderConfirmActivity extends AppCompatActivity {

    private final String TAG="OrderConfirmActivity";
    private TextView orderKitchenNameTextView;
    private TextView orderDishTimeTextView;
    private TextView orderKitchenLocationTextView;
    private TextView orderPredictBillTextView;
    private Button waitConfirmButton;
    private Order mOrder=null;
    private Kitchen mKitchen=null;
    private Button orderBackMainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        orderKitchenNameTextView=(TextView)this.findViewById(R.id.order_kitchen_name_textView);
        orderDishTimeTextView=(TextView)this.findViewById(R.id.order_dish_time_textView);
        orderKitchenLocationTextView=(TextView)this.findViewById(R.id.order_kitchen_location_textView);
        orderPredictBillTextView=(TextView)this.findViewById(R.id.order_predict_bill_textView);
        waitConfirmButton=(Button)this.findViewById(R.id.wait_confirm_button);
        orderBackMainButton=(Button)this.findViewById(R.id.order_back_main_button);



        if(((mOrder=(Order)getIntent().getSerializableExtra("order"))!=null)
                &&((mKitchen=(Kitchen) getIntent().getSerializableExtra("kitchen"))!=null)){
            orderKitchenNameTextView.setText(mKitchen.getName());
            orderDishTimeTextView.setText(judgeKDishTime(mOrder.getArrayDate_1()));
            orderKitchenLocationTextView.setText(mKitchen.getLocation());
            orderPredictBillTextView.setText(String.valueOf(mOrder.getTotalPrice()));
        }else{
            Log.d(TAG,"获取传递过来的数据失败");
        }
        waitConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        orderBackMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OrderConfirmActivity.this,"回到主界面",Toast.LENGTH_LONG).show();
                Intent intentGuest = new Intent(OrderConfirmActivity.this, GuestMainActivity.class);
                startActivity(intentGuest);
                finish();
            }
        });
    }
    //4:00-11:00    早餐
    //11:00-15:00    午餐
    //15:00-20:00   晚餐
    //其他    夜宵
    private int judgeKDishTime(Date mDate){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(mDate);
        final int hour=calendar.get(Calendar.HOUR_OF_DAY);
        if(hour>=4&&hour<11){
            return R.string.breakfast_dish_time;
        }else if(hour>=11&&hour<15){
            return R.string.lunch_dish_time;
        }else if(hour>=15&&hour<20){
            return R.string.supper_dish_time;
        }else{
            return R.string.snack_dish_time;
        }
    }
}
