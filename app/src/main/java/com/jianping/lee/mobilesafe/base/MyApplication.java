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

import net.youmi.android.AdManager;

import java.util.ArrayList;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * Created by Li on 2016/11/27.
 */
public class MyApplication extends Application {

    private ArrayList<Activity> activityList = new ArrayList<>();

    public static String BMOB_APP_ID = "a38816603444895eb5ec33d75f480b0f";

    public static String UMENG_APP_ID = "58764940310c9315d90002f1";

    public static String YOUMI_APP_ID = "066e34d4e400074b";

    public static String YOUMI_KEY = "bb138c0ca3d921d1";

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

        initYoumiConfig();

        CommonUtils.startBlackNumService(context);

        CommonUtils.startAppLockService(context);

        if (!DEBUG){
            CrashHandler handler = CrashHandler.getInstance();
            handler.init(this);
        }
    }

    /**
     * 初始化优米广告
     */
    private void initYoumiConfig() {
        AdManager.getInstance(this).init(YOUMI_APP_ID, YOUMI_KEY, false, false);
    }

    private void initUMengConfig() {
        MobclickAgent.setDebugMode(false);
        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this,
                UMENG_APP_ID, "Umeng", MobclickAgent.EScenarioType.E_UM_NORMAL));
    }

    private void initBmobConfig() {
//        Bmob.initialize(this, BMOB_APP_ID);

        BmobConfig config =new BmobConfig.Builder(this)
        //设置appkey
        .setApplicationId(BMOB_APP_ID)
        //请求超时时间（单位为秒）：默认15s
        .setConnectTimeout(5)
        //文件分片上传时每片的大小（单位字节），默认512*1024
        .setUploadBlockSize(1024*1024)
        //文件的过期时间(单位为秒)：默认1800s
        .setFileExpiration(2500)
        .build();
        Bmob.initialize(config);

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
