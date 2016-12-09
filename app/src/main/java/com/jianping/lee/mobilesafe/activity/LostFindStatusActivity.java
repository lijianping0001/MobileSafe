package com.jianping.lee.mobilesafe.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.utils.SPUtils;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class LostFindStatusActivity extends BaseActivity {

    @InjectView(R.id.tv_title_center)
    TextView mTitle;

    @InjectView(R.id.iv_title_back)
    ImageView mBack;

    @InjectView(R.id.switch_lost_find_status)
    Switch mProtecting;

    @InjectView(R.id.switch_lost_find_uninstall)
    Switch mUnistall;

    @InjectView(R.id.tv_lost_find_status_num)
    TextView mPhoneNum;

    private EditText mInputNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_find_status);
        initView();
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.func_protection));
        mBack.setVisibility(View.VISIBLE);
        mBack.setImageResource(R.drawable.nav_back);

        mProtecting.setChecked(true);
        if ((boolean)SPUtils.get(this, SPUtils.UNINTALL, false)){
            mUnistall.setChecked(true);
        }
        String phoneNum = (String) SPUtils.get(this, SPUtils.PHONE_NUM, "");
        if (!TextUtils.isEmpty(phoneNum)){
            mPhoneNum.setText(phoneNum);
        }

    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.iv_title_back)
    void OnClickBack(){
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    /**
     * 开启手机防盗状态变化事件
     * @param buttonView
     * @param isChecked
     */
    @OnCheckedChanged(R.id.switch_lost_find_status)
    void OnCheckedProtecting(CompoundButton buttonView, boolean isChecked){
        SPUtils.put(this, SPUtils.PROTECTING, isChecked);
    }

    /**
     * 防卸载状态变化事件
     * @param buttonView
     * @param isChecked
     */
    @OnCheckedChanged(R.id.switch_lost_find_uninstall)
    void OnCheckedUninstall(CompoundButton buttonView, boolean isChecked){
        SPUtils.put(this, SPUtils.UNINTALL, isChecked);
    }

    /**
     * 修改亲友号码
     */
    @OnClick(R.id.rl_lost_find_status_num)
    void OnClickNum(){
        final Dialog dialog = new Dialog(this, R.style.addProDialog);
        dialog.setTitle(R.string.phone_num);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_selected_contact, null);
        mInputNum = (EditText) dialogView.findViewById(R.id.et_selected_contact);
        mInputNum.setText(mPhoneNum.getText().toString());
        mInputNum.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable = mInputNum.getCompoundDrawables()[2];

                if (drawable == null){
                    return false;
                }

                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP){
                    return false;
                }

                if (event.getX() > mInputNum.getWidth()
                        - mInputNum.getPaddingRight()
                        - drawable.getIntrinsicWidth()){
                    selectContact();
                }
                return false;
            }
        });

        final Button cancel = (Button) dialogView.findViewById(R.id.btn_tool_cancel);
        Button ok = (Button) dialogView.findViewById(R.id.btn_tool_ok);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String newNum = mInputNum.getText().toString().trim();
                mPhoneNum.setText(newNum);
                SPUtils.put(LostFindStatusActivity.this, SPUtils.PHONE_NUM, newNum);
            }
        });
        dialog.setContentView(dialogView);
        dialog.show();
    }


    private void selectContact(){
        //选择联系人,开启一个新的界面,
        //开启界面获取返回值
        Intent intent = new Intent(this, SelectedContactActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null){
            String phoneNum = data.getStringExtra("phone").replace("-", "").replace(" ", "").trim();
            if (mInputNum != null){
                mInputNum.setText(phoneNum);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.rl_lost_find_status_password)
    void OnClickSetupPuzzle(){
        Intent intent = new Intent(this, SetupPasswordActivity.class);
        intent.putExtra("setup",true);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
