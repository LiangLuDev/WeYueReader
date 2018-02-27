package com.lianglu.weyue.view.base;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.lianglu.weyue.utils.LogUtils;
import com.lianglu.weyue.viewmodel.BaseViewModel;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Liang_Lu on 2017/9/29.
 * Fragment基类
 */

public class BaseFragment extends Fragment {

    protected BaseViewModel mModel;
    protected Context mContext;
    private View mBindView;
    private View mView;
    protected CompositeDisposable mDisposable;

    /**
     * 获得全局的，防止使用getActivity()为空
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }


    /**
     * Databinding设置布局绑定
     *
     * @param resId      布局layout
     * @param brVariavle BR
     * @param mModel     viewmodel
     */
    public View setBinddingView(LayoutInflater inflater, ViewGroup container, @LayoutRes int resId, int brVariavle, BaseViewModel mModel) {
        if (mBindView == null) {
            ViewDataBinding dataBinding = DataBindingUtil.inflate(inflater, resId, container, false);
            dataBinding.setVariable(brVariavle, mModel);
            mBindView = dataBinding.getRoot();
            ButterKnife.bind(this, mBindView);
            this.mModel = mModel;
        }
        return mBindView;
    }

    /**
     * 不使用Databinding设置布局
     *
     * @param resId  布局layout
     * @param mModel viewmodel
     */
    public View setContentView(ViewGroup container, @LayoutRes int resId, BaseViewModel mModel) {
        if (mView == null) {
            mView = LayoutInflater.from(getActivity()).inflate(resId, container, false);
            ButterKnife.bind(this, mView);
            this.mModel = mModel;
            initView();
        }
        return mView;
    }

    public void initView() {

    }

    protected void addDisposable(Disposable d) {
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(d);
    }

    /**
     * activity跳转（无参数）
     *
     * @param className
     */
    public void startActivity(Class<?> className) {
        Intent intent = new Intent(mContext, className);
        startActivity(intent);
    }

    /**
     * activity跳转（有参数）
     *
     * @param className
     */
    public void startActivity(Class<?> className, Bundle bundle) {
        Intent intent = new Intent(mContext, className);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mModel != null) {
            LogUtils.d("onDestroy");
            mModel.onDestroy();
        }
    }

}
