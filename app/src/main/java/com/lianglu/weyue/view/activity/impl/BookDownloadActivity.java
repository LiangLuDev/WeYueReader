package com.lianglu.weyue.view.activity.impl;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianglu.weyue.R;
import com.lianglu.weyue.db.entity.DownloadTaskBean;
import com.lianglu.weyue.utils.LogUtils;
import com.lianglu.weyue.view.adapter.BookDownloadAdapter;
import com.lianglu.weyue.view.base.BaseActivity;
import com.lianglu.weyue.view.service.BookDownloadService;
import com.lianglu.weyue.viewmodel.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BookDownloadActivity extends BaseActivity implements BookDownloadService.OnDownloadListener {

    @BindView(R.id.rv_book_download)
    RecyclerView mRvBookDownload;
    private List<DownloadTaskBean> mTaskBeans = new ArrayList<>();
    private BookDownloadAdapter mDownloadAdapter;
    private ServiceConnection mServiceConnection;
    private BookDownloadService.IDownloadManager mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinddingView(R.layout.activity_book_download, NO_BINDDING, new BaseViewModel(this));
    }


    @Override
    protected void initView() {
        super.initView();
        initThemeToolBar("缓存列表");

        //设置下载列表数据
        mDownloadAdapter = new BookDownloadAdapter(mTaskBeans);
        mRvBookDownload.setLayoutManager(new LinearLayoutManager(this));
        mRvBookDownload.setAdapter(mDownloadAdapter);

        mDownloadAdapter.setOnItemClickListener((adapter, view, position) -> {
            DownloadTaskBean taskBean = mTaskBeans.get(position);
            switch (taskBean.getStatus()) {
                //准备暂停
                case DownloadTaskBean.STATUS_LOADING:
                    mService.setDownloadStatus(taskBean.getTaskName(), DownloadTaskBean.STATUS_PAUSE);
                    break;
                //准备暂停
                case DownloadTaskBean.STATUS_WAIT:
                    mService.setDownloadStatus(taskBean.getTaskName(), DownloadTaskBean.STATUS_PAUSE);
                    break;
                //准备启动
                case DownloadTaskBean.STATUS_PAUSE:
                    mService.setDownloadStatus(taskBean.getTaskName(), DownloadTaskBean.STATUS_WAIT);
                    break;
                //准备启动
                case DownloadTaskBean.STATUS_ERROR:
                    mService.setDownloadStatus(taskBean.getTaskName(), DownloadTaskBean.STATUS_WAIT);
                    break;
            }
        });

        //获取缓存列表数据
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
//                mTaskBeans.clear();
                mService = (BookDownloadService.IDownloadManager) service;
                //添加数据到队列中
                mTaskBeans.addAll(mService.getDownloadTaskList());
                mService.setOnDownloadListener(BookDownloadActivity.this);
                mDownloadAdapter.notifyDataSetChanged();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        //绑定
        Intent service = new Intent(this, BookDownloadService.class);
        bindService(service, mServiceConnection, Service.BIND_AUTO_CREATE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        LogUtils.print("onDestory");
        unbindService(mServiceConnection);
    }

    @Override
    public void onDownloadChange(int pos, int status, String msg) {
        DownloadTaskBean taskBean = mDownloadAdapter.getItem(pos);
        taskBean.setStatus(status);
        if (DownloadTaskBean.STATUS_LOADING == status) {
            taskBean.setCurrentChapter(Integer.valueOf(msg));
        }

        mDownloadAdapter.notifyItemChanged(pos);
    }

    @Override
    public void onDownloadResponse(int pos, int status) {
        DownloadTaskBean taskBean = mDownloadAdapter.getItem(pos);
        taskBean.setStatus(status);
        mDownloadAdapter.notifyItemChanged(pos);
    }
}
