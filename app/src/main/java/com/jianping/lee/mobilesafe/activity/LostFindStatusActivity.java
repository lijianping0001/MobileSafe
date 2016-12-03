package com.jianping.lee.mobilesafe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.base.BaseActivity;

import butterknife.InjectView;
import butterknife.OnClick;

public class LostFindStatusActivity extends BaseActivity {

    @InjectView(R.id.tv_title_center)
    TextView mTitle;

    @InjectView(R.id.iv_title_back)
    ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_find_status);
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

    @OnClick(R.id.iv_title_back)
    void OnClickBack(){
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
