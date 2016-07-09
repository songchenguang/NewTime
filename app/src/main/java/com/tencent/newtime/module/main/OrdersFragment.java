package com.tencent.newtime.module.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.newtime.R;
import com.tencent.newtime.base.BaseFragment;
import com.tencent.newtime.util.LogUtils;

/**
 * Created by 晨光 on 2016-07-09.
 */
public class OrdersFragment extends BaseFragment {

    private final String TAG = "OrdersFragment";
    public static OrdersFragment newInstance() {
        Bundle args = new Bundle();
        OrdersFragment fragment = new OrdersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String tag() {
        return TAG;
    }

}