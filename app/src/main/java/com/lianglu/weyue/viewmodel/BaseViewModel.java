package com.lianglu.weyue.viewmodel;

import android.content.Context;

import com.lianglu.weyue.WYApplication;
import com.lianglu.weyue.utils.SharedPreUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;


/**
 * Created by Liang_Lu on 2017/9/1.
 */

public class BaseViewModel {
    protected Context mContext;
    private List<Disposable> disposables = new ArrayList<>();

    public BaseViewModel(Context mContext) {
        this.mContext = mContext;
    }


    public void addDisposadle(Disposable disposable) {
        disposables.add(disposable);
    }

    /**
     * 获取token
     *
     * @return
     */
    public Map tokenMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("access-token", SharedPreUtils.getInstance().getString("token", "weyue"));
        map.put("app-type", "Android");
//        map.put("version-code", WYApplication.packageInfo.versionCode);
        return map;
    }

    public void onDestroy() {
        if (disposables.size() > 0) {
            for (Disposable disposable : disposables) {
                disposable.dispose();
            }
            disposables.clear();
        }
    }


}
