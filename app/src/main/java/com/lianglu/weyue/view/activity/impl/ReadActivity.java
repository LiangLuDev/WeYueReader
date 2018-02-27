package com.lianglu.weyue.view.activity.impl;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lianglu.weyue.R;
import com.lianglu.weyue.db.entity.BookChapterBean;
import com.lianglu.weyue.db.entity.CollBookBean;
import com.lianglu.weyue.db.helper.BookChapterHelper;
import com.lianglu.weyue.model.BookChaptersBean;
import com.lianglu.weyue.utils.BrightnessUtils;
import com.lianglu.weyue.utils.LogUtils;
import com.lianglu.weyue.utils.ReadSettingManager;
import com.lianglu.weyue.utils.ScreenUtils;
import com.lianglu.weyue.utils.StatusBarUtils;
import com.lianglu.weyue.utils.StringUtils;
import com.lianglu.weyue.utils.rxhelper.RxUtils;
import com.lianglu.weyue.view.activity.IBookChapters;
import com.lianglu.weyue.view.adapter.ReadCategoryAdapter;
import com.lianglu.weyue.view.base.BaseActivity;
import com.lianglu.weyue.viewmodel.BaseViewModel;
import com.lianglu.weyue.viewmodel.activity.VMBookContentInfo;
import com.lianglu.weyue.widget.dialog.ReadSettingDialog;
import com.lianglu.weyue.widget.page.NetPageLoader;
import com.lianglu.weyue.widget.page.PageLoader;
import com.lianglu.weyue.widget.page.PageView;
import com.lianglu.weyue.widget.page.TxtChapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.View.combineMeasuredStates;

public class ReadActivity extends BaseActivity implements IBookChapters {
    //    @BindView(R.id.read_tv_brief)
//    TextView mReadTvBrief;
//    @BindView(R.id.read_tv_community)
//    TextView mReadTvCommunity;
    @BindView(R.id.tv_toolbar_title)
    TextView mTvToolbarTitle;
    @BindView(R.id.read_abl_top_menu)
    AppBarLayout mReadAblTopMenu;
    @BindView(R.id.pv_read_page)
    PageView mPvReadPage;
    @BindView(R.id.read_tv_page_tip)
    TextView mReadTvPageTip;
    @BindView(R.id.read_tv_pre_chapter)
    TextView mReadTvPreChapter;
    @BindView(R.id.read_sb_chapter_progress)
    SeekBar mReadSbChapterProgress;
    @BindView(R.id.read_tv_next_chapter)
    TextView mReadTvNextChapter;
    @BindView(R.id.read_tv_category)
    TextView mReadTvCategory;
    @BindView(R.id.read_tv_night_mode)
    TextView mReadTvNightMode;
    @BindView(R.id.read_tv_setting)
    TextView mReadTvSetting;
    @BindView(R.id.read_ll_bottom_menu)
    LinearLayout mReadLlBottomMenu;
    @BindView(R.id.rv_read_category)
    RecyclerView mRvReadCategory;
    @BindView(R.id.read_dl_slide)
    DrawerLayout mReadDlSlide;


