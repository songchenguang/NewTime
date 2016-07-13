package com.tencent.newtime.model;

import org.json.JSONObject;

/**
 * Created by 晨光 on 2016-07-10.
 */
public class DishHost {
    public String foodId;
    public String foodName;
    public String foodMonthSales;
    public String foodPrice;
    public String foodImg;
    public String disable;

    public static DishHost fromJSON(JSONObject j){
        DishHost a = new DishHost();
        a.foodId = j.optString("foodId");
        a.foodName = j.optString("foodName");
        a.foodMonthSales = j.optString("foodMonthSales");
        a.foodImg = j.optString("foodImg");
        a.foodPrice = j.optString("foodPrice");
        a.disable = j.optString("disable");
        return a;
    }
}
