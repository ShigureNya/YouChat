package com.github.youchatproject.tools;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： guhaoran
 * 创建于： 2017/5/26
 * 包名： io.github.youchart.tools
 * 文档描述：Json工具类
 */
public class JsonFormat {
    private static Gson gson ;
    static{
        if(gson == null){
            gson = new Gson();
        }
    }

    private JsonFormat(){}

    /**
     * @param json Json字符串
     * @param cls 实体类对象
     * @param <T> 实体类
     * @return 实体
     * Json转实体类对象
     */
    public static <T>T jsonToBean(String json , Class<T> cls){
        T t = null ;
        if(json != null){
            t = gson.fromJson(json , cls);
        }
        return t ;
    }

    /**
     * @param json Json字符串
     * @param cls 实体类对象
     * @param <T> 实体类
     * @return 实体集合
     * Json赚实体类集合
     */
    public static <T> List<T> jsonToList(String json , Class<T> cls){
        ArrayList<T> mList = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for(final JsonElement elem : array){
            mList.add(gson.fromJson(elem, cls));
        }
        return mList;
    }

    /**
     * @param object 实体
     * @return Json字符串
     * 实体类转Json字符串
     */
    public static String beanToJson(Object object){
        String jsonStr = null ;
        if(gson != null){
            jsonStr = gson.toJson(object);
        }
        return jsonStr ;
    }
}
