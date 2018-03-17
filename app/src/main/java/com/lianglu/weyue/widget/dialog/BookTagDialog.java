package com.lianglu.weyue.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.lianglu.weyue.R;
import com.lianglu.weyue.api.BookService;
import com.lianglu.weyue.utils.rxhelper.RxObserver;
import com.lianglu.weyue.model.BookBean;
import com.lianglu.weyue.utils.SharedPreUtils;
import com.lianglu.weyue.view.activity.impl.BookDetailActivity;
import com.lianglu.weyue.view.adapter.BookTagsAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;

/**
 * Created by Liang_Lu on 2017/12/20.
 */

public class BookTagDialog extends Dialog {
    Context mContext;
    String tagName;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvBooks;
    List<BookBean> mBeans = new ArrayList<>();
    private BookTagsAdapter mBookTagsAdapter;
    int page = 1;
    private Disposable mDisposable;

    public BookTagDialog(@NonNull Context context, String tagName) {
        super(context);
        this.mContext = context;
        this.tagName = tagName;
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_book_tag, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        mRvBooks = view.findViewById(R.id.rv_books);
        tvTitle.setText(tagName);
        setContentView(view);

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams attributes = window.getAttributes();
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        attributes.width = display.getWidth();
        attributes.height = display.getHeight() / 2;
        window.setAttributes(attributes);
        window.setWindowAnimations(R.style.DialogAnimation);

        mRvBooks.setLayoutManager(new LinearLayoutManager(mContext));
        mBookTagsAdapter = new BookTagsAdapter(mBeans);
        mRvBooks.setAdapter(mBookTagsAdapter);
        mBookTagsAdapter.setOnItemClickListener((adapter, view1, position) -> {
            Intent intent = new Intent(mContext, BookDetailActivity.class);
            intent.putExtra("bookid", mBeans.get(position).get_id());
            mContext.startActivity(intent);
        });


        mRefreshLayout.setOnLoadmoreListener(refreshlayout -> {
            ++page;
            BookTagDialog.this.getBooksByTag();
        });


        getBooksByTag();
    }


    private void getBooksByTag() {
        Map<String, Object> map = new HashMap<>();
        map.put("access-token", SharedPreUtils.getInstance().getString("token", "weyue"));
        map.put("app-type", "Android");
        RxHttpUtils.getSInstance().addHeaders(map).createSApi(BookService.class)
                .booksByTag(tagName, page)
                .compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver<List<BookBean>>() {
                    @Override
                    protected void onError(String errorMsg) {
                        mRefreshLayout.finishLoadmore();
                    }

                    @Override
                    protected void onSuccess(List<BookBean> data) {
                        mRefreshLayout.finishLoadmore();
                        mBeans.addAll(data);
                        if (mBeans.size() > 0) {
                            mBookTagsAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mDisposable = d;
                    }
                });

    }

    @Override
    public void dismiss() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        super.dismiss();
    }


}
