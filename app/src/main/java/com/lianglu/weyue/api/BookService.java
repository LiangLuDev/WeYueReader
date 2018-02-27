package com.lianglu.weyue.api;

import com.allen.library.bean.BaseData;
import com.lianglu.weyue.model.BookBean;
import com.lianglu.weyue.model.BookChaptersBean;
import com.lianglu.weyue.model.BookClassifyBean;
import com.lianglu.weyue.model.ChapterContentBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Liang_Lu on 2017/12/3.
 * 书籍模块接口
 */

public interface BookService {

    /**
     * 获取所有分类
     *
     * @return
     */
    @GET(ModelPath.API + "/classify")
    Observable<BaseData<BookClassifyBean>> bookClassify();

    /**
     * 获取分类下的书籍
     *
     * @param type
     * @param major
     * @param page
     * @return
     */
    @GET(ModelPath.BOOKS)
    Observable<BaseData<List<BookBean>>> books( @Query("type") String type,
                                               @Query("major") String major, @Query("page") int page);

    /**
     * 获取书籍信息
     *
     * @param bookId
     * @return
     */
    @GET(ModelPath.BOOKS + "/{bookId}")
    Observable<BaseData<BookBean>> bookInfo(@Path("bookId") String bookId);
    /**
     * 获取书籍目录
     *
     * @param bookId
     * @return
     */
    @GET(ModelPath.BOOKS + "/{bookId}/chapters")
    Observable<BaseData<BookChaptersBean>> bookChapters(@Path("bookId") String bookId);

    /**
     * 根据link获取正文
     *
     * @param link 正文链接
     * @return
     */
    @GET("http://chapterup.zhuishushenqi.com/chapter/{link}")
    Observable<ChapterContentBean> bookContent(@Path("link") String link);

    /**
     * 根据tag获取书籍
     *
     * @param bookTag
     * @param page
     * @return
     */
    @GET(ModelPath.BOOKS + "/tag")
    Observable<BaseData<List<BookBean>>> booksByTag(@Query("bookTag") String bookTag, @Query("page") int page);

    /**
     * 搜索书籍
     *
     * @param keyword
     * @return
     */
    @GET(ModelPath.API + "/search")
    Observable<BaseData<List<BookBean>>> booksSearch(@Query("keyword") String keyword);

}
