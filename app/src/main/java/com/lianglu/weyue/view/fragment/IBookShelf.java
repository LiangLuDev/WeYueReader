package com.lianglu.weyue.view.fragment;

import com.lianglu.weyue.db.entity.CollBookBean;
import com.lianglu.weyue.model.BookBean;
import com.lianglu.weyue.view.base.IBaseLoadView;

import java.util.List;

/**
 * Created by Liang_Lu on 2018/1/19.
 */

public interface IBookShelf extends IBaseLoadView {
    void booksShelfInfo(List<CollBookBean> beans);

    void bookInfo(CollBookBean bean);

    void deleteSuccess();
}
