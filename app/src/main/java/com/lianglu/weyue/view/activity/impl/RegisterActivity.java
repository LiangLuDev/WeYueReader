package com.lianglu.weyue.view.activity.impl;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.lianglu.weyue.R;
import com.lianglu.weyue.utils.ToastUtils;
import com.lianglu.weyue.view.activity.IUserRegister;
import com.lianglu.weyue.view.base.BaseActivity;
import com.lianglu.weyue.viewmodel.activity.VMUserRegisterInfo;

import butterknife.BindView;

public class RegisterActivity extends BaseActivity implements IUserRegister {

    @BindView(R.id.actv_username)
    AutoCompleteTextView mActvUsername;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.et_password_confirm)
    EditText mEtPasswordConfirm;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    private VMUserRegisterInfo mModel;
    private String mUsername;
    private String mPassword1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new VMUserRegisterInfo(this, this);
        setBinddingView(R.layout.activity_register, NO_BINDDING, mModel);

    }

    @Override
    protected void initView() {
        super.initView();
        initThemeToolBar("用户注册");

        mFab.setOnClickListener(v -> {
            mUsername = mActvUsername.getText().toString();
            mPassword1 = mEtPassword.getText().toString();
            String password2 = mEtPasswordConfirm.getText().toString();
            if (TextUtils.isEmpty(mUsername)) {
                ToastUtils.show("用户名不能为空");
                return;
            }
            if (TextUtils.isEmpty(mPassword1) || TextUtils.isEmpty(password2)) {
                ToastUtils.show("密码不能为空");
                return;
            }
            if (!mPassword1.equals(password2)) {
                ToastUtils.show("两次输入密码不一样");
                return;
            }
            mModel.register(mUsername, mPassword1);
        });
    }

    @Override
    public void registerSuccess() {
        setResult(RESULT_OK, new Intent().putExtra("username", mUsername).putExtra("password", mPassword1));
        finish();
    }
}
