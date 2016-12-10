package com.jianping.lee.mobilesafe.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Li on 2016/12/10.
 */
public class AppInfo {
    /**
     * 应用程序的图标
     */
    private Drawable icon;
    /**
     * 应用程序的名称
     */
    private String appName;
    /**
     * 应用程序的包名
     */
    private String packName;
    /**
     * 应用是否安装在手机内存
     */
    private boolean inRom;
    /**
     * 应用程序apk的大小
     */
    private long apkSize;
    /**
     * 是否是用户程序
     */
    private boolean userApp;
    /**
     * 获取app版本
     */
    private String appVersion;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public boolean isInRom() {
        return inRom;
    }

    public void setInRom(boolean inRom) {
        this.inRom = inRom;
    }

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
}
