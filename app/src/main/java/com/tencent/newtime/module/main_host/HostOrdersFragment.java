package com.tencent.newtime.module.main_host;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
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

    // data
    int page = 1;
    boolean isLoading = false;
    boolean isRefreshing = false;
    boolean canLoadMore = true;
    int orderState = 0;

    public static HostOrdersFragment newInstance() {
        Bundle args = new Bundle();
        HostOrdersFragment fragment = new HostOrdersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_guest, container, false);
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_orders_swipe_layout_host);
        mSwipeLayout.setColorSchemeColors(R.color.colorPrimary);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isRefreshing) {
                    LogUtils.d(TAG, "ignore manually update!");
                } else {
                    refresh();
                }
            }
        });

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_orders_recycler_view_host);
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
        return rootView;
    }
    private void refresh(){

    }

    private void loadPage(int page){
        ArrayMap<String,String> params = new ArrayMap<>(3);
//        params.put("token", StrUtils.token());
        params.put("token", "123456");
        params.put("page", "" + page);
        isLoading = true;
        OkHttpUtils.post(StrUtils.CUSTOMER_ORDER0, params, TAG, new OkHttpUtils.SimpleOkCallBack() {
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
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_orders_host, parent, false);
            vh = new ItemViewHolder(v);
            return vh;

        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
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
                    break;
                case 1:
                    item.orderPrice.setText(ordersHost.orderPrice);
                    item.orderTime.setText(ordersHost.orderPayTime);
                    break;
                case 2:
                    item.orderPrice.setText(ordersHost.orderPayPrice);
                    item.orderTime.setText(ordersHost.orderPayTime);
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
            }
        }




        @Override
        public int getItemCount() {
            return mOrdersHostList==null?1:1+mOrdersHostList.size();
        }
    }


    protected String tag() {
        return TAG;
    }

}
