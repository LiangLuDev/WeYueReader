package com.lianglu.weyue.db.helper;

import com.lianglu.weyue.db.entity.CollBookBean;
import com.lianglu.weyue.db.gen.CollBookBeanDao;
import com.lianglu.weyue.db.gen.DaoSession;
import com.lianglu.weyue.utils.Constant;
import com.lianglu.weyue.utils.FileUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Liang_Lu on 2017/12/1.
 * 书架数据库操作工具类
 */

public class CollBookHelper {
    private static volatile CollBookHelper sInstance;
    private static DaoSession daoSession;
    private static CollBookBeanDao collBookBeanDao;

    public static CollBookHelper getsInstance() {
        if (sInstance == null) {
            synchronized (CollBookHelper.class) {
                if (sInstance == null) {
                    sInstance = new CollBookHelper();
                    daoSession = DaoDbHelper.getInstance().getSession();
                    collBookBeanDao = daoSession.getCollBookBeanDao();
                }
            }
        }
        return sInstance;
    }

    /**
     * 保存一本书籍 同步
     *
     * @param collBookBean
     */
    public void saveBook(CollBookBean collBookBean) {
        collBookBeanDao.insertOrReplace(collBookBean);
    }

    /**
     * 保存多本书籍 同步
     *
     * @param collBookBeans
     */
    public void saveBooks(List<CollBookBean> collBookBeans) {
        collBookBeanDao.insertOrReplaceInTx(collBookBeans);
    }


    /**
     * 保存一本书籍 异步
     *
     * @param collBookBean
     */
    public void saveBookWithAsync(CollBookBean collBookBean) {
        daoSession.startAsyncSession().runInTx(() -> {
            if (collBookBean.getBookChapters() != null) {
                //存储BookChapterBean(需要找个免更新的方式)
                daoSession.getBookChapterBeanDao()
                        .insertOrReplaceInTx(collBookBean.getBookChapters());
            }
            //存储CollBook (确保先后顺序，否则出错)
            collBookBeanDao.insertOrReplace(collBookBean);
        });
    }

    /**
     * 保存多本书籍 异步
     *
     * @param collBookBeans
     */
    public void saveBooksWithAsync(List<CollBookBean> collBookBeans) {
        daoSession.startAsyncSession()
                .runInTx(
                        () -> {
                            for (CollBookBean bean : collBookBeans) {
                                if (bean.getBookChapters() != null) {
                                    //存储BookChapterBean(需要修改，如果存在id相同的则无视)
                                    daoSession.getBookChapterBeanDao()
                                            .insertOrReplaceInTx(bean.getBookChapters());
                                }
                            }
                            //存储CollBook (确保先后顺序，否则出错)
                            collBookBeanDao.insertOrReplaceInTx(collBookBeans);
                        }
                );

    }

    /**
     * 删除书籍
     *
     * @param collBookBean
     */
    public Observable<String> removeBookInRx(CollBookBean collBookBean) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                //查看文本中是否存在删除的数据
                FileUtils.deleteFile(Constant.BOOK_CACHE_PATH + collBookBean.get_id());
                //删除任务
                BookDownloadHelper.getsInstance().removeDownloadTask(collBookBean.get_id());
                //删除目录
                BookChapterHelper.getsInstance().removeBookChapters(collBookBean.get_id());
                //删除CollBook
                collBookBeanDao.delete(collBookBean);
                e.onNext("删除成功");
            }
        });
    }

    /**
     * 删除所有书籍
     */
    public void removeAllBook() {
        for (CollBookBean collBookBean : findAllBooks()) {
            removeBookInRx(collBookBean);
        }
    }

    /**
     * 查询一本书籍
     */
    public CollBookBean findBookById(String id) {
        CollBookBean bookBean = collBookBeanDao.queryBuilder().where(CollBookBeanDao.Properties._id.eq(id))
                .unique();
        return bookBean;
    }

    /**
     * 查询所有书籍
     */
    public List<CollBookBean> findAllBooks() {
        return collBookBeanDao
                .queryBuilder()
                .orderDesc(CollBookBeanDao.Properties.LastRead)
                .list();
    }


}
