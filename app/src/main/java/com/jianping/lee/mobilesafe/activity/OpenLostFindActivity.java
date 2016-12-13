package com.jianping.lee.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.utils.SPUtils;

import butterknife.InjectView;
import butterknife.OnClick;

public class OpenLostFindActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_lost_find);
        initView();
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.func_protection));
        mBack.setVisibility(View.VISIBLE);
        mBack.setImageResource(R.drawable.nav_back);
    }

    @Override
    protected void initData() {

    }


    @OnClick(R.id.btn_open_lost)
    void OnClickOpenFind(View view){
        SPUtils.put(this, SPUtils.PROTECTING, true);
        //开启手机防盗
        String phoneNum = (String) SPUtils.get(this, SPUtils.PHONE_NUM, "");
        if (TextUtils.isEmpty(phoneNum)){//未设置亲友号码
            finish();
            startActivity(new Intent(this, SetupPhoneNumActivity.class));
            overridePendingTransition(R.anim.push_left_in,
                    R.anim.push_left_out);
        }else {//已设置
            finish();
            startActivity(new Intent(this, LostFindStatusActivity.class));
            overridePendingTransition(R.anim.push_left_in,
                    R.anim.push_left_out);
        }
    }
}
