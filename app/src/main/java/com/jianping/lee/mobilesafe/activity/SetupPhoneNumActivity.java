package com.jianping.lee.mobilesafe.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.receiver.MyAdmin;
import com.jianping.lee.mobilesafe.utils.IntentUtils;
import com.jianping.lee.mobilesafe.utils.SPUtils;

import butterknife.InjectView;
import butterknife.OnClick;

public class SetupPhoneNumActivity extends BaseActivity {

    @InjectView(R.id.cb_setup_phone_backup)
    CheckBox mBackup;

    @InjectView(R.id.et_setup_phone_num)
    EditText mPhoneNum;

    @InjectView(R.id.btn_setup_phone_active)
    Button mActive;

    private String findHint = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_phone_num);
        findHint = getString(R.string.find_cmd_gps) + "\n"
                + getString(R.string.find_cmd_delete) + "\n"
                + getString(R.string.find_cmd_lock) + "\n"
                + getString(R.string.find_cmd_music);
        initView();
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.phone_num));
        mBack.setVisibility(View.VISIBLE);
        mBack.setImageResource(R.drawable.nav_back);

        String phoneNum = (String) SPUtils.get(this, SPUtils.PHONE_NUM, "");
        if (!TextUtils.isEmpty(phoneNum)){
            mPhoneNum.setText(phoneNum);
        }
    }

    @Override
    protected void initData() {

    }

    /**
     * 按下完成按钮
     * @param view
     */
    @OnClick(R.id.btn_setup_phone_done)
    void OnClickDone(View view){
        /**
         * 保存亲友号码
         */
        String phoneNum = mPhoneNum.getText().toString();
        if (TextUtils.isEmpty(phoneNum) || phoneNum.length() < 11){
            showToast(getString(R.string.phone_num_error));
            return;
        }
        SPUtils.put(this, SPUtils.PHONE_NUM, phoneNum);

        //检测是否发送短信
        if (mBackup.isChecked()){
            //发送短信
            SmsManager.getDefault().sendTextMessage(phoneNum, null, findHint, null, null);
        }

        //绑定SIM卡
        bindSim();

        //进入下一个界面
        finish();
        IntentUtils.startActivityWithAnim(this, LostFindStatusActivity.class,
                R.anim.push_left_in, R.anim.push_left_out);
    }

    private void bindSim() {
        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String simNum = manager.getSimSerialNumber();
        SPUtils.put(this, SPUtils.SIM_NUM, simNum);
    }

    @OnClick(R.id.btn_setup_phone_active)
    void onClickActive(){
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        ComponentName name = new ComponentName(this, MyAdmin.class);
        //把要激活的组件名称告诉系统
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, name);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启后可以实现远程锁屏和销毁数据");
        startActivity(intent);
    }
}
