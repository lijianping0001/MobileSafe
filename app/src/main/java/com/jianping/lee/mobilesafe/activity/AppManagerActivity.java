package com.jianping.lee.mobilesafe.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.adapter.AppAdapter;
import com.jianping.lee.mobilesafe.adapter.ViewPagerAdapter;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.engine.AppInfoProvider;
import com.jianping.lee.mobilesafe.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public class AppManagerActivity extends BaseActivity {
    @InjectView(R.id.tl_app_manager)
    TabLayout mTablayout;

    @InjectView(R.id.vp_app_manager)
    ViewPager mViewPager;

    @InjectView(R.id.ll_app_manager_loading)
    LinearLayout mLoading;

    private View systemAPP, userApp;

    private List<String> mTitleList = new ArrayList<>();

    private List<View> mViewList = new ArrayList<>();
    /**
     * 系统应用集合
     */
    private List<AppInfo> systemAppInfos;
    /**
     * 用户应用集合
     */
    private List<AppInfo> userAppInfos;
    /**
     * 所有应用集合
     */
    private List<AppInfo> allAppInfos;

    private MyHandler handler = new MyHandler(this);

    private AppAdapter systemAdapter;

    private AppAdapter userAdapter;

    private BroadcastReceiver uninstallReceiver;

    private AppInfo mSelectedInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.func_app_manager));
        mBack.setVisibility(View.VISIBLE);

        systemAPP = LayoutInflater.from(this).inflate(R.layout.layout_system_app, null);
        userApp = LayoutInflater.from(this).inflate(R.layout.layout_user_app, null);

        mTitleList.add(getString(R.string.user_app));
        mTitleList.add(getString(R.string.system_app));

        mViewList.add(userApp);
        mViewList.add(systemAPP);

        mTablayout.setTabMode(TabLayout.MODE_FIXED);
        mTablayout.addTab(mTablayout.newTab().setText(mTitleList.get(0)));
        mTablayout.addTab(mTablayout.newTab().setText(mTitleList.get(1)));

        ViewPagerAdapter adapter = new ViewPagerAdapter(mTitleList, mViewList);
        mViewPager.setAdapter(adapter);
        mTablayout.setupWithViewPager(mViewPager);

        uninstallReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mSelectedInfo.isUserApp()){
                    userAppInfos.remove(mSelectedInfo);
                    userAdapter.notifyDataSetChanged();
                }else {
                    systemAppInfos.remove(mSelectedInfo);
                    systemAdapter.notifyDataSetChanged();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(uninstallReceiver, filter);
    }

    @Override
    protected void initData() {
        mLoading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                allAppInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
                systemAppInfos = new ArrayList<AppInfo>();
                userAppInfos = new ArrayList<AppInfo>();
                for (AppInfo appInfo : allAppInfos){
                    if (appInfo.isUserApp()){
                        userAppInfos.add(appInfo);
                    }else {
                        systemAppInfos.add(appInfo);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();


    }

    @Override
    protected void onDestroy() {
        if (uninstallReceiver != null){
            unregisterReceiver(uninstallReceiver);
            uninstallReceiver = null;
        }
        super.onDestroy();
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        RecyclerView systemList = (RecyclerView) systemAPP.findViewById(R.id.rv_system_app);
        RecyclerView userList = (RecyclerView) userApp.findViewById(R.id.rv_user_app);

        systemAdapter = new AppAdapter(this, systemAppInfos, listener);
        userAdapter = new AppAdapter(this, userAppInfos, listener);

        systemList.setHasFixedSize(false);
        systemList.setLayoutManager(new LinearLayoutManager(this));
        systemList.setAdapter(systemAdapter);

        userList.setHasFixedSize(false);
        userList.setLayoutManager(new LinearLayoutManager(this));
        userList.setAdapter(userAdapter);

        mLoading.setVisibility(View.GONE);
    }

    /**
     * 卸载点击事件
     */
    private AppAdapter.UninstallListener listener = new AppAdapter.UninstallListener() {
        @Override
        public void onClickUninstall(AppInfo info) {
            mSelectedInfo = info;
            if (info.isUserApp()){
                Intent intent = new Intent();
                intent.setAction("android.intent.action.DELETE");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + info.getPackName()));
                startActivity(intent);
            }else {
                showToast(getString(R.string.uninstall_system_hint));
            }
        }
    };
}
