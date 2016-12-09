package com.jianping.lee.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.utils.IntentUtils;
import com.jianping.lee.mobilesafe.utils.SPUtils;
import com.jianping.lee.mobilesafe.views.LocusPassWordView;

import butterknife.InjectView;
import butterknife.OnClick;

public class SetupPasswordActivity extends BaseActivity {

    @InjectView(R.id.tv_title_center)
    TextView mTitle;

    @InjectView(R.id.iv_title_back)
    ImageView mBack;

    @InjectView(R.id.tv_setup_password_hint)
    TextView mHint;

    @InjectView(R.id.lv_setup_password_view)
    LocusPassWordView passWordView;

    private boolean mSetupPassword = false;

    private int count = 0;

    private boolean reSetup = false;

    private String mPassword = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_password);
        initView();
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.func_protection));
        mBack.setVisibility(View.VISIBLE);
        mBack.setImageResource(R.drawable.nav_back);

        mPassword = (String) SPUtils.get(this, "password", "");
        if (TextUtils.isEmpty(mPassword)){//未设置密码
            mSetupPassword = false;
        }else {//已设置密码
            mSetupPassword = true;
        }
        //重新设置密码
        Intent intent = getIntent();
        reSetup = intent.getBooleanExtra("setup", false);
        if (reSetup){
            mSetupPassword = false;
        }

        if (passWordView != null){
            passWordView.setOnCompleteListener(new LocusPassWordView.OnCompleteListener() {
                @Override
                public void onComplete(String password) {
                    //已经设置过解锁图案
                    if (mSetupPassword){//验证密码
                        checkPassword(password);
                    }else {
                        mHint.setText(getString(R.string.setup_puzzle_password));
                        setupPassword(password);
                    }
                }
            });
        }
    }

    /**
     * 设置密码
     * @param password
     */
    private void setupPassword(String password) {
        count++;
        if (count == 2){
            //设置密码成功
            if (password.equals(mPassword)){
                //保存密码
                SPUtils.put(SetupPasswordActivity.this, "password", mPassword);
                goNext();
            }else {
                mHint.setText(getString(R.string.puzzle_password_dismatch));
                passWordView.clearPassword();
                count = 0;
            }
        }else {
            mPassword = password;
            passWordView.clearPassword();
            mHint.setText(getString(R.string.check_puzzle_password));
        }
    }

    /**
     * 验证密码
     * @param password
     */
    private void checkPassword(String password) {
        if (password.equals(mPassword)){
            //密码正确，进入下一步
            goNext();
        }else {
            passWordView.markError();
            mHint.setText(getString(R.string.password_wrong));
        }
    }

    private void goNext(){
        if (reSetup){
            OnClickBack();
            return;
        }
        boolean protecting = (boolean) SPUtils.get(this, SPUtils.PROTECTING, false);
        if (protecting){
            //开启防盗，是否设置过亲友号码
            String phoneNum = (String) SPUtils.get(this, SPUtils.PHONE_NUM, "");
            if (TextUtils.isEmpty(phoneNum)){
                //未设置亲友号码
                jumpToActivity(SetupPhoneNumActivity.class);
            }else {
                jumpToActivity(LostFindStatusActivity.class);
            }

        }else {
            //未开启防盗
            jumpToActivity(OpenLostFindActivity.class);
        }
    }

    private void jumpToActivity(Class<?> cls){
        finish();
        IntentUtils.startActivityWithAnim(this, cls,
                R.anim.push_left_in, R.anim.push_left_out);
    }


    @Override
    protected void initData() {

    }


    @OnClick(R.id.iv_title_back)
    void OnClickBack(){
        finish();
        overridePendingTransition(R.anim.push_right_in,
                R.anim.push_right_out);
    }
}
