package com.lianglu.weyue.model;

import java.io.Serializable;

/**
 * Created by Liang_Lu on 2017/11/28.
 */

public class MainMenuBean implements Serializable {
    private String name;
    private int icon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
