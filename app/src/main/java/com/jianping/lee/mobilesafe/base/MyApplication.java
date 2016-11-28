package com.jianping.lee.mobilesafe.base;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;

/**
 * Created by Li on 2016/11/27.
 */
public class MyApplication extends Application {

    private ArrayList<Activity> activityList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     *
     * 像列表中添加activity对象
     * @param activity
     */
    public void addAcitivity(Activity activity){
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
}
