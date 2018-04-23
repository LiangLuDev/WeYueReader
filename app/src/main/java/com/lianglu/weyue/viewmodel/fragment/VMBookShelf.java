package com.lianglu.weyue.viewmodel.fragment;

import android.content.Context;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.lianglu.weyue.api.BookService;
import com.lianglu.weyue.api.UserService;
import com.lianglu.weyue.db.entity.BookChapterBean;
import com.lianglu.weyue.db.entity.CollBookBean;
import com.lianglu.weyue.db.helper.CollBookHelper;
import com.lianglu.weyue.model.BookBean;
import com.lianglu.weyue.model.BookChaptersBean;
import com.lianglu.weyue.model.DeleteBookBean;
import com.lianglu.weyue.utils.LoadingHelper;
import com.lianglu.weyue.utils.LogUtils;
import com.lianglu.weyue.utils.ToastUtils;
import com.lianglu.weyue.utils.rxhelper.RxObserver;
import com.lianglu.weyue.view.fragment.IBookShelf;
import com.lianglu.weyue.viewmodel.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by Liang_Lu on 2018/1/19.
 */

public class VMBookShelf extends BaseViewModel {
    IBookShelf iBookShelf;

    public VMBookShelf(Context mContext, IBookShelf iBookShelf) {
        super(mContext);
        this.iBookShelf = iBookShelf;
    }

    /**
     * 删除服务器书架书籍信息
     *
     * @param mCollBookBean
     */
    public void deleteBookShelfToServer(CollBookBean mCollBookBean) {
        LoadingHelper.getInstance().showLoading(mContext);
        DeleteBookBean bean=new DeleteBookBean();
        bean.setBookid(mCollBookBean.get_id());
        RxHttpUtils.getSInstance().addHeaders(tokenMap()).createSApi(UserService.class)
                .deleteBookShelf(bean).compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver<String>() {
                    @Override
                    protected void onError(String errorMsg) {
                        LoadingHelper.getInstance().hideLoading();
                    }

                    @Override
                    protected void onSuccess(String data) {
                        LoadingHelper.getInstance().hideLoading();
                        ToastUtils.show(data);
                        CollBookHelper.getsInstance().removeBookInRx(mCollBookBean);
                        iBookShelf.deleteSuccess();
                    }
                });

    }

    /**
     * 获取用户信息
     */
    public void getBookShelf(List<CollBookBean> mAllBooks) {
        RxHttpUtils.getSInstance().addHeaders(tokenMap()).createSApi(UserService.class)
                .getBookShelf().compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver<List<BookBean>>() {
                    @Override
                    protected void onError(String errorMsg) {
                        iBookShelf.stopLoading();
                    }

                    @Override
                    protected void onSuccess(List<BookBean> bookBeans) {
                        iBookShelf.stopLoading();
                        List<CollBookBean> beans = new ArrayList<>();
                        for (BookBean bookBean : bookBeans) {
                            beans.add(bookBean.getCollBookBean());
                            for (CollBookBean collBookBean : mAllBooks) {
                                if (bookBean.get_id().equals(collBookBean.get_id())) {
                                    //删除出用户收藏并且本地缓存的书籍
                                    beans.remove(bookBean.getCollBookBean());
                                }
                            }
                        }
                        iBookShelf.booksShelfInfo(beans);
                    }
                });
    }

    /**
     * 1、判断本地数据库有没有收藏书籍的数据。
     * 2、本地数据库没有收藏书籍数据就网络请求。否则就取本地数据
     *
     * @param collBookBean
     */
    public void setBookInfo(CollBookBean collBookBean) {
        LoadingHelper.getInstance().showLoading(mContext);
        if (CollBookHelper.getsInstance().findBookById(collBookBean.get_id()) == null) {
            RxHttpUtils.getSInstance().addHeaders(tokenMap()).createSApi(BookService.class)
                    .bookChapters(collBookBean.get_id())
                    .compose(Transformer.switchSchedulers())
                    .subscribe(new RxObserver<BookChaptersBean>() {
                        @Override
                        protected void onError(String errorMsg) {
                            LoadingHelper.getInstance().hideLoading();
                        }

                        @Override
                        protected void onSuccess(BookChaptersBean data) {
                            LoadingHelper.getInstance().hideLoading();
                            List<BookChapterBean> bookChapterList = new ArrayList<>();
                            for (BookChaptersBean.ChatpterBean bean : data.getChapters()) {
                                BookChapterBean chapterBean = new BookChapterBean();
                                chapterBean.setBookId(data.getBook());
                                chapterBean.setLink(bean.getLink());
                                chapterBean.setTitle(bean.getTitle());
//                                chapterBean.setTaskName("下载");
                                chapterBean.setUnreadble(bean.isRead());
                                bookChapterList.add(chapterBean);
                            }
                            collBookBean.setBookChapters(bookChapterList);
                            CollBookHelper.getsInstance().saveBookWithAsync(collBookBean);
                            iBookShelf.bookInfo(collBookBean);
                        }

                        @Override
                        public void onSubscribe(Disposable d) {
                            addDisposadle(d);
                        }
                    });
        } else {
            LoadingHelper.getInstance().hideLoading();
            iBookShelf.bookInfo(collBookBean);
        }


    }

}
