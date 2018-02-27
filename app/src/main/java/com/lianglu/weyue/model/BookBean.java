package com.lianglu.weyue.model;

import com.lianglu.weyue.db.entity.CollBookBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Liang_Lu on 2017/12/6.
 */

public class BookBean implements Serializable {


    /**
     * _id : 59ba0dbb017336e411085a4e
     * title : 元尊
     * author : 天蚕土豆
     * longIntro : 彼时的归途，已是一条命运倒悬的路。 昔日的荣华，如白云苍狗，恐大梦一场。 少年执笔，龙蛇飞动。 是为一抹光芒劈开暮气沉沉之乱世，问鼎玉宇苍穹。 复仇之路，与吾同行。 一口玄黄真气定可吞天地日月星辰，雄视草木苍生。 铁画夕照，雾霭银钩，笔走游龙冲九州。 横姿天下，墨洒青山，鲸吞湖海纳百川。
     * cover : /agent/http%3A%2F%2Fimg.1391.com%2Fapi%2Fv1%2Fbookcenter%2Fcover%2F1%2F2107590%2F2107590_55d1f1bf10684e62a51d9f0ca3dd08fc.jpg%2F
     * majorCate : 玄幻
     * minorCate : 东方玄幻
     * hasCopyright : true
     * contentType : txt
     * latelyFollower : 95840
     * wordCount : 432088
     * serializeWordCount : 3717
     * retentionRatio : 51.69
     * updated : 2017-12-06T15:49:21.246Z
     * chaptersCount : 169
     * lastChapter : 正文 第一百六十八章 再遇
     * rating : {"count":6755,"score":8.119,"isEffect":true}
     * tags : []
     * gender : ["male"]
     */

    private String _id;
    private String title;
    private String author;
    private String longIntro;
    private String cover;
    private String majorCate;
    private String minorCate;
    private boolean hasCopyright;
    private boolean isCollect;
    private String contentType;
    private int latelyFollower;
    private int wordCount;
    private int serializeWordCount;
    private String retentionRatio;
    private String updated;
    private int chaptersCount;
    private String lastChapter;
    private String copyright;
    private RatingBean rating;
    private List<String> tags;
    private List<String> gender;

    private CollBookBean mCollBookBean;

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLongIntro() {
        return longIntro;
    }

    public void setLongIntro(String longIntro) {
        this.longIntro = longIntro;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getMajorCate() {
        return majorCate;
    }

    public void setMajorCate(String majorCate) {
        this.majorCate = majorCate;
    }

    public String getMinorCate() {
        return minorCate;
    }

    public void setMinorCate(String minorCate) {
        this.minorCate = minorCate;
    }

    public boolean isHasCopyright() {
        return hasCopyright;
    }

    public void setHasCopyright(boolean hasCopyright) {
        this.hasCopyright = hasCopyright;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getLatelyFollower() {
        return latelyFollower;
    }

    public void setLatelyFollower(int latelyFollower) {
        this.latelyFollower = latelyFollower;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public int getSerializeWordCount() {
        return serializeWordCount;
    }

    public void setSerializeWordCount(int serializeWordCount) {
        this.serializeWordCount = serializeWordCount;
    }

    public String getRetentionRatio() {
        return retentionRatio;
    }

    public void setRetentionRatio(String retentionRatio) {
        this.retentionRatio = retentionRatio;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public int getChaptersCount() {
        return chaptersCount;
    }

    public void setChaptersCount(int chaptersCount) {
        this.chaptersCount = chaptersCount;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public RatingBean getRating() {
        return rating;
    }

    public void setRating(RatingBean rating) {
        this.rating = rating;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getGender() {
        return gender;
    }

    public void setGender(List<String> gender) {
        this.gender = gender;
    }

    public static class RatingBean implements Serializable{
        /**
         * count : 6755
         * score : 8.119
         * isEffect : true
         */

        private int count;
        private double score;
        private boolean isEffect;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public boolean isIsEffect() {
            return isEffect;
        }

        public void setIsEffect(boolean isEffect) {
            this.isEffect = isEffect;
        }
    }


    public CollBookBean getCollBookBean() {
        if (mCollBookBean == null) {
            mCollBookBean = createCollBookBean();
        }
        return mCollBookBean;
    }

    public CollBookBean createCollBookBean() {
        CollBookBean bean = new CollBookBean();
        bean.set_id(get_id());
        bean.setTitle(getTitle());
        bean.setAuthor(bean.getAuthor());
        bean.setShortIntro(getLongIntro());
        bean.setCover(getCover());
//        bean.setHasCp(isHasCp());
        bean.setLatelyFollower(getLatelyFollower());
        bean.setRetentionRatio(Double.parseDouble(getRetentionRatio()));
        bean.setUpdated(getUpdated());
        bean.setChaptersCount(getChaptersCount());
        bean.setLastChapter(getLastChapter());
        return bean;
    }

}
