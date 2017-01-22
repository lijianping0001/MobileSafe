package com.jianping.lee.mobilesafe.activity;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.utils.CommonUtils;
import com.jianping.lee.mobilesafe.utils.IntentUtils;

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

public class SplashActivity extends AppCompatActivity {

    private MyHandler handler;

    @InjectView(R.id.iv_splash_icon)
    ImageView icon;

    @InjectView(R.id.tv_splash_name)
    TextView name;

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

        handler = new MyHandler(this);

        copyDB("address.db");
        copyDB("antivirus.db");

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                checkAppUpdate();
            }
        }).start();

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


    private void checkAppUpdate(){
        //允许在非wifi环境下检测应用更新
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        //自动更新的逻辑，设置监听
        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
            @Override
            public void onUpdateReturned(int i, UpdateResponse updateResponse) {
                BmobException exception = updateResponse.getException();
                if (exception != null) {
                    handler.sendEmptyMessage(0);
                }
            }
        });
        //设置对话框的点击事件
        BmobUpdateAgent.setDialogListener(new BmobDialogButtonListener() {
            @Override
            public void onClick(int i) {
                switch (i) {
                    case UpdateStatus.Update://立即更新
                        handler.sendEmptyMessage(0);
                        break;
                    case UpdateStatus.NotNow://以后再说
                        handler.sendEmptyMessage(0);
                        break;
                    case UpdateStatus.Close://关闭,强制更新才会出现
                        handler.sendEmptyMessage(0);
                        break;
                    case UpdateStatus.IGNORED://忽略此版本
                        handler.sendEmptyMessage(0);
                        break;
                }
            }
        });
        BmobUpdateAgent.update(SplashActivity.this);
    }


    private static class MyHandler extends Handler {

        private final WeakReference<SplashActivity> mActivity;

        public MyHandler(SplashActivity activity){
            mActivity = new WeakReference<SplashActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SplashActivity activity = mActivity.get();
            activity.handleMessage(msg);
        }
    }

    private void handleMessage(Message msg){
        jumpMainActivity();
    }
}
