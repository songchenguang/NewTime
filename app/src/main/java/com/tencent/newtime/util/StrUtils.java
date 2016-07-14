package com.tencent.newtime.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import com.tencent.newtime.APP;
import com.tencent.newtime.R;

/**
 * Created by Liujilong on 16/1/22.
 * liujilong.me@gmail.com
 */
public final class StrUtils {

    private StrUtils(){}

    public static final String PHONE_PATTERN =
            "^1[34578]\\d{9}$";

    /** ####################### URLs ############################### **/
    private static final String BASE_URL = "http:/119.29.233.72:3000/";

    public static final String LOGIN_URL = BASE_URL + "applogin";

    public static final String LOGIN_URL_CUS = BASE_URL + "cuslogin";

    public static final String CUSTOMER_HOME_PAGE = BASE_URL + "customerHomePage";

    public static final String SELLER_HOME_PAGE = BASE_URL + "sellerHomePage";

    public static final String CUSTOMER_ORDER0 = BASE_URL + "customerOrder/0";

    public static final String CUSTOMER_ORDER1 = BASE_URL + "customerOrder/1";

    public static final String CUSTOMER_ORDER2 = BASE_URL + "customerOrder/2";

    public static final String SELLER_ORDER0 = BASE_URL + "sellerOrder/0";

    public static final String SELLER_ORDER1 = BASE_URL + "sellerOrder/1";

    public static final String SELLER_ORDER2 = BASE_URL + "sellerOrder/2";

    public static final String REGISTER_PHONE = BASE_URL + "registerphone";

    public static final String REGISTER_PHONE_CUS = BASE_URL + "registerphonecus";

    public static final String SELLER_CONFIRM_ORDER = BASE_URL + "sellerConfirmOrder";

    public static final String SELLER_CANCEL_ORDER = BASE_URL + "sellerCancelOrder";

    public static final String CUSTOMER_CANCEL_ORDER = BASE_URL + "customerCancelOrder";

    public static final String SELLER_REQUEST_PAY = BASE_URL + "sellerRequestPay";

    public static final String CUSTOMER_CONFIRM_PAY = BASE_URL + "customerConfirmPay";

    public static final String CUNSTOMER_RATED_ORDER = BASE_URL + "ratedOrder";

    public static final String SEND_CODE = BASE_URL + "sendsmscode";

    public static final String SEND_CODE_CUS = BASE_URL + "sendsmscodecus";

    public static final String EDIT_FOOD = BASE_URL + "editFood";

    /** ################## SharedPreferences ####################### **/

    public static final String SP_USER = "StrUtils_sp_user";
    public static final String SP_USER_TOKEN = SP_USER + "_token";
    public static final String SP_USER_ID = SP_USER + "_id";
    public static final String SP_USER_GENDER = SP_USER + "_gender";
    public static final String SP_USER_IDENTITY = SP_USER + "_identity";
    public static final String SP_USER_CAN_FOUND = SP_USER +"_can_found";

    public static String token(){
        SharedPreferences sp = APP.context().getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
        return sp.getString(SP_USER_TOKEN,"");
    }

    public static String id(){
        SharedPreferences sp = APP.context().getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
        return sp.getString(SP_USER_ID,"");
    }

    public static String identity(){
        SharedPreferences sp = APP.context().getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
        return sp.getString(SP_USER_IDENTITY,"");
    }

    public static String md5(String input){
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] inputBytes = input.getBytes();
            byte[] outputBytes = messageDigest.digest(inputBytes);
            return bytesToHex(outputBytes);
        }catch (NoSuchAlgorithmException e){
            return "";
        }
    }
    private static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
