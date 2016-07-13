package com.tencent.newtime.module.main_guest;

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
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.newtime.R;
import com.tencent.newtime.base.BaseFragment;
import com.tencent.newtime.model.OrdersGuest;
import com.tencent.newtime.util.LogUtils;
import com.tencent.newtime.util.OkHttpUtils;
import com.tencent.newtime.util.StrUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 晨光 on 2016-07-09.
 */
public class GuestOrdersFragment extends BaseFragment {

    private final String TAG = "GuestOrdersFragment";

    // views
    private SwipeRefreshLayout mSwipeLayout;
    private Adapter mRvAdapter;
    private Button ordersOn;
    private Button ordersToGo;
    private Button ordersOff;
    RecyclerView mRecyclerView;


    public static GuestOrdersFragment newInstance() {
        Bundle args = new Bundle();
        GuestOrdersFragment fragment = new GuestOrdersFragment();
        fragment.setArguments(args);
        return fragment;
    }
    // data
    int page = 1;
    boolean isLoading = false;
    boolean isRefreshing = false;
    boolean canLoadMore = true;
    int orderState = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_orders_guest, container, false);
        ordersOn = (Button) rootView.findViewById(R.id.button_orders_on_guest);
        ordersToGo = (Button) rootView.findViewById(R.id.button_orders_to_go_guest);
        ordersOff = (Button) rootView.findViewById(R.id.button_orders_off_guest);
        ordersOn.setOnClickListener(listener);
        ordersOff.setOnClickListener(listener);
        ordersToGo.setOnClickListener(listener);

        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_orders_swipe_layout_guest);
        mSwipeLayout.setColorSchemeResources(R.color.colorPrimary);
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

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_orders_recycler_view_guest);
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
                case R.id.button_orders_on_guest:
                    orderState = 0;
                    loadPage(page);
                    break;
                case R.id.button_orders_to_go_guest:
                    orderState = 1;
                    loadPage(page);
                    break;
                case R.id.button_orders_off_guest:
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
                List<OrdersGuest> infoList = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    OrdersGuest info = OrdersGuest.fromJSON(array.optJSONObject(i));
                    infoList.add(info);
                }
                mRvAdapter.setOrdersList(infoList);
                mRvAdapter.notifyDataSetChanged();
                isRefreshing = false;
                isLoading = false;
                mSwipeLayout.setRefreshing(false);
            }
        });
    }

    private class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        List<OrdersGuest> mOrdersList;
        public Adapter(){}


        public void setOrdersList(List<OrdersGuest> ordersList){
            this.mOrdersList = ordersList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh;
            View view= LayoutInflater.from(getActivity()).inflate(R.layout.fragment_orders_item_guest, parent, false);
            vh = new ItemViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            OrdersGuest ordersGuest = mOrdersList.get(position);
            ItemViewHolder item = (ItemViewHolder) holder;
            Uri uriFoodImg = Uri.parse(ordersGuest.sellerHeadImg);
            item.shopImage.setImageURI(uriFoodImg);
            item.dishName.setText(ordersGuest.foodName);
            item.dishPrice.setText(ordersGuest.orderPrice);
            item.orderTime.setText(ordersGuest.orderTime);
            item.orderTimeTogo.setText(ordersGuest.orderPlanEatTime);
        }

        @Override
        public int getItemCount() {
            return mOrdersList==null?0:mOrdersList.size();
        }


        class ItemViewHolder extends RecyclerView.ViewHolder{
            SimpleDraweeView shopImage;
            TextView orderCancel;
            TextView orderState;
            TextView shopName;
            TextView dishName;
            TextView dishPrice;
            TextView orderTime;
            TextView orderTimeTogo;
            public ItemViewHolder(View itemView){
                super(itemView);
                shopImage = (SimpleDraweeView) itemView.findViewById(R.id.fragment_orders_item_shop_image_guest);
                orderCancel = (TextView) itemView.findViewById(R.id.fragment_orders_item_cancel_guest);
                orderState = (TextView) itemView.findViewById(R.id.fragment_orders_item_state_guest);
                shopName = (TextView) itemView.findViewById(R.id.fragment_orders_item_shop_name_guest);
                dishName = (TextView) itemView.findViewById(R.id.fragment_orders_item_dish_name_guest);
                dishPrice = (TextView) itemView.findViewById(R.id.fragment_orders_item_dish_price_guest);
                orderTime = (TextView) itemView.findViewById(R.id.fragment_orders_item_time_guest);
                orderTimeTogo = (TextView) itemView.findViewById(R.id.fragment_orders_item_time_togo_guest);

            }

        }
    }

    @Override
    protected String tag() {
        return TAG;
    }

}