package com.tencent.newtime.module.main_host;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.newtime.APP;
import com.tencent.newtime.R;
import com.tencent.newtime.base.BaseFragment;
import com.tencent.newtime.model.OrdersHost;
import com.tencent.newtime.util.LogUtils;
import com.tencent.newtime.util.OkHttpUtils;
import com.tencent.newtime.util.StrUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 晨光 on 2016-07-11.
 */
public class HostOrdersFragment extends BaseFragment {

    private static final String TAG = "HostOrdersFragment";

    // views
    private SwipeRefreshLayout mSwipeLayout;
    private Adapter mRvAdapter;
    RecyclerView mRecyclerView;
    // data
    int page = 1;
    boolean isLoading = false;
    boolean isRefreshing = false;
    boolean canLoadMore = true;
    int orderState = 0;
    private Button ordersOn;
    private Button ordersToGo;
    private Button ordersOff;
    public static HostOrdersFragment newInstance() {
        Bundle args = new Bundle();
        HostOrdersFragment fragment = new HostOrdersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_orders_host, container, false);

        ordersOn = (Button) rootView.findViewById(R.id.button_orders_on_host);
        ordersToGo = (Button) rootView.findViewById(R.id.button_orders_Todo_host);
        ordersOff = (Button) rootView.findViewById(R.id.button_orders_off_host);
        ordersOn.setOnClickListener(listener);
        ordersOff.setOnClickListener(listener);
        ordersToGo.setOnClickListener(listener);

        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_orders_swipe_layout_host);
        mSwipeLayout.setColorSchemeColors(R.color.colorPrimary);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isRefreshing) {
                    LogUtils.d(TAG, "ignore manually update!");
                } else {
                    loadPage(page);
                }
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_orders_recycler_view_host);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if(canLoadMore){
                        Log.d(TAG,"ignore manually load!");
                    } else{
                        loadPage(page + 1);
                        Log.d(TAG,"load page:" + page);
                        canLoadMore = false;
                    }
                }
            }

        });

        mRvAdapter = new Adapter();
        mRecyclerView.setAdapter(mRvAdapter);
        loadPage(page);
        return rootView;
    }

    Button.OnClickListener listener = new Button.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()){
                case R.id.button_orders_on_host:
                    orderState = 0;
                    loadPage(page);
                    break;
                case R.id.button_orders_Todo_host:
                    orderState = 1;
                    loadPage(page);
                    break;
                case R.id.button_orders_off_host:
                    orderState = 2;
                    loadPage(page);
                    break;
            }
        }
    };
    private void loadPage(int page){
        ArrayMap<String,String> params = new ArrayMap<>(3);
//        params.put("token", StrUtils.token());
        params.put("token", "123456");
        params.put("page", "" + page);
        isLoading = true;
        String post = StrUtils.SELLER_ORDER0;
        switch (orderState){
            case 0:
                post = StrUtils.SELLER_ORDER0;
                break;
            case 1:
                post = StrUtils.SELLER_ORDER1;
                break;
            case 2:
                post = StrUtils.SELLER_ORDER2;
                break;

        }
        OkHttpUtils.post(post, params, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                LogUtils.d(TAG, "response" + s);
                JSONObject j = OkHttpUtils.parseJSON(getActivity(),s);
                if(j==null){
                    return;
                }
                JSONArray array = j.optJSONArray("availableOrder");
                LogUtils.d(TAG, j.toString());
                if (array == null) return;
                List<OrdersHost> infoList = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    OrdersHost info = OrdersHost.fromJSON(array.optJSONObject(i));
                    infoList.add(info);
                }
                mRvAdapter.setOrdersHostList(infoList);
                mRvAdapter.notifyDataSetChanged();
                isRefreshing = false;
                isLoading = false;
                mSwipeLayout.setRefreshing(false);
            }
        });
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<OrdersHost> mOrdersHostList;

        public Adapter(){}

        public void setOrdersHostList(List<OrdersHost> ordersHostList) {
            this.mOrdersHostList = ordersHostList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder vh;
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_orders_item_host, parent, false);
            vh = new ItemViewHolder(v);
            return vh;

        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (mOrdersHostList == null){
                return;
            }
            OrdersHost ordersHost = mOrdersHostList.get(position);
            ItemViewHolder item = (ItemViewHolder) holder;
            Uri uriFoodImg = Uri.parse(ordersHost.customerHeadImg);
            item.customerHeadImg.setImageURI(uriFoodImg);
            item.customerFriendly.setText(ordersHost.customerFriendly);
            item.customerHonesty.setText(ordersHost.customerHonesty);
            item.customerPassion.setText(ordersHost.customerPassion);
            item.customerName.setText(ordersHost.customerName);
            item.foodName.setText(ordersHost.foodName);
            item.foodCounts.setText(ordersHost.foodCounts);
            switch (orderState){
                case 0:
                    item.orderPrice.setText(ordersHost.orderPrice);
                    item.orderTime.setText(ordersHost.planeEatTime);
                    item.orderStateButton.setText("婉拒");
                    item.cashState.setText("接单");
                    break;
                case 1:
                    item.orderPrice.setText(ordersHost.orderPrice);
                    item.orderTime.setText(ordersHost.orderPayTime);
                    item.orderStateButton.setText("取消接单");
                    item.cashState.setText("去结算");
                    break;
                case 2:
                    item.orderPrice.setText(ordersHost.orderPayPrice);
                    item.orderTime.setText(ordersHost.orderPayTime);
                    item.orderStateButton.setText("原价：" + ordersHost.orderPrice);
                    item.cashState.setText("折扣价：" + ordersHost.orderPayPrice);
                    break;
            }
        }
        class ItemViewHolder extends RecyclerView.ViewHolder{
            SimpleDraweeView customerHeadImg;
            TextView customerFriendly;
            TextView customerHonesty;
            TextView customerName;
            TextView customerPassion;
            TextView foodCounts;
            TextView foodName;
            TextView orderPeopleNumber;
            TextView orderPrice;
            TextView orderTime;
            TextView orderStateButton;
            TextView cashState;
            public ItemViewHolder(View itemView){
                super(itemView);
                customerHeadImg = (SimpleDraweeView) itemView.findViewById(R.id.fragment_orders_item_guest_image_host);
                customerFriendly = (TextView) itemView.findViewById(R.id.orders_item_friendly_host);
                customerHonesty = (TextView) itemView.findViewById(R.id.orders_item_honesty_host);
                customerName = (TextView) itemView.findViewById(R.id.fragment_orders_item_guest_name_host);
                customerPassion = (TextView) itemView.findViewById(R.id.orders_item_passion_host);
                foodCounts = (TextView) itemView.findViewById(R.id.fragment_orders_item_food_counts_host);
                foodName = (TextView) itemView.findViewById(R.id.fragment_orders_item_food_name_host);
                orderPeopleNumber = (TextView) itemView.findViewById(R.id.fragment_orders_item_people_number_host);
                orderPrice = (TextView) itemView.findViewById(R.id.fragment_orders_item_order_price_host);
                orderTime = (TextView) itemView.findViewById(R.id.fragment_orders_item_time_host);
                orderStateButton = (TextView) itemView.findViewById(R.id.fragment_orders_item_order_state_host);
                cashState = (TextView) itemView.findViewById(R.id.fragment_orders_item_cash_state_host);
                orderStateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (orderState){
                            case 0:
                                // 拒绝订单
                                DialogInterface.OnClickListener dialogOnclicListener=new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch(which){
                                            case Dialog.BUTTON_POSITIVE:
                                                ArrayMap<String,String> params = new ArrayMap<>(3);
                                                //        params.put("token", StrUtils.token());
                                                params.put("token", "123456");
                                                params.put("orderId", "" + mOrdersHostList.get(getAdapterPosition()).orderId);
                                                isLoading = true;
                                                OkHttpUtils.post(StrUtils.SELLER_CANCEL_ORDER, params, TAG, new OkHttpUtils.SimpleOkCallBack() {
                                                    @Override
                                                    public void onResponse(String s) {
                                                        LogUtils.d(TAG, "response" + s);
                                                        JSONObject j = OkHttpUtils.parseJSON(getActivity(),s);
                                                        if(j==null){
                                                            return;
                                                        }
                                                        if(j.optString("state").equals("successful")){
                                                            Toast.makeText(APP.context(), "取消订单成功", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                                break;
                                            case Dialog.BUTTON_NEGATIVE:
                                                Toast.makeText(getActivity(), "取消", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                break;
                                        }
                                    }
                                };
                                //dialog参数设置
                                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());  //先得到构造器
                                builder.setTitle("提示"); //设置标题
                                builder.setMessage("是否确认取消订单?"); //设置内容
                                builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
                                builder.setPositiveButton("确认",dialogOnclicListener);
                                builder.setNegativeButton("取消", dialogOnclicListener);
                                builder.create().show();
                                break;
                            case 1:
                                //取消订单
                                DialogInterface.OnClickListener dialogOnclicListener_1=new DialogInterface.OnClickListener(){

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch(which){
                                            case Dialog.BUTTON_POSITIVE:
                                                ArrayMap<String,String> params = new ArrayMap<>(3);
                                                //        params.put("token", StrUtils.token());
                                                params.put("token", "123456");
                                                params.put("orderId", "" + mOrdersHostList.get(getAdapterPosition()).orderId);
                                                isLoading = true;
                                                OkHttpUtils.post(StrUtils.SELLER_CANCEL_ORDER, params, TAG, new OkHttpUtils.SimpleOkCallBack() {
                                                    @Override
                                                    public void onResponse(String s) {
                                                        LogUtils.d(TAG, "response" + s);
                                                        JSONObject j = OkHttpUtils.parseJSON(getActivity(),s);
                                                        if(j==null){
                                                            return;
                                                        }
                                                        if(j.optString("state").equals("successful")){
                                                            Toast.makeText(APP.context(), "取消订单成功", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                                break;
                                            case Dialog.BUTTON_NEGATIVE:
                                                dialog.dismiss();
                                                Toast.makeText(getActivity(), "取消" + which, Toast.LENGTH_SHORT).show();
                                                break;
                                        }
                                    }
                                };
                                //dialog参数设置
                                AlertDialog.Builder builder_1=new AlertDialog.Builder(getActivity());  //先得到构造器
                                builder_1.setTitle("提示"); //设置标题
                                builder_1.setMessage("是否确认取消订单?"); //设置内容
                                builder_1.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
                                builder_1.setPositiveButton("确认",dialogOnclicListener_1);
                                builder_1.setNegativeButton("取消", dialogOnclicListener_1);
                                builder_1.create().show();
                                break;
                            case 2:
                                break;
                        }
                    }
                });

                cashState.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        switch (orderState){
                            case 0:
                                // 卖家接受订单
                                DialogInterface.OnClickListener dialogOnclicListener=new DialogInterface.OnClickListener(){

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch(which){
                                            case Dialog.BUTTON_POSITIVE:
                                                ArrayMap<String,String> params = new ArrayMap<>(3);
                                                //        params.put("token", StrUtils.token());
                                                params.put("token", "123456");
                                                params.put("orderId", "" + mOrdersHostList.get(getAdapterPosition()).orderId);
                                                isLoading = true;
                                                OkHttpUtils.post(StrUtils.SELLER_CONFIRM_ORDER, params, TAG, new OkHttpUtils.SimpleOkCallBack() {
                                                    @Override
                                                    public void onResponse(String s) {
                                                        LogUtils.d(TAG, "response" + s);
                                                        JSONObject j = OkHttpUtils.parseJSON(getActivity(),s);
                                                        if(j==null){
                                                            return;
                                                        }
                                                        if(j.optString("state").equals("successful")){
                                                            Toast.makeText(APP.context(), "接受订单成功", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                                break;
                                            case Dialog.BUTTON_NEGATIVE:
                                                Toast.makeText(getActivity(), "取消" + which, Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                break;
                                        }
                                    }
                                };
                                //dialog参数设置
                                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());  //先得到构造器
                                builder.setTitle("提示"); //设置标题
                                builder.setMessage("是否接受订单?"); //设置内容
                                builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
                                builder.setPositiveButton("确认",dialogOnclicListener);
                                builder.setNegativeButton("取消", dialogOnclicListener);
                                builder.create().show();
                                break;
                            case 1:
                                final EditText editText = new EditText(getActivity());
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                //先new出一个监听器，设置好监听
                                DialogInterface.OnClickListener dialogOnclicListener_1=new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch(which){
                                            case Dialog.BUTTON_POSITIVE:
                                                String price = editText.getText().toString();
                                                ArrayMap<String,String> params = new ArrayMap<>(6);
                                                //        params.put("token", StrUtils.token());
                                                params.put("token", "123456");
                                                params.put("orderId", "" + mOrdersHostList.get(getAdapterPosition()).orderId);
                                                params.put("discount", price);
                                                params.put("price", mOrdersHostList.get(getAdapterPosition()).orderPrice);
                                                params.put("payprice", "" + Integer.valueOf(price) * Integer.valueOf(mOrdersHostList.get(getAdapterPosition()).orderPrice));

                                                isLoading = true;
                                                OkHttpUtils.post(StrUtils.SELLER_REQUEST_PAY, params, TAG, new OkHttpUtils.SimpleOkCallBack() {
                                                    @Override
                                                    public void onResponse(String s) {
                                                        LogUtils.d(TAG, "response" + s);
                                                        JSONObject j = OkHttpUtils.parseJSON(getActivity(),s);
                                                        if(j==null){
                                                            return;
                                                        }
                                                        if(j.optString("state").equals("successful")){
                                                            Toast.makeText(APP.context(), "发起付款请求", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                                Toast.makeText(getActivity(), "确认" + which, Toast.LENGTH_SHORT).show();
                                                break;
                                            case Dialog.BUTTON_NEGATIVE:
                                                Toast.makeText(getActivity(), "取消" + which, Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                break;
                                        }
                                    }
                                };
                                //dialog参数设置
                                AlertDialog.Builder builder_1=new AlertDialog.Builder(getActivity());  //先得到构造器
                                builder_1.setTitle("请选择您给食客的折扣(0-10折)"); //设置标题
                                builder_1.setView(editText); // 输入折扣
                                builder_1.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
                                builder_1.setPositiveButton("确认",dialogOnclicListener_1);
                                builder_1.setNegativeButton("取消", dialogOnclicListener_1);
                                builder_1.create().show();
                                break;
                            case 2:

                                break;
                        }
                    }
                });

            }
        }
        @Override
        public int getItemCount() {
            return mOrdersHostList==null?0:mOrdersHostList.size();
        }
    }


    protected String tag() {
        return TAG;
    }

}
