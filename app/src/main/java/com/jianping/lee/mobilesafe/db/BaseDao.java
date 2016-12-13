package com.jianping.lee.mobilesafe.db;

import com.jianping.lee.greendao.DaoSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Li on 2016/12/11.
 */
public abstract class BaseDao<T> {
    public static final String DB_NAME = "mobileSafe";

    protected static DaoSession mDaoSession;

    public abstract void clearAllData();

    public abstract List<T> getData();

    public abstract void addData(T model, int index);
}
