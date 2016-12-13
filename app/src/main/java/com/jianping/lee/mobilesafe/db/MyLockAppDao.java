package com.jianping.lee.mobilesafe.db;

import android.content.Context;

import com.jianping.lee.greendao.LockedApp;
import com.jianping.lee.greendao.LockedAppDao;
import com.jianping.lee.mobilesafe.base.MyApplication;

import java.util.List;

/**
 * Created by Li on 2016/12/11.
 */
public class MyLockAppDao extends BaseDao {

    private static MyLockAppDao instance;

    private static LockedAppDao mLockedAppDao;

    public static MyLockAppDao getInstance(Context context){
        if (instance == null){
            synchronized (MyLockAppDao.class){
                if (instance == null){
                    instance = new MyLockAppDao();
                }
            }

            mDaoSession = MyApplication.getDaoSession(context);
            mLockedAppDao = mDaoSession.getLockedAppDao();
        }
        return instance;
    }

    @Override
    public void clearAllData() {
        mLockedAppDao.deleteAll();
    }

    @Override
    public List<LockedApp> getData() {
        return mLockedAppDao.loadAll();
    }

    @Override
    public void addData(Object model, int index) {
        if (model instanceof LockedApp){
            mLockedAppDao.insert((LockedApp)model);
        }
    }

    public boolean find(String packageName){
        LockedApp findApp = mLockedAppDao.queryBuilder().where(LockedAppDao.Properties.PackName.eq(packageName)).unique();
        if (findApp != null){
            return true;
        }

        return false;
    }

    public void deleteData(String packageName){
        LockedApp findApp = mLockedAppDao.queryBuilder().where(LockedAppDao.Properties.PackName.eq(packageName)).unique();
        if (findApp != null){
            mLockedAppDao.delete(findApp);
        }

    }

}
