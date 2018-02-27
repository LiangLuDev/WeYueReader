package com.lianglu.weyue.viewmodel.activity;

import android.content.Context;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.lianglu.weyue.api.BookService;
import com.lianglu.weyue.utils.rxhelper.RxObserver;
import com.lianglu.weyue.model.BookChaptersBean;
import com.lianglu.weyue.model.ChapterContentBean;
import com.lianglu.weyue.utils.BookManager;
import com.lianglu.weyue.utils.BookSaveUtils;
import com.lianglu.weyue.utils.LogUtils;
import com.lianglu.weyue.view.activity.IBookChapters;
import com.lianglu.weyue.viewmodel.BaseViewModel;
import com.lianglu.weyue.widget.page.TxtChapter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liang_Lu on 2017/12/11.
 */

public class VMBookContentInfo extends BaseViewModel {
    IBookChapters iBookChapters;

    Disposable mDisposable;
    String title;

    public VMBookContentInfo(Context mContext, IBookChapters iBookChapters) {
        super(mContext);
        this.iBookChapters = iBookChapters;
    }

    public void loadChapters(String bookId) {
        RxHttpUtils.getSInstance().addHeaders(tokenMap()).createSApi(BookService.class)
                .bookChapters(bookId)
                .compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver<BookChaptersBean>() {
                    @Override
                    protected void onError(String errorMsg) {

                    }

                    @Override
                    protected void onSuccess(BookChaptersBean data) {
                        if (iBookChapters != null) {
                            iBookChapters.bookChapters(data);
                        }
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposadle(d);
                    }
                });
    }

    /**
     * 加载正文
     *
     * @param bookId
     * @param bookChapterList
     */
    public void loadContent(String bookId, List<TxtChapter> bookChapterList) {
        int size = bookChapterList.size();
        //取消上次的任务，防止多次加载
        if (mDisposable != null) {
            mDisposable.dispose();
        }

        List<Observable<ChapterContentBean>> chapterContentBeans = new ArrayList<>(bookChapterList.size());
        ArrayDeque<String> titles = new ArrayDeque<>(bookChapterList.size());
        //首先判断是否Chapter已经存在
        for (int i = 0; i < size; i++) {
            TxtChapter bookChapter = bookChapterList.get(i);
            if (!(BookManager.isChapterCached(bookId, bookChapter.getTitle()))) {
                Observable<ChapterContentBean> contentBeanObservable = RxHttpUtils
                        .createApi(BookService.class).bookContent(bookChapter.getLink());
                chapterContentBeans.add(contentBeanObservable);
                titles.add(bookChapter.getTitle());
            }
            //如果已经存在，再判断是不是我们需要的下一个章节，如果是才返回加载成功
            else if (i == 0) {
                if (iBookChapters != null) {
                    iBookChapters.finishChapters();
                }
            }
        }
        title = titles.poll();
        Observable.concat(chapterContentBeans)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<ChapterContentBean>() {
                            @Override
                            public void accept(ChapterContentBean bean) throws Exception {
                                BookSaveUtils.getInstance().saveChapterInfo(bookId, title, bean.getChapter().getCpContent());
                                iBookChapters.finishChapters();
                                title = titles.poll();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (bookChapterList.get(0).getTitle().equals(title)) {
                                    iBookChapters.errorChapters();
                                }
                                LogUtils.e(throwable.getMessage());
                            }
                        }, new Action() {
                            @Override
                            public void run() throws Exception {

                            }
                        }, new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                mDisposable = disposable;
                            }
                        });

    }
}
