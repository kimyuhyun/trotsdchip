package net.hongslab.trotsdchip.Libs;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;


/**
 * Created by admin on 2017-03-16.
 */

public class LoginSharedPreference {

    public static String getUserInfo(Context ctx, String column){
        SharedPreferences pref = ctx.getSharedPreferences("USER_INFO", AppCompatActivity.MODE_PRIVATE);
        return pref.getString(column, "");
    }

    public static void setUserInfo(Context ctx, String column, String value){
        SharedPreferences pref = ctx.getSharedPreferences("USER_INFO", AppCompatActivity.MODE_PRIVATE);
        SharedPreferences.Editor userInfo = pref.edit();
        userInfo.putString(column, value);
        userInfo.commit();
    }

}
