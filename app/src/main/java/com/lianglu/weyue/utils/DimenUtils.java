package com.lianglu.weyue.utils;

import com.lianglu.weyue.WYApplication;

/**
 * Created by Liang_Lu on 2017/11/28.
 * 尺寸工具箱，提供与Android尺寸相关的工具方法
 */

public class DimenUtils {
    /**
     * dp单位转换为px
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(float dpValue){
        return (int)(dpValue * (WYApplication.getAppResources().getDisplayMetrics().density) + 0.5f);
    }

    /**
     * px单位转换为dp
     * @param pxValue px值
     * @return dp值
     */
    public static int px2dp(float pxValue){
        return (int)(pxValue / (WYApplication.getAppResources().getDisplayMetrics().density) + 0.5f);
    }
}
