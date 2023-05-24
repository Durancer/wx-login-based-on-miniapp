# Wx-login-miniapp

#### 项目介绍

基于 Spring Boot 实现微信扫描小程序码登录，获取用户微信信息 ，封装了对于**access_token**，获取小程序码的过程。完成配置后，直接调用相关 api 完成业务。

#### 需求分析

业务场景：
    1、网站需要做微信登陆，获取用户微信信息。
    2、需要同步关联  **网站用户内容**  和  **微信小程序用户**  的需求。即用户在不同的客户端登录同步数据

#### 使用前置

1.  在微信公众平台注册 小程序 [https://mp.weixin.qq.com/](http://) 获取到响应的小程序信息 appId,app secret
2.  需要搭建与实体类 Login.java 和 User.java 相同子段的数据库表（可拓展）
3.  编写小程序认证界面，可按需求自定义编写，小程序相关 js 编写模板见  **src/static/oauth.js**  文件
4.  **建议优先阅读文末操作流程，理解本项目执行过程**

#### 如何使用？

##### 1、导入项目

将本项目 clone 或下载到本地打开后，执行 mvn install 命令，将项目 Jar 包安装到本地 maven 仓库



在项目 pom 文件中添加

```xml
        <dependency>
            <groupId>com.xueyu</groupId>
            <artifactId>Wx-login</artifactId>
            <version>1.0.0</version>
        </dependency>
```



##### 2、添加配置文件信息

在 application.yml 中添加配置信息

```yml
server:
  port: 8080

mybatis-plus:
  global-config:
    db-config:
      id-type: auto

file:
  # 图片保存地址
  qrcode: D://

login:
  appId: #小程序appid
  secret: #小程序密钥
  authpage: pages/auth/auth #小程序认证界面,扫码后将跳转到这个界面
```

解释：由于方案是基于小程序码的，所以需要填写一个小程序码图片保存地址。也可以填写 linux 操作系统路径，如：/root/home

（如果使用的是分布式文件存储来进行存储，其中一个api将以 InputStream 流的形式返回，提高灵活性）

其次就是配置小程序相关信息，以及认证界面。

##### 3、api调用

本版本 api 提供两个方案。（在添加配置后，直接注入 loginService 即可使用）

###### **无侵入式方案（推荐）**

该方案提供相关 api 来一键获取和保存小程序码相关操作

**（再次提醒优先阅读文末登录认证流程，便于理解接口返回值用来做什么，方便接入自己的业务逻辑）**

**调用示例：**

```java
@Autowired
LoginService loginService;

@PostMapping("login/create")
public String createQrcode(){
   return loginService.saveQrcodeFile();
}
```

直接调用即可，执行后能够在刚才所配置的路径中查看到生成的小程序码

###### 有一定侵入式方案

此方案能够完整按照文末操作流程执行

但是需要建立对应的 sql表、实体类等信息，即采用文末方案实行

调用方式与上述无侵入方案相同，都是调用相关方法



##### 4、api文档

此 api 都在 LoginService 的方法中

**无侵入式**

```java
	/**
	 * 创建扫码图片，无侵入式，保存图片后返回图片文件名
	 * 无额外进行任何业务逻辑
	 *
	 * @return 返回扫码图片名称 名称结构   scene + ".jpg"
	 */
	String saveQrcodeFile();

	/**
	 * 获取图片 inputStream流
	 * 用户分布式文件存储系统，如 minIO、fastFDS等场景使用
	 *
	 * @return 图片 inputStream流
	 */
	InputStream getQrcdoeInputStream();
```

**有侵入式**

```java
	/**
	 * 请求创建图片文件并返回文件名
	 * 有一定侵入性，需要有对应的sql数据库表
	 *
	 * @return 返回scene值，作为网站做剩余操作的依据
	 */
	String saveQrcodeFileAndData();
```



#### 操作流程

此项目已封装有关操作，可以结合 上述 js 文件来理解对应的流程。理解后，也可以修改或增加业务逻辑的执行

 **微信扫码登录时序图** 

此方案为 **轮询** 检测扫码及认证状态，有需要建立长连接或其它操作可以自行更改。

<img src="https://img-blog.csdnimg.cn/0ccd087583164d5f88ea9acfcc5c98ab.png" alt="输入图片说明" style="zoom: 25%;" />

 **准备工作。**我们需要准备两个表 一个是**用户信息表**，一个是 **小程序码状态表** 也就是存放 **scene值** 的表，当然，这个表在认证成功之后需要关联用户id，来告知前端认证用户的信息。

小程序码状态表为 **status** 扫码状态、**scene场景值**。每张小程序码都有唯一的 scene值，扫码进到认证页面后，能够将该参数带到小程序的认证页面，即在认证界面能够接收。这是方案的核心。

1、获取小程序码时，在后台数据库创建一条数据，并返回前端scene值。如下图所示

<img src="https://img-blog.csdnimg.cn/e1e3e4a1470b4be598f4535743c18ab3.png" alt="输入图片说明" style="zoom: 50%;" />

此时user_id是没有值的，并且状态是等待扫码。

2、之后网站端根据 scene值 轮询 该条数据的认证状态。用扫码状态作为是否继续轮询的依据

3、扫码进入后更新扫码状态为 正在扫码。（即在 onload函数发送请求，修改数据库）

<img src="https://img-blog.csdnimg.cn/f261dd6c75bf4ede84fabd8f7cb893f3.png" alt="输入图片说明" style="zoom: 50%;" />

4、用户点击认证或者取消认证，更改扫码状态为对应的状态。当web端获取到的 状态为 取消认证或已认证时，即可结束轮询。已认证将在修改状态之前先插入用户信息。（因为认证了才知道用户信息）

先插入用户表用户信息，（用户数据就是在小程序得到的，可以调用小程序的api获取）

<img src="https://img-blog.csdnimg.cn/c4b2829ebc5d4bb18e3121ba33abe149.png" alt="输入图片说明" style="zoom: 50%;" />

 同样，根据scene值，修改登录表

<img src="https://img-blog.csdnimg.cn/0c2003b4ee3b40cf82f1621b880d9eff.png" alt="输入图片说明" style="zoom: 50%;" />

可以看到到这一步，扫码状态已经变成了 success，表示认证成功，如果用户取消认证将为 cancel

并且有了userId。

5、我们将查询 status为 success 的数据，如果是已认证，我们需要先结束轮询。该项数据库数据将会携带刚才加入的 userId，如上图所示。web端可以根据userId查询到认证用户的信息。

这是就基本完成了我们的任务。

6、为了避免造成资源的浪费和数据库垃圾信息。在已认证或取消认证后应该删除登录表有关信息

