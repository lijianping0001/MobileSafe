package com.jianping.lee.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.jianping.lee.mobilesafe.model.CacheInfo;
import com.jianping.lee.mobilesafe.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Li on 2016/12/19.
 */
public class ApkProvider {

    private ArrayList<CacheInfo> cacheInfos;

    private Long totalSize;

    public ApkProvider(Long totalSize){
        cacheInfos = new ArrayList<>();
        this.totalSize = totalSize;
    }

    @WorkerThread
    public ArrayList<CacheInfo> findAllAPKFile(Context context, File file) {
        // 手机上的文件,目前只判断SD卡上的APK文件
        // file = Environment.getDataDirectory();
        // SD卡上的文件目录
        if (file.isFile()) {
            String nameStr = file.getName();
            CacheInfo cacheInfo = new CacheInfo();
            String apkPath = null;
            // MimeTypeMap.getSingleton()
            if (nameStr.toLowerCase().endsWith(".apk")) {
                LogUtils.i("file path===" + nameStr);
                apkPath = file.getAbsolutePath();// apk文件的绝对路劲
                PackageManager pm = context.getPackageManager();
                PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
                ApplicationInfo appInfo = packageInfo.applicationInfo;


                /**获取apk的图标 */
                appInfo.sourceDir = apkPath;
                appInfo.publicSourceDir = apkPath;
                Drawable apkIcon = appInfo.loadIcon(pm);
                cacheInfo.setIcon(apkIcon);
                /** 得到包名 */
                String packageName = packageInfo.packageName;
                cacheInfo.setPackName(packageName);

                cacheInfo.setAppName(file.getName());
                /**安装处理类型*/
                int type = doType(pm, packageName, packageInfo.versionCode);
                if (type == 2){
                    cacheInfo.setInstalled(false);
                }else {
                    cacheInfo.setInstalled(true);
                }
                totalSize += file.length();
                cacheInfo.setCacheSize(file.length());
                cacheInfos.add(cacheInfo);
            }
        } else {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File fileStr : files) {
                    findAllAPKFile(context, fileStr);
                }
            }
        }
        return cacheInfos;
    }

    /**
     * 判断该应用在手机中的安装情况
     * @param pm                   PackageManager
     * @param packageName  要判断应用的包名
     * @param versionCode     要判断应用的版本号
     */
    private int doType(PackageManager pm, String packageName, int versionCode) {
        List<PackageInfo> pakageinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo pi : pakageinfos) {
            String pi_packageName = pi.packageName;
            int pi_versionCode = pi.versionCode;
            //如果这个包名在系统已经安装过的应用中存在
            if(packageName.endsWith(pi_packageName)){
                //Log.i("test","此应用安装过了");
                if(versionCode==pi_versionCode){
//                    Log.i("test", "已经安装，不用更新，可以卸载该应用");
                    return 0;
                }else if(versionCode>pi_versionCode){
//                    Log.i("test","已经安装，有更新");
                    return 1;
                }
            }
        }
//        Log.i("test","未安装该应用，可以安装");
        return 2;
    }
}
