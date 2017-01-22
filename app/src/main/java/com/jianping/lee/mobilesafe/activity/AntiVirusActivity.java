package com.jianping.lee.mobilesafe.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.adapter.VirusAdapter;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.db.AntivirusDao;
import com.jianping.lee.mobilesafe.model.AppInfo;
import com.jianping.lee.mobilesafe.utils.CommonUtils;
import com.jianping.lee.mobilesafe.utils.LogUtils;
import com.jianping.lee.mobilesafe.utils.SPUtils;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

public class AntiVirusActivity extends BaseActivity {

    @InjectView(R.id.tv_anti_virus)
    TextView tvStatus;

    @InjectView(R.id.btn_anti_virus_start)
    Button btnStart;

    @InjectView(R.id.rv_anti_virus)
    RecyclerView rvScanList;

    @InjectView(R.id.btn_anti_virus_cancel)
    Button btnCancel;

    @InjectView(R.id.ll_anti_virus_start)
    LinearLayout llStart;

    @InjectView(R.id.ll_anti_virus_scan)
    LinearLayout llScan;

    /**
     * 所有应用集合
     */
    private List<AppInfo> allAppInfos;

    private VirusAdapter mAdapter;

    private int mScanStatus;

    private final int SCANNING = 1;

    private final int PAUSE = 2;

    private final int SCAN_FINISHED = 3;

    private boolean mCancelScan = false;

    private MyHandler handler = new MyHandler(this);

    private int virusCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.func_virus));
        mBack.setVisibility(View.VISIBLE);

        String time = (String) SPUtils.get(this, SPUtils.SCAN_VIRUS_TIME, "");
        if (!TextUtils.isEmpty(time)){
            tvStatus.setText(getString(R.string.last_scan) + time);
        }
    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.btn_anti_virus_start)
    void onClickStart(){
        startAnimation();
    }

    /**
     * 开始界面隐藏动画
     */
    private void startAnimation(){
        final int height = llStart.getHeight();
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, height);
        translateAnimation.setDuration(400);
        llStart.startAnimation(translateAnimation);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                scanAnimation(height);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * 扫描界面出现动画
     * @param height
     */
    private void scanAnimation(int height){
        llStart.setVisibility(View.GONE);
        llScan.setVisibility(View.VISIBLE);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, height, 0);
        translateAnimation.setDuration(400);
        llScan.startAnimation(translateAnimation);

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startScanVirus();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 扫描病毒
     */
    private void startScanVirus() {
        //更新时间
        String time = DateFormat.format("yyyy年MM月dd日 HH:mm:ss", Calendar.getInstance()).toString();
        SPUtils.put(this, SPUtils.SCAN_VIRUS_TIME, time);
        mScanStatus = SCANNING;
        if (allAppInfos == null){
            allAppInfos = new ArrayList<>();
        }else {
            allAppInfos.clear();
        }
        if (mAdapter == null){
            mAdapter = new VirusAdapter(this, allAppInfos);
            rvScanList.setHasFixedSize(false);
            rvScanList.setLayoutManager(new LinearLayoutManager(this));
            rvScanList.setAdapter(mAdapter);
        }

        mAdapter.notifyDataSetChanged();
        new Thread(new Runnable() {
            @Override
            public void run() {
                PackageManager manager = getPackageManager();
                List<PackageInfo> packageInfos = manager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES +
                        PackageManager.GET_SIGNATURES);
                for (PackageInfo packageInfo : packageInfos){
                    String apkPath = packageInfo.applicationInfo.sourceDir;
                    String md5Result = CommonUtils.getMd5ByFilePath(apkPath);
                    final String info = AntivirusDao.isVirus(md5Result);
                    final String appName = packageInfo.applicationInfo.loadLabel(manager).toString();
                    Drawable icon = packageInfo.applicationInfo.loadIcon(manager);
                    AppInfo appInfo = new AppInfo();
                    appInfo.setAppName(appName);
                    LogUtils.i("扫描：" + appName);
                    appInfo.setIcon(icon);
                    if (info != null){
                        //病毒
                        virusCount++;
                        appInfo.setUserApp(false);
                    }else {
                        //不是病毒
                        appInfo.setUserApp(true);
                    }
                    allAppInfos.add(0, appInfo);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvStatus.setText(getString(R.string.scanning) + appName);
                            mAdapter.notifyDataSetChanged();
                        }
                    });

                    if (mCancelScan){
                        break;
                    }
                    SystemClock.sleep(50);
                }
                if (!mCancelScan){
                    handler.sendEmptyMessage(0);
                }
            }
        }).start();
    }

    private void finishScanVirus() {
        mScanStatus = SCAN_FINISHED;
        btnCancel.setText(getString(R.string.completed));
        if (virusCount == 0){
            tvStatus.setText(getString(R.string.scan_result) + getString(R.string.safe));
        }else {
            tvStatus.setText(getString(R.string.scan_result) + "发现" + virusCount + "个危险项");
        }

    }

    @OnClick(R.id.btn_anti_virus_cancel)
    void onClickCancel(){
        switch (mScanStatus){
            case SCANNING://正在扫描
                mCancelScan = true;
                mScanStatus = PAUSE;
                btnCancel.setText(getString(R.string.restart_scan));
                break;
            case PAUSE://暂停状态
                mScanStatus = SCANNING;
                mCancelScan = false;
                btnCancel.setText(getString(R.string.stop));
                startScanVirus();
                break;
            case SCAN_FINISHED://扫描完毕
                OnClickBack(null);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        OnClickBack(null);
    }

    @OnClick(R.id.iv_title_back)
    protected void OnClickBack(View view){
        mCancelScan = true;
        finish();
        overridePendingTransition(R.anim.push_right_in,
                R.anim.push_right_out);
    }

    @Override
    protected void handleMessage(Message msg) {
        finishScanVirus();
    }


}
