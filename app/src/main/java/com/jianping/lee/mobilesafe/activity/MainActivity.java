package com.jianping.lee.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.adapter.IconAdapter;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.model.Icon;
import com.jianping.lee.mobilesafe.utils.DensityUtils;
import com.jianping.lee.mobilesafe.utils.IntentUtils;
import com.jianping.lee.mobilesafe.utils.SPUtils;
import com.jianping.lee.mobilesafe.utils.ScreenUtils;
import com.jianping.lee.mobilesafe.utils.SystemUtils;
import com.jianping.lee.mobilesafe.views.RoundProgressBar;

import java.util.ArrayList;

import butterknife.InjectView;

public class MainActivity extends BaseActivity {

    private boolean progressRun = true;

    @InjectView(R.id.view_main_cpu)
    RoundProgressBar mCpuProgress;

    @InjectView(R.id.view_main_mem)
    RoundProgressBar mMemProgress;

    @InjectView(R.id.gv_main_content)
    GridView mGridView;

    @InjectView(R.id.iv_title_right)
    ImageView mSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }


    protected void initView() {
        mSetting.setVisibility(View.VISIBLE);
        mSetting.setImageResource(R.drawable.icon_app);
        //动态计算GridView的宽度，适配不同的屏幕
        int screenWidth = ScreenUtils.getScreenWidth(this);
        int iconWidth = (screenWidth - DensityUtils.dp2px(this, 120)) / 3;
        mGridView.setColumnWidth(iconWidth);

        ArrayList<Icon> dataList = new ArrayList<>();
        addAllFunction(dataList);
        IconAdapter adapter = new IconAdapter(this, dataList);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(clickListener);

    }

    private AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0://手机防盗
                    IntentUtils.startActivityWithAnim(MainActivity.this, SetupPasswordActivity.class,
                            R.anim.push_left_in, R.anim.push_left_out);
                    break;
            }
        }
    };

    protected void initData() {

        //获取cpu的使用率和内存使用率
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressRun){
                    updateCPU();
                    updateMem();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


        }).start();
    }

    /**
     * 更新内存使用率
     */
    private void updateMem() {
        int rate = SystemUtils.getMemRate(this);
        if (rate > 100){
            rate = 100;
        }
        if (rate <= 0){
            rate = 1;
        }
        if (mMemProgress != null){
            mMemProgress.setProgress(rate);
        }
    }

    /**
     * 更新CPU使用率
     */
    private void updateCPU(){
        int rate = SystemUtils.getCPURate();
        if (rate > 100){
            rate = 100;
        }
        if (rate <= 0){
            rate = 1;
        }
        if (mCpuProgress != null){
            mCpuProgress.setProgress(rate);
        }
    }

    private void addAllFunction(ArrayList<Icon> dataList){
        dataList.clear();
        dataList.add(new Icon(R.drawable.round_background_green, R.drawable.icon_protection,
                getString(R.string.func_protection)));
        dataList.add(new Icon(R.drawable.round_background_orange, R.drawable.icon_speed,
                getString(R.string.func_speed_up)));
        dataList.add(new Icon(R.drawable.round_background_purple, R.drawable.icon_app,
                getString(R.string.func_app_manager)));
        dataList.add(new Icon(R.drawable.round_background_blue, R.drawable.icon_defend,
                getString(R.string.func_defend)));
        dataList.add(new Icon(R.drawable.round_background_green, R.drawable.icon_app_key,
                getString(R.string.func_app_lock)));
        dataList.add(new Icon(R.drawable.round_background_red, R.drawable.icon_add,
                getString(R.string.func_virus)));
        dataList.add(new Icon(R.drawable.round_background_orange, R.drawable.icon_clean,
                getString(R.string.func_clean_cache)));
        dataList.add(new Icon(R.drawable.round_background_purple, R.drawable.icon_check,
                getString(R.string.func_check)));
        dataList.add(new Icon(R.drawable.round_background_yellow, R.drawable.icon_location,
                getString(R.string.func_location)));
        dataList.add(new Icon(R.drawable.round_background_green, R.drawable.icon_qrcode,
                getString(R.string.func_qrcode)));
        dataList.add(new Icon(R.drawable.round_background_blue, R.drawable.icon_test_speed,
                getString(R.string.func_net_speed)));
        dataList.add(new Icon(R.drawable.round_background_green, R.drawable.icon_contact,
                getString(R.string.func_backup)));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressRun = false;
    }
}
