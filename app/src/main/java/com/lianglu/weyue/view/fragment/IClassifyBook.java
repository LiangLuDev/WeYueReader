package com.lianglu.weyue.view.fragment;

import com.lianglu.weyue.model.BookClassifyBean;
import com.lianglu.weyue.view.base.IBaseDataView;
import com.lianglu.weyue.view.base.IBaseLoadView;

/**
 * Created by Liang_Lu on 2017/12/4.
 */

public interface IClassifyBook extends IBaseDataView {

    void getBookClassify(BookClassifyBean bookClassifyBean);

}
