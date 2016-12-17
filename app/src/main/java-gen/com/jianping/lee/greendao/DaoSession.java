package com.jianping.lee.greendao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.jianping.lee.greendao.BlackNum;
import com.jianping.lee.greendao.LockedApp;

import com.jianping.lee.greendao.BlackNumDao;
import com.jianping.lee.greendao.LockedAppDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig blackNumDaoConfig;
    private final DaoConfig lockedAppDaoConfig;

    private final BlackNumDao blackNumDao;
    private final LockedAppDao lockedAppDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        blackNumDaoConfig = daoConfigMap.get(BlackNumDao.class).clone();
        blackNumDaoConfig.initIdentityScope(type);

        lockedAppDaoConfig = daoConfigMap.get(LockedAppDao.class).clone();
        lockedAppDaoConfig.initIdentityScope(type);

        blackNumDao = new BlackNumDao(blackNumDaoConfig, this);
        lockedAppDao = new LockedAppDao(lockedAppDaoConfig, this);

        registerDao(BlackNum.class, blackNumDao);
        registerDao(LockedApp.class, lockedAppDao);
    }
    
    public void clear() {
        blackNumDaoConfig.getIdentityScope().clear();
        lockedAppDaoConfig.getIdentityScope().clear();
    }

    public BlackNumDao getBlackNumDao() {
        return blackNumDao;
    }

    public LockedAppDao getLockedAppDao() {
        return lockedAppDao;
    }

}
