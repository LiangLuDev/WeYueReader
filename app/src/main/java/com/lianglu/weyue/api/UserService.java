package com.lianglu.weyue.api;

import com.allen.library.bean.BaseData;
import com.lianglu.weyue.db.entity.UserBean;
import com.lianglu.weyue.model.AppUpdateBean;
import com.lianglu.weyue.model.BookBean;
import com.lianglu.weyue.model.DeleteBookBean;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Liang_Lu on 2018/1/5.
 * 用户模块接口
 */

public interface UserService {

    /**
     * 用户注册
     *
     * @return
     */
    @POST(ModelPath.USER + "/register")
    @FormUrlEncoded
    Observable<BaseData<String>> register(@Field("name") String username, @Field("password") String password);

    /**
     * 用户登录
     *
     * @return
     */
    @GET(ModelPath.USER + "/login")
    Observable<BaseData<UserBean>> login(@Query("name") String username, @Query("password") String password);

    /**
     * 修改用户密码
     *
     * @param password 用户密码
     * @return
     */
    @PUT(ModelPath.USER + "/password")
    @FormUrlEncoded
    Observable<BaseData<String>> updatePassword(@Field("password") String password);

    /**
     * 修改用户信息
     *
     * @param nickname 昵称
     * @param brief    简介
     * @return
     */
    @PUT(ModelPath.USER + "/userinfo")
    @FormUrlEncoded
    Observable<BaseData<String>> updateUserInfo(@Field("nickname") String nickname, @Field("brief") String brief);

    /**
     * 修改用户信息
     *
     * @return
     */
    @GET(ModelPath.USER + "/userinfo")
    Observable<BaseData<UserBean>> getUserInfo();


    /**
     * 更换用户头像
     *
     * @return
     */
    @Multipart
    @POST(ModelPath.USER + "/uploadavatar")
    Observable<BaseData<String>> avatar(@Part MultipartBody.Part part);

    /**
     * 获取服务器书架信息
     *
     * @return
     */
    @GET(ModelPath.USER + "/bookshelf")
    Observable<BaseData<List<BookBean>>> getBookShelf();

    /**
     * 加入书架到服务器
     *
     * @param bookid 书籍id
     * @return
     */
    @POST(ModelPath.USER + "/bookshelf")
    @FormUrlEncoded
    Observable<BaseData<String>> addBookShelf(@Field("bookid") String bookid);

    /**
     * 移除书架
     *
     * @return
     */
    @HTTP(method = "DELETE", path = ModelPath.USER + "/bookshelf", hasBody = true)
    Observable<BaseData<String>> deleteBookShelf(@Body DeleteBookBean bean);

    /**
     * 用户反馈
     *
     * @param qq       qq
     * @param feedback 反馈内容
     * @return
     */
    @POST(ModelPath.API + "/feedback")
    @FormUrlEncoded
    Observable<BaseData<String>> userFeddBack(@Field("qq") String qq, @Field("feedback") String feedback);

    /**
     * 用户反馈
     *
     * @return
     */
    @GET(ModelPath.API + "/appupdate")
    Observable<BaseData<AppUpdateBean>> appUpdate();

}
