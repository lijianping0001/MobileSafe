package com.jianping.lee.mobilesafe.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.jianping.lee.greendao.DaoMaster;
import com.jianping.lee.greendao.DaoSession;
import com.jianping.lee.mobilesafe.db.BaseDao;
import com.jianping.lee.mobilesafe.engine.CrashHandler;
import com.jianping.lee.mobilesafe.utils.CommonUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * Created by Li on 2016/11/27.
 */
public class MyApplication extends Application {

    private ArrayList<Activity> activityList = new ArrayList<>();

    public static String BMOB_APP_ID = "a38816603444895eb5ec33d75f480b0f";

    public static String UMENG_APP_ID = "58764940310c9315d90002f1";

    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    public static final boolean DEBUG = false;

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initBmobConfig();
        context = this;

        initUMengConfig();

        CommonUtils.startBlackNumService(context);

        CommonUtils.startAppLockService(context);

        if (!DEBUG){
            CrashHandler handler = CrashHandler.getInstance();
            handler.init(this);
        }
    }

    private void initUMengConfig() {
        MobclickAgent.setDebugMode(false);
        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this,
                UMENG_APP_ID, "Umeng", MobclickAgent.EScenarioType.E_UM_NORMAL));
    }

    private void initBmobConfig() {
        Bmob.initialize(this, BMOB_APP_ID);

        //自动更新初始化
//        BmobUpdateAgent.initAppVersion();
    }

    /**
     *
     * 像列表中添加activity对象
     * @param activity
     */
    public void addActivity(Activity activity){
        activityList.add(activity);
    }

    /**
     * 删除activity
     * @param activity
     */
    public void removeActivity(Activity activity){
        activityList.remove(activity);
    }

    /**
     * 结束所有activity
     */
    public void finishAllActivities(){
        for (Activity activity : activityList){
            if (null != activity){
                activity.finish();
            }
        }
    }

    public static DaoMaster getDaoMaster(Context context){
        if (daoMaster == null){
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, BaseDao.DB_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    public static DaoSession getDaoSession(Context context){
        if (daoSession == null){
            if (daoMaster == null){
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

}
