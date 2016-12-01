package com.jianping.lee.mobilesafe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.base.BaseActivity;
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

    private boolean mSetupPassword = false;

    private int count = 0;

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

        mPassword = (String) SPUtils.get(this, "password", null);
        if (TextUtils.isEmpty(mPassword)){//未设置密码
            mSetupPassword = false;
        }else {//已设置密码
            mSetupPassword = true;
        }

        final LocusPassWordView passWordView = (LocusPassWordView) findViewById(R.id.lv_setup_password_view);
        if (passWordView != null){
            passWordView.setOnCompleteListener(new LocusPassWordView.OnCompleteListener() {
                @Override
                public void onComplete(String password) {
                    if (mSetupPassword){//验证密码
                        if (password.equals(mPassword)){
                            //密码正确，进入下一步
                        }else {
                            count++;
                            passWordView.markError();
                            mHint.setText("密码错误，还可以尝试4次");
                            mHint.setTextColor(0xff0000);
                        }
                    }else {
                        count++;
                        if (count == 2){
                            if (password.equals(mPassword)){
                                Toast.makeText(SetupPasswordActivity.this, "设置密码成功" + mPassword, Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(SetupPasswordActivity.this, OpenLostFindActivity.class));
                            }else {
                                mHint.setText("两次密码不一致，请重新设置密码");
                                passWordView.clearPassword();
                                count = 0;
                            }
                        }else {
                            mPassword = password;
                            passWordView.clearPassword();
                            mHint.setText("请再次确认解锁图案");
                        }

                    }
                }
            });
        }
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
