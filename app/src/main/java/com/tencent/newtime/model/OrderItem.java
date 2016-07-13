package com.tencent.newtime.model;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Guang on 2016/7/10.
 */
public class OrderItem implements Serializable{
    private int dishId; //菜品id；
    private int num;    //份数；
    private String dishName;
    private float price;
    private float itemTotalPrice;


    //订单提交
    public JSONObject toJSON(){
        JSONObject object=new JSONObject();
       // object.put("")
        return object;
    }






    public float getItemTotalPrice() {
        return itemTotalPrice;
    }

    public void setItemTotalPrice(float itemTotalPrice) {
        this.itemTotalPrice = itemTotalPrice;
    }

    //如果订单里面两道菜的的id相同，则认为只有一道菜
    public boolean equals(Object item){
        if(item!=null&&(item instanceof OrderItem)){
            if(dishId==((OrderItem) item).dishId)
                return true;
        }
        return false;
    }




    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
