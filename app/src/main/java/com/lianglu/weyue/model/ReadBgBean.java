package com.lianglu.weyue.model;

/**
 * Created by Liang_Lu on 2017/11/24.
 */

public class ReadBgBean {
    private int bgColor;
    private boolean isSelect;


    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }
}
