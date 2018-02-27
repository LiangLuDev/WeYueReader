package com.lianglu.weyue.utils.rxhelper;


import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liang_Lu on 17/9/7.
 * 线程切换封装
 */
public class RxSchedulers {
    public static <T> FlowableTransformer<T, T> io_main() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
