package com.jianping.lee.mobilesafe.activity;

import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.db.NumberAddressDao;

import butterknife.InjectView;
import butterknife.OnClick;

public class NumberAddressActivity extends BaseActivity {

    @InjectView(R.id.et_number_address)
    EditText etNumber;

    @InjectView(R.id.btn_number_address)
    Button btnQuery;

    @InjectView(R.id.tv_number_address_result)
    TextView mResult;

    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_address);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.func_location));
        mBack.setVisibility(View.VISIBLE);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.btn_number_address)
    void onClick(){
        String number = etNumber.getText().toString().trim();
        if (TextUtils.isEmpty(number)){
            showToast(getString(R.string.number_empty));
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            etNumber.startAnimation(shake);
            vibrator.vibrate(100);
            return;
        }

        String address = NumberAddressDao.getAddress(number);
        mResult.setText("归属地：" + address);
    }
}
