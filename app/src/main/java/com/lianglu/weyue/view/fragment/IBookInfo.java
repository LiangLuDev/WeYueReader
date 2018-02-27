package com.lianglu.weyue.view.fragment;

import com.lianglu.weyue.model.BookBean;
import com.lianglu.weyue.view.base.IBaseDataView;

import java.util.List;

/**
 * Created by Liang_Lu on 2017/12/6.
 */

public interface IBookInfo extends IBaseDataView {
    void getBooks(List<BookBean> bookBeans,boolean isLoadMore);
}
