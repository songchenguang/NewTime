package com.tencent.newtime.module.main_guest;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.newtime.R;
import com.tencent.newtime.base.BaseFragment;
import com.tencent.newtime.model.HomeGuest;
import com.tencent.newtime.module.shop_detail.KitchenNdActivity;
import com.tencent.newtime.util.LogUtils;
import com.tencent.newtime.util.DimensionUtils;
import com.tencent.newtime.util.OkHttpUtils;
import com.tencent.newtime.util.StrUtils;
import com.tencent.newtime.widget.PageIndicator;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 晨光 on 2016-07-09.
 */

public class GuestHomeFragment extends BaseFragment  implements TencentLocationListener {

    private static final String TAG = "GuestHomeFragment";
    // views
    private SwipeRefreshLayout mSwipeLayout;
    private BannerAdapter mBannerAdapter;
    private Adapter mRvAdapter;

    private RelativeLayout location_layout;
    private TextView location_textview;
    // 商家数据
    List<HomeGuest> infoList;
    private TencentLocationManager mLocationManager;

    public static GuestHomeFragment newInstance() {
        Bundle args = new Bundle();
        GuestHomeFragment fragment = new GuestHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    // data
    int page = 1;
    boolean isLoading = false;
    boolean isRefreshing = false;
    boolean canLoadMore = true;
    String longitude;
    String latitude;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_guest,container,false);
        location_layout = (RelativeLayout) rootView.findViewById(R.id.location_layout);
        mLocationManager=TencentLocationManager.getInstance(getActivity());
        location_layout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                refreshLocation();
            }
        });
        location_textview = (TextView) rootView.findViewById(R.id.location_tetview);
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_home_swipe_layout_guest);
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

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_home_recycler_view_guest);
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
        mBannerAdapter = new BannerAdapter(getActivity());
        mBannerAdapter.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = (int) v.getTag();
                Intent detail = new Intent(getActivity(), KitchenNdActivity.class);
                detail.putExtra("token", infoList.get(id).sellerId);
                //LogUtils.d(TAG, "id:" + mActivityList.get(getAdapterPosition() - 1).activityID);
                startActivity(detail);
            }
        });
        mRvAdapter = new Adapter();
        mRecyclerView.setAdapter(mRvAdapter);


        refresh();
        return rootView;
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {
        Log.d(TAG,"S="+s+"     I="+i+"     s1="+s1);
    }

    public  void startLocation(){
        TencentLocationRequest request = TencentLocationRequest.create()
                .setInterval(2000)  // 设置定位周期
                .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA);  	// 设置定位level
        int errorNo=mLocationManager.requestLocationUpdates(request, this);
        Log.d(TAG,"注册监听返回码："+errorNo);
    }


    public void stopLocation(){
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int error, String reason) {
        String msg = null;
        if (error == TencentLocation.ERROR_OK) {
            // 定位成功
//            msg = locationToString(tencentLocation);
            msg = tencentLocation.getStreetNo();
            longitude = ""+(float)tencentLocation.getLongitude();
            latitude = ""+(float)tencentLocation.getLatitude();
            Log.d(TAG, "location："+ longitude + " " + latitude + " " + tencentLocation.getLatitude()+" " +tencentLocation.getLongitude());
            location_textview.setText(msg);
            stopLocation();
        } else {
            // 定位失败
            msg = "定位失败: " + reason;
            location_textview.setText(msg);
        }
        Log.d("位置改变",error+":"+msg);
    }


    private  String locationToString(TencentLocation location) {
        StringBuilder sb = new StringBuilder();
        sb.append("latitude=").append(location.getLatitude()).append(",");
        sb.append("longitude=").append(location.getLongitude()).append(",");
        sb.append("altitude=").append(location.getAltitude()).append(",");
        sb.append("accuracy=").append(location.getAccuracy()).append(",");
        sb.append("nation=").append(location.getNation()).append(",");
        sb.append("province=").append(location.getProvince()).append(",");
        sb.append("city=").append(location.getCity()).append(",");
        sb.append("district=").append(location.getDistrict()).append(",");
        sb.append("town=").append(location.getTown()).append(",");
        sb.append("village=").append(location.getVillage()).append(",");
        sb.append("street=").append(location.getStreet()).append(",");
        sb.append("streetNo=").append(location.getStreetNo()).append(",");
        return sb.toString();
    }

    public void refresh(){
        isRefreshing = true;
        canLoadMore = true;
        ArrayMap<String,String> params = new ArrayMap<>(4);
        params.put("token", StrUtils.token());
//        params.put("token", "123456");
        params.put("page", "1");
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        OkHttpUtils.post(StrUtils.CUSTOMER_HOME_PAGE, params, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                //LogUtils.i(TAG, s);
                JSONObject j = OkHttpUtils.parseJSON(getActivity(),s);
                if(j==null){
                    return;
                }
                JSONArray array = j.optJSONArray("bannerImgUrls");
                if (array == null) return;
                List<BannerInfo> infoList = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    BannerInfo info = BannerInfo.fromJSON(array.optJSONObject(i));
                    infoList.add(info);
                }
                mBannerAdapter.setInfoList(infoList);
                mBannerAdapter.notifyDataSetChanged();
            }
        });
        loadPage(page);
        startLocation();
    }

    public void refreshLocation(){
        startLocation();
    }


    private void loadPage(int page){
        ArrayMap<String,String> params = new ArrayMap<>(3);
        params.put("token", StrUtils.token());
//        params.put("token", "123456");
        params.put("page", "" + page);
        isLoading = true;
        OkHttpUtils.post(StrUtils.CUSTOMER_HOME_PAGE, params, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                //LogUtils.i(TAG, s);
                JSONObject j = OkHttpUtils.parseJSON(getActivity(),s);
                if(j==null){
                    return;
                }
                JSONArray array = j.optJSONArray("sellerView");
                LogUtils.d(TAG, j.toString());
                if (array == null) return;
                infoList = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    HomeGuest info = HomeGuest.fromJSON(array.optJSONObject(i));
                    infoList.add(info);
                }
                mRvAdapter.setHomeList(infoList);
                mRvAdapter.notifyDataSetChanged();
                isRefreshing = false;
                isLoading = false;
                mSwipeLayout.setRefreshing(false);
            }
        });
    }

    private class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        static final int TYPE_BANNER = 1;
        static final int TYPE_BUTTON = 2;
        static final int TYPE_ITEM = 3;
        List<HomeGuest> mHomeGuestList;

        public Adapter(){}


        public void setHomeList(List<HomeGuest> homeGuestList){
            this.mHomeGuestList = homeGuestList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh;
            if (viewType == TYPE_BANNER){
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home_banner_pager, parent, false);
                ViewGroup.LayoutParams params = v.getLayoutParams();
                params.height = DimensionUtils.getDisplay().widthPixels/2;
                v.setLayoutParams(params);
                vh = new BannerViewHolder(v);
            }else if(viewType == TYPE_BUTTON){
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home_button, parent, false);
                vh = new ButtonViewHolder(v);
            }else{
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home_item_guest, parent, false);
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



            }else if(holder instanceof ItemViewHolder){
                HomeGuest homeGuest = mHomeGuestList.get(position - 2);
                ItemViewHolder item = (ItemViewHolder) holder;
                Uri uriFoodImg = Uri.parse(homeGuest.foodImg);
                Uri uriShopImg = Uri.parse(homeGuest.headImg);
                item.shopFoodImage.setImageURI(uriFoodImg);
                item.shopImage.setImageURI(uriShopImg);
                item.shopName.setText(homeGuest.sellerName);
                item.shopAddress.setText(homeGuest.location);
                item.shopSalesVolumeMonth.setText(""+ homeGuest.monthSales);
                item.shopScore.setText("" + homeGuest.scores);
                item.shopAverage.setText("" + homeGuest.personPrice);
            }
        }

        @Override
        public int getItemCount() {
            if (mHomeGuestList == null){
                return 2;
            }else{
                return 2 + mHomeGuestList.size();
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
                        LogUtils.d(TAG, "id:" + (getAdapterPosition() - 2) + " "+ infoList.get(getAdapterPosition() - 2).sellerId);
                        Intent detail = new Intent(getActivity(), KitchenNdActivity.class);
                        detail.putExtra("token", "" + infoList.get(getAdapterPosition() - 2).sellerId);
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

    private static class BannerInfo {
        public int id;
        public String url;
        public static BannerInfo fromJSON(JSONObject j){
            BannerInfo info = new BannerInfo();
            info.id = j.optInt("rank");
            info.url = j.optString("imgUrls");
            return info;
        }
    }

    private static class BannerAdapter extends PagerAdapter{
        List<BannerInfo> infoList;
        Context context;
        View.OnClickListener mListener;

        public BannerAdapter(Context context){
            this.context = context;
        }

        public void setInfoList(List<BannerInfo> infoList) {
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
