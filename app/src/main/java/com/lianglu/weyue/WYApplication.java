package com.lianglu.weyue;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import com.allen.library.RxHttpUtils;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;
import com.lianglu.weyue.utils.Constant;
import com.lianglu.weyue.utils.ThemeUtils;
import com.lianglu.weyue.view.service.BookDownloadService;
import com.lianglu.weyue.widget.CircleHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.weavey.loading.lib.LoadingLayout;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;


/**
 * Created by Liang_Lu on 2017/11/21.
 */

public class WYApplication extends Application {
    private static WYApplication app;

    public static Context getAppContext() {
        return app;
    }

    public static Resources getAppResources() {
        return app.getResources();
    }
    public static PackageInfo packageInfo;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        app = this;

        try {
            packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        startService(new Intent(getAppContext(), BookDownloadService.class));
        initRxHttpUtils();
        initRefresh();
        initLoadingLayout();
    }

    private void initLoadingLayout() {
        LoadingLayout.getConfig()
                .setErrorText("出错啦~请稍后重试！")
                .setEmptyText("抱歉，暂无数据")
                .setNoNetworkText("无网络连接，请检查您的网络···")
                .setErrorImage(R.drawable.ic_error_icon)
                .setEmptyImage(R.drawable.ic_empty_error)
                .setNoNetworkImage(R.drawable.ic_net_error)
                .setAllTipTextColor(R.color.black)
                .setAllTipTextSize(14)
                .setReloadButtonText("点我重试哦")
                .setReloadButtonTextSize(14)
                .setReloadButtonTextColor(R.color.black)
                .setReloadButtonWidthAndHeight(150, 40);
    }

    private void initRxHttpUtils() {
        /**
         * 初始化配置
         */
        RxHttpUtils.init(this);
        OkHttpClient.Builder client = new OkHttpClient().newBuilder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor.Builder()
                        .setLevel(Level.BASIC)
                        .log(Platform.INFO)
                        .request("Request")
                        .response("Response")
                        .build());
        RxHttpUtils
                .getInstance()
                //开启全局配置
                .config()
                //全局的BaseUrl
                .setBaseUrl(Constant.BASE_URL)
                //全局的请求头信息
//                .setHeaders(map)
                //全局持久话cookie,保存本地每次都会携带在header中
                .setCookie(false)
                //全局ssl证书认证
                //信任所有证书,不安全有风险
//                .setSslSocketFactory()
                //使用预埋证书，校验服务端证书（自签名证书）
                //.setSslSocketFactory(getAssets().open("your.cer"))
                //使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
                //.setSslSocketFactory(getAssets().open("your.bks"), "123456", getAssets().open("your.cer"))
                //全局超时配置
                //全局是否打开请求log日志
                .setOkClient(client.build())
                .setLog(true);


    }

    private void initRefresh() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreater((context, layout) -> {
            CircleHeader header = new CircleHeader(context);
            layout.setPrimaryColorsId(ThemeUtils.getThemeColorId(), R.color.white);
            return header;
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreater((context, layout) -> new BallPulseFooter(context).setSpinnerStyle(SpinnerStyle.Translate));
    }
}
