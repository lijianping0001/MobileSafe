package com.jianping.lee.mobilesafe.db;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.jianping.lee.greendao.BlackNum;
import com.jianping.lee.greendao.BlackNumDao;
import com.jianping.lee.mobilesafe.base.MyApplication;
import com.jianping.lee.mobilesafe.service.AppLockService;
import com.jianping.lee.mobilesafe.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Li on 2016/12/11.
 */
public class MyBlackNumDao extends BaseDao {

    private static MyBlackNumDao instance;

    private static BlackNumDao mBlackNumDao;

    private Context context;

    private MyBlackNumDao(Context context){
        this.context = context;
    }

    public static MyBlackNumDao getInstance(Context context){
        if (instance == null){
            synchronized (MyBlackNumDao.class){
                if (instance == null){
                    instance = new MyBlackNumDao(context);
                }
            }

            mDaoSession = MyApplication.getDaoSession(context);
            mBlackNumDao = mDaoSession.getBlackNumDao();
        }
        return instance;
    }

    @Override
    public void clearAllData() {
        mBlackNumDao.deleteAll();
    }

    /**
     * 根据index找到拦截对象
     * @param index
     * @return
     */
    public List<BlackNum> getData(int index) {
        QueryBuilder<BlackNum> queryBuilder = mBlackNumDao.queryBuilder().where(BlackNumDao.Properties.Index.eq("" + index));

        if (queryBuilder.list().size() > 0){
            return queryBuilder.list();
        }

        return null;
    }

    /**
     * 根据电话号码找到拦截模式
     * @param phoneNum
     * @return
     */
    public String find(String phoneNum){
        QueryBuilder<BlackNum> queryBuilder = mBlackNumDao.queryBuilder().where(BlackNumDao.Properties.Phone.eq(phoneNum));
        if (queryBuilder.list().size() > 0){
            return queryBuilder.list().get(0).getMode() + "";
        }

        return null;
    }

    @Override
    public void addData(Object model, int index) {
        if (model instanceof BlackNum){
            ((BlackNum)model).setIndex(index);
            mBlackNumDao.insert(((BlackNum) model));
        }
    }

    @Override
    public List<BlackNum> getData(){
        List<BlackNum> list = mBlackNumDao.loadAll();
        return list;
    }

    public void deleteData(BlackNum blackNum){
        mBlackNumDao.deleteByKey(blackNum.getId());
    }



}
