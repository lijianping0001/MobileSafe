package com.jianping.lee.mobilesafe.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.adapter.AppSettingAdapter;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.model.MyUser;
import com.jianping.lee.mobilesafe.utils.CommonUtils;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;

public class AppSettingActivity extends BaseActivity {

    @InjectView(R.id.rv_app_setting)
    RecyclerView mListView;

    @InjectView(R.id.btn_app_setting_logout)
    Button mLogout;

    private AppSettingAdapter mAdapter;

    private MyUser mUser;

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
//        mListView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL));
        mAdapter.loadData();
        mAdapter.notifyDataSetChanged();

        mUser = BmobUser.getCurrentUser(MyUser.class);
        if (mUser == null){
            mLogout.setVisibility(View.INVISIBLE);
        }else{
            mLogout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void initData() {
        mAdapter.setOnItemClickListener(new AppSettingAdapter.SettingItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position){
                    case 0://检查更新
                        checkAppUpdate();
                        break;
                    case 3://关于
                        showAbout();
                        break;
                }
            }
        });
    }

    private void showAbout() {
        new AlertDialog.Builder(this)
                .setTitle("关于安全卫士")
                .setIcon(R.drawable.icon_contact_num)
                .setMessage("当前版本：" + CommonUtils.getAppVersionName(this) + "\n"
                + "邮箱：grlee1989@gmail.com")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void checkAppUpdate() {
        final Dialog dialog = CommonUtils.createLoadingDialog(this, "检查更新");
        dialog.show();
        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
            @Override
            public void onUpdateReturned(int i, UpdateResponse updateResponse) {
                BmobException exception = updateResponse.getException();
                dialog.dismiss();
                if (i == UpdateStatus.No){
                    showToast("当前版本已经是最新版本");
                }else if (i == UpdateStatus.TimeOut){
                    showToast("当前网络不给力，请稍后再试");
                }
            }
        });
        BmobUpdateAgent.forceUpdate(this);
    }

    @OnClick(R.id.btn_app_setting_logout)
    void onClickLogout(){
        if (mUser != null){
            BmobUser.logOut();
            mUser = null;
            OnClickBack(null);
        }
    }
}
