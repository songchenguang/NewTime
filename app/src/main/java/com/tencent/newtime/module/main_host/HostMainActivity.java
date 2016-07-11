package com.tencent.newtime.module.main_host;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.tencent.newtime.R;
import com.tencent.newtime.base.BaseActivity;
import com.tencent.newtime.module.main_guest.GuestHomeFragment;
import com.tencent.newtime.module.main_guest.GuestMeFragment;
import com.tencent.newtime.module.main_guest.GuestOrdersFragment;
import com.tencent.newtime.widget.TabItem;

/**
 * Created by 晨光 on 2016-07-11.
 */
public class HostMainActivity extends BaseActivity {

    private static final String TAG = "HostMainActivity";
    private static final int PAGE_COUNT = 3;
    private int[] mTitleTexts = new int[]{
            R.string.dish,
            R.string.orders,
            R.string.me
    };
    private TabItem[] tabItems;
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getIntent().getBooleanExtra(INTENT_LOGOUT, false))
//        {
//            Intent i = new Intent(GuestMainActivity.this,LoginActivity.class);
//            startActivity(i);
//            finish();
//            return;
//        }
        setContentView(R.layout.activity_main_host);

        initViews();
    }

    private void initViews(){
        tabItems = new TabItem[3];
        tabItems[0] = (TabItem) findViewById(R.id.main_item_dish_host);
        tabItems[1] = (TabItem) findViewById(R.id.main_item_orders_host);
        tabItems[2] = (TabItem) findViewById(R.id.main_item_me_host);
        tabItems[0].setEnable(true);

        mPager = (ViewPager) findViewById(R.id.main_pager_host);
        Adapter mAdapter = new Adapter(getFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                for(int i = 0;i<PAGE_COUNT; i++){
                    tabItems[i].setEnable(i==position);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        View.OnClickListener mTabItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = (int) v.getTag();
                mPager.setCurrentItem(p);
            }
        };
        for(int i = 0; i<PAGE_COUNT; i++){
            tabItems[i].setTag(i);
            tabItems[i].setOnClickListener(mTabItemClickListener);
        }
    }


    private class Adapter extends FragmentPagerAdapter {

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return HostDishFragment.newInstance();
                case 1:
                    return HostOrdersFragment.newInstance();
                case 2:
                    return HostMeFragment.newInstance();
                default:
                    throw new RuntimeException("position can not be larger than 3");
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }
    @Override
    protected String tag() {
        return TAG;
    }
}
