package com.lianglu.weyue.view.fragment.impl;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianglu.weyue.R;
import com.lianglu.weyue.db.entity.CollBookBean;
import com.lianglu.weyue.db.entity.DownloadTaskBean;
import com.lianglu.weyue.db.helper.BookRecordHelper;
import com.lianglu.weyue.db.helper.CollBookHelper;
import com.lianglu.weyue.event.DeleteResponseEvent;
import com.lianglu.weyue.event.DeleteTaskEvent;
import com.lianglu.weyue.event.DownloadMessage;
import com.lianglu.weyue.model.BookBean;
import com.lianglu.weyue.utils.GsonUtils;
import com.lianglu.weyue.utils.LoadingHelper;
import com.lianglu.weyue.utils.LogUtils;
import com.lianglu.weyue.utils.ToastUtils;
import com.lianglu.weyue.utils.rxhelper.RxBus;
import com.lianglu.weyue.utils.rxhelper.RxUtils;
import com.lianglu.weyue.view.activity.impl.ReadActivity;
import com.lianglu.weyue.view.adapter.BookShelfAdapter;
import com.lianglu.weyue.view.base.BaseFragment;
import com.lianglu.weyue.view.fragment.IBookShelf;
import com.lianglu.weyue.viewmodel.BaseViewModel;
import com.lianglu.weyue.viewmodel.fragment.VMBookShelf;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Liang_Lu on 2017/11/28.
 */

public class BookShelfFragment extends BaseFragment implements IBookShelf {


