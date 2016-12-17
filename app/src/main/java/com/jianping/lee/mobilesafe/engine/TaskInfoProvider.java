package com.jianping.lee.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.model.ProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Li on 2016/12/15.
 */
public class TaskInfoProvider {

    public static List<ProcessInfo> getRunningProcess(Context context){
        List<ProcessInfo> processInfos = new ArrayList<>();

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager packageManager = context.getPackageManager();

        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos){
            ProcessInfo processInfo = new ProcessInfo();
            processInfo.setPackName(info.processName);

            long memSize = manager.getProcessMemoryInfo(new int[]{info.pid})[0].getTotalPrivateDirty() * 1024;
            processInfo.setMemSize(memSize);

            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(info.processName, 0);
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                processInfo.setAppName(appName);

                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                processInfo.setIcon(icon);

                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 ){
                    processInfo.setUserTask(false);
                }else {
                    processInfo.setUserTask(true);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                processInfo.setAppName(info.processName);
                processInfo.setIcon(context.getResources().getDrawable(R.drawable.default_image));
            }

            processInfos.add(processInfo);

        }

        return processInfos;
    }
}
