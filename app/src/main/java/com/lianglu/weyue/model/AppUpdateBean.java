package com.lianglu.weyue.model;

/**
 * Created by Liang_Lu on 2018/1/22.
 */

public class AppUpdateBean {
    private int versioncode;
    private String downloadurl;

    public int getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(int versioncode) {
        this.versioncode = versioncode;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }
}
