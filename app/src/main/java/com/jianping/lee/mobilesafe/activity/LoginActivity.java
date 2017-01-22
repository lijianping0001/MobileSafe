package com.jianping.lee.mobilesafe.activity;

import android.app.Dialog;
import android.os.Message;
import android.support.v4.view.ViewPager;
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
import com.jianping.lee.mobilesafe.model.MyUser;
import com.jianping.lee.mobilesafe.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

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

    private Dialog mDialog;

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
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case FIND_PASSWORD://
                        mTitle.setText("找回密码");
                        break;
                    case LOGIN:
                        mTitle.setText("登录");
                        break;
                    case REGISTER:
                        mTitle.setText("注册");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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

    @OnClick(R.id.iv_title_back)
    void onClickBack(){
        finish();
        overridePendingTransition(0,
                R.anim.push_top_out);
    }

    /**
     * 登录界面的点击事件
     */
    View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_login_register:
                    mViewPager.setCurrentItem(REGISTER);
                    break;
                case R.id.btn_login_button:
                    loginCommit();
                    break;
                case R.id.tv_login_find:
                    mViewPager.setCurrentItem(FIND_PASSWORD);
                    break;
            }
        }
    };

    /**
     * 登录提交
     */
    private void loginCommit() {
        final String emailStr = loginEmail.getText().toString().trim();
        final String pwdStr = loginPassword.getText().toString().trim();

        //检测格式是否正确
        if (!checkEmail(emailStr)){
            showToast("邮箱格式不正确");
            return;
        }
        //密码不能为空
        if (pwdStr.length() == 0){
            showToast("密码不能为空");
            return;
        }

        mDialog = CommonUtils.createLoadingDialog(this, "登录");
        mDialog.show();
        MyUser user = new MyUser();
        user.setUsername(emailStr);
        user.setPassword(pwdStr);
        user.login(new SaveListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if (e == null) {
                    showToast(R.drawable.item_checked, "登录成功");
                    onClickBack();
                } else if (e.getErrorCode() == 101) {
                    showToast("用户名或密码错误");
                } else {
                    showToast("当前网络不给力，请稍后再试");
                }
                mDialog.dismiss();
            }
        });

    }


    /**
     * 注册界面的点击事件
     */
    View.OnClickListener registerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_register_account:
                    mViewPager.setCurrentItem(LOGIN);
                    break;
                case R.id.btn_register_sign:
                    registerCommit();
                    break;
            }
        }
    };

    /**
     * 注册提交
     */
    private void registerCommit() {
        final String emailStr = registerEmail.getText().toString().trim();
        final String pwdStr = registerPassword.getText().toString().trim();
        String pwdRepeatStr = registerRepeat.getText().toString().trim();


        //检测格式是否正确
        if (!checkEmail(emailStr)){
            showToast("邮箱格式不正确");
            return;
        }
        //密码不能为空
        if (pwdStr.length() == 0){
            showToast("密码不能为空");
            return;
        }

        if (!pwdStr.equals(pwdRepeatStr)){
            showToast("两次输入密码不一致");
            return;
        }

        mDialog = CommonUtils.createLoadingDialog(this, "注册");
        mDialog.show();

        MyUser user = new MyUser();
        user.setUsername(emailStr);
        user.setPassword(pwdStr);
        user.setEmail(emailStr);
        addSubscription(user.signUp(new SaveListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                Message msg = new Message();
                if (e == null) {
                    showToast(R.drawable.item_checked, "注册成功");
                } else {
                    //202用户已存在
                    if (e.getErrorCode() == 202) {
                        showToast("用户名已存在");
                    } else {
                        showToast("当前网络不给力，请稍后再试");
                    }
                }
                mDialog.dismiss();
            }
        }));
    }

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
                    commitResetPwd();
                    break;
            }
        }
    };

    /**
     * 重置密码提交
     */
    private void commitResetPwd() {
        final String emailStr = findEmail.getText().toString().trim();
        if (!checkEmail(emailStr)){
            showToast("邮箱格式不正确");
            return;
        }

        mDialog = CommonUtils.createLoadingDialog(this, "重置密码");
        mDialog.show();
        addSubscription(BmobUser.resetPasswordByEmail(emailStr, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast(R.drawable.item_checked,"重置密码成功");
                    mViewPager.setCurrentItem(LOGIN);
                } else {
                    //205 用户不存在
                    if (e.getErrorCode() == 205) {
                        showToast("用户名不存在");
                    } else {
                        showToast("当前网络不给力，请稍后再试");
                    }
                }
                mDialog.dismiss();
            }
        }));

    }

    @Override
    protected void initData() {

    }

    /**
     * 检查email格式是否正确
     * @param email
     * @return
     */
    private boolean checkEmail(String email){
        Pattern pattern = Pattern.compile("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
