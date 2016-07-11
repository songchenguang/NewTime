package com.tencent.newtime.model;

import org.json.JSONObject;

/**
 * Created by 晨光 on 2016-07-10.
 */
public class HomeGuest {
    public int id;
    public static HomeGuest fromJSON(JSONObject j){
        HomeGuest a = new HomeGuest();
        a.id = j.optInt("id");
        return a;
    }
}
