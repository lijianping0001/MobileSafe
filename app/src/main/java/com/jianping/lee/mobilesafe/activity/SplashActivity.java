package com.jianping.lee.mobilesafe.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.base.MyApplication;
import com.jianping.lee.mobilesafe.db.MyLockAppDao;
import com.jianping.lee.mobilesafe.engine.PermissionHelper;
import com.jianping.lee.mobilesafe.utils.CommonUtils;
import com.jianping.lee.mobilesafe.utils.IntentUtils;
import com.jianping.lee.mobilesafe.utils.LogUtils;

import net.youmi.android.normal.common.ErrorCode;
import net.youmi.android.normal.spot.SplashViewSettings;
import net.youmi.android.normal.spot.SpotListener;
import net.youmi.android.normal.spot.SpotManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SplashActivity extends AppCompatActivity {

    @InjectView(R.id.iv_splash_icon)
    ImageView icon;

    @InjectView(R.id.tv_splash_name)
    TextView name;

    @InjectView(R.id.ll_splash_ad)
    LinearLayout llAd;

    private PermissionHelper mPermissionHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);

        icon.setImageResource(R.mipmap.ic_launcher);
        name.setText("安全卫士\n" + CommonUtils.getAppVersionName(this));


        copyDB("address.db");
        copyDB("antivirus.db");

        SystemClock.sleep(500);

        requestPermission();
    }

    private void requestPermission() {
        // 当系统为6.0以上时，需要申请权限
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
            @Override
            public void onAfterApplyAllPermission() {
                LogUtils.i("All of requested permissions has been granted, so run app logic.");
                setupSplashAd();
            }
        });
        if (Build.VERSION.SDK_INT < 23) {
            // 如果系统版本低于23，直接跑应用的逻辑
            LogUtils.i("The api level of system is lower than 23, so run app logic directly.");
            setupSplashAd();
        } else {
            // 如果权限全部申请了，那就直接跑应用逻辑
            if (mPermissionHelper.isAllRequestedPermissionGranted()) {
                LogUtils.i( "All of requested permissions has been granted, so run app logic directly.");
                setupSplashAd();
            } else {
                // 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
                LogUtils.i("Some of requested permissions hasn't been granted, so apply permissions first.");
                mPermissionHelper.applyPermissions();
            }
        }
    }

    /**
     * 设置开屏广告
     */
    private void setupSplashAd() {
        // 对开屏进行设置
        SplashViewSettings splashViewSettings = new SplashViewSettings();
        //		// 设置是否展示失败自动跳转，默认自动跳转
        //		splashViewSettings.setAutoJumpToTargetWhenShowFailed(false);
        // 设置跳转的窗口类
        splashViewSettings.setTargetClass(MainActivity.class);
        // 设置开屏的容器
        splashViewSettings.setSplashViewContainer(llAd);

        // 展示开屏广告
        SpotManager.getInstance(this)
                .showSplash(this, splashViewSettings, new SpotListener() {

                    @Override
                    public void onShowSuccess() {
                        LogUtils.i("开屏展示成功");
                    }

                    @Override
                    public void onShowFailed(int errorCode) {
                        LogUtils.i("开屏展示失败");
                        switch (errorCode) {
                            case ErrorCode.NON_NETWORK:
                                LogUtils.i("网络异常");
                                break;
                            case ErrorCode.NON_AD:
                                LogUtils.i("暂无开屏广告");
                                break;
                            case ErrorCode.RESOURCE_NOT_READY:
                                LogUtils.i("开屏资源还没准备好");
                                break;
                            case ErrorCode.SHOW_INTERVAL_LIMITED:
                                LogUtils.i("开屏展示间隔限制");
                                break;
                            case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                                LogUtils.i("开屏控件处在不可见状态");
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onSpotClosed() {
                        LogUtils.i("开屏被关闭");
                    }

                    @Override
                    public void onSpotClicked(boolean isWebPage) {
                        LogUtils.i("开屏被点击");
                        LogUtils.i("是否是网页广告？%s", isWebPage ? "是" : "不是");
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }

    private void jumpMainActivity(){
        IntentUtils.startActivityWithAnim(SplashActivity.this,
                MainActivity.class,
                R.anim.effect_fade_in, R.anim.effect_fade_out);
        finish();
    }

    // 拷贝资产目录下的数据库到Android系统目录
    private void copyDB(final String dbname) {
        new Thread() {
            public void run() {
                File file = new File(getFilesDir(), dbname);
                if (file.exists() && file.length() > 0) {
                    //System.out.println("已经拷贝过了数据库,无需重新拷贝");
                } else {
                    try {
                        InputStream is = getAssets().open(dbname);
                        byte[] buffer = new byte[1024];
                        FileOutputStream fos = new FileOutputStream(file);
                        int len = -1;
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//		 Back key disabled
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 开屏展示界面的 onDestroy() 回调方法中调用
        SpotManager.getInstance(this).onDestroy();
    }
}
