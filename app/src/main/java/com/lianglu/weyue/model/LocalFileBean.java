package com.lianglu.weyue.model;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Liang_Lu on 2017/11/30.
 */

public class LocalFileBean implements Serializable{
    private File file;
    private boolean isSelect;


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
