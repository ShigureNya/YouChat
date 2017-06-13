package com.github.youchatproject.tools;

import android.util.Log;

/**
 * 作者： guhaoran
 * 创建于： 2017/5/31
 * 包名： io.github.youchart.tools
 * 文档描述：日志类
 */
public class Loger {
    public static void i(String content){
        Log.i("INFO",content);
    }
    public static void i(String tag , String content){
        Log.i(tag,content);
    }

    public static void e(String content){
        Log.e("ERROR",content);
    }

    public static void e(String tag , String content){
        Log.e(tag,content);
    }

    public static void d(String content){
        Log.d("DEBUG",content);
    }

    public static void d(String tag , String content){
        Log.d(tag,content);
    }

}
