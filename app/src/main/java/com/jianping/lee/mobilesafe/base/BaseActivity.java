package com.jianping.lee.mobilesafe.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.utils.ScreenUtils;
import com.jianping.lee.mobilesafe.utils.StatusBarUtils;

import butterknife.ButterKnife;

/**
 * Created by Li on 2016/11/27.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected boolean landscape = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication)getApplication()).addAcitivity(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar();
        ButterKnife.inject(this);
    }

    private void setStatusBar() {
        //不是全屏设置状态栏颜色
        if (!ScreenUtils.isFullScreen(this)){
            StatusBarUtils.setColorNoTranslucent(this, ContextCompat.getColor(this, R.color.toolbar));
        }
    }

    @Override
    protected void onResume() {
        if (landscape){
            //设置为横屏
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }else {
            //设置为竖屏
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MyApplication)getApplication()).removeActivity(this);
    }

    protected abstract void initView();

    protected abstract void initData();
}
