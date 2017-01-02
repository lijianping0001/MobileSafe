package com.jianping.lee.mobilesafe.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.base.BaseActivity;

import butterknife.InjectView;
import butterknife.OnClick;

public class ScanResultActivity extends BaseActivity {

    @InjectView(R.id.tv_scan_result_content)
    TextView mContent;

    @InjectView(R.id.btn_scan_result)
    Button mButton;

    private boolean isUrl = false;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.func_qrcode));
        mBack.setVisibility(View.VISIBLE);

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        if (!TextUtils.isEmpty(result)){
            mContent.setText(result);
        }

        if (result.startsWith("http")){
            isUrl = true;
            url = result;
            mButton.setText("安全访问");
        }else {
            isUrl = false;
            mButton.setText("继续扫描");
        }

    }

    @OnClick(R.id.btn_scan_result)
    void onClickJump(){
        if (isUrl){
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }else {
            OnClickBack(null);
        }
    }
}
