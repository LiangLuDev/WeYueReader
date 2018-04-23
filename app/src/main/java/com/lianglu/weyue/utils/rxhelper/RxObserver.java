package com.lianglu.weyue.utils.rxhelper;

import android.content.Intent;

import com.allen.library.RxHttpUtils;
import com.allen.library.base.BaseDataObserver;
import com.allen.library.base.BaseObserver;
import com.allen.library.bean.BaseData;
import com.allen.library.utils.ToastUtils;
import com.lianglu.weyue.WYApplication;
import com.lianglu.weyue.utils.LoadingHelper;
import com.lianglu.weyue.view.activity.impl.LoginActivity;

import io.reactivex.disposables.Disposable;

/**
 * Created by Liang_Lu on 2017/12/4.
 */

public abstract class RxObserver<T> extends BaseDataObserver<T> {

    boolean isLoading;

    public RxObserver() {
    }

    public RxObserver(boolean isLoading) {
        this.isLoading = isLoading;
    }

    /**
     * 失败回调
     *
     * @param errorMsg 错误信息
     */
    protected abstract void onError(String errorMsg);

    /**
     * 成功回调
     *
     * @param data 结果
     */
    protected abstract void onSuccess(T data);

    /**
     * //     * 成功回调
     * //     *
     * //     * @param d 结果
     * //
     */
//    public abstract void onSubscribe(Disposable d);
    @Override
    public void doOnSubscribe(Disposable d) {
//        onSubscribe(d);
        RxHttpUtils.addDisposable(d);
    }

    @Override
    public void doOnError(String errorMsg) {
        dismissLoading();
        ToastUtils.showToast(errorMsg);
        onError(errorMsg);
    }


    @Override
    public void doOnNext(BaseData<T> data) {
        //可以根据需求对code统一处理
        switch (data.getCode()) {
            case 10000:
                onSuccess(data.getData());
                break;
            case 60001:
            case 60002:
                ToastUtils.showToast(data.getMsg());
                Intent intent = new Intent(WYApplication.getAppContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                WYApplication.getAppContext().startActivity(intent);
                break;
            case 10005:
            case 40000:
            case 40001:
            case 40003:
            case 40004:
            case 40005:
            case 50000:
            case 50003:
                ToastUtils.showToast(data.getMsg());
                onError(data.getMsg());
                break;
            default:
                onError(data.getMsg());
                break;
        }
    }

    @Override
    public void doOnCompleted() {
        dismissLoading();
    }

    /**
     * 隐藏loading对话框
     */
    private void dismissLoading() {
        if (isLoading) {
            LoadingHelper.getInstance().hideLoading();
        }
    }


}
