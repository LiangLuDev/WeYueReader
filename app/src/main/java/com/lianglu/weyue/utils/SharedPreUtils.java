package com.lianglu.weyue.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lianglu.weyue.WYApplication;
import com.lianglu.weyue.widget.theme.Theme;

/**
 * Created by newbiechen on 17-4-16.
 */

public class SharedPreUtils {
    private static final String SHARED_NAME = "WeYue_SP";
    private static SharedPreUtils sInstance;
    private static SharedPreferences sharedReadable;
    private static SharedPreferences.Editor sharedWritable;

    private SharedPreUtils() {
        sharedReadable = WYApplication.getAppContext()
                .getSharedPreferences(SHARED_NAME, Context.MODE_MULTI_PROCESS);
        sharedWritable = sharedReadable.edit();
    }

    public static SharedPreUtils getInstance() {
        if (sInstance == null) {
            synchronized (SharedPreUtils.class) {
                if (sInstance == null) {
                    sInstance = new SharedPreUtils();
                }
            }
        }
        return sInstance;
    }

    /**
     * 清除本地数据
     */
    public void sharedPreClear() {
        sharedWritable.clear().apply();
    }

    /**
     * 清除本地数据指定key
     */
    public void sharedPreRemove(String key) {
        sharedWritable.remove(key).apply();
    }

    public String getString(String key, String defValue) {
        return sharedReadable.getString(key, defValue);
    }

    public void putString(String key, String value) {
        sharedWritable.putString(key, value);
        sharedWritable.apply();
    }

    public void putInt(String key, int value) {
        sharedWritable.putInt(key, value);
        sharedWritable.apply();
    }

    public void putBoolean(String key, boolean value) {
        sharedWritable.putBoolean(key, value);
        sharedWritable.apply();
    }

    public int getInt(String key, int def) {
        return sharedReadable.getInt(key, def);
    }

    public boolean getBoolean(String key, boolean def) {
        return sharedReadable.getBoolean(key, def);
    }


    public Theme getCurrentTheme() {
        return Theme.valueOf(getString("app_theme", Theme.Cyan.name()));
    }

    public void setCurrentTheme(Theme currentTheme) {
        putString("app_theme", currentTheme.name());
    }
}
