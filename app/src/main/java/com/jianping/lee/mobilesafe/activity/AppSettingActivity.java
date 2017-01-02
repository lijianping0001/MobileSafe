package com.jianping.lee.mobilesafe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.adapter.AppSettingAdapter;
import com.jianping.lee.mobilesafe.adapter.MoreAdapter;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.views.RecyclerViewDivider;

import butterknife.InjectView;

public class AppSettingActivity extends BaseActivity {

    @InjectView(R.id.rv_app_setting)
    RecyclerView mListView;

    private AppSettingAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setting);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTitle.setText("设置");
        mBack.setVisibility(View.VISIBLE);

        mListView.setHasFixedSize(false);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AppSettingAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL));
        mAdapter.loadData();
        mAdapter.notifyDataSetChanged();

    }

    @Override
    protected void initData() {
        mAdapter.setOnItemClickListener(new AppSettingAdapter.SettingItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showToast("点击了item==" + position);
            }
        });
    }
}
