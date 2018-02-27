package com.lianglu.weyue.view.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lianglu.weyue.R;
import com.lianglu.weyue.utils.DimenUtils;
import com.lianglu.weyue.utils.SharedPreUtils;
import com.lianglu.weyue.utils.SystemUtils;
import com.lianglu.weyue.viewmodel.BaseViewModel;
import com.lianglu.weyue.widget.theme.ColorView;
import com.lianglu.weyue.widget.theme.Theme;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Liang_Lu on 2017/11/21.
 */

public class BaseActivity extends AppCompatActivity {
    protected static int NO_BINDDING = -1;//不用绑定布局

    protected Context mContext;
    private Toolbar mToolbar;
    protected BaseViewModel mModel;
    private Unbinder mUnbinder;
    private boolean isSlideBack = true;//是否设置滑动返回
    private ColorView mStatusBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        onPreCreate();
    }

    /**
     * Databinding设置布局绑定
     *
     * @param resId      布局layout
     * @param brVariavle BR或者不用绑定
     * @param mModel     viewmodel
     */
    public void setBinddingView(@LayoutRes int resId, int brVariavle, BaseViewModel mModel) {
        if (brVariavle == NO_BINDDING) {
            setContentView(resId);
        } else {
            ViewDataBinding dataBinding = DataBindingUtil.setContentView(this, resId);
            dataBinding.setVariable(brVariavle, mModel);
        }
        this.mModel = mModel;
        mUnbinder = ButterKnife.bind(this);
        mContext = this;
        initView();

    }


    /**
     * 自定义titlebar 默认带返回按钮
     *
     * @param title
     */
    public void initThemeToolBar(String title) {
        initStatusBar();
        AppCompatImageView mIvToolbarMore = findViewById(R.id.iv_toolbar_more);
        AppCompatImageView mIvToolbarBack = findViewById(R.id.iv_toolbar_back);
        TextView mTvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        mTvToolbarTitle.setSelected(true);
        mIvToolbarBack.setImageResource(R.drawable.ic_arrow_back_white_24dp);
//        setIconDrawable(mTvToolbarTitle, R.drawable.ic_arrow_back_white_24dp);
        mIvToolbarMore.setVisibility(View.GONE);
        mTvToolbarTitle.setText(title);
        mIvToolbarBack.setOnClickListener(v -> {
            finish();
        });
//        onPreCreate();
    }


    /**
     * 自定义titlebar 自定义图标
     *
     * @param title
     * @param iconRes
     */
    public void initThemeToolBar(String title, boolean isMoreIcon, @DrawableRes int iconRes, View.OnClickListener clickListener) {
        initStatusBar();
        AppCompatImageView mIvToolbarMore = findViewById(R.id.iv_toolbar_more);
        AppCompatImageView mIvToolbarBack = findViewById(R.id.iv_toolbar_back);
        TextView mTvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        mTvToolbarTitle.setSelected(true);
        if (isMoreIcon) {
            mIvToolbarBack.setImageResource(R.drawable.ic_arrow_back_white_24dp);
//            setIconDrawable(mTvToolbarTitle, R.drawable.ic_arrow_back_white_24dp);
            mIvToolbarMore.setVisibility(View.VISIBLE);
            mTvToolbarTitle.setText(title);
            mIvToolbarBack.setOnClickListener(v -> finish());
            mIvToolbarMore.setOnClickListener(clickListener);
            mIvToolbarMore.setImageResource(iconRes);
        } else {
            Glide.with(mContext).load(iconRes).into(mIvToolbarBack);
            mIvToolbarBack.setImageResource(iconRes);
            mIvToolbarMore.setVisibility(View.GONE);
            mTvToolbarTitle.setText(title);
            mIvToolbarBack.setOnClickListener(clickListener);
        }
    }

    /**
     * 自定义titlebar 自定义图标
     *
     * @param title
     * @param iconRes
     */
    public void initThemeToolBar(String title, @DrawableRes int iconRes, @DrawableRes int moreRes, View.OnClickListener iconClickListener, View.OnClickListener moreClickListener) {
        initStatusBar();
        AppCompatImageView mIvToolbarMore = findViewById(R.id.iv_toolbar_more);
        AppCompatImageView mIvToolbarBack = findViewById(R.id.iv_toolbar_back);
        TextView mTvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        mTvToolbarTitle.setSelected(true);
        mIvToolbarBack.setImageResource(iconRes);
//        setIconDrawable(mTvToolbarTitle, iconRes);
        mIvToolbarMore.setVisibility(View.VISIBLE);
        mTvToolbarTitle.setText(title);
        mIvToolbarBack.setOnClickListener(iconClickListener);
//        mTvToolbarTitle.setOnClickListener(iconClickListener);
        mIvToolbarMore.setImageResource(moreRes);
        mIvToolbarMore.setOnClickListener(moreClickListener);
//        onPreCreate();
    }

    /**
     * 设置textview图标
     *
     * @param view
     * @param iconRes
     */
    private void setIconDrawable(TextView view, @DrawableRes int iconRes) {
        view.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(iconRes),
                null, null, null);
        view.setCompoundDrawablePadding(DimenUtils.dp2px(10));
    }

    private void initStatusBar() {
        mStatusBar = findViewById(R.id.status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mStatusBar.setVisibility(View.VISIBLE);
            mStatusBar.getLayoutParams().height = SystemUtils.getStatusHeight(this);
            mStatusBar.setLayoutParams(mStatusBar.getLayoutParams());
        } else {
            mStatusBar.setVisibility(View.GONE);
        }
    }


    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void onPreCreate() {
        Theme theme = SharedPreUtils.getInstance().getCurrentTheme();
        switch (theme) {
            case Blue:
                setTheme(R.style.BlueTheme);
                break;
            case Red:
                setTheme(R.style.RedTheme);
                break;
            case Brown:
                setTheme(R.style.BrownTheme);
                break;
            case Green:
                setTheme(R.style.GreenTheme);
                break;
            case Purple:
                setTheme(R.style.PurpleTheme);
                break;
            case Teal:
                setTheme(R.style.TealTheme);
                break;
            case Pink:
                setTheme(R.style.PinkTheme);
                break;
            case DeepPurple:
                setTheme(R.style.DeepPurpleTheme);
                break;
            case Orange:
                setTheme(R.style.OrangeTheme);
                break;
            case Indigo:
                setTheme(R.style.IndigoTheme);
                break;
            case LightGreen:
                setTheme(R.style.LightGreenTheme);
                break;
            case Lime:
                setTheme(R.style.LimeTheme);
                break;
            case DeepOrange:
                setTheme(R.style.DeepOrangeTheme);
                break;
            case Cyan:
                setTheme(R.style.CyanTheme);
                break;
            case BlueGrey:
                setTheme(R.style.BlueGreyTheme);
                break;
        }

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

    /**
     * 初始化view
     */
    protected void initView() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mModel != null) {
            mModel.onDestroy();
        }
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

}