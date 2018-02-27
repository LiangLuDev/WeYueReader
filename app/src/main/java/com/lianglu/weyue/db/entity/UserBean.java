package com.lianglu.weyue.db.entity;

import com.lianglu.weyue.model.BookBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

import java.util.List;

/**
 * Created by Liang_Lu on 2018/1/5.
 */
@Entity
public class UserBean {

    @Id
    public String name;
    public String password;
    public String icon;
    public String brief;
    public String token;
    public String nickname;
    @Transient
    public List<BookBean> likebooks;
    
    @Generated(hash = 2010221856)
    public UserBean(String name, String password, String icon, String brief,
            String token, String nickname) {
        this.name = name;
        this.password = password;
        this.icon = icon;
        this.brief = brief;
        this.token = token;
        this.nickname = nickname;
    }
    @Generated(hash = 1203313951)
    public UserBean() {
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getIcon() {
        return this.icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getBrief() {
        return this.brief;
    }
    public void setBrief(String brief) {
        this.brief = brief;
    }
    public String getToken() {
        return this.token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getNickname() {
        return this.nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<BookBean> getLikebooks() {
        return likebooks;
    }

    public void setLikebooks(List<BookBean> likebooks) {
        this.likebooks = likebooks;
    }
}
