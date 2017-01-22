package com.jianping.lee.mobilesafe.model;

import cn.bmob.v3.BmobObject;

/**
 * @fileName: Feedback
 * @Author: Li Jianping
 * @Date: 2016/8/10 12:05
 * @Description:
 */
public class Feedback extends BmobObject{
    //反馈内容
    private String content;
    //联系方式
    private String contact;
    //版本
    private String version;
    //设备类型
    private String deviceType = "android";

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
