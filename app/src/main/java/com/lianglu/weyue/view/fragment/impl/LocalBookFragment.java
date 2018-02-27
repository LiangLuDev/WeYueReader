package com.lianglu.weyue.view.fragment.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lianglu.weyue.R;
import com.lianglu.weyue.db.helper.CollBookHelper;
import com.lianglu.weyue.model.LocalFileBean;
import com.lianglu.weyue.utils.FileUtils;
import com.lianglu.weyue.utils.LoadingHelper;
import com.lianglu.weyue.utils.rxhelper.RxUtils;
import com.lianglu.weyue.view.adapter.LocalFileAdapter;
import com.lianglu.weyue.view.base.BaseFileFragment;
import com.lianglu.weyue.viewmodel.BaseViewModel;
import com.lianglu.weyue.widget.DividerItemDecoration;
import com.lianglu.weyue.widget.theme.ColorButton;
import com.weavey.loading.lib.LoadingLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Liang_Lu on 2017/11/29.
 */

public class LocalBookFragment extends BaseFileFragment {

    @BindView(R.id.btn_scan)
    ColorButton mBtnScan;
    @BindView(R.id.rv_files)
    RecyclerView mRvFiles;
    @BindView(R.id.loadlayout)
    LoadingLayout loadlayout;

    List<LocalFileBean> mFileBeans = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = setContentView(container, R.layout.fragment_local_book, new BaseViewModel(mContext));
        return view;
    }


    public static LocalBookFragment newInstance() {
        LocalBookFragment fragment = new LocalBookFragment();
        return fragment;
    }

    @Override
    public void initView() {
        super.initView();


        mAdapter = new LocalFileAdapter(mFileBeans);
        mRvFiles.setLayoutManager(new LinearLayoutManager(mContext));
        mRvFiles.addItemDecoration(new DividerItemDecoration(mContext));
        mRvFiles.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            //如果是已加载的文件，则点击事件无效。
            String id = mFileBeans.get(position).getFile().getAbsolutePath();
            if (CollBookHelper.getsInstance().findBookById(id) != null) {
                return;
            }
            mAdapter.setCheckedItem(position);
            //反馈
            if (mListener != null) {
                mListener.onItemCheckedChange(mAdapter.getItemIsChecked(position));
            }
        });

        mBtnScan.setOnClickListener(v -> {
            scanFiles();
        });

    }

    /**
     * 搜索文件
     */
    private void scanFiles() {
        LoadingHelper.getInstance().showLoading(mContext);
        addDisposable(FileUtils.getSDTxtFile()
                .compose(RxUtils::toSimpleSingle)
                .subscribe(
                        files -> {
                            LoadingHelper.getInstance().hideLoading();
                            mFileBeans.clear();
                            if (files.size() == 0) {
                                loadlayout.setStatus(LoadingLayout.Empty);
                            } else {
                                loadlayout.setStatus(LoadingLayout.Success);
                                for (File file : files) {
                                    LocalFileBean localFileBean = new LocalFileBean();
                                    localFileBean.setSelect(false);
                                    localFileBean.setFile(file);
                                    mFileBeans.add(localFileBean);
                                }
                                mAdapter.notifyDataSetChanged();

                                //反馈
                                if (mListener != null) {
                                    mListener.onCategoryChanged();
                                }
                            }
                        }));
    }

}
