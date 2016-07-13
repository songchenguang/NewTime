package com.tencent.newtime.model;

import org.json.JSONObject;

/**
 * Created by 晨光 on 2016-07-11.
 */
public class OrdersHost {
    public String customerFriendly;
    public String customerHeadImg;
    public String customerHonesty;
    public String customerId;
    public String customerName;
    public String customerPassion;
    public String foodCounts;
    public String foodName;
    public String orderId;
    public String orderPayPrice;
    public String orderPayTime;
    public String orderPeopleNumber;
    public String orderPrice;
    public String orderTime;
    public String planeEatTime;
//    public String cus
    public static OrdersHost fromJSON(JSONObject j){
        OrdersHost a = new OrdersHost();
//        a.id = j.optInt("id");
        a.customerFriendly = j.optString("customerFriendly");
        a.customerHonesty = j.optString("customerHonesty");
        a.customerPassion = j.optString("customerPassion");
        a.customerHeadImg = j.optString("customerHeadImg");
        a.customerId = j.optString("customerId");
        a.customerName = j.optString("customerName");
        a.foodCounts = j.optString("foodCounts");
        a.foodName = j.optString("foodName");
        a.orderId = j.optString("orderId");
        a.orderPayPrice = j.optString("orderPayPrice"); //支付时的价格
        a.orderPayTime = j.optString("orderPayTime"); //支付时间
        a.orderPeopleNumber = j.optString("orderPeopleNumber"); //吃饭人数
        a.orderPrice = j.optString("orderPrice"); //下订单时的价格
        a.orderTime = j.optString("orderTime"); //下订单时间
        a.planeEatTime = j.optString("planeEatTime"); //计划吃饭时间

        return a;
    }


}
