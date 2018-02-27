package com.lianglu.weyue.view.activity;

import com.lianglu.weyue.model.AppUpdateBean;
import com.lianglu.weyue.view.base.IBaseLoadView;

/**
 * Created by Liang_Lu on 2018/1/22.
 */

public interface ISetting extends IBaseLoadView{
    void appUpdate(AppUpdateBean appUpdateBean);
}
