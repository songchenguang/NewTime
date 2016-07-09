package com.tencent.newtime.module.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.newtime.R;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_me, container, false);
        return rootView;

    }


    @Override
    protected String tag() {
        return TAG;
    }
}