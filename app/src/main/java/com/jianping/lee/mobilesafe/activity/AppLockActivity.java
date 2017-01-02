package com.jianping.lee.mobilesafe.activity;

import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.jianping.lee.greendao.LockedApp;
import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.adapter.AppLockAdapter;
import com.jianping.lee.mobilesafe.adapter.ViewPagerAdapter;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.db.MyLockAppDao;
import com.jianping.lee.mobilesafe.engine.AppInfoProvider;
import com.jianping.lee.mobilesafe.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public class AppLockActivity extends BaseActivity {

    @InjectView(R.id.tl_app_lock)
    TabLayout mTablayout;

    @InjectView(R.id.vp_app_lock)
    ViewPager mViewPager;

    @InjectView(R.id.ll_app_lock_loading)
    LinearLayout mLoading;

    private View unlock, lock;

    private List<String> mTitleList = new ArrayList<>();

    private List<View> mViewList = new ArrayList<>();

    /**
     * 已加锁应用集合
     */
    private List<AppInfo> appLockedList;
    /**
     * 未加锁应用集合
     */
    private List<AppInfo> appUnlockList;
    /**
     * 所有应用集合
     */
    private List<AppInfo> allAppInfos;

    private MyHandler handler = new MyHandler(this);

    private AppLockAdapter lockedAdapter;

    private AppLockAdapter unlockAdapter;

    private AppInfo mSelectedInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.func_app_lock));
        mBack.setVisibility(View.VISIBLE);

        unlock = LayoutInflater.from(this).inflate(R.layout.layout_system_app, null);
        lock = LayoutInflater.from(this).inflate(R.layout.layout_user_app, null);

        mTitleList.add(getString(R.string.unlocked));
        mTitleList.add(getString(R.string.locked));

        mViewList.add(unlock);
        mViewList.add(lock);

        mTablayout.setTabMode(TabLayout.MODE_FIXED);
        mTablayout.addTab(mTablayout.newTab().setText(mTitleList.get(0)));
        mTablayout.addTab(mTablayout.newTab().setText(mTitleList.get(1)));

        ViewPagerAdapter adapter = new ViewPagerAdapter(mTitleList, mViewList);
        mViewPager.setAdapter(adapter);
        mTablayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void initData() {
        mLoading.setVisibility(View.VISIBLE);

        new Thread(){
            @Override
            public void run() {
                super.run();
                allAppInfos = AppInfoProvider.getAppInfos(AppLockActivity.this);
                appUnlockList = new ArrayList<AppInfo>();
                appLockedList = new ArrayList<AppInfo>();
                for (AppInfo appInfo : allAppInfos){
                    if (MyLockAppDao.getInstance(AppLockActivity.this).find(appInfo.getPackName())){
                        appLockedList.add(appInfo);
                    }else {
                        appUnlockList.add(appInfo);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }


    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        RecyclerView unlockList = (RecyclerView) unlock.findViewById(R.id.rv_system_app);
        RecyclerView lockedList = (RecyclerView) lock.findViewById(R.id.rv_user_app);

        unlockAdapter = new AppLockAdapter(this, appUnlockList, listener, false);
        lockedAdapter = new AppLockAdapter(this, appLockedList, listener, true);

        unlockList.setHasFixedSize(false);
        unlockList.setLayoutManager(new LinearLayoutManager(this));
        unlockList.setAdapter(unlockAdapter);

        lockedList.setHasFixedSize(false);
        lockedList.setLayoutManager(new LinearLayoutManager(this));
        lockedList.setAdapter(lockedAdapter);

        mLoading.setVisibility(View.GONE);
    }

    private AppLockAdapter.LockListener listener = new AppLockAdapter.LockListener() {
        @Override
        public void onClickLock(final AppInfo info, View view) {
            //未加锁
            if (mTablayout.getSelectedTabPosition() == 0){
                TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0);
                animation.setDuration(200);
                view.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        LockedApp lockedApp = new LockedApp();
                        lockedApp.setAppName(info.getAppName());
                        lockedApp.setPackName(info.getPackName());

                        appUnlockList.remove(info);
                        appLockedList.add(info);
                        MyLockAppDao.getInstance(AppLockActivity.this).addData(lockedApp, 0);
                        lockedAdapter.notifyDataSetChanged();
                        unlockAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }else {
                TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, -1.0f,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0);
                animation.setDuration(200);
                view.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        appLockedList.remove(info);
                        appUnlockList.add(info);
                        MyLockAppDao.getInstance(AppLockActivity.this).deleteData(info.getPackName());
                        lockedAdapter.notifyDataSetChanged();
                        unlockAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        }
    };
}
