package com.lianglu.weyue.view.fragment.impl;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianglu.weyue.R;
import com.lianglu.weyue.model.BookBean;
import com.lianglu.weyue.view.activity.impl.BookDetailActivity;
import com.lianglu.weyue.view.adapter.BookInfoAdapter;
import com.lianglu.weyue.view.base.BaseFragment;
import com.lianglu.weyue.view.fragment.IBookInfo;
import com.lianglu.weyue.viewmodel.fragment.VMBooksInfo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.weavey.loading.lib.LoadingLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Liang_Lu on 2017/12/4.
 */

public class BooksInfoFragment extends BaseFragment implements IBookInfo {


    @BindView(R.id.rv_bookinfo)
    RecyclerView mRvBookinfo;
    @BindView(R.id.refresh)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.loadinglayout)
    LoadingLayout mLoadinglayout;

    String titleName;
    String getder;//男生、女生
    String type;//热门、完结
    private VMBooksInfo mModel;
    List<BookBean> mBookBeans = new ArrayList<>();
    private BookInfoAdapter mBookInfoAdapter;
    private int loadPage = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mModel = new VMBooksInfo(mContext, this);
        View view = setContentView(container, R.layout.fragment_book_info, mModel);
        return view;
    }

    public static BooksInfoFragment newInstance(String titleName, String getder, String type) {
        Bundle args = new Bundle();
        args.putString("titleName", titleName);
        args.putString("getder", getder);
        args.putString("type", type);
        BooksInfoFragment fragment = new BooksInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initView() {
        super.initView();
        titleName = getArguments().getString("titleName");
        getder = getArguments().getString("getder");
        type = getArguments().getString("type");

        mRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                ++loadPage;
                mModel.getBooks(type, titleName, loadPage);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadPage = 1;
                mModel.getBooks(type, titleName, 1);
            }
        });

        mRefreshLayout.autoRefresh();

        mLoadinglayout.setOnReloadListener(v -> mModel.getBooks(type, titleName, 1));

        mBookInfoAdapter = new BookInfoAdapter(mBookBeans);
        mRvBookinfo.setLayoutManager(new LinearLayoutManager(mContext));
        mBookInfoAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        mRvBookinfo.setAdapter(mBookInfoAdapter);

        mBookInfoAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent();
            intent.setClass(mContext, BookDetailActivity.class);
            intent.putExtra("bookid", mBookBeans.get(position).get_id());
            if (android.os.Build.VERSION.SDK_INT > 20) {
                ImageView imageView = view.findViewById(R.id.book_brief_iv_portrait);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(), imageView, "bookImage").toBundle());
            } else {
                startActivity(intent);
            }
        });

    }

    @Override
    public void getBooks(List<BookBean> bookBeans, boolean isLoadMore) {
        if (!isLoadMore) {
            mBookBeans.clear();
        }
        mBookBeans.addAll(bookBeans);
        mBookInfoAdapter.notifyDataSetChanged();
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void stopLoading() {
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadmore();
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


}
