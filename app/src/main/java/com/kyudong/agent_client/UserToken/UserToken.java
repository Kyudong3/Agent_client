package com.kyudong.agent_client.UserToken;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Kyudong on 2017. 12. 12..
 */

public class UserToken {

    public static void setPreferences(Context context, String key, String token){
        SharedPreferences pref = context.getSharedPreferences("prefs",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key,token);
        //editor.putInt(key, token);
        editor.apply();
    }

    public static String getPreferences(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences("prefs",context.MODE_PRIVATE);
        return pref.getString(key,"empty");
        //return pref.getInt(key, -1);
    }

    public static void removePreferences(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences("prefs",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
    }

}