    @BindView(R.id.rv_book_shelf)
    RecyclerView mRvBookShelf;
    @BindView(R.id.refresh)
    SmartRefreshLayout mSmartRefreshLayout;
    private BookShelfAdapter mBookAdapter;
    private List<CollBookBean> mAllBooks = new ArrayList<>();
    private VMBookShelf mModel;
    private boolean isCheck;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mModel = new VMBookShelf(mContext, this);
        View view = setContentView(container, R.layout.fragment_book_shelf, mModel);
        return view;
    }

    public static BookShelfFragment newInstance() {
        BookShelfFragment fragment = new BookShelfFragment();
        return fragment;
    }


    @Override
    public void initView() {
        super.initView();


//        mAllBooks.addAll(CollBookHelper.getsInstance().findAllBooks());
        mBookAdapter = new BookShelfAdapter(mAllBooks);
        mRvBookShelf.setLayoutManager(new LinearLayoutManager(getContext()));
        mBookAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        mRvBookShelf.setAdapter(mBookAdapter);

        mSmartRefreshLayout.setOnRefreshListener(refreshlayout -> {
            mAllBooks.clear();
            mAllBooks.addAll(CollBookHelper.getsInstance().findAllBooks());
            mBookAdapter.notifyDataSetChanged();
            mModel.getBookShelf(CollBookHelper.getsInstance().findAllBooks());
        });
//        mSmartRefreshLayout.autoRefresh();
        mBookAdapter.setOnItemClickListener(
                (adapter, view, position) -> {
                    //如果是本地文件，首先判断这个文件是否存在
                    CollBookBean collBook = mBookAdapter.getItem(position);
                    if (collBook.isLocal()) {
                        //id表示本地文件的路径
                        String path = collBook.get_id();
                        File file = new File(path);
                        //判断这个本地文件是否存在
                        if (file.exists()) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(ReadActivity.EXTRA_COLL_BOOK, collBook);
                            bundle.putBoolean(ReadActivity.EXTRA_IS_COLLECTED, true);
                            startActivity(ReadActivity.class, bundle);
                        } else {
                            //提示(从目录中移除这个文件)
                            new MaterialDialog.Builder(mContext)
                                    .title(BookShelfFragment.this.getResources().getString(R.string.wy_common_tip))
                                    .content("文件不存在,是否删除?")
                                    .positiveText(BookShelfFragment.this.getResources().getString(R.string.wy_common_sure))
                                    .onPositive((dialog, which) -> deleteBook(collBook, position))
                                    .negativeText(BookShelfFragment.this.getResources().getString(R.string.wy_common_cancel))
                                    .onNegative((dialog, which) -> dialog.dismiss())
                                    .show();
                        }
                    } else {
                        mModel.setBookInfo(collBook);
                    }
                }
        );

        //添加书籍下载任务处理
        Disposable downloadDisp = RxBus.getInstance()
                .toObservable(DownloadMessage.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    //使用Toast提示
                    ToastUtils.show(event.message);
                });
        addDisposable(downloadDisp);


        //删除书籍处理
        Disposable deleteDisp = RxBus.getInstance()
                .toObservable(DeleteResponseEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    if (event.isDelete) {
                        ProgressDialog progressDialog = new ProgressDialog(mContext);
                        progressDialog.setMessage("正在删除中");
                        progressDialog.show();
                        CollBookHelper.getsInstance().removeBookInRx(event.collBook)
                                .compose(RxUtils::toSimpleSingle)
                                .subscribe(aVoid -> {
                                    progressDialog.dismiss();
                                    mAllBooks.clear();
                                    mAllBooks.addAll(CollBookHelper.getsInstance().findAllBooks());
                                    mBookAdapter.notifyDataSetChanged();
                                });
                    } else {
                        //弹出一个Dialog
                        AlertDialog tipDialog = new AlertDialog.Builder(getContext())
                                .setTitle("您的任务正在加载")
                                .setMessage("先请暂停任务再进行删除")
                                .setPositiveButton("确定", (dialog, which) -> {
                                    dialog.dismiss();
                                }).create();
                        tipDialog.show();
                    }
                });
        addDisposable(deleteDisp);

        mBookAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            openItemDialog(mAllBooks.get(position), position);
            return true;
        });
    }


    private void openItemDialog(CollBookBean collBook, int position) {
        String[] menus;
//        if (collBook.isLocal()) {
            menus = getResources().getStringArray(R.array.wy_menu_local_book);
//        } else {
//            menus = getResources().getStringArray(R.array.wy_menu_net_book);
//        }

        new MaterialDialog.Builder(mContext)
                .title(collBook.getTitle())
                .items(menus)
                .itemsCallback((dialog, itemView, which, text) -> onItemMenuClick(menus[which], collBook, position))
                .show();
    }

    private void onItemMenuClick(String which, CollBookBean collBook, int position) {
        switch (which) {
            //缓存
            case "缓存":
                //2. 进行判断，如果CollBean中状态为未更新。那么就创建Task，加入到Service中去。
                //3. 如果状态为finish，并且isUpdate为true，那么就根据chapter创建状态
                //4. 如果状态为finish，并且isUpdate为false。
                downloadBook(collBook);
                break;
            //删除
            case "删除":
                deleteBook(collBook, position);
                break;
            default:
                break;
        }
    }


    private void downloadBook(CollBookBean collBookBean) {
        //创建任务
//        mPresenter.createDownloadTask(collBook);
        DownloadTaskBean task = new DownloadTaskBean();
        task.setTaskName(collBookBean.getTitle());
        task.setBookId(collBookBean.get_id());
        task.setBookChapters(collBookBean.getBookChapters());
        task.setLastChapter(collBookBean.getBookChapters().size());

        RxBus.getInstance().post(task);
    }

    /**
     * 默认删除本地文件
     *
     * @param collBook
     */
    private void deleteBook(CollBookBean collBook, int position) {
        if (collBook.isLocal()) {
            new MaterialDialog.Builder(mContext)
                    .title("删除本地书籍")
                    .checkBoxPrompt("同时删除本地文件", false, (buttonView, isChecked) -> isCheck = isChecked)
                    .positiveText(R.string.wy_common_sure)
                    .onPositive((dialog, which) -> {
                        if (isCheck) {
                            LoadingHelper.getInstance().showLoading(mContext);
                            //删除
                            File file = new File(collBook.get_id());
                            if (file.exists()) file.delete();
                            CollBookHelper.getsInstance().removeBookInRx(collBook)
                                    .subscribe(s -> {
                                                ToastUtils.show(s);
                                                BookRecordHelper.getsInstance().removeBook(collBook.get_id());
                                                //从Adapter中删除
                                                mBookAdapter.remove(position);
                                                LoadingHelper.getInstance().hideLoading();
                                            }
                                            , throwable -> {
                                                ToastUtils.show("删除失败");
                                                LoadingHelper.getInstance().hideLoading();
                                            });
                        } else {
                            CollBookHelper.getsInstance().removeBookInRx(collBook);
                            BookRecordHelper.getsInstance().removeBook(collBook.get_id());
                            //从Adapter中删除
                            mBookAdapter.remove(position);
                        }
                        mBookAdapter.notifyDataSetChanged();
                    })
                    .negativeText(R.string.wy_common_cancel)
                    .onNegative((dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        } else {
            RxBus.getInstance().post(new DeleteTaskEvent(collBook));
            mModel.deleteBookShelfToServer(collBook);
        }
    }


    @Override
    public void onResume() {
        mAllBooks.clear();
        mAllBooks.addAll(CollBookHelper.getsInstance().findAllBooks());
        mBookAdapter.notifyDataSetChanged();
        mModel.getBookShelf(CollBookHelper.getsInstance().findAllBooks());
        super.onResume();
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void stopLoading() {
        mSmartRefreshLayout.finishRefresh();
    }

    @Override
    public void booksShelfInfo(List<CollBookBean> beans) {
        mAllBooks.addAll(beans);
        mBookAdapter.notifyDataSetChanged();
    }


    @Override
    public void bookInfo(CollBookBean bean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ReadActivity.EXTRA_COLL_BOOK, bean);
        bundle.putBoolean(ReadActivity.EXTRA_IS_COLLECTED, true);
        startActivity(ReadActivity.class, bundle);
    }

    @Override
    public void deleteSuccess() {
        mBookAdapter.notifyDataSetChanged();
    }
}
