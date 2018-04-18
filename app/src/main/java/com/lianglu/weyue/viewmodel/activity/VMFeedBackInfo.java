package com.lianglu.weyue.viewmodel.activity;

import android.content.Context;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.lianglu.weyue.api.UserService;
import com.lianglu.weyue.utils.ToastUtils;
import com.lianglu.weyue.utils.rxhelper.RxObserver;
import com.lianglu.weyue.view.activity.IFeedBack;
import com.lianglu.weyue.viewmodel.BaseViewModel;


/**
 * Created by Liang_Lu on 2018/1/22.
 */

public class VMFeedBackInfo extends BaseViewModel {
    IFeedBack iFeedBack;

    public VMFeedBackInfo(Context mContext, IFeedBack iFeedBack) {
        super(mContext);
        this.iFeedBack = iFeedBack;
    }

    /**
     * 提交反馈
     *
     * @param qq
     * @param feedback
     */
    public void commitFeedBack(String qq, String feedback) {
        RxHttpUtils.getSInstance().addHeaders(tokenMap()).createSApi(UserService.class)
                .userFeddBack(qq, feedback)
                .compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver<String>() {
                    @Override
                    protected void onError(String errorMsg) {

                    }

                    @Override
                    protected void onSuccess(String data) {
                        ToastUtils.show("提交反馈成功");
                        iFeedBack.feedBackSuccess();
                    }
                });
    }
}
