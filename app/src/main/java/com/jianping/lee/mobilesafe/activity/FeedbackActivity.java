package com.jianping.lee.mobilesafe.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.model.Feedback;
import com.jianping.lee.mobilesafe.utils.CommonUtils;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class FeedbackActivity extends BaseActivity {

    private Dialog mDialog;

    @InjectView(R.id.et_feedback_content)
    EditText etContent;

    @InjectView(R.id.et_feedback_tel)
    EditText etTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTitle.setText("意见反馈");
        mBack.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.btn_feedback_commit)
    void onClickCommit(View view){
        //检测输入是否合法
        if (etContent.getText().toString().length() < 10){
            showToast("内容太短");
            return;
        }

        if (etTel.getText().toString().length() < 8){
            showToast("电话格式错误");
            return;
        }

        mDialog = CommonUtils.createLoadingDialog(this, "提交");
        mDialog.show();
        Feedback feedback = new Feedback();
        feedback.setContent(etContent.getText().toString());
        feedback.setContact(etTel.getText().toString());
        feedback.setVersion(CommonUtils.getAppVersionName(FeedbackActivity.this));
        feedback.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    showToast(R.drawable.item_checked, "提交成功！");
                } else {
                    showToast("当前网络不给力，请稍微再试");
                }
                mDialog.dismiss();
            }
        });

    }
}
