package com.jianping.lee.mobilesafe.activity;

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
import com.jianping.lee.mobilesafe.utils.CommonUtils;
import com.jianping.lee.mobilesafe.utils.IntentUtils;
import com.jianping.lee.mobilesafe.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobDialogButtonListener;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;
import cn.domob.android.ads.RTSplashAd;
import cn.domob.android.ads.RTSplashAdListener;
import cn.domob.android.ads.SplashAd;
import cn.domob.android.ads.SplashAdListener;

public class SplashActivity extends AppCompatActivity {

    @InjectView(R.id.iv_splash_icon)
    ImageView icon;

    @InjectView(R.id.tv_splash_name)
    TextView name;

    @InjectView(R.id.ll_splash_ad)
    RelativeLayout llAd;

    SplashAd splashAd;
    RTSplashAd rtSplashAd;
    //	 缓存开屏广告:true   实时开屏广告:false
    private boolean isSplash = false;

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

        showSplashAd();

    }

    /**
     * 展示开屏广告
     */
    private void showSplashAd() {
        /**
         *
         * DomobSplashMode.DomobSplashModeFullScreen 请求开屏广告的尺寸为全屏
         * DomobSplashMode.DomobSplashModeSmallEmbed 请求开屏广告的尺寸不是全屏，根据设备分辨率计算出合适的小屏尺寸
         * DomobSplashMode.DomobSplashModeBigEmbed 请求开屏广告的尺寸不是全屏，更具设备分辨率计算出合适的相对SmallMode的尺寸
         *
         */
        if (isSplash) {
//			 缓存开屏广告
            splashAd = new SplashAd(this, MyApplication.DOMOB_PUBLISH_ID, MyApplication.SPLASH_PPID,
                    SplashAd.SplashMode.SplashModeBigEmbed);
//		    setSplashTopMargin is available when you choose non-full-screen splash mode.
//			splashAd.setSplashTopMargin(200);
            splashAd.setSplashAdListener(new SplashAdListener() {
                @Override
                public void onSplashPresent() {
                    LogUtils.i("DomobSDKDemo", "onSplashStart");
                }

                @Override
                public void onSplashDismiss() {
                    LogUtils.i("DomobSDKDemo", "onSplashClosed");
//					 开屏回调被关闭时，立即进行界面跳转，从开屏界面到主界面。
                    jumpMainActivity();
//					如果应用没有单独的闪屏Activity，需要调用closeSplash方法去关闭开屏广告
//					splashAd.closeSplash();
                }

                @Override
                public void onSplashLoadFailed() {
                    LogUtils.i("DomobSDKDemo", "onSplashLoadFailed");
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (splashAd.isSplashAdReady()) {
                        splashAd.splash(SplashActivity.this, llAd);
                    } else {
                        Toast.makeText(SplashActivity.this, "Splash ad is NOT ready.", Toast.LENGTH_SHORT).show();
                        jumpMainActivity();
                    }
                }
            }, 1);
        } else {
//			 实时开屏广告
            rtSplashAd = new RTSplashAd(this, MyApplication.DOMOB_PUBLISH_ID, MyApplication.SPLASH_PPID,
                    SplashAd.SplashMode.SplashModeFullScreen);
//		    setRTSplashTopMargin is available when you choose non-full-screen splash mode.
//			rtSplashAd.setRTSplashTopMargin(200);
            rtSplashAd.setRTSplashAdListener(new RTSplashAdListener() {
                @Override
                public void onRTSplashDismiss() {
                    LogUtils.i("DomobSDKDemo", "onRTSplashClosed");
//					 开屏回调被关闭时，立即进行界面跳转，从开屏界面到主界面。
                    jumpMainActivity();
//					如果应用没有单独的闪屏Activity，需要调用closeRTSplash方法去关闭开屏广告
//					rtSplashAd.closeRTSplash();
                }

                @Override
                public void onRTSplashLoadFailed() {
                    Log.i("DomobSDKDemo", "onRTSplashLoadFailed");
                }

                @Override
                public void onRTSplashPresent() {
                    Log.i("DomobSDKDemo", "onRTSplashStart");
                }

            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rtSplashAd.splash(SplashActivity.this, llAd);
                }
            }, 1);
        }
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
}
