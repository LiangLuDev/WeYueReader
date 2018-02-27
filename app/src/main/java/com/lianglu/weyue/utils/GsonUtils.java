package com.lianglu.weyue.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装的是使用Gson解析json的方法
 *
 * @author Administrator
 */
public class GsonUtils {

    /**
     * 把一个json字符串变成对象
     *
     * @param json
     * @param cls
     * @return
     */
    public static <T> T parseJsonToBean(String json, Class<T> cls) {
        Gson gson = new Gson();
        T t = null;
        try {
            t = gson.fromJson(json, cls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return t;
    }

    //解析json数组
    public static <T> List<T> fromJsonArray(String json, Class<T> clazz) {
        List<T> lst = null;
        try {
            lst = new ArrayList<T>();

            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
            for (final JsonElement elem : array) {
                lst.add(new Gson().fromJson(elem, clazz));
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }

        return lst;
    }


    /**
     * 对象转为json字符串
     *
     * @param target
     * @return
     */
    public static String toJson(Object target) {
        Gson gson = new Gson();
        return gson.toJson(target);
    }


    /**
     * 获取json串中某个字段的值，注意，只能获取同一层级的value
     *
     * @param json
     * @param key
     * @return
     */
    public static String getFieldValue(String json, String key) {
        if (TextUtils.isEmpty(json))
            return null;
        if (!json.contains(key))
            return "";
        JSONObject jsonObject = null;
        String value = null;
        try {
            jsonObject = new JSONObject(json);
            value = jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }

        return value;
    }

}
