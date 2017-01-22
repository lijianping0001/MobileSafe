package com.jianping.lee.mobilesafe.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Li on 2017/1/7.
 */
public class UserContact extends BmobObject {

    private String userName;

    private String filePath;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
