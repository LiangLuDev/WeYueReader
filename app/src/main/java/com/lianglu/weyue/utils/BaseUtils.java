package com.lianglu.weyue.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.lianglu.weyue.R;
import com.lianglu.weyue.WYApplication;
import com.lianglu.weyue.widget.theme.Theme;

/**
 * Created by Liang_Lu on 2017/11/29.
 */

public class BaseUtils {

    /**
     * 设置textview 图标
     *
     * @param view
     * @param iconRes
     */
    public static void setIconDrawable(TextView view, @DrawableRes int iconRes) {
        view.setCompoundDrawablesWithIntrinsicBounds(WYApplication.getAppResources().getDrawable(iconRes),
                null, null, null);
        view.setCompoundDrawablePadding(DimenUtils.dp2px(10));
    }

    /**
     * 跳转到权限设置界面
     */
    public static void getAppDetailSettingIntent(Context context, String packageName) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", packageName, null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", packageName);
        }
        context.startActivity(intent);
    }


    /**
     * 保留1位小数
     *
     * @param d
     * @return
     */
    public static String format1Digits(Object d) {
        return String.format("%.1f", Double.parseDouble(d+""));
    }

    /**
     * EditText聚焦，键盘升起
     */
    public static void showInput(final EditText et) {
        InputMethodManager imm = (InputMethodManager)
                et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        et.requestFocus();
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 切换页面，键盘隐藏
     */
    public static void hideInput(final EditText et) {
        InputMethodManager imm = (InputMethodManager)
                et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

}
