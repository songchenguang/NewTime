package com.tencent.newtime.module.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v13.app.FragmentPagerAdapter;


import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tencent.newtime.R;
import com.tencent.newtime.base.BaseActivity;
import com.tencent.newtime.module.login.LoginActivity;
import com.tencent.newtime.widget.TabItem;
import com.tencent.newtime.util.*;

/**
 * Created by 晨光 on 2016-07-09.
 */
public class MainActivity extends BaseActivity{

    private static final String TAG = "MainActivity";

    private static final int PAGE_COUNT = 3;

    private int[] mTitleTexts = new int[]{
            R.string.home,
            R.string.orders,
            R.string.me
    };

    private TabItem[] tabItems;
    private ViewPager mPager;
    private ViewGroup wholeLayout;
    private TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getIntent().getBooleanExtra(INTENT_LOGOUT, false))
//        {
//            Intent i = new Intent(MainActivity.this,LoginActivity.class);
//            startActivity(i);
//            finish();
//            return;
//        }
        setContentView(R.layout.activity_main);
        bindViews();
    }

    private void bindViews(){
        tabItems = new TabItem[3];
        tabItems[0] = (TabItem) findViewById(R.id.main_item_home);
        tabItems[1] = (TabItem) findViewById(R.id.main_item_orders);
        tabItems[2] = (TabItem) findViewById(R.id.main_item_me);
        tabItems[0].setEnable(true);


//        toolbar.inflateMenu(R.menu.menu_main);
//        toolbar.setOnMenuItemClickListener(this);

        mPager = (ViewPager) findViewById(R.id.main_pager);
        wholeLayout = (ViewGroup) findViewById(R.id.whole_layout);
        mTvTitle = (TextView) findViewById(R.id.main_title);
        Adapter mAdapter = new Adapter(getFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                mTvTitle.setText(mTitleTexts[position]);
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

    public class Adapter extends FragmentPagerAdapter {

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return HomeFragment.newInstance();
                case 1:
                    return OrdersFragment.newInstance();
                case 2:
                    return MeFragment.newInstance();
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
