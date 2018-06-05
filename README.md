# 微Yue电子书
###  项目功能
- 注册登录
- 用户信息、用户密码、用户图像修改
- 书籍分类
- 本地书籍扫描
- 书架
- 书籍搜索（作者名或书籍名） 
- 书籍阅读（仅txt格式，暂不支持PDF等其他格式）
- 阅读字体、背景颜色、翻页效果等设置
- 意见反馈（反馈信息发送到我的邮箱）
- 应用版本更新
###  项目截图
![登录](http://upload-images.jianshu.io/upload_images/2635045-e0ab3a72f801e761.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/200) ![首页](http://upload-images.jianshu.io/upload_images/2635045-d8ec2b5a958988dd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/200)  ![用户信息](http://upload-images.jianshu.io/upload_images/2635045-31055b37a06638d3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/200) ![书籍分类](http://upload-images.jianshu.io/upload_images/2635045-9819bb21c48db3fd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/200) ![书籍详情](http://upload-images.jianshu.io/upload_images/2635045-5ead07ef67f0ceab.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/200) ![书籍类型](http://upload-images.jianshu.io/upload_images/2635045-4fa9a9065073ad19.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/200) ![书籍阅读](http://upload-images.jianshu.io/upload_images/2635045-e02ce07acff8fc64.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/200) ![本地扫描](http://upload-images.jianshu.io/upload_images/2635045-0802c61304a51045.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/200) ![搜索](http://upload-images.jianshu.io/upload_images/2635045-1cfd6fbfa8771c7f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/200)  ![选择主题](http://upload-images.jianshu.io/upload_images/2635045-192dfd92b96644e6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/200) ![版本更新](http://upload-images.jianshu.io/upload_images/2635045-de9f820895039cf1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/200)  ![意见反馈](http://upload-images.jianshu.io/upload_images/2635045-2da5435b109ada73.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/200)

### 使用开源库
1. **Rx2网络封装** [RxHttpUtils](https://github.com/lygttpod/RxHttpUtils) 
2. **6.0权限库** [RxPermissions](https://github.com/tbruyelle/RxPermissions)
3. **Glide图片加载库** [Glide](https://github.com/bumptech/glide)
4. **下拉刷新库** [SmartRefreshLayout](https://github.com/scwang90/SmartRefreshLayout)
5. **RecyclerView简化框架** [BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)
6. **MD风格Dialog** [material-dialogs](https://github.com/afollestad/material-dialogs)
7. **TabLaout选择** [NavigationTabStrip](https://github.com/Devlight/NavigationTabStrip)
8. **数据加载动画** [Android-SpinKit](https://github.com/ybq/Android-SpinKit)
9. **展开折叠TextView** [ExpandTextView](https://github.com/lcodecorex/ExpandTextView)
10. **流式标签** [FlowLayout](https://github.com/hongyangAndroid/FlowLayout)
11. **数据库** [greenDAO](https://github.com/greenrobot/greenDAO)
12. **版本更新进度条** [NumberProgressBar](https://github.com/daimajia/NumberProgressBar)
13. **图片选择器** [TakePhoto](https://github.com/crazycodeboy/TakePhoto)
14. **项目首页**- [GanK](https://github.com/dongjunkun/GanK) -在基础上修改
### 项目介绍
> 书籍数据爬取<追书神器>,付费章节是无法阅读的，因为付费章节的书籍内容是加密字符串。书籍数据也不会跟及时更新，只是隔段时间会去更新。这个项目主要是学习为主。项目最开始是准备使用MVVM架构配合DataBinding开发项目，实际运用中DataBinding在Android Studio支持不太友好，就放弃使用（因为太耗费时间），后来也懒得改，不过代码量不多，代码清晰。

### 下载体验
- 项目已经上架酷安市场 [下载链接](https://www.coolapk.com/apk/184655)

- ![微Yue.apk](http://upload-images.jianshu.io/upload_images/2635045-4ce921c9353b879c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 感谢
- [Allen](https://github.com/lygttpod) -使用RxHttpUtils相识（竟然是老乡），帮我解答不少问题
- [Richard Liu](https://github.com/XiqingLiu) -好机油，经常开技术研讨会（是他演讲，我听着）

### 意见反馈
如果遇到问题或者好的建议，请反馈到：927195249@qq.com 或者LiangLuDev@gmail.com

如果觉得还行的话，赞一下吧! 谢谢啦！


### TODO
- 支持PDF等更多格式文件
- 添加更多电子书（如果哪位朋友有免费电子书网站可以给我说一下，谢谢）


### 说明
> 本项目仅提供技术学习交流，不可作为商用。

#### 常见问题
##### 1、Clone下来自己运行之后拿不到数据。
>修改utils-Constant里面的为!BuildConfig.DEBUG即可（切换为服务器地址）
![server_address_switch.png](https://upload-images.jianshu.io/upload_images/2635045-4280b873649a0b74.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 2、Android 4.x版本崩溃。
>由于4.x版本不支持svg图片，所以会保错，目前没有解决（谅解，时间有限）
##### 3、应用内版本更新
> 从我提供的二维码扫描下载的，在应用内是可以正常更新的，自己clone跑起来，提示更新，下载是无法覆盖安装的。（签名问题）  
