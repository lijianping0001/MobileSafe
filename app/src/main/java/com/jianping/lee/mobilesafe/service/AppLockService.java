package com.jianping.lee.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObservable;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.jianping.lee.greendao.LockedApp;
import com.jianping.lee.mobilesafe.activity.EnterPasswordActivity;
import com.jianping.lee.mobilesafe.db.MyLockAppDao;
import com.jianping.lee.mobilesafe.utils.LogUtils;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Li on 2017/1/8.
 */
public class AppLockService extends Service {
    public static String actionFilter = "com.jianping.lee.mobilesafe.applock";

    public static String actionChangeFileter = "com.jianping.lee.mobilesafe.applock.change";

    private boolean flag;

    private Intent intent;

    private MyLockAppDao dao;

    private ActivityManager manager;

    private InnerAppLockReceiver receiver;

    private ApplockDBObserver observer;

    private List<ActivityManager.RunningTaskInfo> taskInfos;

    private List<LockedApp> lockedPackNames;

    private String packName;
    /**
     * 临时要停止保护的应用程序包名
     */
    private String tempStopProtectPackname;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LogUtils.i("开启程序锁服务==");
        receiver = new InnerAppLockReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(actionFilter);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        registerReceiver(receiver, filter);

//        changeReceiver = new InnerAppLockChangeReceiver();
//        IntentFilter changeFilter = new IntentFilter();
//        changeFilter.addAction(actionChangeFileter);
//        registerReceiver(changeReceiver, changeFilter);

        Uri uri = Uri.parse("content://com.jianping.lee.mobilesafe.applock");
        observer = new ApplockDBObserver(new Handler());
        getContentResolver().registerContentObserver(uri, true, observer);
        manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        dao = MyLockAppDao.getInstance(this);
        lockedPackNames = dao.getData();
        intent = new Intent(AppLockService.this, EnterPasswordActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startWatchDog();

    }

    private void startWatchDog() {
        if (flag){
            return;
        }

        flag = true;
        new Thread(){
            @Override
            public void run() {
                while (flag){

                    packName = getTaskPackageName();

                    for (LockedApp app : lockedPackNames){
                        if (app.getPackName().equals(packName)){
                            //应用需要被保护，弹出密码界面
                            //判断这个应用是否需要临时停止保护
                            if (!packName.equals(tempStopProtectPackname)){
                                intent.putExtra("packName", packName);
                                startActivity(intent);
                            }
                        }
                    }

                    SystemClock.sleep(30);
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        unregisterReceiver(receiver);
        getContentResolver().unregisterContentObserver(observer);
        observer = null;
        receiver = null;
    }

    private String getTaskPackageName(){
        String currentApp = "CurrentNULL";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService("usagestats");
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
        return currentApp;
//        taskInfos = manager.getRunningTasks(1);
//        packName = taskInfos.get(0).topActivity.getPackageName();
    }

    private class InnerAppLockReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (actionFilter.equals(intent.getAction())){
                tempStopProtectPackname = intent.getStringExtra("packName");
            }else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())){
                flag = false;
                tempStopProtectPackname = null;
            }else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())){
                startWatchDog();
            }
        }
    }


    private class InnerAppLockChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.i("接收到数据变化了");
            if (actionChangeFileter.equals(intent.getAction())){
                LogUtils.i("数据变化了。。");
                lockedPackNames = dao.getData();
            }
        }
    }

    private class ApplockDBObserver extends ContentObserver{

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public ApplockDBObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            lockedPackNames = dao.getData();
            LogUtils.i("数据变化了。。");
        }
    }


}
