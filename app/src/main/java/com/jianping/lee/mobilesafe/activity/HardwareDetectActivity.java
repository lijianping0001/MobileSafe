package com.jianping.lee.mobilesafe.activity;

import android.os.Build;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.adapter.HardwareAdapter;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.views.RecyclerViewDivider;

import butterknife.InjectView;
import butterknife.OnClick;

public class HardwareDetectActivity extends BaseActivity {
    @InjectView(R.id.tv_hardware_detect_type)
    TextView mType;

    @InjectView(R.id.tv_hardware_detect_version)
    TextView mVersion;

    @InjectView(R.id.rv_hardware_detect)
    RecyclerView mRecyclerView;

    HardwareAdapter mAdapter;

    private MyHandler handler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardware_detect);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.func_check));
        mBack.setVisibility(View.VISIBLE);

        mType.setText(Build.MODEL);
        mVersion.setText(getString(R.string.android_version) + Build.VERSION.RELEASE);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new HardwareAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL));

        new Thread(new Runnable() {
            @Override
            public void run() {
                mAdapter.loadData();

                handler.sendEmptyMessage(0);
            }
        }).start();

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null){
            mAdapter.release();
        }
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        mAdapter.notifyDataSetChanged();
    }
}
