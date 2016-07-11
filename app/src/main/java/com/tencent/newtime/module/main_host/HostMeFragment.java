package com.tencent.newtime.module.main_host;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.newtime.R;
import com.tencent.newtime.base.BaseFragment;

/**
 * Created by 晨光 on 2016-07-11.
 */
public class HostMeFragment extends BaseFragment {

    private static final String TAG = "HostMeFragment";

    public static HostMeFragment newInstance() {
        Bundle args = new Bundle();
        HostMeFragment fragment = new HostMeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_me, container, false);

        return rootView;
    }
    protected String tag() {
        return TAG;
    }
}
