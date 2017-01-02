package com.jianping.lee.mobilesafe.activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.adapter.ViewPagerAdapter;
import com.jianping.lee.mobilesafe.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public class LoginActivity extends BaseActivity {

    private final int FIND_PASSWORD = 0;
    private final int LOGIN = 1;
    private final int REGISTER = 2;

    @InjectView(R.id.vp_user_login)
    ViewPager mViewPager;

    EditText registerEmail;

    EditText registerPassword;

    EditText registerRepeat;

    EditText loginEmail;

    EditText loginPassword;

    EditText findEmail;

    private View findView, registerView, loginView;

    private List<View> mViewList = new ArrayList<>();

    private List<String> mTitleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTitle.setText("登录");
        mBack.setVisibility(View.VISIBLE);
        mBack.setImageResource(R.drawable.icon_error);

        findView = LayoutInflater.from(this).inflate(R.layout.layout_find_password, null);
        loginView = LayoutInflater.from(this).inflate(R.layout.layout_login, null);
        registerView = LayoutInflater.from(this).inflate(R.layout.layout_register, null);

        mViewList.add(findView);
        mViewList.add(loginView);
        mViewList.add(registerView);

        mTitleList.add("找回密码");
        mTitleList.add("登录");
        mTitleList.add("注册");

        ViewPagerAdapter adapter = new ViewPagerAdapter(mTitleList, mViewList);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(LOGIN);
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        initFindPassword();
        initRegister();
        initLogin();
    }

    private void initRegister() {
        registerEmail = (EditText) registerView.findViewById(R.id.et_register_email);
        registerPassword = (EditText) registerView.findViewById(R.id.et_register_password);
        registerRepeat = (EditText) registerView.findViewById(R.id.et_register_repeat);
        Button btRegister = (Button) registerView.findViewById(R.id.btn_register_sign);
        TextView tvLogin = (TextView) registerView.findViewById(R.id.tv_register_account);

        tvLogin.setOnClickListener(registerClickListener);
        btRegister.setOnClickListener(registerClickListener);
    }

    /**
     * 登录界面
     */
    private void initLogin() {
        loginEmail = (EditText) loginView.findViewById(R.id.et_login_email);
        loginPassword = (EditText) loginView.findViewById(R.id.et_login_password);
        Button btLogin = (Button) loginView.findViewById(R.id.btn_login_button);
        TextView tvFind = (TextView) loginView.findViewById(R.id.tv_login_find);
        TextView tvRegister = (TextView) loginView.findViewById(R.id.tv_login_register);

        tvRegister.setOnClickListener(loginClickListener);
        btLogin.setOnClickListener(loginClickListener);
        tvFind.setOnClickListener(loginClickListener);
    }

    /**
     * 登录界面的点击事件
     */
    View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_login_register:
                    mViewPager.setCurrentItem(2);
                    break;
                case R.id.btn_login_button:
//                    loginCommit();
                    break;
                case R.id.tv_login_find:
                    mViewPager.setCurrentItem(0);
                    break;
            }
        }
    };


    /**
     * 注册界面的点击事件
     */
    View.OnClickListener registerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_register_account:
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.btn_register_sign:
//                    registerCommit();
                    break;
            }
        }
    };

    private void initFindPassword() {
        findEmail = (EditText) findView.findViewById(R.id.et_find_password_email);
        TextView findLogin = (TextView) findView.findViewById(R.id.tv_find_password_login);
        Button btReset = (Button) findView.findViewById(R.id.btn_find_password_reset);

        btReset.setOnClickListener(resetPassword);
        findLogin.setOnClickListener(resetPassword);
    }

    View.OnClickListener resetPassword = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_find_password_login:
                    mViewPager.setCurrentItem(LOGIN);
                    break;
                case R.id.btn_find_password_reset:
//                    commitResetPwd();
                    break;
            }
        }
    };



    @Override
    protected void initData() {

    }
}
