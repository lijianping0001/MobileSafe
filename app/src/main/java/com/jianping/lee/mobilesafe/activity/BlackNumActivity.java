package com.jianping.lee.mobilesafe.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jianping.lee.greendao.BlackNum;
import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.adapter.BlackNumAdapter;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.db.MyBlackNumDao;
import com.jianping.lee.mobilesafe.utils.SPUtils;
import com.jianping.lee.mobilesafe.views.RecyclerViewDivider;

import butterknife.InjectView;
import butterknife.OnClick;

public class BlackNumActivity extends BaseActivity {

    @InjectView(R.id.rv_black_num)
    RecyclerView mRecyclerView;

    @InjectView(R.id.iv_title_right)
    ImageView mAddNum;

    @InjectView(R.id.tv_black_num_empty)
    TextView emptyLayout;

    private EditText mInputNum;

    private String mName;

    private BlackNumAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_num);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.func_defend));
        mBack.setVisibility(View.VISIBLE);
        mAddNum.setVisibility(View.VISIBLE);
        mAddNum.setImageResource(R.drawable.nav_add);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BlackNumAdapter(this, emptyLayout);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.loadData();
    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.iv_title_right)
    void onClickAdd(){
        mName = null;
        final Dialog dialog = new Dialog(this, R.style.addProDialog);
        dialog.setTitle(R.string.blacknum);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_add_black_num, null);
        mInputNum = (EditText) dialogView.findViewById(R.id.et_add_black_num);
        final RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.rg_add_black_num);
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
                String phoneNum = mInputNum.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNum)){
                    showToast(getString(R.string.blacknum_empty));
                    return;
                }
                int radioBtnId = radioGroup.getCheckedRadioButtonId();
                int mode = 3;
                switch (radioBtnId){
                    case R.id.rb_add_black_num_phone:
                        mode = 1;
                        break;
                    case R.id.rb_add_black_num_text:
                        mode = 2;
                        break;
                    case R.id.rb_add_black_num_all:
                        mode = 3;
                        break;
                }
                addBlackNum2Db(phoneNum, mode);
            }
        });
        dialog.setContentView(dialogView);
        dialog.show();
    }

    /**
     * 添加一个黑名单号码
     * @param phoneNum
     * @param mode
     */
    private void addBlackNum2Db(String phoneNum, int mode) {
        //添加到数据库
        BlackNum blackNum = new BlackNum();
        blackNum.setName(mName);
        blackNum.setPhone(phoneNum);
        blackNum.setMode(mode);
        MyBlackNumDao.getInstance(this).addData(blackNum, 0);

        mAdapter.loadData();
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
            mName = data.getStringExtra("name").trim();
            if (mInputNum != null){
                mInputNum.setText(phoneNum);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
