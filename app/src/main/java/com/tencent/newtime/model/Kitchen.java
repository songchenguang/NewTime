package com.tencent.newtime.model;

import android.support.v4.util.ArrayMap;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Guang on 2016/7/10.
 */
//菜品以厨房方式呈现，一个厨房就是一个商家。
public class Kitchen implements Serializable{
    private int id;
    private String photo;
    private String name;
    private String description;
    private boolean isCertification;
    private String ownerPhoto;
    private ArrayList<Dish> dishList;
    private String location;
    private String token;

    public static Kitchen fromJSON(JSONObject j){
        Kitchen mkitchen=new Kitchen();
        mkitchen.name=j.optString("username","");
        mkitchen.isCertification=j.optString("confirm","").equals("1")?true:false;
        mkitchen.ownerPhoto=j.optString("headimgurl","");
        mkitchen.photo=j.optString("homeimgurl","");
        mkitchen.location=j.optString("location","");
        mkitchen.token=j.optString("token","");
        return mkitchen;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<Dish> getDishList() {
        return dishList;
    }

    public void setDishList(ArrayList<Dish> dishList) {
        this.dishList = dishList;
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

    public boolean isCertification() {
        return isCertification;
    }

    public void setCertification(boolean certification) {
        isCertification = certification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerPhoto() {
        return ownerPhoto;
    }

    public void setOwnerPhoto(String ownerPhoto) {
        this.ownerPhoto = ownerPhoto;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