    private static final String TAG = "ReadActivity";
    public static final int REQUEST_MORE_SETTING = 1;
    //注册 Brightness 的 uri
    private final Uri BRIGHTNESS_MODE_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE);
    private final Uri BRIGHTNESS_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
    private final Uri BRIGHTNESS_ADJ_URI =
            Settings.System.getUriFor("screen_auto_brightness_adj");

    public static final String EXTRA_COLL_BOOK = "extra_coll_book";
    public static final String EXTRA_IS_COLLECTED = "extra_is_collected";

    private boolean isRegistered = false;

    /*****************view******************/
    private ReadSettingDialog mSettingDialog;
    private PageLoader mPageLoader;
    private Animation mTopInAnim;
    private Animation mTopOutAnim;
    private Animation mBottomInAnim;
    private Animation mBottomOutAnim;
    //    private CategoryAdapter mCategoryAdapter;
    private CollBookBean mCollBook;
    //控制屏幕常亮
    private PowerManager.WakeLock mWakeLock;

    /***************params*****************/
    private boolean isCollected = false; //isFromSDCard
    private boolean isNightMode = false;
    private boolean isFullScreen = false;
    private String mBookId;
    ReadCategoryAdapter mReadCategoryAdapter;
    List<TxtChapter> mTxtChapters = new ArrayList<>();
    private VMBookContentInfo mVmContentInfo;
    List<BookChapterBean> bookChapterList = new ArrayList<>();


    // 接收电池信息和时间更新的广播
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", 0);
                mPageLoader.updateBattery(level);
            }
            //监听分钟的变化
            else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                mPageLoader.updateTime();
            }
        }
    };


    //亮度调节监听
    //由于亮度调节没有 Broadcast 而是直接修改 ContentProvider 的。所以需要创建一个 Observer 来监听 ContentProvider 的变化情况。
    private ContentObserver mBrightObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange);

            //判断当前是否跟随屏幕亮度，如果不是则返回
            if (selfChange || !mSettingDialog.isBrightFollowSystem()) return;

            //如果系统亮度改变，则修改当前 Activity 亮度
            if (BRIGHTNESS_MODE_URI.equals(uri)) {
                Log.d(TAG, "亮度模式改变");
            } else if (BRIGHTNESS_URI.equals(uri) && !BrightnessUtils.isAutoBrightness(ReadActivity.this)) {
                Log.d(TAG, "亮度模式为手动模式 值改变");
                BrightnessUtils.setBrightness(ReadActivity.this, BrightnessUtils.getScreenBrightness(ReadActivity.this));
            } else if (BRIGHTNESS_ADJ_URI.equals(uri) && BrightnessUtils.isAutoBrightness(ReadActivity.this)) {
                Log.d(TAG, "亮度模式为自动模式 值改变");
                BrightnessUtils.setBrightness(ReadActivity.this, BrightnessUtils.getScreenBrightness(ReadActivity.this));
            } else {
                Log.d(TAG, "亮度调整 其他");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVmContentInfo = new VMBookContentInfo(mContext, this);
        setBinddingView(R.layout.activity_read, NO_BINDDING, mVmContentInfo);

    }

    @Override
    protected void initView() {
        super.initView();
        mCollBook = (CollBookBean) getIntent().getSerializableExtra(EXTRA_COLL_BOOK);
        isCollected = getIntent().getBooleanExtra(EXTRA_IS_COLLECTED, false);
        isNightMode = ReadSettingManager.getInstance().isNightMode();
        isFullScreen = ReadSettingManager.getInstance().isFullScreen();
        mBookId = mCollBook.get_id();

        mTvToolbarTitle.setText(mCollBook.getTitle());
        StatusBarUtils.transparencyBar(this);
        //获取页面加载器

        mPageLoader = mPvReadPage.getPageLoader(mCollBook.isLocal());
        mReadDlSlide.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


        initData();


        //更多设置dialog
        mSettingDialog = new ReadSettingDialog(this, mPageLoader);

        setCategory();


        toggleNightMode();

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mReceiver, intentFilter);

        //设置当前Activity的Brightness
        if (ReadSettingManager.getInstance().isBrightnessAuto()) {
            BrightnessUtils.setBrightness(this, BrightnessUtils.getScreenBrightness(this));
        } else {
            BrightnessUtils.setBrightness(this, ReadSettingManager.getInstance().getBrightness());
        }

        //初始化屏幕常亮类
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "keep bright");
        //隐藏StatusBar
        mPvReadPage.post(
                () -> hideSystemBar()
        );

        //初始化TopMenu
        initTopMenu();

        //初始化BottomMenu
        initBottomMenu();


        mPageLoader.setOnPageChangeListener(new PageLoader.OnPageChangeListener() {
            @Override
            public void onChapterChange(int pos) {
                setCategorySelect(pos);

            }

            @Override
            public void onLoadChapter(List<TxtChapter> chapters, int pos) {
                mVmContentInfo.loadContent(mBookId, chapters);
                setCategorySelect(mPageLoader.getChapterPos());

                if (mPageLoader.getPageStatus() == NetPageLoader.STATUS_LOADING
                        || mPageLoader.getPageStatus() == NetPageLoader.STATUS_ERROR) {
                    //冻结使用
                    mReadSbChapterProgress.setEnabled(false);
                }

                //隐藏提示
                mReadTvPageTip.setVisibility(GONE);
                mReadSbChapterProgress.setProgress(0);
            }

            @Override
            public void onCategoryFinish(List<TxtChapter> chapters) {
                mTxtChapters.clear();
                mTxtChapters.addAll(chapters);
                mReadCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageCountChange(int count) {
                mReadSbChapterProgress.setEnabled(true);
                mReadSbChapterProgress.setMax(count - 1);
                mReadSbChapterProgress.setProgress(0);
            }

            @Override
            public void onPageChange(int pos) {
                mReadSbChapterProgress.post(() -> {
                    mReadSbChapterProgress.setProgress(pos);
                });
            }
        });


        mReadSbChapterProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mReadLlBottomMenu.getVisibility() == VISIBLE) {
                    //显示标题
                    mReadTvPageTip.setText((progress + 1) + "/" + (mReadSbChapterProgress.getMax() + 1));
                    mReadTvPageTip.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //进行切换
                int pagePos = mReadSbChapterProgress.getProgress();
                if (pagePos != mPageLoader.getPagePos()) {
                    mPageLoader.skipToPage(pagePos);
                }
                //隐藏提示
                mReadTvPageTip.setVisibility(GONE);
            }
        });

        mPvReadPage.setTouchListener(new PageView.TouchListener() {
            @Override
            public void center() {
                toggleMenu(true);
            }

            @Override
            public boolean onTouch() {
                return !hideReadMenu();
            }

            @Override
            public boolean prePage() {
                return true;
            }

            @Override
            public boolean nextPage() {
                return true;
            }

            @Override
            public void cancel() {
            }
        });
    }

    private void initData() {
        if (mCollBook.isLocal()) {
            mPageLoader.openBook(mCollBook);
        } else {
            //如果是网络文件
            //如果是已经收藏的，那么就从数据库中获取目录
            if (isCollected) {
                Disposable disposable = BookChapterHelper.getsInstance().findBookChaptersInRx(mBookId)
                        .compose(RxUtils::toSimpleSingle)
                        .subscribe(beans -> {
                            mCollBook.setBookChapters(beans);
                            mPageLoader.openBook(mCollBook);
                            //如果是被标记更新的,重新从网络中获取目录
                            if (mCollBook.isUpdate()) {
                                mVmContentInfo.loadChapters(mBookId);
                            }
                        });
                mVmContentInfo.addDisposadle(disposable);
            } else {
                //加载书籍目录
                mVmContentInfo.loadChapters(mBookId);
            }
        }
    }

    private void setCategory() {
        mRvReadCategory.setLayoutManager(new LinearLayoutManager(mContext));
        mReadCategoryAdapter = new ReadCategoryAdapter(mTxtChapters);
        mRvReadCategory.setAdapter(mReadCategoryAdapter);

        if (mTxtChapters.size() > 0) {
            setCategorySelect(0);
        }

        mReadCategoryAdapter.setOnItemClickListener((adapter, view, position) -> {
            setCategorySelect(position);
            mReadDlSlide.closeDrawer(Gravity.START);
            mPageLoader.skipToChapter(position);
        });

    }

    /**
     * 设置选中目录
     *
     * @param selectPos
     */
    private void setCategorySelect(int selectPos) {
        for (int i = 0; i < mTxtChapters.size(); i++) {
            TxtChapter chapter = mTxtChapters.get(i);
            if (i == selectPos) {
                chapter.setSelect(true);
            } else {
                chapter.setSelect(false);
            }
        }

        mReadCategoryAdapter.notifyDataSetChanged();
    }

    private void toggleNightMode() {
        if (isNightMode) {
            mReadTvNightMode.setText(StringUtils.getString(R.string.wy_mode_morning));
            Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.read_menu_morning);
            mReadTvNightMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        } else {
            mReadTvNightMode.setText(StringUtils.getString(R.string.wy_mode_night));
            Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.read_menu_night);
            mReadTvNightMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }
    }

    private void showSystemBar() {
        //显示
        StatusBarUtils.showUnStableStatusBar(this);
        if (isFullScreen) {
            StatusBarUtils.showUnStableNavBar(this);
        }
    }

    private void hideSystemBar() {
        //隐藏
        StatusBarUtils.hideStableStatusBar(this);
        if (isFullScreen) {
            StatusBarUtils.hideStableNavBar(this);
        }
    }

    private void initTopMenu() {
        if (Build.VERSION.SDK_INT >= 19) {
            mReadAblTopMenu.setPadding(0, ScreenUtils.getStatusBarHeight(), 0, 0);
        }
    }

    private void initBottomMenu() {
        //判断是否全屏
        if (ReadSettingManager.getInstance().isFullScreen()) {
            //还需要设置mBottomMenu的底部高度
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mReadLlBottomMenu.getLayoutParams();
            params.bottomMargin = ScreenUtils.getNavigationBarHeight();
            mReadLlBottomMenu.setLayoutParams(params);
        } else {
            //设置mBottomMenu的底部距离
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mReadLlBottomMenu.getLayoutParams();
            params.bottomMargin = 0;
            mReadLlBottomMenu.setLayoutParams(params);
        }
    }


    /**
     * 隐藏阅读界面的菜单显示
     *
     * @return 是否隐藏成功
     */
    private boolean hideReadMenu() {
        hideSystemBar();
        if (mReadAblTopMenu.getVisibility() == VISIBLE) {
            toggleMenu(true);
            return true;
        } else if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
            return true;
        }
        return false;
    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    private void toggleMenu(boolean hideStatusBar) {
        initMenuAnim();

        if (mReadAblTopMenu.getVisibility() == View.VISIBLE) {
            //关闭
            mReadAblTopMenu.startAnimation(mTopOutAnim);
            mReadLlBottomMenu.startAnimation(mBottomOutAnim);
            mReadAblTopMenu.setVisibility(GONE);
            mReadLlBottomMenu.setVisibility(GONE);
            mReadTvPageTip.setVisibility(GONE);

            if (hideStatusBar) {
                hideSystemBar();
            }
        } else {
            mReadAblTopMenu.setVisibility(View.VISIBLE);
            mReadLlBottomMenu.setVisibility(View.VISIBLE);
            mReadAblTopMenu.startAnimation(mTopInAnim);
            mReadLlBottomMenu.startAnimation(mBottomInAnim);

            showSystemBar();
        }
    }


    //初始化菜单动画
    private void initMenuAnim() {
        if (mTopInAnim != null) return;

        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in);
        mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out);
        mBottomInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in);
        mBottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out);
        //退出的速度要快
        mTopOutAnim.setDuration(200);
        mBottomOutAnim.setDuration(200);
    }


    @OnClick({R.id.read_tv_pre_chapter, R.id.read_tv_next_chapter, R.id.read_tv_category,
            R.id.read_tv_night_mode, R.id.read_tv_setting, R.id.tv_toolbar_title})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.read_tv_pre_chapter:
                setCategorySelect(mPageLoader.skipPreChapter());
                break;
            case R.id.read_tv_next_chapter:
                setCategorySelect(mPageLoader.skipNextChapter());
                break;
            case R.id.read_tv_category:
                setCategorySelect(mPageLoader.getChapterPos());
                //切换菜单
                toggleMenu(true);
                //打开侧滑动栏
                mReadDlSlide.openDrawer(Gravity.START);
                break;
            case R.id.read_tv_night_mode:
                if (isNightMode) {
                    isNightMode = false;
                } else {
                    isNightMode = true;
                }
                mPageLoader.setNightMode(isNightMode);
                toggleNightMode();
                break;
            case R.id.read_tv_setting:
                toggleMenu(false);
                mSettingDialog.show();
                break;
            case R.id.tv_toolbar_title:
                finish();
                break;
        }
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void bookChapters(BookChaptersBean bookChaptersBean) {
        bookChapterList.clear();
        for (BookChaptersBean.ChatpterBean bean : bookChaptersBean.getChapters()) {
            BookChapterBean chapterBean = new BookChapterBean();
            chapterBean.setBookId(bookChaptersBean.getBook());
            chapterBean.setLink(bean.getLink());
            chapterBean.setTitle(bean.getTitle());
//            chapterBean.setTaskName("下载");
            chapterBean.setUnreadble(bean.isRead());
            bookChapterList.add(chapterBean);
        }
        mCollBook.setBookChapters(bookChapterList);

        //如果是更新加载，那么重置PageLoader的Chapter
        if (mCollBook.isUpdate() && isCollected) {
            mPageLoader.setChapterList(bookChapterList);
            //异步下载更新的内容存到数据库
            //TODO
            BookChapterHelper.getsInstance().saveBookChaptersWithAsync(bookChapterList);

        } else {
            mPageLoader.openBook(mCollBook);
        }


    }

    @Override
    public void finishChapters() {
        if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING) {
            mPvReadPage.post(() -> {
                mPageLoader.openChapter();
            });
        }
        //当完成章节的时候，刷新列表
        mReadCategoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void errorChapters() {
        if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING) {
            mPageLoader.chapterError();
        }
    }


    //注册亮度观察者
    private void registerBrightObserver() {
        try {
            if (mBrightObserver != null) {
                if (!isRegistered) {
                    final ContentResolver cr = getContentResolver();
                    cr.unregisterContentObserver(mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_MODE_URI, false, mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_URI, false, mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_ADJ_URI, false, mBrightObserver);
                    isRegistered = true;
                }
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "[ouyangyj] register mBrightObserver error! " + throwable);
        }
    }

    //解注册
    private void unregisterBrightObserver() {
        try {
            if (mBrightObserver != null) {
                if (isRegistered) {
                    getContentResolver().unregisterContentObserver(mBrightObserver);
                    isRegistered = false;
                }
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "unregister BrightnessObserver error! " + throwable);
        }
    }


    @Override
    public void onBackPressed() {
        if (mReadAblTopMenu.getVisibility() == View.VISIBLE) {
            //非全屏下才收缩，全屏下直接退出
            if (!ReadSettingManager.getInstance().isFullScreen()) {
                toggleMenu(true);
                return;
            }
        } else if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
            return;
        } else if (mReadDlSlide.isDrawerOpen(Gravity.START)) {
            mReadDlSlide.closeDrawer(Gravity.START);
            return;
        }


        super.onBackPressed();
    }

    //退出
    private void exit() {
        //返回给BookDetail。
        Intent result = new Intent();
//        result.putExtra(BookDetailActivity.RESULT_IS_COLLECTED, isCollected);
        setResult(Activity.RESULT_OK, result);
        //退出
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBrightObserver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWakeLock.release();
        if (isCollected) {
            mPageLoader.saveRecord();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterBrightObserver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mPageLoader.closeBook();
    }


}
