package com.lianglu.weyue.viewmodel.activity;

import android.content.Context;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.lianglu.weyue.api.UserService;
import com.lianglu.weyue.utils.MD5Utils;
import com.lianglu.weyue.utils.ToastUtils;
import com.lianglu.weyue.utils.rxhelper.RxObserver;
import com.lianglu.weyue.view.activity.IUserRegister;
import com.lianglu.weyue.viewmodel.BaseViewModel;

/**
 * Created by Liang_Lu on 2018/1/5.
 */

public class VMUserRegisterInfo extends BaseViewModel {
    IUserRegister userRegister;

    public VMUserRegisterInfo(Context mContext, IUserRegister userRegister) {
        super(mContext);
        this.userRegister = userRegister;
    }


    public void register(String username, String password) {
        //对密码进行md5加密
        String md5Pass = MD5Utils.encrypt(password);
        RxHttpUtils.getSInstance().addHeaders(tokenMap())
                .createSApi(UserService.class)
                .register(username, md5Pass)
                .compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver<String>() {
                    @Override
                    protected void onError(String errorMsg) {

                    }

                    @Override
                    protected void onSuccess(String data) {
                        ToastUtils.show(data);
                        if (data.equals("注册成功")) {
                            userRegister.registerSuccess();
                        }
                    }
                });
    }
}
