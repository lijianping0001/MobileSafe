package com.jianping.lee.mobilesafe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.base.BaseActivity;

import butterknife.InjectView;

public class BackUpActivity extends BaseActivity {

    @InjectView(R.id.tv_back_up_name)
    TextView mLogin;

    @InjectView(R.id.rb_back_up_download)
    RadioButton mDownload;

    @InjectView(R.id.rb_back_up_upload)
    RadioButton mUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_up);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.func_backup));
        mBack.setVisibility(View.VISIBLE);
        mLogin.setText("点击登录");

    }

    @Override
    protected void initData() {

    }
}
