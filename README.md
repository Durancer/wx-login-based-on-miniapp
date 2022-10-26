# Wx-login-miniapp

#### 介绍
基于 springboot 实现微信扫描小程序码登录，获取用户微信信息 ，封装了对于access_token，获取小程序码的过程。可以直接使用接口

#### 技术栈使用
基于 springboot 搭建服务
持久层使用 mybatis-plus
请求使用 httpclient 发送请求

#### 使用前置

1.  在微信公众平台注册 小程序 [https://mp.weixin.qq.com/](http://) 获取到响应的小程序信息 appId,app secret
2.  需要搭建与实体类 Login.java 和 User.java 相同子段的数据库表（可拓展）
3.  编写小程序认证界面，可按需求自定义编写，小程序相关 js 编写模板见  **src/static/oauth.js**  文件

![输入图片说明](https://img-blog.csdnimg.cn/736fcb8bec0844dba1b9d5f6184c5e27.jpeg)

#### 操作流程

微信扫码登录时序图

![输入图片说明](https://img-blog.csdnimg.cn/0ccd087583164d5f88ea9acfcc5c98ab.png)

1、获取小程序码时，在后台数据库创建一条数据，并返回scene值。

2、之后网站端根据 scene值 轮询 该条数据的认证状态。用扫码状态作为是否继续轮询的依据

3、扫码进入后更新扫码状态为 正在扫码

4、用户点击认证或者取消认证，更改扫码状态为对应的内容。当web端获取到的 状态为 取消认证或已认证时，即可结束轮询。已认证将在修改状态之前先插入用户信息。

5、如果是已认证，该项数据将会携带userid，web端可以根据userid查询到认证用户的信息。

6、为了避免造成资源的浪费和数据库垃圾信息。在已认证或取消认证后应该删除有关信息

