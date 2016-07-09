package com.tencent.newtime.base;

import com.umeng.analytics.MobclickAgent;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import com.tencent.newtime.util.*;

/**
 * Created by Liujilong on 2016/1/30.
 * liujilong.me@gmail.com
 */
public abstract class SwipeActivity extends SwipeBackActivity {
    /**
     * set the tag for Activity, which may used for cancel okhttp calls
     * @return the tag for this activity;
     */
    protected abstract String tag();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.cancel(tag());
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
