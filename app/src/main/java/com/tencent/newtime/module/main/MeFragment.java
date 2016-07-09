package com.tencent.newtime.module.main;

import android.os.Bundle;

import com.tencent.newtime.base.BaseFragment;

/**
 * Created by 晨光 on 2016-07-09.
 */
public class MeFragment extends BaseFragment {


    private final String TAG = "MeFragment";

    public static MeFragment newInstance() {
        Bundle args = new Bundle();
        MeFragment fragment = new MeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected String tag() {
        return TAG;
    }
}