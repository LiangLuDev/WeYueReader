package com.lianglu.weyue.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.allen.library.RxHttpUtils;
import com.allen.library.download.DownloadObserver;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.lianglu.weyue.R;
import com.lianglu.weyue.model.AppUpdateBean;

import java.io.File;

import io.reactivex.disposables.Disposable;

/**
 * Created by Liang_Lu on 2018/1/23.
 */

public class AppUpdateUtils {

    public static  AppUpdateUtils mAppUpdateUtils;

    Disposable mDisposable;


    public static AppUpdateUtils getInstance() {
        if (mAppUpdateUtils==null) {
            mAppUpdateUtils=new AppUpdateUtils();
        }
        return mAppUpdateUtils;
    }

    public void appUpdate(Context context, AppUpdateBean appUpdateBean) {
        new MaterialDialog.Builder(context)
                .title("版本更新")
                .content("是否更新到最新版本？")
                .positiveText("立即更新")
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    updateDownload(context, appUpdateBean.getDownloadurl());
                })
                .negativeText("取消")
                .onNegative((dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }


    /**
     * 应用下载
     *
     * @param downloadUrl
     */
    private void updateDownload(Context mContext, String downloadUrl) {
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .customView(R.layout.layout_app_update, false)
                .title("更新下载中...")
                .negativeText("取消下载")
                .onNegative((dialog12, which) -> {
                    if (mDisposable != null) {
                        mDisposable.dispose();
                    }
                    ToastUtils.show("取消更新");
                    dialog12.dismiss();
                })
                .build();

        dialog.setOnDismissListener(dialog1 -> {
            if (mDisposable != null) {
                mDisposable.dispose();
            }
            ToastUtils.show("取消更新");
            dialog1.dismiss();
        });

        NumberProgressBar npb_download = dialog.getCustomView().findViewById(R.id.npb_download);
        dialog.show();
        String url = Constant.BASE_URL + downloadUrl;
        RxHttpUtils.downloadFile(url)
                .subscribe(new DownloadObserver("WeYue.apk") {
                    @Override
                    protected void getDisposable(Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    protected void onError(String s) {

                    }

                    @Override
                    protected void onSuccess(long bytesRead, long contentLength, float progress, boolean done, String filePath) {
                        if (done) {
                            dialog.dismiss();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            //判断是否是AndroidN以及更高的版本
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Uri contentUri = FileProvider.getUriForFile(mContext, "com.lianglu.weyue.FileProvider", new File(filePath));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                            } else {
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
                            }

                            mContext.startActivity(intent);
                        } else {
                            npb_download.setProgress(((int) progress));
                        }
                    }
                });

    }
}
