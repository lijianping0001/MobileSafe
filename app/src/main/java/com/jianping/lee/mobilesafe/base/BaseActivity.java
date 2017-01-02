package com.jianping.lee.mobilesafe.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.utils.ScreenUtils;
import com.jianping.lee.mobilesafe.utils.StatusBarUtils;
import com.jianping.lee.mobilesafe.utils.ToastUtils;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Li on 2016/11/27.
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Optional
    @InjectView(R.id.tv_title_center)
    protected TextView mTitle;
    @Optional
    @InjectView(R.id.iv_title_back)
    protected ImageView mBack;

    protected boolean landscape = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication)getApplication()).addActivity(this);
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

    /**
     * 返回按钮
     * @param view
     */
    @Optional
    @OnClick(R.id.iv_title_back)
    protected void OnClickBack(@Nullable View view){
        finish();
        overridePendingTransition(R.anim.push_right_in,
                R.anim.push_right_out);
    }


    /**
     * 打印吐司
     * @param msg
     */
    protected void showToast(String msg){
        ToastUtils.showToast(this, msg, Gravity.CENTER);
    }

    protected abstract void initView();

    protected abstract void initData();


    protected void handleMessage(Message msg){

    }

    protected static class MyHandler extends Handler {

        private final WeakReference<BaseActivity> mActivity;

        public MyHandler(BaseActivity activity){
            mActivity = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseActivity activity = mActivity.get();
            activity.handleMessage(msg);
        }
    }
}
