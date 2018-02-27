package com.lianglu.weyue.view.fragment;

import com.lianglu.weyue.model.BookBean;
import com.lianglu.weyue.view.base.IBaseDataView;
import com.lianglu.weyue.view.base.IBaseLoadView;

import java.util.List;

/**
 * Created by Liang_Lu on 2017/12/6.
 */

public interface IBookSearchInfo extends IBaseLoadView {
    void getSearchBooks(List<BookBean> bookBeans);
}
