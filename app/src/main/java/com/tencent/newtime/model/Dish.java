package com.tencent.newtime.model;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Guang on 2016/7/10.
 */
//菜品    对应food表。
public class Dish implements Serializable{
    private int id;
    private int pid;    //属于哪个厨房
    private String name;
    private String description;
    private String photo;
    private float price;
    private int monthSales;

    public static Dish fromJSON(JSONObject j){
        Dish mDish=new Dish();
        mDish.id=Integer.valueOf(j.optString("foodid","-1"));
        mDish.name=j.optString("foodname"," ");
        mDish.description=j.optString("description"," ");
        mDish.price=Float.valueOf(j.optString("price","-1"));
        mDish.monthSales=Integer.valueOf(j.optString("monthSales",""));
        return mDish;
    }




    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMonthSales() {
        return monthSales;
    }

    public void setMonthSales(int monthSales) {
        this.monthSales = monthSales;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
