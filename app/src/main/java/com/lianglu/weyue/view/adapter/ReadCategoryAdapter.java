package com.lianglu.weyue.view.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianglu.weyue.R;
import com.lianglu.weyue.widget.page.TxtChapter;

import java.util.List;

/**
 * Created by Liang_Lu on 2017/11/24.
 */

public class ReadCategoryAdapter extends BaseQuickAdapter<TxtChapter, BaseViewHolder> {


    public ReadCategoryAdapter(@Nullable List<TxtChapter> data) {
        super(R.layout.item_category, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TxtChapter item) {
        //首先判断是否该章已下载
        Drawable drawable = null;

        //TODO:目录显示设计的有点不好，需要靠成员变量是否为null来判断。
        //如果没有链接地址表示是本地文件
        if (item.getLink() == null) {
            drawable = ContextCompat.getDrawable(mContext, R.drawable.selector_category_load);
        } else {
            if (item.getBookId() != null) {
                drawable = ContextCompat.getDrawable(mContext, R.drawable.selector_category_load);
            } else {
                drawable = ContextCompat.getDrawable(mContext, R.drawable.selector_category_unload);
            }
        }

        TextView category_tv_chapter = helper.getView(R.id.category_tv_chapter);
        category_tv_chapter.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        category_tv_chapter.setSelected(item.isSelect());
        category_tv_chapter.setText(item.getTitle());
        if (item.isSelect()) {
            category_tv_chapter.setTextColor(ContextCompat.getColor(mContext, R.color.color_ec4a48));
        } else {
            category_tv_chapter.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }

    }
}
