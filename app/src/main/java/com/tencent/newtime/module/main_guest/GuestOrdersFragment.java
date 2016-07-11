package com.tencent.newtime.module.main_guest;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    boolean orderState = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_orders_guest, container, false);
        ordersOn = (Button) rootView.findViewById(R.id.button_orders_on);
        ordersOff = (Button) rootView.findViewById(R.id.button_orders_off);
        ordersOn.setOnClickListener(listener);
        ordersOff.setOnClickListener(listener);

        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_orders_swipe_layout_guest);
        mSwipeLayout.setColorSchemeResources(R.color.colorPrimary);
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

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_orders_recycler_view_guest);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRvAdapter = new Adapter();
        mRecyclerView.setAdapter(mRvAdapter);

        refresh();
        return rootView;

    }

    Button.OnClickListener listener = new Button.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()){
                case R.id.button_orders_on:
                    orderState = true;
                    refresh();
                    break;
                case R.id.button_orders_off:
                    orderState = false;
                    refresh();
                    break;
            }
        }
    };

    private void refresh(){

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
            View view= LayoutInflater.from(getActivity()).inflate(R.layout.orders_item_guest, parent, false);
            vh = new GuestOrdersItemViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            OrdersGuest item = mOrdersList.get(position);

        }

        @Override
        public int getItemCount() {
            return mOrdersList==null?0:mOrdersList.size();
        }

        class GuestOrdersItemViewHolder extends RecyclerView.ViewHolder{

            public GuestOrdersItemViewHolder(View itemView){
                super(itemView);
                SimpleDraweeView shopImage = (SimpleDraweeView) itemView.findViewById(R.id.fragment_orders_item_shop_image_guest);
                TextView orderCancel = (TextView) itemView.findViewById(R.id.fragment_orders_item_cancel_guest);
                TextView orderState = (TextView) itemView.findViewById(R.id.fragment_orders_item_state);
                TextView shopName = (TextView) itemView.findViewById(R.id.fragment_orders_item_shop_name_guest);
                TextView dishName = (TextView) itemView.findViewById(R.id.fragment_orders_item_dish_name_guest);
                TextView dishPrice = (TextView) itemView.findViewById(R.id.fragment_orders_item_dish_price_guest);
                TextView orderTime = (TextView) itemView.findViewById(R.id.fragment_orders_item_time_guest);
                TextView orderTimeTogo = (TextView) itemView.findViewById(R.id.fragment_orders_item_time_togo);

            }

        }
    }

    @Override
    protected String tag() {
        return TAG;
    }

}