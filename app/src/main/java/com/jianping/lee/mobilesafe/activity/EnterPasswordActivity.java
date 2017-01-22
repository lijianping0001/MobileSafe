package com.jianping.lee.mobilesafe.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.service.AppLockService;
import com.jianping.lee.mobilesafe.utils.IntentUtils;
import com.jianping.lee.mobilesafe.utils.SPUtils;
import com.jianping.lee.mobilesafe.views.LocusPassWordView;

import butterknife.InjectView;

public class EnterPasswordActivity extends BaseActivity {

    @InjectView(R.id.tv_setup_password_hint)
    TextView mHint;

    @InjectView(R.id.lv_setup_password_view)
    LocusPassWordView passWordView;

    @InjectView(R.id.iv_setup_password)
    ImageView mIcon;

    private String packName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_password);
        initView();
    }

    @Override
    protected void initView() {
        mBack.setVisibility(View.GONE);

        Intent intent = getIntent();
        packName = intent.getStringExtra("packName");

        PackageManager manager = getPackageManager();
        try {
            String appname = manager.getPackageInfo(packName, 0).applicationInfo.loadLabel(manager).toString();
            Drawable icon = manager.getPackageInfo(packName, 0).applicationInfo.loadIcon(manager);
            mTitle.setText(appname);
            mIcon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        if (passWordView != null){
            passWordView.setOnCompleteListener(new LocusPassWordView.OnCompleteListener() {
                @Override
                public void onComplete(String password) {
                    //已经设置过解锁图案
                    String passwordRestore = (String) SPUtils.get(EnterPasswordActivity.this, "password", "");
                    if (password.equals(passwordRestore)){
                        Intent intent1 = new Intent();
                        intent1.setAction(AppLockService.actionFilter);
                        intent1.putExtra("packName", packName);
                        sendBroadcast(intent1);
                        finish();
                    }else {
                        showToast("密码错误");
                        passWordView.clearPassword();
                    }
                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void initData() {

    }
}
