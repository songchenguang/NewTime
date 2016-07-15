package com.tencent.newtime.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Guang on 2016/7/10.
 */
public class Order implements Serializable{
    private int id;
    private int guestId;
    private ArrayList<OrderItem>  dishes;
    private float totalPrice;
    private float discount;
    private float strikePrice;  //totalPrice*discount;
    private int kitchenId;
    private int guestCount;
    private Date arrayDate_1;
    private String note;


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getArrayDate_1() {
        return arrayDate_1;
    }

    public void setArrayDate_1(Date arrayDate_1) {
        this.arrayDate_1 = arrayDate_1;
    }


    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public int getKitchenId() {
        return kitchenId;
    }

    public void setKitchenId(int kitchenId) {
        this.kitchenId = kitchenId;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public ArrayList<OrderItem> getDishes() {
        return dishes;
    }

    public void setDishes(ArrayList<OrderItem> dishes) {
        this.dishes = dishes;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(float strikePrice) {
        this.strikePrice = strikePrice;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }
}

