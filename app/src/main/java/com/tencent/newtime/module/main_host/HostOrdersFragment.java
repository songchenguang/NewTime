package com.tencent.newtime.module.main_host;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.newtime.R;
import com.tencent.newtime.base.BaseFragment;
import com.tencent.newtime.model.DishHost;
import com.tencent.newtime.model.OrdersHost;
import com.tencent.newtime.util.LogUtils;

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
                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                if (!isLoading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + 2) && canLoadMore) {
                    Log.i(TAG, "scroll to end  load page " + (page + 1));
                    loadPage(page + 1);
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
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home_item_host, parent, false);
            vh = new ItemViewHolder(v);
            return vh;

        }

        class ItemViewHolder extends RecyclerView.ViewHolder{

            public ItemViewHolder(View itemView){
                super(itemView);

            }
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

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
