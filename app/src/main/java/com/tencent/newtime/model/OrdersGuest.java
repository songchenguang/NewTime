package com.tencent.newtime.model;

import org.json.JSONObject;

/**
 * Created by 晨光 on 2016-07-11.
 */
public class OrdersGuest {

    public int id;
    public static OrdersGuest fromJSON(JSONObject j){
        OrdersGuest a = new OrdersGuest();
        a.id = j.optInt("id");
        return a;
    }
}
