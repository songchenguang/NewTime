package com.tencent.newtime.model;

import org.json.JSONObject;

/**
 * Created by 晨光 on 2016-07-11.
 */
public class OrdersHost {

//    public String cus
    public static OrdersHost fromJSON(JSONObject j){
        OrdersHost a = new OrdersHost();
//        a.id = j.optInt("id");
        return a;
    }


}
