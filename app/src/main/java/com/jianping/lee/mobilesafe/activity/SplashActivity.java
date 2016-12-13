package com.jianping.lee.mobilesafe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.utils.IntentUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        copyDB("address.db");

        IntentUtils.startActivityForDelay(this, MainActivity.class, 2000);
        overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
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
}
