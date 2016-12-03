package com.jianping.lee.mobilesafe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.utils.IntentUtils;

import butterknife.InjectView;
import butterknife.OnClick;

public class SetupPhoneNumActivity extends BaseActivity {

    @InjectView(R.id.tv_title_center)
    TextView mTitle;

    @InjectView(R.id.iv_title_back)
    ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_phone_num);
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.phone_num));
        mBack.setVisibility(View.VISIBLE);
        mBack.setImageResource(R.drawable.nav_back);
    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.iv_title_back)
    void OnClickBack(View view){
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @OnClick(R.id.btn_setup_phone_done)
    void OnClickDone(View view){
        finish();
        IntentUtils.startActivityWithAnim(this, LostFindStatusActivity.class,
                R.anim.push_left_in, R.anim.push_left_out);
    }
}
