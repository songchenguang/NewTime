package com.tencent.newtime.module.main_guest;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v13.app.FragmentPagerAdapter;


import android.view.View;

import com.tencent.newtime.R;
import com.tencent.newtime.base.BaseActivity;
import com.tencent.newtime.widget.TabItem;

/**
 * Created by 晨光 on 2016-07-09.
 */
public class GuestMainActivity extends BaseActivity{

    private static final String TAG = "GuestMainActivity";

    private static final int PAGE_COUNT = 3;

    private int[] mTitleTexts = new int[]{
            R.string.home,
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
        setContentView(R.layout.activity_main_guest);

        bindViews();
    }

    private void bindViews(){
        tabItems = new TabItem[3];
        tabItems[0] = (TabItem) findViewById(R.id.main_item_home_guest);
        tabItems[1] = (TabItem) findViewById(R.id.main_item_orders_guest);
        tabItems[2] = (TabItem) findViewById(R.id.main_item_me_guest);
        tabItems[0].setEnable(true);


//        toolbar.inflateMenu(R.menu.menu_main);
//        toolbar.setOnMenuItemClickListener(this);

        mPager = (ViewPager) findViewById(R.id.main_pager_guest);
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
                    return GuestHomeFragment.newInstance();
                case 1:
                    return GuestOrdersFragment.newInstance();
                case 2:
                    return GuestMeFragment.newInstance();
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
