package com.lianglu.weyue.view.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianglu.weyue.R;
import com.lianglu.weyue.model.BookClassifyBean;
import com.lianglu.weyue.utils.Constant;

import java.util.List;

/**
 * Created by Liang_Lu on 2017/12/4.
 */

public class ClassifyAdapter extends BaseQuickAdapter<BookClassifyBean.ClassifyBean, BaseViewHolder> {


    public ClassifyAdapter(@Nullable List<BookClassifyBean.ClassifyBean> data) {
        super(R.layout.item_classify, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookClassifyBean.ClassifyBean item) {
        helper.setText(R.id.tv_name, item.getName())
                .setText(R.id.tv_count, item.getBookCount() + "本");

        Glide.with(mContext).load(Constant.BASE_URL+item.getIcon())
                .apply(new RequestOptions().placeholder(R.drawable.ic_default))
                .into((ImageView) helper.getView(R.id.iv_icon));
    }
}
