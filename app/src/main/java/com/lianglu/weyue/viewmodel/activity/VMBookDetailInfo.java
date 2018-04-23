package com.lianglu.weyue.viewmodel.activity;

import android.content.Context;
import android.util.Log;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.lianglu.weyue.api.BookService;
import com.lianglu.weyue.api.UserService;
import com.lianglu.weyue.model.BookBean;
import com.lianglu.weyue.model.DeleteBookBean;
import com.lianglu.weyue.utils.LogUtils;
import com.lianglu.weyue.utils.ToastUtils;
import com.lianglu.weyue.utils.rxhelper.RxObserver;
import com.lianglu.weyue.db.entity.BookChapterBean;
import com.lianglu.weyue.db.entity.CollBookBean;
import com.lianglu.weyue.db.helper.CollBookHelper;
import com.lianglu.weyue.model.BookChaptersBean;
import com.lianglu.weyue.view.activity.IBookDetail;
import com.lianglu.weyue.viewmodel.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by Liang_Lu on 2017/12/8.
 */

public class VMBookDetailInfo extends BaseViewModel {
    IBookDetail iBookDetail;


    public VMBookDetailInfo(Context mContext, IBookDetail iBookDetail) {
        super(mContext);
        this.iBookDetail = iBookDetail;
    }

    /**
     * 获取书籍信息
     *
     * @param bookid
     */
    public void bookInfo(String bookid) {
        iBookDetail.showLoading();
        RxHttpUtils.getSInstance().addHeaders(tokenMap())
                .createSApi(BookService.class).bookInfo(bookid)
                .compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver<BookBean>() {
                    @Override
                    protected void onError(String errorMsg) {
                        iBookDetail.stopLoading();
                    }

                    @Override
                    protected void onSuccess(BookBean bookBean) {
                        iBookDetail.stopLoading();
                        iBookDetail.getBookInfo(bookBean);
                    }
                });
    }

    /**
     * 添加书籍到书架
     *
     * @param collBookBean
     */
    public void addBookShelf(CollBookBean collBookBean) {

        iBookDetail.showLoading();
        RxHttpUtils.getSInstance().addHeaders(tokenMap()).createSApi(BookService.class)
                .bookChapters(collBookBean.get_id())
                .compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver<BookChaptersBean>() {
                    @Override
                    protected void onError(String errorMsg) {
                        iBookDetail.stopLoading();
                    }

                    @Override
                    protected void onSuccess(BookChaptersBean data) {
                        iBookDetail.stopLoading();
                        List<BookChapterBean> bookChapterList = new ArrayList<>();
                        for (BookChaptersBean.ChatpterBean bean : data.getChapters()) {
                            BookChapterBean chapterBean = new BookChapterBean();
                            chapterBean.setBookId(data.getBook());
                            chapterBean.setLink(bean.getLink());
                            chapterBean.setTitle(bean.getTitle());
//                            chapterBean.setTaskName("下载");
                            chapterBean.setUnreadble(bean.isRead());
                            bookChapterList.add(chapterBean);
                        }
                        collBookBean.setBookChapters(bookChapterList);
                        CollBookHelper.getsInstance().saveBookWithAsync(collBookBean);

                        addBookShelfToServer(collBookBean.get_id());
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposadle(d);
                    }
                });
    }

    /**
     * 添加书籍信息到书架
     *
     * @param bookid
     */
    public void addBookShelfToServer(String bookid) {
        RxHttpUtils.getSInstance().addHeaders(tokenMap()).createSApi(UserService.class)
                .addBookShelf(bookid).compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver<String>() {
                    @Override
                    protected void onError(String errorMsg) {

                    }

                    @Override
                    protected void onSuccess(String data) {
                        ToastUtils.show(data);
                    }
                });

    }

    /**
     * 删除书籍信息到书架
     *
     * @param mCollBookBean
     */
    public void deleteBookShelfToServer(CollBookBean mCollBookBean) {
        DeleteBookBean bean=new DeleteBookBean();
        bean.setBookid(mCollBookBean.get_id());
        RxHttpUtils.getSInstance().addHeaders(tokenMap()).createSApi(UserService.class)
                .deleteBookShelf(bean).compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver<String>() {
                    @Override
                    protected void onError(String errorMsg) {

                    }

                    @Override
                    protected void onSuccess(String data) {
                        ToastUtils.show(data);
                        CollBookHelper.getsInstance().removeBookInRx(mCollBookBean);
                    }
                });

    }

}
