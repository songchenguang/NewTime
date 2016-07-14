package com.tencent.newtime.module.shop_detail;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.newtime.R;
import com.tencent.newtime.model.Dish;
import com.tencent.newtime.model.Kitchen;
import com.tencent.newtime.model.Order;
import com.tencent.newtime.model.OrderItem;
import com.tencent.newtime.util.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Guang on 2016/7/11.
 */
public class PayConfirmActivity extends AppCompatActivity{
    private final String TAG="PayConfirmActivity";
    private SimpleDraweeView payKitchenPhotoImageView;
    private TextView payKitchenNameTextView;
    private TextView payKitchenLocationTextView;
    // private TextView payArrayTime2TextView;
    private TextView payArrayTime1TextView;
    private TextView payArrayDateTextView;
    private TextView payNoteTextView;
    private TextView paySetTimeTextView;
    private TextView paySetNoteTextView;
    private ListView payOrderListView;
    private TextView payBillTextView;
    private Button   payConfirmButton;
    private TextView payGuestCountTextView;
    private RelativeLayout paySetNoteLayout;
    private RelativeLayout paySetTimeLayout;
    Kitchen mKitchen=new Kitchen();
    ArrayList<Dish> dishList=new ArrayList<Dish>();
    Dish dishItem;
    Order order=new Order();
    ArrayList<OrderItem> list=new ArrayList<OrderItem>();
    OrderItem tempItem;

    private void prepareData() {
        mKitchen.setPhoto(null);
        mKitchen.setDescription("description:张大妈按时发生大发送到发送到发送到发送到发送到");
        mKitchen.setId(100);
        mKitchen.setCertification(true);
        mKitchen.setOwnerPhoto(null);
        mKitchen.setName("张大妈");
        mKitchen.setLocation("深南大道1006号");
        for (int i = 0; i < 10; i++) {
            dishItem = new Dish();
            dishItem.setId(i);
            dishItem.setDescription("阿斯顿发送到发送到发生大发生的发生的发生");
            dishItem.setPrice(i * 2);
            dishItem.setMonthSales(i * 3);
            dishItem.setPhoto(null);
            dishItem.setPid(mKitchen.getId());
            dishItem.setName("菜品——" + i);
            dishList.add(dishItem);
        }
        mKitchen.setDishList(dishList);


        order.setTotalPrice(100);
        order.setGuestCount(3);

        order.setArrayDate_1(new Date(System.currentTimeMillis()));
        for (int i = 0; i < 3; i++) {
            tempItem = new OrderItem();
            tempItem.setNum(i);
            tempItem.setDishId(i);
            tempItem.setPrice(i * 2);
            tempItem.setDishName("菜名" + i);
            tempItem.setItemTotalPrice(i * 2 * i);
            list.add(tempItem);
        }
        order.setDishes(list);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_confirm);
        payKitchenPhotoImageView=(SimpleDraweeView) this.findViewById(R.id.pay_kitchen_photo_imageView);
        payKitchenNameTextView=(TextView)this.findViewById(R.id.pay_kitchen_name_textView);
        payKitchenLocationTextView=(TextView)this.findViewById(R.id.pay_kitchen_location_textView);
        payArrayTime1TextView =(TextView)this.findViewById(R.id.pay_array_time_1_textView);
        payArrayDateTextView=(TextView)this.findViewById(R.id.pay_array_date_textView);
        payNoteTextView=(TextView)this.findViewById(R.id.pay_note_textView);
        paySetTimeTextView=(TextView)this.findViewById(R.id.pay_set_time_textView);
        paySetNoteTextView=(TextView)this.findViewById(R.id.pay_set_note_textView);
        payOrderListView=(ListView)this.findViewById(R.id.pay_order_listView);
        payBillTextView=(TextView)this.findViewById(R.id.pay_bill_textView);
        payConfirmButton=(Button)this.findViewById(R.id.pay_confirm_button);
        payGuestCountTextView=(TextView)this.findViewById(R.id.pay_guest_count_textView);
        paySetNoteLayout=(RelativeLayout)this.findViewById(R.id.pay_set_note_layout);
        paySetTimeLayout=(RelativeLayout)this.findViewById(R.id.pay_set_time_layout);


