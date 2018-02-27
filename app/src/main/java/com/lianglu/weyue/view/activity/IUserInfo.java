package com.lianglu.weyue.view.activity;

import com.lianglu.weyue.db.entity.UserBean;
import com.lianglu.weyue.view.base.IBaseLoadView;

/**
 * Created by Liang_Lu on 2018/1/11.
 */

public interface IUserInfo extends IBaseLoadView{

    void uploadSuccess(String imageUrl);
    void userInfo(UserBean userBean);

}
