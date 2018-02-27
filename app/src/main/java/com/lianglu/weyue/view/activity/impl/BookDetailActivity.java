package com.lianglu.weyue.view.activity.impl;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lianglu.weyue.R;
import com.lianglu.weyue.db.entity.CollBookBean;
import com.lianglu.weyue.db.helper.CollBookHelper;
import com.lianglu.weyue.model.BookBean;
import com.lianglu.weyue.utils.BaseUtils;
import com.lianglu.weyue.utils.Constant;
import com.lianglu.weyue.utils.LoadingHelper;
import com.lianglu.weyue.utils.ToastUtils;
import com.lianglu.weyue.view.activity.IBookDetail;
import com.lianglu.weyue.view.base.BaseActivity;
import com.lianglu.weyue.viewmodel.activity.VMBookDetailInfo;
import com.lianglu.weyue.widget.dialog.BookTagDialog;
import com.lianglu.weyue.widget.theme.ColorRelativeLayout;
import com.lianglu.weyue.widget.theme.ColorTextView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class BookDetailActivity extends BaseActivity implements IBookDetail {

    @BindView(R.id.iv_book_image)
    ImageView mIvBookImage;
    @BindView(R.id.tv_book_name)
    TextView mTvBookName;
    @BindView(R.id.ctv_book_author)
    ColorTextView mCtvBookAuthor;
    @BindView(R.id.tv_book_classify)
    TextView mTvBookClassify;
    @BindView(R.id.tv_word_updatetime)
    TextView mTvWordUpdatetime;
    @BindView(R.id.ctv_score)
    ColorTextView mCtvScore;
    @BindView(R.id.tv_fow_num)
    TextView mTvFowNum;
    @BindView(R.id.tv_good_num)
    TextView mTvGoodNum;
    @BindView(R.id.tv_word_count)
    TextView mTvWordCount;
    @BindView(R.id.tv_book_brief)
    TextView mTvBookBrief;
    @BindView(R.id.ll_fow)
    LinearLayout mLlFow;
    @BindView(R.id.crl_start_read)
    ColorRelativeLayout mCrlStartRead;
    @BindView(R.id.ctv_addbook)
    TextView mCtvAddbook;
    @BindView(R.id.tv_evaluate)
    TextView mTvEvaluate;
    @BindView(R.id.fl_tags)
    TagFlowLayout mFlTags;
    @BindView(R.id.ll_tag)
    LinearLayout mLlTag;
    @BindView(R.id.tv_copyright)
    TextView mTvCopyRight;
    @BindView(R.id.tv_read)
    TextView mTvRead;
    @BindView(R.id.rl_rootview)
    RelativeLayout rl_rootview;
    private CollBookBean mCollBookBean;
    private BookBean mBookBean;
    private VMBookDetailInfo mModel;
    private String mBookid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new VMBookDetailInfo(mContext, this);
        setBinddingView(R.layout.activity_book_detail, NO_BINDDING, mModel);
        mBookid = getIntent().getStringExtra("bookid");
        mModel.bookInfo(mBookid);
    }


    private void init() {
        initThemeToolBar(mBookBean.getTitle());

        Glide.with(mContext).load(Constant.ZHUISHU_IMAGE_URL + mBookBean.getCover())
               /* .placeholder(R.mipmap.ic_book_loading).transform(new GlideRoundTransform(mContext))*/
                .into(mIvBookImage);

        mTvBookName.setText(mBookBean.getTitle());
        mCtvBookAuthor.setText(mBookBean.getAuthor());
        mTvBookClassify.setText("  |  " + mBookBean.getMajorCate());
        mTvWordCount.setText(mBookBean.getSerializeWordCount() + "");
        mTvFowNum.setText(mBookBean.getLatelyFollower() + "");
        mTvGoodNum.setText(mBookBean.getRetentionRatio() + "%");
        mTvBookBrief.setText(mBookBean.getLongIntro());
//        mTvCopyRight.setText("版权："+mBookBean.getCopyright()==null?"LuLiang盗版":mBookBean.getCopyright());
        if (mBookBean.getTags().size() > 0) {
            mLlTag.setVisibility(View.VISIBLE);
            mFlTags.setAdapter(new TagAdapter<String>(mBookBean.getTags()) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.tags_tv,
                            mFlTags, false);
                    tv.setText(s);
                    return tv;
                }
            });

            mFlTags.setOnTagClickListener((view, position, parent) -> {
                String tag = mBookBean.getTags().get(position);
                showTagDialog(tag);
                return true;
            });

        } else {
            mLlTag.setVisibility(View.GONE);
            mFlTags.setVisibility(View.GONE);
        }
        String wordCount = mBookBean.getWordCount() / 10000 > 0 ? mBookBean.getWordCount() / 10000 + "万字" : mBookBean.getWordCount() + "字";

        if (mBookBean.getRating() != null) {
            mCtvScore.setText(BaseUtils.format1Digits(mBookBean.getRating().getScore()));
            mTvEvaluate.setText(mBookBean.getRating().getCount() + "人评");
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");//注意格式化的表达式
        try {
            Date d = format.parse(mBookBean.getUpdated().replace("Z", " UTC"));//注意是空格+UTC
            Date nowDate = new Date();
            int day = (int) ((nowDate.getTime() - d.getTime()) / (1000 * 3600 * 24));
            int hour = (int) ((nowDate.getTime() - d.getTime()) / (1000 * 3600));
            String time = day > 0 ? day + "天前" : hour + "小时前";
            mTvWordUpdatetime.setText(wordCount + "  |  " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //设置书籍
        mCollBookBean = CollBookHelper.getsInstance().findBookById(mBookBean.get_id());

        if (mBookBean.isCollect()) {
            mCtvAddbook.setText("移除书架");
        } else {
            mCtvAddbook.setText("加入书架");
        }

        if (mCollBookBean == null) {
            mCollBookBean = mBookBean.getCollBookBean();
        }
    }

    private void showTagDialog(String tag) {
        BookTagDialog bookTagDialog = new BookTagDialog(mContext, tag);
        bookTagDialog.show();
        bookTagDialog.setOnDismissListener(dialog -> hideAnimator());


        long duration = 500;
        Display display = getWindowManager().getDefaultDisplay();
        float[] scale = new float[2];
        scale[0] = 1.0f;
        scale[1] = 0.8f;
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(rl_rootview, "scaleX", scale).setDuration(duration);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(rl_rootview, "scaleY", scale).setDuration(duration);
        float[] rotation = new float[]{0, 10, 0};
        ObjectAnimator rotationX = ObjectAnimator.ofFloat(rl_rootview, "rotationX", rotation).setDuration(duration);

        float[] translation = new float[1];
        translation[0] = -display.getWidth() * 0.2f / 2;
        ObjectAnimator translationY = ObjectAnimator.ofFloat(rl_rootview, "translationY", translation).setDuration(duration);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, rotationX, translationY);
        animatorSet.setTarget(rl_rootview);
        animatorSet.start();

    }


    /**
     * 弹框关闭页面动画
     */
    protected void hideAnimator() {
        long duration = 500;
        float[] scale = new float[2];
        scale[0] = 0.8f;
        scale[1] = 1.0f;
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(rl_rootview, "scaleX", scale).setDuration(duration);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(rl_rootview, "scaleY", scale).setDuration(duration);
        float[] rotation = new float[]{0, 10, 0};
        ObjectAnimator rotationX = ObjectAnimator.ofFloat(rl_rootview, "rotationX", rotation).setDuration(duration);

        float[] translation = new float[1];
        translation[0] = 0;
        ObjectAnimator translationY = ObjectAnimator.ofFloat(rl_rootview, "translationY", translation).setDuration(duration);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, rotationX, translationY);
        animatorSet.setTarget(rl_rootview);
        animatorSet.start();
    }


    @OnClick({R.id.ll_fow, R.id.crl_start_read})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_fow:
                String collectStatus = mCtvAddbook.getText().toString();
                if (collectStatus.equals("移除书架")) {
                    mCtvAddbook.setText("加入书架");
                    mModel.deleteBookShelfToServer(mCollBookBean);
                } else {
                    mCtvAddbook.setText("移除书架");
                    mModel.addBookShelf(mCollBookBean);
                }
                break;
            case R.id.crl_start_read:
                Bundle bundle = new Bundle();
                bundle.putSerializable(ReadActivity.EXTRA_COLL_BOOK, mCollBookBean);
                bundle.putBoolean(ReadActivity.EXTRA_IS_COLLECTED, false);
                startActivity(ReadActivity.class, bundle);
                break;
        }
    }

    @Override
    public void showLoading() {
        LoadingHelper.getInstance().showLoading(mContext);
    }

    @Override
    public void stopLoading() {
        LoadingHelper.getInstance().hideLoading();
    }

    @Override
    public void addBookCallback() {
        ToastUtils.show("加入书架成功");
    }

    @Override
    public void getBookInfo(BookBean bookBean) {
        mBookBean = bookBean;
        init();
    }
}
