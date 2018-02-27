package com.lianglu.weyue.viewmodel.activity;

import android.content.Context;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.lianglu.weyue.api.BookService;
import com.lianglu.weyue.utils.rxhelper.RxObserver;
import com.lianglu.weyue.model.BookBean;
import com.lianglu.weyue.view.fragment.IBookSearchInfo;
import com.lianglu.weyue.viewmodel.BaseViewModel;

import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by Liang_Lu on 2017/12/21.
 */

public class VMBookSearchInfo extends BaseViewModel {
    IBookSearchInfo iBookSearchInfo;

    public VMBookSearchInfo(Context mContext, IBookSearchInfo iBookSearchInfo) {
        super(mContext);
        this.iBookSearchInfo = iBookSearchInfo;
    }


    public void searchBooks(String keyword) {

        RxHttpUtils.getSInstance().addHeaders(tokenMap()).createSApi(BookService.class)
                .booksSearch(keyword)
                .compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver<List<BookBean>>() {
                    @Override
                    protected void onError(String errorMsg) {

                    }

                    @Override
                    protected void onSuccess(List<BookBean> data) {
                        if (iBookSearchInfo != null) {
                            iBookSearchInfo.getSearchBooks(data);
                        }
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposadle(d);
                    }
                });
    }


}
