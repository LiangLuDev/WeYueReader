package com.lianglu.weyue.view.activity.impl;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.lianglu.weyue.R;
import com.lianglu.weyue.utils.ToastUtils;
import com.lianglu.weyue.view.base.BaseActivity;
import com.lianglu.weyue.viewmodel.BaseViewModel;
import com.lianglu.weyue.viewmodel.activity.VMUseLoginInfo;
import com.lianglu.weyue.widget.theme.ColorTextView;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.actv_username)
    AutoCompleteTextView mActvUsername;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.ctv_register)
    ColorTextView mCtvRegister;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    private VMUseLoginInfo mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new VMUseLoginInfo(this);
        setBinddingView(R.layout.activity_login, NO_BINDDING, mModel);

    }


    @Override
    protected void initView() {
        super.initView();
        initThemeToolBar("用户登录");
    }

    @OnClick({R.id.ctv_register, R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ctv_register:
                startActivityForResult(new Intent(this, RegisterActivity.class), 10000);
                break;
            case R.id.fab:
                String username = mActvUsername.getText().toString();
                String password = mEtPassword.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    ToastUtils.show("用户名不能为空");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    ToastUtils.show("密码不能为空");
                    return;
                }
                mModel.login(username, password);
                break;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 10000 && data != null) {
            String username = data.getStringExtra("username");
            mActvUsername.setText(username != null ? username : "");
            String password = data.getStringExtra("password");
            mEtPassword.setText(password != null ? password : "");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
