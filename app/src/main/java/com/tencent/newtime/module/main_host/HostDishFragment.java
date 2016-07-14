package com.tencent.newtime.module.main_host;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.newtime.R;
import com.tencent.newtime.base.BaseFragment;
import com.tencent.newtime.model.DishHost;
import com.tencent.newtime.module.shop_detail.DishActivity;
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
public class HostDishFragment extends BaseFragment {

    private static final String TAG = "HostDishFragment";
    // views
    private SwipeRefreshLayout mSwipeLayout;
    private Adapter mRvAdapter;
    RecyclerView mRecyclerView;
    // data
    int page = 1;
    boolean isLoading = false;
    boolean isRefreshing = false;
    boolean canLoadMore = true;
    boolean isPubulish = true;

    public static HostDishFragment newInstance() {
        Bundle args = new Bundle();
        HostDishFragment fragment = new HostDishFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_home_host, container, false);
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_home_swipe_layout_host);
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

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_home_recycler_view_host);
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
        FloatingActionButton fab = (FloatingActionButton)rootView.findViewById(R.id.fab_host);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dishIntent = new Intent(getActivity(), DishActivity.class);
                startActivity(dishIntent);

            }
        });

        mRvAdapter = new Adapter();
        mRecyclerView.setAdapter(mRvAdapter);
        loadPage(page);
        return rootView;
    }

    private void loadPage(int page){
        ArrayMap<String,String> params = new ArrayMap<>(3);
        params.put("token", StrUtils.token());
//        params.put("token", "123456");
        params.put("page", "" + page);
        isLoading = true;
        OkHttpUtils.post(StrUtils.SELLER_HOME_PAGE, params, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                LogUtils.d(TAG, "response" + s);
                JSONObject j = OkHttpUtils.parseJSON(getActivity(),s);
                if(j==null){
                    return;
                }
                JSONArray array = j.optJSONArray("foodList");
                LogUtils.d(TAG, j.toString());
                if (array == null) return;
                List<DishHost> infoList = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    DishHost info = DishHost.fromJSON(array.optJSONObject(i));
                    infoList.add(info);
                }
                mRvAdapter.setDishHostList(infoList);
                mRvAdapter.notifyDataSetChanged();
                isRefreshing = false;
                isLoading = false;
                mSwipeLayout.setRefreshing(false);
            }
        });
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<DishHost> mDishHostList;

        public Adapter(){}

        public void setDishHostList(List<DishHost> dishHostList) {
            this.mDishHostList = dishHostList;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder vh;
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home_item_host, parent, false);
            vh = new ItemViewHolder(v);
            return vh;
        }

            @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if(mDishHostList == null){
                    return;
                }
            DishHost dishHost = mDishHostList.get(position);
            ItemViewHolder item = (ItemViewHolder) holder;
            Uri uriFoodImg = Uri.parse(dishHost.foodImg);
            item.foodImageView.setImageURI(uriFoodImg);
            item.foodName.setText(dishHost.foodName);
            item.foodPrice.setText(dishHost.foodPrice);
            item.foodMonthSales.setText(dishHost.foodMonthSales);
            LogUtils.d(TAG, "disable:" + dishHost.disable);
            if (dishHost.disable == "1")
            {
                isPubulish = false;
                item.is_publish.setEnabled(false);
                item.is_publish.setText("下架中");
            }else {
                isPubulish = true;
                item.is_publish.setEnabled(true);
                item.is_publish.setText("上架中");
            }
        }
        class ItemViewHolder extends RecyclerView.ViewHolder{
            SimpleDraweeView foodImageView;
            TextView foodName;
            TextView foodMonthSales;
            TextView foodPrice;
            Button is_publish;

            public ItemViewHolder(View itemView){
                super(itemView);
                foodImageView = (SimpleDraweeView) itemView.findViewById(R.id.home_item_food_image_host);
                foodName = (TextView) itemView.findViewById(R.id.home_item_food_name_host);
                foodMonthSales = (TextView) itemView.findViewById(R.id.home_item_food_month_sales_host);
                foodPrice = (TextView) itemView.findViewById(R.id.home_item_food_price_host);
                is_publish = (Button) itemView.findViewById(R.id.is_publish);
                is_publish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayMap<String,String> params = new ArrayMap<>(3);
                        params.put("token", StrUtils.token());
//        params.put("token", "123456");
                        params.put("foodid", mDishHostList.get(getAdapterPosition()).foodId);
                        String disable = mDishHostList.get(getAdapterPosition()).disable.equals("0")?"1":"0";
                        params.put("disable", disable);
                        isLoading = true;
                        OkHttpUtils.post(StrUtils.EDIT_FOOD, params, TAG, new OkHttpUtils.SimpleOkCallBack() {
                            @Override
                            public void onResponse(String s) {
                                LogUtils.d(TAG, "response" + s);
                                JSONObject j = OkHttpUtils.parseJSON(getActivity(),s);
                                if(j==null){
                                    return;
                                }
                                String state = j.optString("state");
                                String reason = j.optString("reason");
                                Toast.makeText(getActivity(), state, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        }
        @Override
        public int getItemCount() {
            return mDishHostList==null?0:mDishHostList.size();
        }
    }


    protected String tag() {
        return TAG;
    }

}
