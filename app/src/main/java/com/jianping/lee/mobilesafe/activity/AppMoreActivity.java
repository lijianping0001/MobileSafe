package com.jianping.lee.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.adapter.HardwareAdapter;
import com.jianping.lee.mobilesafe.adapter.MoreAdapter;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.model.MyUser;
import com.jianping.lee.mobilesafe.utils.IntentUtils;
import com.jianping.lee.mobilesafe.views.RecyclerViewDivider;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

public class AppMoreActivity extends BaseActivity {

    @InjectView(R.id.tv_app_more_login)
    TextView mLogin;

    @InjectView(R.id.rv_app_more)
    RecyclerView mListView;

    private MoreAdapter mAdapter;

    private MyUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_more);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUser = BmobUser.getCurrentUser(MyUser.class);
        if (mUser != null){
            mLogin.setText(mUser.getUsername());
        }else {
            mLogin.setText("登录");
        }
    }

    @Override
    protected void initView() {
        mTitle.setText("更多");
        mBack.setVisibility(View.VISIBLE);
        mLogin.setText("登录");


        mListView.setHasFixedSize(false);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MoreAdapter(this);
        mListView.setAdapter(mAdapter);
        mAdapter.loadData();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initData() {
        mAdapter.setOnItemClickListener(new MoreAdapter.MoreItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0://反馈
                        IntentUtils.startActivityWithAnim(AppMoreActivity.this, FeedbackActivity.class,
                                R.anim.push_left_in, R.anim.push_left_out);
                        break;

                    case 2://推荐给朋友
                        shareApp();
                        break;
                    case 3://设置
                        IntentUtils.startActivityWithAnim(AppMoreActivity.this, AppSettingActivity.class,
                                R.anim.push_left_in, R.anim.push_left_out);
                        break;
                }
            }
        });
    }

    private void shareApp() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "手机卫士时刻保卫你的手机，界面清爽，功能强大，快来试试吧^_^");
        startActivity(Intent.createChooser(intent, "分享到"));
    }


    @OnClick(R.id.tv_app_more_login)
    void onClickLogin(){
        mUser = BmobUser.getCurrentUser(MyUser.class);
        if (mUser == null){
            IntentUtils.startActivityWithAnim(this, LoginActivity.class,
                    R.anim.push_bottom_in, R.anim.push_bottom_out);
        }
    }
}