        if(((order=(Order)getIntent().getSerializableExtra("order"))!=null)
                &&((mKitchen=(Kitchen) getIntent().getSerializableExtra("kitchen"))!=null)){
            order.setArrayDate_1(new Date(System.currentTimeMillis()));
            order.setNote(getString(R.string.pay_note_default));

            Log.d(TAG,"头像Uri"+mKitchen.getOwnerPhoto());
        }else{
            Log.d(TAG,"传递数据失败");
            prepareData();
        }

        updateDateTime();

        //初始化
        payKitchenNameTextView.setText(mKitchen.getName());
        payKitchenLocationTextView.setText(mKitchen.getLocation());
        payOrderListView.setAdapter(new PayConfirmOrderAdapter(this,android.R.layout.simple_list_item_1,order.getDishes()));
        payGuestCountTextView.setText(String.valueOf(order.getGuestCount()));
        payBillTextView.setText(String.valueOf(order.getTotalPrice()));
        payKitchenPhotoImageView.setImageURI(Uri.parse(mKitchen.getOwnerPhoto()));

        paySetNoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出窗口，设置备注
                Log.d(TAG, "设置备注");
                final EditText noteEditView=new EditText(PayConfirmActivity.this);
                new AlertDialog.Builder(PayConfirmActivity.this) .setTitle("请输入")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(noteEditView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                payNoteTextView.setText(noteEditView.getText());
                                order.setNote(noteEditView.getText().toString());
                            }
                        })
                        .setNegativeButton("取消", null) .show();
            }
        });

        paySetTimeLayout.setOnClickListener(new View.OnClickListener()

        {
            @TargetApi(23)
            @Override
            public void onClick (View v){
                Log.d(TAG, "设置时间");
                Calendar calendar=Calendar.getInstance();
                calendar.setTime(order.getArrayDate_1());

                int year=calendar.get(Calendar.YEAR);
                final int month=calendar.get(Calendar.MONTH);
                final int day=calendar.get(Calendar.DAY_OF_MONTH);
                final int hour=calendar.get(Calendar.HOUR_OF_DAY);
                final int minute=calendar.get(Calendar.MINUTE);


                View dtView= PayConfirmActivity.this.getLayoutInflater().inflate(R.layout.dialog_date_time,null);
                final DatePicker datePicker=(DatePicker)dtView.findViewById(R.id.dialog_date_picker);
                datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        order.setArrayDate_1(new GregorianCalendar(year,monthOfYear,dayOfMonth).getTime());
                    }
                });

                final TimePicker timePicker=(TimePicker) dtView.findViewById(R.id.dialog_timePicker);
                if(Build.VERSION_CODES.M<=Build.VERSION.SDK_INT){
                    timePicker.setHour(hour);
                    timePicker.setMinute(minute);
                    timePicker.setIs24HourView(true);
                }else{
                    timePicker.setCurrentMinute(hour);
                    timePicker.setCurrentMinute(minute);
                    timePicker.setIs24HourView(true);
                }
                new AlertDialog.Builder(PayConfirmActivity.this)
                        .setTitle(R.string.pay_set_time_title)
                        .setView(dtView)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                order.setArrayDate_1(new Date(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth(),
                                        timePicker.getHour(),timePicker.getMinute()));
                                updateDateTime();
                            }
                        }).create().show();
            }
        });

        payConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"确认下单按钮");
                //提交数据。。。
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateStr=format.format(order.getArrayDate_1());
                // StringBuilder foodlist=new StringBuilder();
                Log.d(TAG,dateStr);
                JSONObject orderJSON=new JSONObject();
                JSONObject jsonObjectTemp;
                List<OrderItem> list=order.getDishes();
                ArrayList<ArrayList<Integer>> foodlist=new ArrayList<ArrayList<Integer>>();
                ArrayList<Integer> orderItemList;
                try {
                    orderJSON.put("sellerId",mKitchen.getToken());
                    orderJSON.put("token","123456");
                    orderJSON.put("peopleNumber",order.getGuestCount());
                    orderJSON.put("planEatTime",dateStr);
                    for (int i=0;i<list.size();i++){
                        orderItemList=new ArrayList<Integer>();
                        orderItemList.add(list.get(i).getDishId());
                        orderItemList.add(list.get(i).getNum());
                        foodlist.add(orderItemList);
                    }
                    orderJSON.put("foodList",foodlist);
                    Log.d(TAG,orderJSON.toString());
                    OkHttpUtils.post(getString(R.string.baseUrl_z)+getString(R.string.commitOrder_z)
                            ,orderJSON,TAG,new OkHttpUtils.SimpleOkCallBack(){
                                @Override
                                public void onResponse(String s) {
                                    Log.d("ithinker", s);
                                    JSONObject j;
                                    try {
                                        j = new JSONObject(s);
                                        Log.d(TAG,"接受数据："+s);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(PayConfirmActivity.this, R.string.login_fail, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    String state = j.optString("state", "");
                                    if (state.equals("successful")) {
                                        Log.d(TAG,"上传成功");
                                        Intent i=new Intent(PayConfirmActivity.this,OrderConfirmActivity.class);
                                        i.putExtra("order",order);
                                        i.putExtra("kitchen",mKitchen);
                                        startActivity(i);
                                    } else {
                                        String reason = j.optString("reason");
                                        Toast.makeText(PayConfirmActivity.this, reason, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void updateDateTime(){
        Date date=order.getArrayDate_1();
        payArrayDateTextView.setText(DateFormat.format("MM月dd日",date));
        payArrayTime1TextView.setText(DateFormat.format("HH:mm",date));
    }


    class PayConfirmOrderAdapter extends ArrayAdapter {
        ArrayList<OrderItem> mOrderItemArrayList;

        public PayConfirmOrderAdapter(Context context, int resource, List<OrderItem> objects) {
            super(context, resource, objects);
            mOrderItemArrayList=(ArrayList)objects;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG,"getView " + position + " " + convertView);
            convertView=getLayoutInflater().inflate(R.layout.pay_confirm_order_list_view_item,null);
            TextView dishName=(TextView)convertView.findViewById(R.id.pay_order_list_dish_name);
            TextView totalPrice=(TextView)convertView.findViewById(R.id.pay_order_list_dish_total_price);
            TextView dishCount=(TextView)convertView.findViewById(R.id.pay_guest_count_textView);
            dishName.setText(mOrderItemArrayList.get(position).getDishName());
            dishCount.setText(String.valueOf(mOrderItemArrayList.get(position).getNum()));
            totalPrice.setText(String.valueOf(mOrderItemArrayList.get(position).getItemTotalPrice()));
            return convertView;
        }
    }
}

//提示框，可以添加时间。
/*
 new AlertDialog.Builder(PayConfirmActivity.this).setTitle("系统提示")//设置对话框标题

                        .setMessage("请确认所有数据都保存后再推出系统！")//设置显示的内容

                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮


                            @Override

                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                                // TODO Auto-generated method stub

                                finish();

                            }

                        }).setNegativeButton("返回", new DialogInterface.OnClickListener() {//添加返回按钮


                    @Override

                    public void onClick(DialogInterface dialog, int which) {//响应事件

                        // TODO Auto-generated method stub

                        Log.i("alertdialog", " 请保存数据！");

                    }

                }).show();//在按键响应事件中显示此对话框
 */
