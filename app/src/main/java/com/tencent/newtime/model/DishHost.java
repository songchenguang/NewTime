package com.tencent.newtime.model;

import org.json.JSONObject;

/**
 * Created by 晨光 on 2016-07-10.
 */
public class DishHost {
    public String foodImg;
    public String headImg;
    public String location;
    public int monthSales;
    public double personPrice;
    public double scores;
    public int sellerId;
    public String sellerName;

    public static DishHost fromJSON(JSONObject j){
        DishHost a = new DishHost();
        a.foodImg = j.optString("foodImg");
        a.headImg = j.optString("headImg");
        a.location = j.optString("location");
        a.monthSales = j.optInt("monthSales");
        a.personPrice = j.optDouble("scores");
        a.scores = j.optDouble("scores");
        a.sellerId = j.optInt("sellerId");
        a.sellerName = j.optString("sellerName");
        return a;
    }
}
