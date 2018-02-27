package com.lianglu.weyue.view.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.lianglu.weyue.R;
import com.lianglu.weyue.WYApplication;
import com.lianglu.weyue.db.entity.DownloadTaskBean;
import com.lianglu.weyue.utils.StringUtils;

import java.util.List;

/**
 * Created by Liang_Lu on 2017/12/27.
 */

public class BookDownloadAdapter extends BaseQuickAdapter<DownloadTaskBean, BaseViewHolder> {


    private NumberProgressBar mNpbDownload;
    private TextView mTvStatus;
    private TextView mTvDownloadCurrentStatus;
    private ImageView mIvStatus;

    public BookDownloadAdapter(@Nullable List<DownloadTaskBean> data) {
        super(R.layout.item_download, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DownloadTaskBean item) {
        helper.setText(R.id.tv_book_name, item.getTaskName());
        mNpbDownload = helper.getView(R.id.npb_download);
        mTvStatus = helper.getView(R.id.tv_download_status);
        mTvDownloadCurrentStatus = helper.getView(R.id.tv_download_current_status);
        mIvStatus = helper.getView(R.id.iv_download_status);

        switch (item.getStatus()) {
            case DownloadTaskBean.STATUS_LOADING:
                changeBtnStyle(R.string.wy_download_pause, R.drawable.ic_download_pause);
                //进度状态
                setProgressMax(item);
                mNpbDownload.setProgress(item.getCurrentChapter());
                setCurrentStatus(R.string.wy_download_loading);
                break;
            case DownloadTaskBean.STATUS_PAUSE:
                changeBtnStyle(R.string.wy_download_start, R.drawable.ic_download_start);
                //进度状态
                setProgressMax(item);
                mNpbDownload.setProgress(item.getCurrentChapter());
                setCurrentStatus(R.string.wy_download_pausing);

                break;
            case DownloadTaskBean.STATUS_WAIT:
                changeBtnStyle(R.string.wy_download_wait, R.drawable.ic_download_wait);
                //进度状态
                setProgressMax(item);
                mNpbDownload.setProgress(item.getCurrentChapter());
                setCurrentStatus(R.string.wy_download_waiting);

                break;
            case DownloadTaskBean.STATUS_ERROR:
                changeBtnStyle(R.string.wy_download_error, R.drawable.ic_download_error);
                //进度状态
                setProgressMax(item);
                mNpbDownload.setProgress(item.getCurrentChapter());
                setCurrentStatus(R.string.wy_download_source_error);
                break;
            case DownloadTaskBean.STATUS_FINISH:
                changeBtnStyle(R.string.wy_download_finish, R.drawable.ic_download_finish);
                //进度状态
                setProgressMax(item);
                mNpbDownload.setProgress(item.getCurrentChapter());
                setCurrentStatus(R.string.wy_download_complete);
                break;
        }


    }

    /**
     * 修改按钮状态和文字
     *
     * @param strRes
     * @param drawableRes
     */
    private void changeBtnStyle(int strRes, int drawableRes) {
        //按钮状态
        if (!mTvStatus.getText().equals(
                StringUtils.getString(strRes))) {
            mTvStatus.setText(StringUtils.getString(strRes));
            mIvStatus.setImageResource(drawableRes);
        }
    }

    /**
     * 设置下载进度条
     *
     * @param value
     */
    private void setProgressMax(DownloadTaskBean value) {
        if (mNpbDownload.getMax() != value.getBookChapterList().size()) {
//            mNpbDownload.setVisibility(View.VISIBLE);
            mNpbDownload.setMax(value.getBookChapterList().size());
        }
    }

    /**
     * 当前下载状态
     *
     * @param strRes
     */
    private void setCurrentStatus(int strRes) {
        if (!mTvDownloadCurrentStatus.getText().equals(StringUtils.getString(strRes))) {
            mTvDownloadCurrentStatus.setText(StringUtils.getString(strRes));
        }
    }
}
