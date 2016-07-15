package com.tencent.newtime.module.shop_detail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.newtime.R;
import com.tencent.newtime.model.Dish;
import com.tencent.newtime.model.Kitchen;
import com.tencent.newtime.model.Order;
import com.tencent.newtime.model.OrderItem;
import com.tencent.newtime.util.LogUtils;
import com.tencent.newtime.util.OkHttpUtils;
import com.tencent.newtime.widget.IconTextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitchenNdActivity extends AppCompatActivity {
    private final String TAG="KitchenNdActivity";

    //第一层
    private RelativeLayout kitchenLayer1layout;
    private SimpleDraweeView kitchenPhotoImageView;
    private TextView   kitchenNameTextView;
    private TextView kitchenIsCertificationTextView;
    private TextView kitchenCertificationTextView;
    private TextView kitchenIntroductionTextView;
    private SimpleDraweeView kitchenOwnerPhotoImageView;
    private ListView    kitchenDishesListView;

    //第二层
    private TextView kitchenMaskTextView;
    private RelativeLayout kitchenLayout2Layout;
    private ListView orderDishesListView;
    private TextView guestCountSubTextView;
    private TextView guestCountAddTextView;
    private TextView guestCountTextView;


    //底部
    private RelativeLayout shoppingCartImageButton;
    private TextView kitchenOrderPriceTextView;
    private Button  kitchenTakeOrderButton;

    //测试数据

    Kitchen mKitchen=new Kitchen();
    ArrayList<Dish>dishList=new ArrayList<Dish>();
    Dish dishItem;


    Order order=new Order();
    ArrayList<OrderItem> list=new ArrayList<OrderItem>();
    OrderItem tempItem;
    private Handler mHandler=new Handler();
    public void prepareData(){
        mKitchen.setPhoto(null);
        mKitchen.setDescription("description:张大妈按时发生大发送到发送到发送到发送到发送到");
        mKitchen.setId(100);
        mKitchen.setCertification(true);
        mKitchen.setOwnerPhoto(null);
        mKitchen.setName("张大妈");

        for(int i=0;i<10;i++){
            dishItem=new Dish();
            dishItem.setId(i);
            dishItem.setDescription("阿斯顿发送到发送到发生大发生的发生的发生");
            dishItem.setPrice(i*2);
            dishItem.setMonthSales(i*3);
            dishItem.setPhoto(null);
            dishItem.setPid(mKitchen.getId());
            dishItem.setName("菜品——"+i);
            dishList.add(dishItem);
        }

        order.setTotalPrice(0);
        order.setGuestCount(1);

        mKitchen.setDishList(dishList);
       /* for(int i=0;i<3;i++){
            tempItem=new OrderItem();
            tempItem.setNum(1);
            tempItem.setDishId(i);
            tempItem.setPrice(i*2);
            tempItem.setDishName("菜名"+i);
            tempItem.setItemTotalPrice(0);
            list.add(tempItem);
        }*/
        order.setDishes(list);
    }
    //测试数据


    //发送商家token，返回商家信息，和商家的菜。
    private void getKitchenFromServer(final Handler mResponseHandler){
        Map map=new HashMap<String,String>();
        map.put("token",mKitchen.getToken());
        OkHttpUtils.post(getString(R.string.baseUrl_z) + getString(R.string.getKitchen_z), map, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j;
                try {
                    j = new JSONObject(s);
                    Log.d(TAG,"接受数据："+s);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(KitchenNdActivity.this, R.string.login_fail, Toast.LENGTH_SHORT).show();
                    return;
                }
                String state = j.optString("state", "");
                if (state.equals("successful")) {
                    String token=mKitchen.getToken();
                    mKitchen=Kitchen.fromJSON(j);
                    mKitchen.setToken(token);
                    try {
                        ArrayList<Dish>dishList=new ArrayList<Dish>();
                        mKitchen.setDishList(dishList);
                        JSONArray jsonArray=j.getJSONArray("foodlist");
                        JSONObject jsonObject;
                        Dish dishTemp;
                        for(int i=0;i<jsonArray.length();i++){
                            jsonObject=jsonArray.getJSONObject(i);
                            dishTemp=Dish.fromJSON(jsonObject);
                            mKitchen.getDishList().add(dishTemp);
                        }

                        mResponseHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                refreshData();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String reason = j.optString("reason");
                    Toast.makeText(KitchenNdActivity.this, reason, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_nd);
        //第一层

        kitchenDishesListView=(ListView) this.findViewById(R.id.kitchen_dishes_listView);

        //第二层
        kitchenMaskTextView =(TextView)this.findViewById(R.id.kitchen_mask_textView);
        kitchenLayout2Layout=(RelativeLayout) this.findViewById(R.id.kitchen_layer_2_layout);
        orderDishesListView=(ListView)this.findViewById(R.id.kitchen_order_dishes_listView);
        guestCountSubTextView=(TextView)this.findViewById(R.id.guest_count_sub_textView);
        guestCountAddTextView=(TextView)this.findViewById(R.id.guest_count_add_textView);
        guestCountTextView=(TextView)this.findViewById(R.id.guest_count_textView);


        //底部
        shoppingCartImageButton=(RelativeLayout)this.findViewById(R.id.kitchen_shopping_cart_imageButton);
        kitchenOrderPriceTextView=(TextView)this.findViewById(R.id.kitchen_order_total_price_textView);
        kitchenTakeOrderButton=(Button)this.findViewById(R.id.kitchen_takeOrder_button);
        String token;
        if((token = this.getIntent().getStringExtra("token"))!=null){
            mKitchen.setToken(token);
            LogUtils.d(TAG, "token:" + token);
        }
        mKitchen.setDishList(dishList);
        order.setDishes(list);
        order.setGuestCount(1);
        getKitchenFromServer(mHandler);
        //初始化工作
        kitchenOrderPriceTextView.setText(String.valueOf(order.getTotalPrice()));
        //  kitchenDishesListView.setAdapter(new DishListAdapter(this,android.R.layout.simple_list_item_1,mKitchen.getDishList()));
        // kitchenDishesListView.setAdapter(new DishListAdapter(mKitchen.getDishList()));
        orderDishesListView.setAdapter(new OrderListAdapter(this,android.R.layout.simple_list_item_1,order.getDishes()));

        shoppingCartImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(kitchenLayout2Layout.getVisibility()== View.GONE){
                    kitchenLayout2Layout.setVisibility(View.VISIBLE);
                }else{
                    kitchenLayout2Layout.setVisibility(View.GONE);
                }
            }
        });

        guestCountSubTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //小于等于零的问题
                Log.d(TAG,"subTextView");
                int guestCount=order.getGuestCount();
                if(guestCount>1){
                    guestCount--;
                    order.setGuestCount(guestCount);
                    guestCountTextView.setText(String.valueOf(guestCount));
                }
            }
        });

        guestCountAddTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"addTextView");
                int guestCount=order.getGuestCount();
                guestCount++;
                order.setGuestCount(guestCount);
                guestCountTextView.setText(String.valueOf(guestCount));
            }
        });

        kitchenMaskTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kitchenLayout2Layout.setVisibility(View.GONE);
            }
        });

        kitchenTakeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下单，需要传数据到下一个activity里面
                if(mKitchen==null||order==null||order.getDishes()==null||order.getDishes().size()==0){
                    Toast.makeText(KitchenNdActivity.this,"请点菜后再下单",Toast.LENGTH_LONG).show();
                }else{
                    Log.d(TAG,"size="+order.getDishes().size());
                    Intent i=new Intent(KitchenNdActivity.this,PayConfirmActivity.class);
                    i.putExtra("order",order);
                    i.putExtra("kitchen",mKitchen);
                    startActivity(i);
                }
            }
        });

    }

    private void refreshData(){
        /*   kitchenNameTextView.setText(mKitchen.getName());
        if(mKitchen.isCertification()){
            kitchenIsCertificationTextView.setText(R.string.if_right);
        }else{
            kitchenIsCertificationTextView.setText(R.string.if_wrong);
        }
        if(mKitchen.getDescription()!=null){
            kitchenIntroductionTextView.setText(mKitchen.getDescription());
        }
        if(mKitchen.getOwnerPhoto()!=null){
            kitchenOwnerPhotoImageView.setImageURI(Uri.parse(mKitchen.getOwnerPhoto()));
        }*/

        kitchenOrderPriceTextView.setText(String.valueOf(order.getTotalPrice()));
        kitchenDishesListView.setAdapter(new DishListAdapter(this,android.R.layout.simple_list_item_1,mKitchen.getDishList()));
        //kitchenDishesListView.setAdapter(new DishListAdapter(mKitchen.getDishList()));
        orderDishesListView.setAdapter(new OrderListAdapter(this,android.R.layout.simple_list_item_1,order.getDishes()));
        ((BaseAdapter)kitchenDishesListView.getAdapter()).notifyDataSetChanged();
    }

    class DishListAdapter extends BaseAdapter {
        ArrayList<Dish> dishList;
        public DishListAdapter(Context context, int resource, ArrayList<Dish> dishList) {
            // super(context, resource, dishList);

            this.dishList = dishList;
            Log.d(TAG,String.valueOf(dishList.size()));
        }
        @Override
        public int getCount() {
            return dishList.size()+1;
        }

        @Override
        public Object getItem(int position) {
            if(position>0)
                return dishList.get(position-1);
            return mKitchen;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Log.d(TAG,"getView " + position + " " + convertView);
            final int newPosition=position-1;
            if(position==0){
                convertView=getLayoutInflater().inflate(R.layout.kitchen_nd_list_zero,null);
                kitchenPhotoImageView=(SimpleDraweeView)convertView.findViewById(R.id.kitchen_photo_imageView);
                kitchenNameTextView=(TextView)convertView.findViewById(R.id.kitchen_name_textView);
                kitchenIsCertificationTextView =(TextView)convertView.findViewById(R.id.kitchen_is_certification_textView);
                kitchenCertificationTextView =(TextView)convertView.findViewById(R.id.kitchen_certification_textView);
                kitchenIntroductionTextView =(TextView)convertView.findViewById(R.id.kitchen_introduction_textView);
                kitchenOwnerPhotoImageView=(SimpleDraweeView)convertView.findViewById(R.id.kitchen_owner_photo_imageView);
                Uri ownerPhotoUri=Uri.parse(mKitchen.getOwnerPhoto());
                kitchenOwnerPhotoImageView.setImageURI(ownerPhotoUri);
                Uri kitchenPhotoUri=Uri.parse(mKitchen.getPhoto());
                kitchenPhotoImageView.setImageURI(kitchenPhotoUri);
                kitchenNameTextView.setText(mKitchen.getName());
                LogUtils.i(TAG," isCertification" +mKitchen.isCertification()+"");
                if(mKitchen.isCertification()){

                    kitchenIsCertificationTextView.setText(R.string.if_right);
                    kitchenCertificationTextView.setText("商家已认证");
                }else{
                    kitchenIsCertificationTextView.setText(R.string.if_wrong);
                    kitchenCertificationTextView.setText("商家未认证");
                }
            }else{
                ViewHolder viewHolder;
                convertView=getLayoutInflater().inflate(R.layout.kitchen_detail_dises_list_item,null);
                viewHolder=new ViewHolder();
                viewHolder.dishPhoto=(SimpleDraweeView) convertView.findViewById(R.id.kitchen_dish_photo);
                viewHolder.dishNameTextView=(TextView)convertView.findViewById(R.id.kitchen_dish_name);
                viewHolder.dishDescriptionTextView=(TextView)convertView.findViewById(R.id.kitchen_dish_description);
                viewHolder.dishAddButtonTextView=(TextView)convertView.findViewById(R.id.kitchen_add_this_dishes);
                viewHolder.dishPriceTextView=(TextView)convertView.findViewById(R.id.kitchen_dishes_price_textView);
                viewHolder.dishMonthSalesTextView=(TextView)convertView.findViewById(R.id.kitchen_dishes_month_sales_textView);
                viewHolder.dishAddButtonTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG,"菜数准备增加");
                        //查看是否已经有了这个菜，没有才添加。
                        OrderItem item=new OrderItem();
                        item.setDishId(dishList.get(newPosition).getId());
                        Log.d(TAG,"position"+String.valueOf(newPosition));
                        if(!order.getDishes().contains(item)){
                            Log.d(TAG,"菜数增加");

                            item.setDishName(dishList.get(newPosition).getName());
                            item.setPrice(dishList.get(newPosition).getPrice());
                            item.setItemTotalPrice(iniFloat(dishList.get(newPosition).getPrice()));
                            item.setNum(1);
                            order.getDishes().add(item);
                            order.setTotalPrice(iniFloat(order.getTotalPrice()+dishList.get(newPosition).getPrice()));
                            kitchenOrderPriceTextView.setText(String.valueOf(order.getTotalPrice()));
                            ((OrderListAdapter)orderDishesListView.getAdapter()).notifyDataSetChanged();
                        }else{
                            Toast.makeText(KitchenNdActivity.this,"这道菜已添加到购物栏",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //图片需要根据String:URL另外加载。此处全部用dish_1测试。
                if (dishList.get(newPosition).getPhoto() != null){
                    Uri dishPhotoUri=Uri.parse(dishList.get(newPosition).getPhoto());
                    viewHolder.dishPhoto.setImageURI(dishPhotoUri);

                }

                viewHolder.dishNameTextView.setText(String.valueOf(dishList.get(newPosition).getName()));
                viewHolder.dishPriceTextView.setText(String.valueOf(dishList.get(newPosition).getPrice()));
                viewHolder.dishMonthSalesTextView.setText(String.valueOf(dishList.get(newPosition).getMonthSales()));
                viewHolder.dishDescriptionTextView.setText(String.valueOf(dishList.get(newPosition).getDescription()));
            }
            return convertView;
        }

        class ViewHolder{
            SimpleDraweeView dishPhoto;
            TextView dishNameTextView;
            TextView dishDescriptionTextView;
            TextView dishMonthSalesTextView;
            TextView dishPriceTextView;
            TextView dishAddButtonTextView;
        }
    }
    class OrderListAdapter extends ArrayAdapter {

        ArrayList<OrderItem> orderList;
        public OrderListAdapter(Context context, int resource, ArrayList<OrderItem> list) {
            super(context, resource,list);
            orderList=list;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                Log.d(TAG, "orderListGetView " + position + " " + convertView);
                convertView = getLayoutInflater().inflate(R.layout.kitchen_detail_orders_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.dishNameTextView = (TextView) convertView.findViewById(R.id.kitchen_order_dish_name_textView);
                viewHolder.dishCountTextView = (TextView) convertView.findViewById(R.id.dishes_order_count_textView);
                viewHolder.dishCountAddTextView = (TextView) convertView.findViewById(R.id.dishes_order_count_add_textView);
                viewHolder.dishCountSubTextView = (TextView) convertView.findViewById(R.id.dishes_order_count_sub_textView);
                viewHolder.dishTotalPriceTextView = (TextView) convertView.findViewById(R.id.dishes_order_dish_total_price_textView);
                viewHolder.dishOrderDeleteTextView = (TextView) convertView.findViewById(R.id.dishes_order_delete_textVeiew);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.dishCountAddTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = orderList.get(position).getNum() + 1;
                    float price=orderList.get(position).getPrice();
                    float totalPrice=orderList.get(position).getItemTotalPrice();
                    float orderTotalPrice=order.getTotalPrice();
                    totalPrice+=price;
                    orderTotalPrice+=price;

                    orderList.get(position).setNum(count);
                    orderList.get(position).setItemTotalPrice(iniFloat(totalPrice));
                    order.setTotalPrice(iniFloat(orderTotalPrice));

                    viewHolder.dishCountTextView.setText(String.valueOf(count));
                    viewHolder.dishTotalPriceTextView.setText(String.valueOf(totalPrice));
                    kitchenOrderPriceTextView.setText(String.valueOf(order.getTotalPrice()));
                }
            });
            viewHolder.dishCountSubTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = orderList.get(position).getNum() - 1;
                    if(count>0){
                        float price=orderList.get(position).getPrice();
                        float totalPrice=orderList.get(position).getItemTotalPrice();
                        float orderTotalPrice=order.getTotalPrice();
                        totalPrice-=price;
                        orderTotalPrice-=price;

                        orderList.get(position).setNum(count);
                        orderList.get(position).setItemTotalPrice(iniFloat(totalPrice));
                        order.setTotalPrice(iniFloat(orderTotalPrice));

                        viewHolder.dishCountTextView.setText(String.valueOf(count));
                        viewHolder.dishTotalPriceTextView.setText(String.valueOf(totalPrice));
                        kitchenOrderPriceTextView.setText(String.valueOf(order.getTotalPrice()));
                    }
                }
            });
            viewHolder.dishOrderDeleteTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // orderList.remove(position);
                    Log.d(TAG, "删除订单项——" + position);
                    order.setTotalPrice(iniFloat(order.getTotalPrice()-orderList.get(position).getItemTotalPrice()));
                    orderList.remove(position);
                    kitchenOrderPriceTextView.setText(String.valueOf(order.getTotalPrice()));
                    ((OrderListAdapter) orderDishesListView.getAdapter()).notifyDataSetChanged();
                }
            });

            viewHolder.dishNameTextView.setText(String.valueOf(orderList.get(position).getDishName()));
            viewHolder.dishCountTextView.setText(String.valueOf(orderList.get(position).getNum()));
            viewHolder.dishTotalPriceTextView.setText(String.valueOf(orderList.get(position).getItemTotalPrice()));
            return convertView;
        }

        class ViewHolder{
            TextView dishNameTextView;
            TextView dishCountTextView;
            TextView dishTotalPriceTextView;
            TextView dishCountAddTextView;
            TextView dishCountSubTextView;
            TextView dishOrderDeleteTextView;
        }
    }
    public static float iniFloat(float a){
        float b = (float)(Math.round(a*100))/100;
        return b;
    }

}

