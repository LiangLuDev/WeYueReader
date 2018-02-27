package com.lianglu.weyue.utils;

import android.content.Context;
import android.content.res.TypedArray;

import com.lianglu.weyue.R;
import com.lianglu.weyue.WYApplication;
import com.lianglu.weyue.widget.theme.Theme;

/**
 * Created by dongjunkun on 2016/2/6.
 */
public class ThemeUtils {
    public static int getThemeColor2Array(Context context, int attrRes) {
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{attrRes});
        int color = typedArray.getColor(0, 0xffffff);
        typedArray.recycle();
        return color;
    }

    /**
     * 获取主题颜色（color）
     *
     * @return
     */
    public static int getThemeColor() {
        Theme theme = SharedPreUtils.getInstance().getCurrentTheme();
        switch (theme) {
            case Blue:
                return WYApplication.getAppResources().getColor(R.color.colorBluePrimary);
            case Red:
                return WYApplication.getAppResources().getColor(R.color.colorRedPrimary);
            case Brown:
                return WYApplication.getAppResources().getColor(R.color.colorBrownPrimary);
            case Green:
                return WYApplication.getAppResources().getColor(R.color.colorGreenPrimary);
            case Purple:
                return WYApplication.getAppResources().getColor(R.color.colorPurplePrimary);
            case Teal:
                return WYApplication.getAppResources().getColor(R.color.colorTealPrimary);
            case Pink:
                return WYApplication.getAppResources().getColor(R.color.colorPinkPrimary);
            case DeepPurple:
                return WYApplication.getAppResources().getColor(R.color.colorDeepPurplePrimary);
            case Orange:
                return WYApplication.getAppResources().getColor(R.color.colorOrangePrimary);
            case Indigo:
                return WYApplication.getAppResources().getColor(R.color.colorIndigoPrimary);
            case LightGreen:
                return WYApplication.getAppResources().getColor(R.color.colorLightGreenPrimary);
            case Lime:
                return WYApplication.getAppResources().getColor(R.color.colorLimePrimary);
            case DeepOrange:
                return WYApplication.getAppResources().getColor(R.color.colorDeepOrangePrimary);
            case Cyan:
                return WYApplication.getAppResources().getColor(R.color.colorCyanPrimary);
            case BlueGrey:
                return WYApplication.getAppResources().getColor(R.color.colorBlueGreyPrimary);

        }
        return WYApplication.getAppResources().getColor(R.color.colorCyanPrimary);
    }

    /**
     * 获取主题颜色（color）
     *
     * @return
     */
    public static int getThemeColorId() {
        Theme theme = SharedPreUtils.getInstance().getCurrentTheme();
        switch (theme) {
            case Blue:
                return R.color.colorBluePrimary;
            case Red:
                return R.color.colorRedPrimary;
            case Brown:
                return R.color.colorBrownPrimary;
            case Green:
                return R.color.colorGreenPrimary;
            case Purple:
                return R.color.colorPurplePrimary;
            case Teal:
                return R.color.colorTealPrimary;
            case Pink:
                return R.color.colorPinkPrimary;
            case DeepPurple:
                return R.color.colorDeepPurplePrimary;
            case Orange:
                return R.color.colorOrangePrimary;
            case Indigo:
                return R.color.colorIndigoPrimary;
            case LightGreen:
                return R.color.colorLightGreenPrimary;
            case Lime:
                return R.color.colorLimePrimary;
            case DeepOrange:
                return R.color.colorDeepOrangePrimary;
            case Cyan:
                return R.color.colorCyanPrimary;
            case BlueGrey:
                return R.color.colorBlueGreyPrimary;

        }
        return R.color.colorCyanPrimary;
    }
}
