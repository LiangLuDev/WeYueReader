package com.lianglu.weyue.view.activity;

import com.lianglu.weyue.model.BookChaptersBean;
import com.lianglu.weyue.view.base.IBaseLoadView;

/**
 * Created by Liang_Lu on 2017/12/11.
 */

public interface IBookChapters extends IBaseLoadView {
    void bookChapters(BookChaptersBean bookChaptersBean);

    void finishChapters();

    void errorChapters();

}
