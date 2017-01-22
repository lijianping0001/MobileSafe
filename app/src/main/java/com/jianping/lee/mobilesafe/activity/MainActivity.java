package com.jianping.lee.mobilesafe.activity;

import android.app.ActivityManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.adapter.IconAdapter;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.engine.TaskInfoProvider;
import com.jianping.lee.mobilesafe.model.Icon;
import com.jianping.lee.mobilesafe.model.ProcessInfo;
import com.jianping.lee.mobilesafe.utils.DensityUtils;
import com.jianping.lee.mobilesafe.utils.IntentUtils;
import com.jianping.lee.mobilesafe.utils.ScreenUtils;
import com.jianping.lee.mobilesafe.utils.SystemUtils;
import com.jianping.lee.mobilesafe.views.RoundProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobDialogButtonListener;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;

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

    @InjectView(R.id.iv_main_cloud_up)
    ImageView mSpeedUp;

    @InjectView(R.id.iv_main_cloud_down)
    ImageView mSpeedDown;

    @InjectView(R.id.iv_main_rocket)
    ImageView mRocket;

    @InjectView(R.id.rl_main_clean)
    RelativeLayout mClean;

    private long mSavedMem = 0;
    private int mKilledCount = 0;
    private long mExitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();

        new Thread(new Runnable() {
            @Override
            public void run() {
                checkAppUpdate();
            }
        }).start();
    }


    protected void initView() {
        mSetting.setVisibility(View.VISIBLE);
        mSetting.setImageResource(R.drawable.icon_item);
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


    private void checkAppUpdate(){
        //允许在非wifi环境下检测应用更新
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        //自动更新的逻辑，设置监听
        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
            @Override
            public void onUpdateReturned(int i, UpdateResponse updateResponse) {
//                BmobException exception = updateResponse.getException();
//                if (exception != null) {
//                    handler.sendEmptyMessage(0);
//                }
            }
        });
        //设置对话框的点击事件
        BmobUpdateAgent.setDialogListener(new BmobDialogButtonListener() {
            @Override
            public void onClick(int i) {
//                switch (i) {
//                    case UpdateStatus.Update://立即更新
//                        handler.sendEmptyMessage(0);
//                        break;
//                    case UpdateStatus.NotNow://以后再说
//                        handler.sendEmptyMessage(0);
//                        break;
//                    case UpdateStatus.Close://关闭,强制更新才会出现
//                        handler.sendEmptyMessage(0);
//                        break;
//                    case UpdateStatus.IGNORED://忽略此版本
//                        handler.sendEmptyMessage(0);
//                        break;
//                }
            }
        });
        BmobUpdateAgent.update(this);
    }

    private AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0://手机防盗
                    startNewActivity(SetupPasswordActivity.class);
                    break;

                case 1://一键清理
                    cleanProcess();
                    break;

                case 2://软件管理
                    startNewActivity(AppManagerActivity.class);
                    break;

                case 3://骚扰拦截
                    startNewActivity(BlackNumActivity.class);
                    break;

                case 4://程序锁
                    startNewActivity(AppLockActivity.class);
                    break;

                case 5://病毒查杀
                    startNewActivity(AntiVirusActivity.class);
                    break;

                case 6://清理缓存
                    startNewActivity(CleanCacheActivity.class);
                    break;

                case 7://硬件查询
                    startNewActivity(HardwareDetectActivity.class);
                    break;

                case 8://归属地查询
                    startNewActivity(NumberAddressActivity.class);
                    break;

                case 9://条码
                    startNewActivity(ScanCodeActivity.class);
                    break;

                case 10://测速
                    startNewActivity(TestSpeedActivity.class);
                    break;

                case 11://安全备份
                    startNewActivity(BackUpActivity.class);
                    break;
            }
        }
    };

    private void startNewActivity(Class<?> cls){
        IntentUtils.startActivityWithAnim(MainActivity.this, cls,
                R.anim.push_left_in, R.anim.push_left_out);
    }

    @OnClick(R.id.iv_title_right)
    void onClick(){
        startNewActivity(AppMoreActivity.class);
    }

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
        dataList.add(new Icon(R.drawable.round_background_purple, R.drawable.icon_appllication,
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

    /**
     * 一键加速
     */
    private void cleanProcess() {
        mClean.setVisibility(View.VISIBLE);
        mGridView.setEnabled(false);
        new Thread(){
            @Override
            public void run() {
                killProcess();
                SystemClock.sleep(1000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startAnimation();

                    }
                });
            }
        }.start();


    }

    /**
     * 火箭动画
     */
    private void startAnimation(){
        AnimationDrawable animationDrawable = (AnimationDrawable) mRocket.getBackground();
        animationDrawable.start();

        AnimationSet translateAnimation = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.rocket_fly);

        mRocket.startAnimation(translateAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(1500);

        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1f);
        scaleAnimation.setDuration(1500);

        mSpeedUp.startAnimation(alphaAnimation);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);

        mSpeedDown.startAnimation(animationSet);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mClean.setVisibility(View.GONE);
                mGridView.setEnabled(true);
                showToast("清理了" + mKilledCount + "个进程," + "释放了" + Formatter.formatFileSize(MainActivity.this, mSavedMem) + "的内存");
                mKilledCount = 0;
                mSavedMem = 0;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 清理进程
     */
    private void killProcess(){
        List<ProcessInfo> infoList = TaskInfoProvider.getRunningProcess(this);
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        for (ProcessInfo processInfo : infoList){
            manager.killBackgroundProcesses(processInfo.getPackName());
            mKilledCount++;
            mSavedMem += processInfo.getMemSize();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressRun = false;
    }

    /**
     * 点击两次返回退出程序
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if ((System.currentTimeMillis() - mExitTime) > 2000){
                showToast("再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            }else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
