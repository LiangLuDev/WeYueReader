package com.lianglu.weyue.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.lianglu.weyue.R;
import com.lianglu.weyue.WYApplication;
import com.lianglu.weyue.widget.theme.Theme;

/**
 * Created by Liang_Lu on 2017/8/31.
 */
public class SnackBarUtils {

    private Snackbar mSnackbar;

    private SnackBarUtils(Snackbar snackbar) {
        mSnackbar = snackbar;
    }

    public static SnackBarUtils makeShort(View view, String text) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
        return new SnackBarUtils(snackbar);
    }

    public static SnackBarUtils makeLong(View view, String text) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        return new SnackBarUtils(snackbar);
    }


    public void show() {
        setSnackBarBackColor();
        mSnackbar.show();
    }

    public void show(String actionText, View.OnClickListener listener) {
        setSnackBarBackColor();
        mSnackbar.setActionTextColor(WYApplication.getAppResources().getColor(R.color.white));
        mSnackbar.setAction(actionText, listener).show();
    }


    private View getSnackBarLayout(Snackbar snackbar) {
        if (snackbar != null) {
            return snackbar.getView();
        }
        return null;

    }

    private Snackbar setSnackBarBackColor() {
        View snackBarView = getSnackBarLayout(mSnackbar);
        if (snackBarView != null) {
            snackBarView.setBackgroundColor(ThemeUtils.getThemeColor());
        }
        return mSnackbar;
    }
}