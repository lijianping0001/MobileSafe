package com.jianping.lee.mobilesafe.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.AndroidCharacter;
import android.text.format.Formatter;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.model.SpeedInfo;
import com.jianping.lee.mobilesafe.utils.LogUtils;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Observable;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class TestSpeedActivity extends BaseActivity {
    private final String fileName = "testSpeed.jpg";

    private final String remotePath = "http://bmob-cdn-8626.b0.upaiyun.com/2017/01/08/7f45cf9b406587a6807e78b925fa3e4f.jpg";

    @InjectView(R.id.tv_test_speed_max)
    TextView tvMaxSpeed;

    @InjectView(R.id.tv_test_speed_min)
    TextView tvMinSpeed;

    @InjectView(R.id.tv_test_speed_average)
    TextView tvAveSpeed;

    @InjectView(R.id.btn_test_speed_start)
    Button start;

    @InjectView(R.id.tv_test_speed_network)
    TextView tvNetwork;

    @InjectView(R.id.iv_test_speed_point)
    ImageView ivPoint;

    private SpeedInfo mInfo;

    private int lastDegree, curDegree;

    private double total, counter;
    private int curSpeed, aveSpeed, maxSpeed, minSpeed;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_speed);
        mContext = this;
        initView();
        initData();
        mInfo = new SpeedInfo();
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.func_net_speed));
        mBack.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.btn_test_speed_start)
    void onClickStart(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null){
            showToast("网络不可用");
            return;
        }

        tvNetwork.setText(networkInfo.getTypeName());
        start.setEnabled(false);
        start.setText("正在测试");
        mInfo.setSpeed(0);
        mInfo.setTransferredByte(0);
        mInfo.setTotalByte(1024);
        downloadStart();
        checkSpeedStart();
    }

    private void checkSpeedStart() {
        Subscription subscription = rx.Observable.create(new rx.Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                total = 0;
                counter = 0;
                curSpeed = 0;
                maxSpeed = 0;
                minSpeed = 0;
                aveSpeed = 0;
                minSpeed = 0;
                boolean first = true;
                while (mInfo.getTransferredByte() < mInfo.getTotalByte()){
                    SystemClock.sleep(1000);

                    total += mInfo.getSpeed();
                    counter++;

                    curSpeed = (int) (mInfo.getSpeed() / 1024);
                    aveSpeed = (int) (total / counter / 1024);

                    if (first){
                        minSpeed = curSpeed;
                        first = false;
                    }

                    if (curSpeed > maxSpeed){
                        maxSpeed = curSpeed;
                    }

                    if (curSpeed < minSpeed){
                        minSpeed = curSpeed;
                    }
                    subscriber.onNext(true);
                }

                if (mInfo.getTransferredByte() == mInfo.getTotalByte()){
                    subscriber.onCompleted();
                }

                LogUtils.i("total===" + mInfo.getTotalByte() + "   transferred===" + mInfo.getTransferredByte());
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        start.setEnabled(true);
                        start.setText("开始测试");
                        startAnimation(0);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        start.setEnabled(true);
                        start.setText("开始测试");
                        startAnimation(0);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                        tvAveSpeed.setText(aveSpeed + "KB/s");
                        tvMaxSpeed.setText(maxSpeed + "KB/s");
                        tvMinSpeed.setText(minSpeed + "KB/s");
                        startAnimation(curSpeed);
                    }
                });
        addSubscription(subscription);
    }

    private void startAnimation(int curSpeed) {
        curDegree = getDegree(curSpeed);

        RotateAnimation animation = new RotateAnimation(lastDegree, curDegree,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setDuration(1000);
        lastDegree = curDegree;
        ivPoint.startAnimation(animation);
    }

    /**
     * 开启下载任务
     */
    private void downloadStart() {
        Subscription subscription = rx.Observable.create(new rx.Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                long startTime, curTime;
                URL url;
                URLConnection connection;
                InputStream inputStream;

                try {
                    url = new URL(remotePath);
                    connection = url.openConnection();

                    mInfo.setTotalByte(connection.getContentLength());
                    inputStream = connection.getInputStream();
                    startTime = System.currentTimeMillis();
                    while (inputStream.read()!= -1){
                        mInfo.setTransferredByte(mInfo.getTransferredByte() + 1);
                        curTime = System.currentTimeMillis();
                        if (curTime - startTime == 0){
                            mInfo.setSpeed(1000);
                        }else {
                            mInfo.setSpeed(mInfo.getTransferredByte() / (curTime - startTime) * 1000);
                        }
                    }
                    inputStream.close();
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(new Throwable());
                }

            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {

                    }
                });
        addSubscription(subscription);
    }


    private int getDegree(double cur_speed)
    {
        int ret=0;
        if(cur_speed>=0 && cur_speed<=512)
        {
            ret=(int) (15.0*cur_speed/128.0);
        }
        else if(cur_speed>=512 && cur_speed<=1024)
        {
            ret=(int) (60+15.0*cur_speed/256.0);
        }
        else if(cur_speed>=1024 && cur_speed<=10*1024)
        {
            ret=(int) (90+15.0*cur_speed/1024.0);
        }else {
            ret=180;
        }
        return ret;
    }
}
