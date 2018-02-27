package com.lianglu.weyue.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by Liang_Lu on 2018/1/5.
 * 重写Textview （文字过长滚动显示）
 */

public class MarqueTextView extends android.support.v7.widget.AppCompatTextView {
    public MarqueTextView(Context context) {
        super(context);
    }

    public MarqueTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        //就是把这里返回true即可
        return true;
    }
}
