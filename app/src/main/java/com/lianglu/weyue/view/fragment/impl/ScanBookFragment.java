package com.lianglu.weyue.view.fragment.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.lianglu.weyue.R;
import com.lianglu.weyue.db.entity.CollBookBean;
import com.lianglu.weyue.db.helper.CollBookHelper;
import com.lianglu.weyue.utils.Constant;
import com.lianglu.weyue.utils.StringUtils;
import com.lianglu.weyue.utils.ToastUtils;
import com.lianglu.weyue.view.activity.impl.MainActivity;
import com.lianglu.weyue.view.base.BaseFragment;
import com.lianglu.weyue.view.base.BaseViewPageAdapter;
import com.lianglu.weyue.view.base.BaseFileFragment;
import com.lianglu.weyue.viewmodel.BaseViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Liang_Lu on 2017/11/29.
 */

public class ScanBookFragment extends BaseFragment {

    @BindView(R.id.nts_scan)
    NavigationTabStrip mNtsScan;
    @BindView(R.id.vp_scan)
    ViewPager mVpScan;


    @BindView(R.id.file_system_cb_selected_all)
    CheckBox mCbSelectAll;
    @BindView(R.id.file_system_btn_add_book)
    Button mBtnAddBook;
    @BindView(R.id.file_system_btn_delete)
    Button mBtnDelete;


    String[] titles = {"智能导入", "手机目录"};
    private List<Fragment> mFragments;

    private BaseFileFragment mCurFragment;
    private LocalBookFragment mLocalBookFragment;

    private BaseFileFragment.OnFileCheckedListener mListener = new BaseFileFragment.OnFileCheckedListener() {
        @Override
        public void onItemCheckedChange(boolean isChecked) {
            changeMenuStatus();
        }

        @Override
        public void onCategoryChanged() {
            //状态归零
            mCurFragment.setCheckedAll(false);
            //改变菜单
            changeMenuStatus();
            //改变是否能够全选
            changeCheckedAllStatus();
        }
    };
    private FileCategoryFragment mCategoryFragment;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = setContentView(container, R.layout.fragment_scan_book, new BaseViewModel(mContext));
        return view;
    }


    public static ScanBookFragment newInstance() {
        ScanBookFragment fragment = new ScanBookFragment();
        return fragment;
    }

    @Override
    public void initView() {
        super.initView();

        mFragments = new ArrayList<>();
        mLocalBookFragment = LocalBookFragment.newInstance();
        mCategoryFragment = FileCategoryFragment.newInstance();
        mCurFragment = mLocalBookFragment;

        mFragments.add(mLocalBookFragment);
        mFragments.add(mCategoryFragment);

        mVpScan.setAdapter(new BaseViewPageAdapter(getActivity().getSupportFragmentManager(), titles, mFragments));
        mVpScan.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ((MainActivity) getActivity()).setLeftSlide(position == 0);
                if (position == 0) {
                    mCurFragment = mLocalBookFragment;
                } else {
                    mCurFragment = mCategoryFragment;
                }

                //改变菜单状态
                changeMenuStatus();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mNtsScan.setTitles(titles);
        mNtsScan.setViewPager(mVpScan);

        mLocalBookFragment.setOnFileCheckedListener(mListener);
    }

    @OnClick({R.id.file_system_cb_selected_all, R.id.file_system_btn_add_book, R.id.file_system_btn_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.file_system_cb_selected_all:
                //设置全选状态
                mCurFragment.setCheckedAll(mCbSelectAll.isChecked());

                //改变菜单状态
                changeMenuStatus();
                break;
            case R.id.file_system_btn_add_book:
                //获取选中的文件
                List<File> files = mCurFragment.getCheckedFiles();
                //转换成CollBook,并存储
                List<CollBookBean> collBooks = convertCollBook(files);
                CollBookHelper.getsInstance().saveBooks(collBooks);
                //设置数据为false
                mCurFragment.setCheckedAll(false);
                //改变菜单状态
                changeMenuStatus();
                //改变是否可以全选
                changeCheckedAllStatus();
                //提示加入书架成功
                ToastUtils.show(getResources().getString(R.string.wy_file_add_succeed, collBooks.size()));
                break;
            case R.id.file_system_btn_delete:
                //弹出，确定删除文件吗。
                new AlertDialog.Builder(mContext)
                        .setTitle("删除文件")
                        .setMessage("确定删除文件吗?")
                        .setPositiveButton(getResources().getString(R.string.wy_common_sure), (dialog, which) -> {
                            //删除选中的文件
                            mCurFragment.deleteCheckedFiles();
                            //提示删除文件成功
                            ToastUtils.show("删除文件成功");
                        })
                        .setNegativeButton(getResources().getString(R.string.wy_common_cancel), null)
                        .show();
                break;
        }
    }

    /**
     * 将文件转换成CollBook
     *
     * @param files:需要加载的文件列表
     * @return
     */
    private List<CollBookBean> convertCollBook(List<File> files) {
        List<CollBookBean> collBooks = new ArrayList<>(files.size());
        for (File file : files) {
            //判断文件是否存在
            if (!file.exists()) continue;

            CollBookBean collBook = new CollBookBean();
            collBook.setLocal(true);
            collBook.set_id(file.getAbsolutePath());
            collBook.setTitle(file.getName().replace(".txt", ""));
            collBook.setLastChapter("开始阅读");
            collBook.setLastRead(StringUtils.
                    dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE));
            collBooks.add(collBook);
        }
        return collBooks;
    }


    /**
     * 改变底部选择栏的状态
     */
    private void changeMenuStatus() {

        //点击、删除状态的设置
        if (mCurFragment.getCheckedCount() == 0) {
            mBtnAddBook.setText(getString(R.string.wy_file_add_shelf));
            //设置某些按钮的是否可点击
            setMenuClickable(false);

            if (mCbSelectAll.isChecked()) {
                mCurFragment.setChecked(false);
                mCbSelectAll.setChecked(mCurFragment.isCheckedAll());
            }

        } else {
            mBtnAddBook.setText(getString(R.string.wy_file_add_shelves, mCurFragment.getCheckedCount()));
            setMenuClickable(true);

            //全选状态的设置

            //如果选中的全部的数据，则判断为全选
            if (mCurFragment.getCheckedCount() == mCurFragment.getCheckableCount()) {
                //设置为全选
                mCurFragment.setChecked(true);
                mCbSelectAll.setChecked(mCurFragment.isCheckedAll());
            }
            //如果曾今是全选则替换
            else if (mCurFragment.isCheckedAll()) {
                mCurFragment.setChecked(false);
                mCbSelectAll.setChecked(mCurFragment.isCheckedAll());
            }
        }

        //重置全选的文字
        if (mCurFragment.isCheckedAll()) {
            mCbSelectAll.setText("取消");
        } else {
            mCbSelectAll.setText("全选");
        }

    }

    private void setMenuClickable(boolean isClickable) {

        //设置是否可删除
        mBtnDelete.setEnabled(isClickable);
        mBtnDelete.setClickable(isClickable);

        //设置是否可添加书籍
        mBtnAddBook.setEnabled(isClickable);
        mBtnAddBook.setClickable(isClickable);
    }

    /**
     * 改变全选按钮的状态
     */
    private void changeCheckedAllStatus() {
        //获取可选择的文件数量
        int count = mCurFragment.getCheckableCount();

        //设置是否能够全选
        if (count > 0) {
            mCbSelectAll.setClickable(true);
            mCbSelectAll.setEnabled(true);
        } else {
            mCbSelectAll.setClickable(false);
            mCbSelectAll.setEnabled(false);
        }
    }
}
