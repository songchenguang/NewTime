package com.tencent.newtime.module.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.tencent.newtime.model.Home;
import com.tencent.newtime.module.detail.DetailActivity;
import com.tencent.newtime.util.LogUtils;
import com.tencent.newtime.util.DimensionUtils;
import com.tencent.newtime.widget.PageIndicator;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 晨光 on 2016-07-09.
 */

public class HomeFragment extends BaseFragment {

    private static final String TAG = "HomeFragment";
    // views
    private SwipeRefreshLayout mSwipeLayout;
    private BannerAdapter mBannerAdapter;
    private Adapter mRvAdapter;


    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    // data
    int page = 1;
    boolean isLoading = false;
    boolean isRefreshing = false;
    boolean canLoadMore = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home,container,false);
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_home_swipe_layout);
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

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_home_recycler_view);
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
        mBannerAdapter = new BannerAdapter(getActivity());
        mBannerAdapter.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = (int) v.getTag();
                Intent detail = new Intent(getActivity(), DetailActivity.class);
                detail.putExtra("activityid", id);
                //LogUtils.d(TAG, "id:" + mActivityList.get(getAdapterPosition() - 1).activityID);
                startActivity(detail);
            }
        });
        mRvAdapter = new Adapter();
        mRecyclerView.setAdapter(mRvAdapter);

        refresh();
        return rootView;
    }


    public void refresh(){
        isRefreshing = true;
        canLoadMore = true;
        List<TopInfo> infoList = new ArrayList<>();
        TopInfo test = new TopInfo();
        test.id = 0;
        test.url = "http://pic30.nipic.com/20130626/8174275_085522448172_2.jpg";
        infoList.add(test);
        mBannerAdapter.setInfoList(infoList);
        mBannerAdapter.notifyDataSetChanged();
    }


    private void loadPage(int page){

        List<Home> homeList = new ArrayList<>();
        mRvAdapter.setHomeList(homeList);
        mRvAdapter.notifyDataSetChanged();
        isRefreshing = false;
        isLoading = false;
        mSwipeLayout.setRefreshing(false);
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        static final int TYPE_BANNER = 1;
        static final int TYPE_BUTTON = 2;
        static final int TYPE_ITEM = 3;
        List<Home> mHomeList;

        public Adapter(){}


        public void setHomeList(List<Home> homeList){
            this.mHomeList = homeList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh;
            if (viewType == TYPE_BANNER){
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.banner_pager, parent, false);
                ViewGroup.LayoutParams params = v.getLayoutParams();
                params.height = DimensionUtils.getDisplay().widthPixels/2;
                v.setLayoutParams(params);
                vh = new BannerViewHolder(v);
            }else if(viewType == TYPE_BUTTON){
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home_button, parent, false);
                vh = new ButtonViewHolder(v);
            }else{
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home_item, parent, false);
                vh = new ItemViewHolder(v);
            }
            return vh;
        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof BannerViewHolder){
                BannerViewHolder banner = (BannerViewHolder) holder;
                banner.mViewPager.setAdapter(mBannerAdapter);
                banner.mIndicator.setViewPager(banner.mViewPager);
            }else if(holder instanceof ButtonViewHolder){
                ButtonViewHolder button = (ButtonViewHolder) holder;

            }else if(holder instanceof ItemViewHolder){

            }
        }

        @Override
        public int getItemCount() {
            if (mHomeList == null){
                return 2;
            }else{
                return 2 + mHomeList.size();
            }
        }

        @Override
        public int getItemViewType(int position){
            switch (position){
                case 0:
                    return TYPE_BANNER;
                case 1:
                    return TYPE_BUTTON;
                default:
                    return TYPE_ITEM;
            }
        }

        class BannerViewHolder extends RecyclerView.ViewHolder{
            ViewPager mViewPager;
            PageIndicator mIndicator;
            public BannerViewHolder(View bannerView){
                super(bannerView);
                mViewPager = (ViewPager) bannerView.findViewById(R.id.banner_pager_view);
                mIndicator = (PageIndicator) bannerView.findViewById(R.id.banner_pager_indicator);
            }
        }

        class ButtonViewHolder extends RecyclerView.ViewHolder{
            TextView distanceButton;
            TextView scoreButton;
            TextView priceButton;
            TextView salesVulumeButton;
            public ButtonViewHolder(View ButtonView){
                super(ButtonView);
                distanceButton = (TextView) ButtonView.findViewById(R.id.distance_button);
                scoreButton = (TextView) ButtonView.findViewById(R.id.score_button);
                priceButton = (TextView) ButtonView.findViewById(R.id.price_button);
                salesVulumeButton = (TextView) ButtonView.findViewById(R.id.sales_volume_button);
            }
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            SimpleDraweeView shopFoodImage;
            SimpleDraweeView shopImage;
            TextView shopName;
            TextView shopAddress;
            TextView shopSalesVolumeMonth;
            TextView shopScore;
            TextView shopAverage;
            public ItemViewHolder(View itemView){
                super(itemView);
                this.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent detail = new Intent(getActivity(), DetailActivity.class);
                        startActivity(detail);
                    }
                });
                shopFoodImage = (SimpleDraweeView) itemView.findViewById(R.id.fragment_home_item_food_image);
                shopImage = (SimpleDraweeView) itemView.findViewById(R.id.fragment_home_item_shop_image);
                shopName = (TextView) itemView.findViewById(R.id.fragment_home_item_shop_name);
                shopAddress = (TextView) itemView.findViewById(R.id.fragment_home_item_shop_address);
                shopSalesVolumeMonth = (TextView) itemView.findViewById(R.id.fragment_home_item_sales_volume_month);
                shopScore = (TextView) itemView.findViewById(R.id.fragment_home_item_shop_score);
                shopAverage = (TextView) itemView.findViewById(R.id.fragment_home_item_shop_average);
            }
        }
    }

    private static class TopInfo{
        public int id;
        public String url;
        public static TopInfo fromJSON(JSONObject j){
            TopInfo info = new TopInfo();
            info.id = j.optInt("activityid");
            info.url = j.optString("imageurl");
            return info;
        }
    }

    private static class BannerAdapter extends PagerAdapter{
        List<TopInfo> infoList;
        Context context;
        View.OnClickListener mListener;

        public BannerAdapter(Context context){
            this.context = context;
        }

        public void setInfoList(List<TopInfo> infoList) {
            this.infoList = infoList;
        }
        public void setListener(View.OnClickListener listener){
            mListener = listener;
        }

        @Override
        public int getCount() {
            return infoList==null?0:infoList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            SimpleDraweeView image = new SimpleDraweeView(context);
            Uri uri = Uri.parse(infoList.get(position).url);
            image.setImageURI(uri);
            LogUtils.d(TAG, "uri:"+ uri.toString());
            image.setTag(infoList.get(position).id);
            image.setOnClickListener(mListener);
            container.addView(image);
            return image;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    protected String tag() {
        return TAG;
    }

}
