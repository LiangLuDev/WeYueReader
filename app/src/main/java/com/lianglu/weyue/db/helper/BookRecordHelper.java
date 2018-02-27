package com.lianglu.weyue.db.helper;

import com.lianglu.weyue.db.entity.BookRecordBean;
import com.lianglu.weyue.db.gen.BookRecordBeanDao;
import com.lianglu.weyue.db.gen.DaoSession;

/**
 * Created by Liang_Lu on 2017/12/1.
 * 阅读记录数据库工具类
 */

public class BookRecordHelper {
    private static volatile BookRecordHelper sInstance;
    private static DaoSession daoSession;
    private static BookRecordBeanDao bookRecordBeanDao;

    public static BookRecordHelper getsInstance() {
        if (sInstance == null) {
            synchronized (BookRecordHelper.class) {
                if (sInstance == null) {
                    sInstance = new BookRecordHelper();
                    daoSession = DaoDbHelper.getInstance().getSession();
                    bookRecordBeanDao = daoSession.getBookRecordBeanDao();
                }
            }
        }
        return sInstance;
    }

    /**
     * 保存阅读记录
     */
    public void saveRecordBook(BookRecordBean collBookBean) {
        bookRecordBeanDao.insertOrReplace(collBookBean);
    }

    /**
     * 删除书籍记录
     */
    public void removeBook(String bookId) {
        bookRecordBeanDao
                .queryBuilder()
                .where(BookRecordBeanDao.Properties.BookId.eq(bookId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }


    /**
     * 查询阅读记录
     */
    public BookRecordBean findBookRecordById(String bookId) {
        return bookRecordBeanDao.queryBuilder()
                .where(BookRecordBeanDao.Properties.BookId.eq(bookId)).unique();
    }


}
