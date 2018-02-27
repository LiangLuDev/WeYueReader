package com.lianglu.weyue.view.fragment.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianglu.weyue.R;
import com.lianglu.weyue.model.BookClassifyBean;
import com.lianglu.weyue.view.activity.impl.BookListActivity;
import com.lianglu.weyue.view.adapter.ClassifyAdapter;
import com.lianglu.weyue.view.base.BaseFragment;
import com.lianglu.weyue.view.fragment.IClassifyBook;
import com.lianglu.weyue.viewmodel.fragment.VMBookClassify;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.weavey.loading.lib.LoadingLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Liang_Lu on 2017/12/4.
 */

public class ClassifyFragment extends BaseFragment implements IClassifyBook {


    @BindView(R.id.rv_classify)
    RecyclerView mRvClassify;
    @BindView(R.id.loadinglayout)
    LoadingLayout mLoadinglayout;

    String tabName;
    ClassifyAdapter mClassifyAdapter;
    private VMBookClassify mModel;
    List<BookClassifyBean.ClassifyBean> mClassifyBeans = new ArrayList<>();
    String getder = "male";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mModel = new VMBookClassify(mContext, this);
        View view = setContentView(container, R.layout.fragment_classify, mModel);
        return view;
    }

    public static ClassifyFragment newInstance(String tabName) {
        Bundle args = new Bundle();
        args.putString("tabName", tabName);
        ClassifyFragment fragment = new ClassifyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initView() {
        super.initView();
        tabName = getArguments().getString("tabName");
        mModel.bookClassify();

        mClassifyAdapter = new ClassifyAdapter(mClassifyBeans);
        mRvClassify.setLayoutManager(new LinearLayoutManager(mContext));
        mClassifyAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        mRvClassify.setAdapter(mClassifyAdapter);
        mClassifyAdapter.setOnItemClickListener((adapter, view, position) -> {
            Bundle bundle = new Bundle();
            bundle.putString("getder", getder);
            bundle.putString("titleName", mClassifyBeans.get(position).getName());
            startActivity(BookListActivity.class, bundle);
        });


    }

    @Override
    public void getBookClassify(BookClassifyBean bookClassifyBean) {
        mLoadinglayout.setStatus(LoadingLayout.Success);
        mClassifyBeans.clear();
        switch (tabName) {
            case "男生":
                getder = "male";
                mClassifyBeans.addAll(bookClassifyBean.getMale());
                break;
            case "女生":
                getder = "female";
                mClassifyBeans.addAll(bookClassifyBean.getFemale());
                break;
            case "出版":
                getder = "press";
                mClassifyBeans.addAll(bookClassifyBean.getPress());
                break;
        }
        mClassifyAdapter.notifyDataSetChanged();
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void stopLoading() {
    }

    @Override
    public void emptyData() {
        mLoadinglayout.setStatus(LoadingLayout.Empty);
    }

    @Override
    public void errorData(String error) {
        mLoadinglayout.setEmptyText(error).setStatus(LoadingLayout.Error);
    }

    @Override
    public void NetWorkError() {
        mLoadinglayout.setStatus(LoadingLayout.No_Network);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
