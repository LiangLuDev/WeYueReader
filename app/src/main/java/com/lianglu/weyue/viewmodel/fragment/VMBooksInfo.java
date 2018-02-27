package com.lianglu.weyue.viewmodel.fragment;

import android.content.Context;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.lianglu.weyue.api.BookService;
import com.lianglu.weyue.utils.rxhelper.RxObserver;
import com.lianglu.weyue.model.BookBean;
import com.lianglu.weyue.utils.ToastUtils;
import com.lianglu.weyue.view.fragment.IBookInfo;
import com.lianglu.weyue.viewmodel.BaseViewModel;

import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by Liang_Lu on 2017/12/6.
 */

public class VMBooksInfo extends BaseViewModel {
    IBookInfo iBookInfo;

    public VMBooksInfo(Context mContext, IBookInfo iBookInfo) {
        super(mContext);
        this.iBookInfo = iBookInfo;
    }

    public void getBooks( String type, String major, int page) {
        RxHttpUtils.getSInstance().addHeaders(tokenMap()).createSApi(BookService.class)
                .books( type, major, page)
                .compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver<List<BookBean>>() {
                    @Override
                    protected void onError(String errorMsg) {
                        if (iBookInfo != null) {
                            iBookInfo.stopLoading();
                        }
                    }

                    @Override
                    protected void onSuccess(List<BookBean> data) {
                        if (iBookInfo != null) {
                            iBookInfo.stopLoading();
                        }
                        if (data.size() > 0) {
                            if (iBookInfo != null) {
                                iBookInfo.getBooks(data, page > 1 ? true : false);
                            }
                        } else {
                            ToastUtils.show("无更多书籍");
                        }

                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposadle(d);
                    }
                });
    }


}
