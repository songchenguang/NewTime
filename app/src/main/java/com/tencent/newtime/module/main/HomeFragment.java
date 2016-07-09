package com.tencent.newtime.module.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.tencent.newtime.module.detail.DetailActivity;
import com.tencent.newtime.util.*;
import com.tencent.newtime.base.BaseFragment;
import com.tencent.newtime.R;
import com.tencent.newtime.model.Activity;
import com.tencent.newtime.widget.PageIndicator;


/**
 * Created by 晨光 on 2016-07-09.
 */

public class HomeFragment extends BaseFragment {

    private static final String TAG = "HomeFragment";

    // views
    private SwipeRefreshLayout mSwipeLayout;

    private TopAdapter mTopAdapter;
    private Adapter mRvAdapter;

    // data
    int page = 1;
    boolean isLoading = false;
    boolean isRefreshing = false;
    boolean canLoadMore = true;

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

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
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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



        mTopAdapter = new TopAdapter(getActivity());
        mTopAdapter.setListener(new View.OnClickListener() {
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

    private void refresh(){
        isRefreshing = true;
        canLoadMore = true;
        ArrayMap<String,String> params = new ArrayMap<>(3);
        params.put("token",StrUtils.token());
        OkHttpUtils.post(StrUtils.GET_TOP_ACTIVITY_URL, params, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                //LogUtils.i(TAG, s);
                JSONObject j = OkHttpUtils.parseJSON(getActivity(),s);
                if(j==null){
                    return;
                }
                JSONArray array = j.optJSONArray("result");
                if (array == null) return;
                List<TopInfo> infoList = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    TopInfo info = TopInfo.fromJSON(array.optJSONObject(i));
                    infoList.add(info);
                }
                mTopAdapter.setInfoList(infoList);
                mTopAdapter.notifyDataSetChanged();
            }
        });
        loadPage(1);
    }

    private void loadPage(int p){
        ArrayMap<String,String> params = new ArrayMap<>(3);
        params.put("token",StrUtils.token());
        params.put("page",p+"");
        isLoading = true;
        OkHttpUtils.post(StrUtils.GET_ACTIVITY_INFO_URL, params, TAG, new OkHttpUtils.SimpleOkCallBack() {

            @Override
            public void onResponse(String s) {
                //LogUtils.i(TAG, s);
                JSONObject j = OkHttpUtils.parseJSON(getActivity(),s);
                if(j==null){
                    return;
                }
                int returnedPage = j.optInt("pages");
                if(page != returnedPage){
                    page = returnedPage;
                }
                JSONArray array = j.optJSONArray("result");
                if(array == null) return;
                if(array.length()==0){
                    canLoadMore = false;
                    return;
                }
                List<Activity> activityList = new ArrayList<>();
                for(int i = 0; i<array.length(); i++){
                    Activity activity = Activity.fromJSON(array.optJSONObject(i));
                    activityList.add(activity);
                }
                mRvAdapter.setActivityList(activityList);
                mRvAdapter.notifyDataSetChanged();
                isRefreshing = false;
                isLoading = false;
                mSwipeLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected String tag() {
        return TAG;
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        static final int TYPE_TOP = 1;
        static final int TYPE_ACTIVITY = 2;
        List<Activity> mActivityList;

        public Adapter(){}

        public void setActivityList(List<Activity> activityList) {
            this.mActivityList = activityList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh;
            if(viewType==TYPE_TOP){
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.top_pager, parent, false);
                ViewGroup.LayoutParams params = v.getLayoutParams();
                params.height = DimensionUtils.getDisplay().widthPixels/2;
                v.setLayoutParams(params);
                vh = new TopViewHolder(v);
            }else{
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home_item,parent,false);
                vh = new ItemViewHolder(v);
            }
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof TopViewHolder){
                TopViewHolder top = (TopViewHolder) holder;
                top.mViewPager.setAdapter(mTopAdapter);
                top.mIndicator.setViewPager(top.mViewPager);
            }else{
                Activity activity = mActivityList.get(position-1);
                ItemViewHolder item = (ItemViewHolder) holder;
                item.mTvTitle.setText(activity.title);
                item.mAvatar.setImageURI(Uri.parse(StrUtils.thumForID(activity.authorID + "")));
                String count = activity.signNumber+"/"+activity.capacity;
                item.mTvCount.setText(count);
                item.mTvTime.setText(activity.time);
                item.mTvLocation.setText(activity.location);
                item.mTimeState.setText(activity.timeState);
                if(activity.timeState.equals("已结束")){
                    item.mTimeState.setTextColor(0xffc8c8dc);
                }else{
                    item.mTimeState.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                }
                int cc = Integer.parseInt(activity.top);
                if(cc!=0){
                    item.mTopImage.setVisibility(View.VISIBLE);
                }else{
                    item.mTopImage.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return mActivityList==null?1:1+mActivityList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position==0?TYPE_TOP:TYPE_ACTIVITY;
        }

        class TopViewHolder extends RecyclerView.ViewHolder{
            ViewPager mViewPager;
            PageIndicator mIndicator;
            public TopViewHolder(View itemView) {
                super(itemView);
                mViewPager = (ViewPager) itemView.findViewById(R.id.top_pager_view);
                mIndicator = (PageIndicator) itemView.findViewById(R.id.top_pager_indicator);
            }
        }
        class ItemViewHolder extends RecyclerView.ViewHolder{
            SimpleDraweeView mAvatar;
            TextView mTvTitle;
            TextView mTvCount;
            TextView mTvTime;
            TextView mTvLocation;
            TextView mTimeState;
            ImageView mTopImage;


            public ItemViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent detail = new Intent(getActivity(), DetailActivity.class);
//                        detail.putExtra(DetailActivity.INT//ENT, mActivityList.get(getAdapterPosition() - 1).activityID);
                        LogUtils.d(TAG, "id:" + mActivityList.get(getAdapterPosition() - 1).activityID);
                        startActivity(detail);
                    }
                });
                mAvatar = (SimpleDraweeView) itemView.findViewById(R.id.fragment_home_item_image);
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
                roundingParams.setRoundAsCircle(true);
                mAvatar.getHierarchy().setRoundingParams(roundingParams);
                mTvTitle = (TextView) itemView.findViewById(R.id.fragment_home_item_title);
                mTopImage.setRotation(45);
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

    private static class TopAdapter extends PagerAdapter {
        List<TopInfo> infoList;
        Context context;
        View.OnClickListener mListener;

        public TopAdapter(Context context){
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


}
