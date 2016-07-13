package com.tencent.newtime.model;

import org.json.JSONObject;

/**
 * Created by 晨光 on 2016-07-11.
 */
public class OrdersGuest {

//    public String cus
    public String foodCounts;
    public String foodName;
    public String orderId;
    public String orderPayPrice;
    public String orderPayTime;
    public String orderPeopleNumber;
    public String orderPlanEatTime;
    public String orderPrice;
    public String orderTime;
    public String sellerHeadImg;
    public String sellerId;
    public String sellerName;
    public String sellerScores;

    public static OrdersGuest fromJSON(JSONObject j){
        OrdersGuest a = new OrdersGuest();
        a.foodCounts = j.optString("foodCounts");
        a.foodName = j.optString("foodName");
        a.orderId = j.optString("orderId");
        a.orderPayPrice = j.optString("orderPayPrice");
        a.orderPayTime = j.optString("orderPayTime");
        a.orderPeopleNumber = j.optString("orderPeopleNumber");
        a.orderPlanEatTime = j.optString("orderPrice");
        a.orderPrice = j.optString("orderPrice");
        a.orderTime = j.optString("orderTime");
        a.sellerHeadImg = j.optString("sellerHeadImg");
        a.sellerId = j.optString("sellerId");
        a.sellerName = j.optString("sellerName");
        a.sellerScores = j.optString("sellerScores");
        return a;
    }


}
