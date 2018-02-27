package com.lianglu.weyue.view.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianglu.weyue.R;
import com.lianglu.weyue.model.MainMenuBean;
import com.lianglu.weyue.utils.DimenUtils;

import java.util.List;

/**
 * Created by Liang_Lu on 2017/11/28.
 */

public class MainMenuAdapter extends BaseQuickAdapter<MainMenuBean,BaseViewHolder> {


    public MainMenuAdapter(@Nullable List<MainMenuBean> data) {
        super(R.layout.adapter_main_menu,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MainMenuBean item) {
        TextView mTvName=helper.getView(R.id.tv_name);
        mTvName.setText(item.getName());
        mTvName.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(item.getIcon()),null,null,null);
        mTvName.setCompoundDrawablePadding(DimenUtils.dp2px(10));
    }
}
