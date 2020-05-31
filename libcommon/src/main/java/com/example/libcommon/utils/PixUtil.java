package com.example.libcommon.utils;

import android.util.DisplayMetrics;

import com.example.libcommon.global.AppGlobals;

public class PixUtil {

    public static int dp2px(int value){
        DisplayMetrics metrics = AppGlobals.getApplication().getResources().getDisplayMetrics();
        return (int) (metrics.density * value + 0.5f);
    }

    public static int getScreenWidth(){
        DisplayMetrics metrics = AppGlobals.getApplication().getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getScreenHeight(){
        DisplayMetrics metrics = AppGlobals.getApplication().getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

}
