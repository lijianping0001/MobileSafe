package com.jianping.lee.mobilesafe.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.jianping.lee.greendao.DaoMaster;
import com.jianping.lee.greendao.DaoSession;
import com.jianping.lee.mobilesafe.db.BaseDao;

import java.util.ArrayList;

/**
 * Created by Li on 2016/11/27.
 */
public class MyApplication extends Application {

    private ArrayList<Activity> activityList = new ArrayList<>();

    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
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
