package com.tencent.newtime.model;

import org.json.JSONObject;

/**
 * Created by Liujilong on 16/1/27.
 * liujilong.me@gmail.com
 */
public class Activity {


    public int activityID;
    public String time;
    public String location;
    public String title;
    public String capacity;
    public boolean state;
    public String signNumber;
    public String remark;
    public String author;
    public String detail;
    public String advertise;
    public boolean needsImage;
    public boolean likeFlag;
    public int authorID;
    public String school;
    public String poster;
    public String status;
    public String timeState;
    public String sponsor;
    public String top;

    public String gender;

    public static Activity fromJSON(JSONObject j){
        Activity a = new Activity();
        a.advertise = j.optString("advertise");
        a.timeState = j.optString("timestate");
        a.author = j.optString("author");
        a.authorID = j.optInt("authorid");
        a.activityID = j.optInt("id");
        a.gender = j.optString("gender");
        a.location = j.optString("location");
        a.capacity = j.optString("number");
        a.remark = j.optString("remark");
        a.school = j.optString("school");
        a.signNumber = j.optString("signnumber");
        a.state = j.optString("state").equals("yes");
        a.time = j.optString("time");
        a.title = j.optString("title");
        a.status = j.optString("status");
        a.status = j.optString("sponsor");
        a.top = j.optString("top");
        return a;
    }

}
