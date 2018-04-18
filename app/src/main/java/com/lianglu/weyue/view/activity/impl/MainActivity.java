package com.lianglu.weyue.view.activity.impl;

import android.Manifest;
import android.animation.Animator;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.lianglu.weyue.R;
import com.lianglu.weyue.db.entity.UserBean;
import com.lianglu.weyue.db.helper.UserHelper;
import com.lianglu.weyue.model.AppUpdateBean;
import com.lianglu.weyue.model.MainMenuBean;
import com.lianglu.weyue.utils.AppUpdateUtils;
import com.lianglu.weyue.utils.BaseUtils;
import com.lianglu.weyue.utils.Constant;
import com.lianglu.weyue.utils.SharedPreUtils;
import com.lianglu.weyue.utils.SnackBarUtils;
import com.lianglu.weyue.utils.ThemeUtils;
import com.lianglu.weyue.utils.ToastUtils;
import com.lianglu.weyue.view.activity.ISetting;
import com.lianglu.weyue.view.adapter.MainMenuAdapter;
import com.lianglu.weyue.view.base.BaseActivity;
import com.lianglu.weyue.view.fragment.impl.BookClassifyFragment;
import com.lianglu.weyue.view.fragment.impl.BookShelfFragment;
import com.lianglu.weyue.view.fragment.impl.ScanBookFragment;
import com.lianglu.weyue.viewmodel.activity.VMSettingInfo;
import com.lianglu.weyue.widget.ResideLayout;
import com.lianglu.weyue.widget.theme.ColorRelativeLayout;
import com.lianglu.weyue.widget.theme.ColorUiUtil;
import com.lianglu.weyue.widget.theme.Theme;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements ColorChooserDialog.ColorCallback, ISetting {
    @BindView(R.id.iv_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.tv_desc)
    TextView mTvDesc;
    @BindView(R.id.top_menu)
    LinearLayout mTopMenu;
    @BindView(R.id.rv_menu)
    RecyclerView mRvMenu;
    @BindView(R.id.tv_theme)
    TextView mTvTheme;
    @BindView(R.id.tv_setting)
    TextView mTvSetting;
    @BindView(R.id.bottom_menu)
    LinearLayout mBottomMenu;
    @BindView(R.id.menu)
    ColorRelativeLayout mMenu;
    @BindView(R.id.container)
    FrameLayout mContainer;
    @BindView(R.id.resideLayout)
    ResideLayout mResideLayout;
    @BindView(R.id.iv_toolbar_more)
    AppCompatImageView mIvToolBarMore;
    @BindView(R.id.iv_toolbar_back)
    AppCompatImageView mIvToolBarBack;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvToolBarTitle;
    private MainMenuAdapter mainMenuAdapter;

    private FragmentManager fragmentManager;
    private String currentFragmentTag;
    private List<MainMenuBean> menuBeans = new ArrayList<>();
    private long fristTime = 0;
    private VMSettingInfo mModel;
    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new VMSettingInfo(this, this);
        setBinddingView(R.layout.activity_main, NO_BINDDING, mModel);
        initThemeToolBar("分类", R.drawable.ic_classify, R.drawable.ic_search, v -> {
            mResideLayout.openPane();
        }, v -> {
            startActivity(BookSearchActivity.class);
        });

        mModel.appUpdate(false);
        fragmentManager = getSupportFragmentManager();

        initMenu();
        switchFragment("分类");
    }

    private void initMenu() {
        mTvDesc.setSelected(true);
        BaseUtils.setIconDrawable(mTvSetting, R.drawable.ic_setting);
        BaseUtils.setIconDrawable(mTvTheme, R.drawable.ic_theme);

        getMenuData();
        mRvMenu.setLayoutManager(new LinearLayoutManager(mContext));
        mainMenuAdapter = new MainMenuAdapter(menuBeans);
        mRvMenu.setAdapter(mainMenuAdapter);
        mainMenuAdapter.setOnItemClickListener((adapter, view, position) -> {
            String name = menuBeans.get(position).getName();
            switch (name) {
                case "扫描书籍":
                    RxPermissions rxPermissions = new RxPermissions(this);
                    rxPermissions
                            .requestEach(Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe(permission -> {
                                if (permission.granted) {
                                    // 用户已经同意该权限
                                    MainActivity.this.switchFragment(name);
                                    mTvToolBarTitle.setText(name);
                                    mIvToolBarBack.setImageResource(menuBeans.get(position).getIcon());
                                    mResideLayout.closePane();
                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                                    ToastUtils.show("用户拒绝开启读写权限");
                                    mResideLayout.closePane();
                                } else {
                                    // 用户拒绝了该权限，并且选中『不再询问』
                                    mResideLayout.closePane();
                                    SnackBarUtils.makeShort(MainActivity.this.getWindow().getDecorView(), "读写权限被禁止,移步到应用管理允许权限").show("去设置", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            BaseUtils.getAppDetailSettingIntent(mContext, getPackageName());
                                        }
                                    });

                                }
                            });
                    break;
                case "书架":
                    switchFragment(name);
                    mTvToolBarTitle.setText(name);
                    mIvToolBarBack.setImageResource(menuBeans.get(position).getIcon());
                    mResideLayout.closePane();
                    break;
                case "分类":
                    switchFragment(name);
                    mTvToolBarTitle.setText(name);
                    mIvToolBarBack.setImageResource(menuBeans.get(position).getIcon());
                    mResideLayout.closePane();
                    break;
                case "缓存列表":
                    startActivity(BookDownloadActivity.class);
                    mResideLayout.closePane();
                    break;
                case "意见反馈":
                    startActivity(FeedBackActivity.class);
                    mResideLayout.closePane();
                    break;
                case "关于作者":
                    startActivity(AboutMineActivity.class);
                    mResideLayout.closePane();
                    break;
                default:
                    ToastUtils.show("功能紧急开发中！！！");
                    break;
            }
        });

    }

    private List<MainMenuBean> getMenuData() {
        menuBeans.clear();
        String[] menuName = getResources().getStringArray(R.array.main_menu_name);
        TypedArray menuIcon = getResources().obtainTypedArray(R.array.main_menu_icon);

        for (int i = 0; i < menuName.length; i++) {
            MainMenuBean menuBean = new MainMenuBean();
            menuBean.setName(menuName[i]);
            menuBean.setIcon(menuIcon.getResourceId(i, 0));
            menuBeans.add(menuBean);
        }
        return menuBeans;
    }


    public void switchFragment(String name) {
        if (currentFragmentTag != null && currentFragmentTag.equals(name))
            return;

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        Fragment currentFragment = fragmentManager.findFragmentByTag(currentFragmentTag);
        if (currentFragment != null) {
            ft.hide(currentFragment);
        }

        Fragment foundFragment = fragmentManager.findFragmentByTag(name);

        if (foundFragment == null) {
            switch (name) {
                case "分类":
                    foundFragment = BookClassifyFragment.newInstance();
                    break;
                case "书架":
                    foundFragment = BookShelfFragment.newInstance();
                    break;
                case "扫描书籍":
                    foundFragment = ScanBookFragment.newInstance();
                    break;
                default:
//                    foundFragment = BookShelfFragment.newInstance();
                    break;
            }
        }

        if (foundFragment == null) {

        } else if (foundFragment.isAdded()) {
            ft.show(foundFragment);
        } else {
            ft.add(R.id.container, foundFragment, name);
        }
        ft.commit();
        currentFragmentTag = name;
    }


    @OnClick({R.id.iv_avatar, R.id.tv_theme, R.id.tv_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_avatar:
                String username = SharedPreUtils.getInstance()
                        .getString("username", "");
                if (username.equals("")) {
                    startActivity(LoginActivity.class);
                } else {
                    startActivity(UserInfoActivity.class);
                }
                break;
            case R.id.tv_theme:
                new ColorChooserDialog.Builder(this, R.string.theme)
                        .customColors(R.array.colors, null)
                        .doneButton(R.string.done)
                        .cancelButton(R.string.cancel)
                        .allowUserColorInput(false)
                        .allowUserColorInputAlpha(false)
                        .show();
                break;
            case R.id.tv_setting:

                if (mUsername.equals("")) {
                    startActivity(LoginActivity.class);
                } else {
                    startActivity(SettingActivity.class);
                }
                break;
        }
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, int selectedColor) {
        if (selectedColor == ThemeUtils.getThemeColor2Array(this, R.attr.colorPrimary))
            return;

        if (selectedColor == getResources().getColor(R.color.colorBluePrimary)) {
            setTheme(R.style.BlueTheme);

            SharedPreUtils.getInstance().setCurrentTheme(Theme.Blue);

        } else if (selectedColor == getResources().getColor(R.color.colorRedPrimary)) {
            setTheme(R.style.RedTheme);
            SharedPreUtils.getInstance().setCurrentTheme(Theme.Red);

        } else if (selectedColor == getResources().getColor(R.color.colorBrownPrimary)) {
            setTheme(R.style.BrownTheme);
            SharedPreUtils.getInstance().setCurrentTheme(Theme.Brown);

        } else if (selectedColor == getResources().getColor(R.color.colorGreenPrimary)) {
            setTheme(R.style.GreenTheme);
            SharedPreUtils.getInstance().setCurrentTheme(Theme.Green);

        } else if (selectedColor == getResources().getColor(R.color.colorPurplePrimary)) {
            setTheme(R.style.PurpleTheme);
            SharedPreUtils.getInstance().setCurrentTheme(Theme.Purple);

        } else if (selectedColor == getResources().getColor(R.color.colorTealPrimary)) {
            setTheme(R.style.TealTheme);
            SharedPreUtils.getInstance().setCurrentTheme(Theme.Teal);

        } else if (selectedColor == getResources().getColor(R.color.colorPinkPrimary)) {
            setTheme(R.style.PinkTheme);
            SharedPreUtils.getInstance().setCurrentTheme(Theme.Pink);

        } else if (selectedColor == getResources().getColor(R.color.colorDeepPurplePrimary)) {
            setTheme(R.style.DeepPurpleTheme);
            SharedPreUtils.getInstance().setCurrentTheme(Theme.DeepPurple);

        } else if (selectedColor == getResources().getColor(R.color.colorOrangePrimary)) {
            setTheme(R.style.OrangeTheme);
            SharedPreUtils.getInstance().setCurrentTheme(Theme.Orange);

        } else if (selectedColor == getResources().getColor(R.color.colorIndigoPrimary)) {
            setTheme(R.style.IndigoTheme);
            SharedPreUtils.getInstance().setCurrentTheme(Theme.Indigo);

        } else if (selectedColor == getResources().getColor(R.color.colorLightGreenPrimary)) {
            setTheme(R.style.LightGreenTheme);
            SharedPreUtils.getInstance().setCurrentTheme(Theme.LightGreen);

        } else if (selectedColor == getResources().getColor(R.color.colorDeepOrangePrimary)) {
            setTheme(R.style.DeepOrangeTheme);
            SharedPreUtils.getInstance().setCurrentTheme(Theme.DeepOrange);

        } else if (selectedColor == getResources().getColor(R.color.colorLimePrimary)) {
            setTheme(R.style.LimeTheme);
            SharedPreUtils.getInstance().setCurrentTheme(Theme.Lime);

        } else if (selectedColor == getResources().getColor(R.color.colorBlueGreyPrimary)) {
            setTheme(R.style.BlueGreyTheme);
            SharedPreUtils.getInstance().setCurrentTheme(Theme.BlueGrey);

        } else if (selectedColor == getResources().getColor(R.color.colorCyanPrimary)) {
            setTheme(R.style.CyanTheme);
            SharedPreUtils.getInstance().setCurrentTheme(Theme.Cyan);

        }
        final View rootView = getWindow().getDecorView();
        rootView.setDrawingCacheEnabled(true);
        rootView.buildDrawingCache(true);

        final Bitmap localBitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);
        if (null != localBitmap && rootView instanceof ViewGroup) {
            final View tmpView = new View(getApplicationContext());
            tmpView.setBackgroundDrawable(new BitmapDrawable(getResources(), localBitmap));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ((ViewGroup) rootView).addView(tmpView, params);
            tmpView.animate().alpha(0).setDuration(400).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ColorUiUtil.changeTheme(rootView, getTheme());
                    System.gc();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ((ViewGroup) rootView).removeView(tmpView);
                    localBitmap.recycle();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
        }
    }


    /**
     * 菜单是否可左滑
     *
     * @param isCanSlide
     */
    public void setLeftSlide(boolean isCanSlide) {
        mResideLayout.setCanLeftSlide(isCanSlide);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mUsername = SharedPreUtils.getInstance().getString("username", "");
        try {
            if (!mUsername.equals("")) {
                UserBean userBean = UserHelper.getsInstance().findUserByName(mUsername);
                Glide.with(mContext).load(Constant.BASE_URL + userBean.getIcon())
                        .apply(new RequestOptions().transform(new CircleCrop()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                        .into(mIvAvatar);
                mTvDesc.setText(userBean.getBrief());
                mTvSetting.setText("设置");
            } else {
                Glide.with(mContext).load(R.mipmap.avatar)
                        .apply(new RequestOptions().transform(new CircleCrop()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                        .into(mIvAvatar);
                mTvDesc.setText("未登录");
                mTvSetting.setText("登录");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        if (mResideLayout.isOpen()) {
            mResideLayout.closePane();
        } else {
            long secondTime = System.currentTimeMillis();
            if (secondTime - fristTime < 2000) {
                finish();
            } else {
                SnackBarUtils.makeShort(getWindow().getDecorView(), "再点击一次退出应用").show();
                fristTime = System.currentTimeMillis();
            }
        }
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void appUpdate(AppUpdateBean appUpdateBean) {
        AppUpdateUtils.getInstance().appUpdate(this, appUpdateBean);
    }
}
