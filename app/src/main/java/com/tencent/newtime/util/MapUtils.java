package com.tencent.newtime.util;

import android.app.Activity;
import android.util.Log;

import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.Location;
import com.tencent.lbssearch.object.param.Address2GeoParam;
import com.tencent.lbssearch.object.result.Address2GeoResultObject;

/**
 * Created by Guang on 2016/7/13.
 */
public class MapUtils {
    private static final String TAG="MapUtils";
    public static void search(Activity context, final String address){
        Log.d(TAG,"进入search()");
      //  final String address="深圳市南山区湾厦村招商路爱榕路2号";//"北京市海淀区彩和坊路海淀西大街74号";
        final String region=null;
        TencentSearch tencentSearch = new TencentSearch(context);
        Address2GeoParam param = new Address2GeoParam().
                address(address).
                region(region);
        tencentSearch.address2geo(param, new HttpResponseListener() {
            //如果成功会调用这个方法，用户需要在这里获取检索结果，调用自己的业务逻辑
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers,
                                  BaseObject object) {
                // TODO Auto-generated method stub
                if(object != null){
                    //这里的object是所有检索结果的父类
                    //用户需要将其转换为其实际类型以获取检索内容
                    //这里将其转换为Address2GeoResultObject

                    Address2GeoResultObject oj =
                            (Address2GeoResultObject)object;
                    String result = "地址转坐标：地址:"+ address+
                            "  region:"+region + "\n\n";
                    if(oj.result != null){
                        Log.v("demo","location:" +
                                oj.result.location.toString());
                        result += oj.result.location.toString();
                    }
                    Log.d(TAG,result);
                }
            }
            @Override
            public void onFailure(int i, org.apache.http.Header[] headers,
                                  String s, Throwable throwable) {
                Log.d(TAG,s);
            }
            //如果失败，会调用这个方法，可以在这里进行错误处理
        });
    }
}
