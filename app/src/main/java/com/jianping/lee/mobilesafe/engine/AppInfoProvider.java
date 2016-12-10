package com.jianping.lee.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.jianping.lee.mobilesafe.model.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Li on 2016/12/10.
 */
public class AppInfoProvider {

    public static List<AppInfo> getAppInfos(Context context){
        PackageManager pm = context.getPackageManager();
        List<AppInfo> appInfos = new ArrayList<>();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);

        for (PackageInfo packageInfo : packageInfos){
            AppInfo appInfo = new AppInfo();
            appInfo.setPackName(packageInfo.packageName);
            appInfo.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
            appInfo.setIcon(packageInfo.applicationInfo.loadIcon(pm));
            appInfo.setAppVersion(packageInfo.versionName);
            String path = packageInfo.applicationInfo.sourceDir;
            File file = new File(path);
            appInfo.setApkSize(file.length());

            int flags = packageInfo.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                appInfo.setUserApp(true);
            }else {
                appInfo.setUserApp(false);
            }

            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0){
                appInfo.setInRom(true);
            }else {
                appInfo.setInRom(false);
            }

            appInfos.add(appInfo);
        }

        return appInfos;

    }
}
