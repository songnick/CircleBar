package com.github.songnick.utils;

import android.util.Log;

import com.nick.library.BuildConfig;

/**
 * Created by qfsong on 15/11/5.
 */
public class LogUtils {

    public static void LogD(String tag, String msg){
//        if (BuildConfig.DEBUG)
            Log.e(tag, msg);
    }

    public static void LogE(String tag, String msg){
        if (BuildConfig.DEBUG)
            Log.e(tag, msg);
    }

}
