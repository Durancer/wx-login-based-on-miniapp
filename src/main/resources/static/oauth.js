// pages/oauth/oauth.js
Page({

    /**
     * 页面的初始数据
     */
    data: {
        // 请求url根路径
        baseUrl: '',
        // 用户唯一标识
        openid: '',
        // 场景值
        scene: '',
        // 扫描状态，用于退出登录界面
        status:'wait'
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        let url = 'localhost:8080'

        // 对scene进行解码
        var scene = decodeURIComponent(options.scene)
        console.log('scene ==>',scene)
        this.setData({
            baseUrl:url,
            scene:scene
        })
        // 1、修改 认证状态为 已扫码
        wx.request({
          url: url+'user/scan',
          method: 'POST',
          header: { 'content-type': 'application/x-www-form-urlencoded' },
          data:{
            scene: scene 
          },
          success:res =>{
              console.log(res)
          }
        })
        
    },

    // 点击认证时绑定的事件，获取用户信息 
    getUserProfile() {
        wx.getUserProfile({ 
            desc:'获取用户信息', 
            success: res =>{
              let user = res.userInfo
              //保存用户信息到本地缓存
              wx.setStorageSync('user',user) 
              // 添加 用户信息 并 修改扫码状态，（为同一个请求）
              wx.request({
                url: this.data.baseUrl+'user/auth',
                method: 'POST',
                header: { 'content-type': 'application/x-www-form-urlencoded' },
                data:{
                    username:res.userInfo.nickName,
                    openid: wx.getStorageSync('openid'),
                    avatar: res.userInfo.avatarUrl,
                    scene: this.data.scene
                },
                success:resp =>{
                    this.setData({
                        // 用于判断是否已认证
                        status:'success'
                    })
                }
              })

            }, 
            fail:err =>{
              console.log('err ==>',err)
              // 拒绝 获取信息 情况，不做处理
            }

        })
  },         

    // 用户点击 取消认证，或以认证，返回微信 绑定的事件
    exit() {
      // 如果状态为 wait 说明用户 未认证点击了取消登录
        if (this.data.status === 'wait') {
          // 我们就需要发送请求，将数据库状态修改为 cancel，在执行退出
        wx.request({
            url: this.data.baseUrl+'user/cancel',
            method: 'POST',
            header: { 'content-type': 'application/x-www-form-urlencoded' },
            data:{ 
                scene: this.data.scene
            }, 
            success: resp => {
                // 执行退出小程序
                wx.exitMiniProgram({
                    success:res=>{
                        console.log(res)
                    }
                })
            }
          })
        } else {
            //否则，说明已认证，直接退出小程序即可
        wx.exitMiniProgram(
            {
                success:res =>{
                    console.log(res)
                }
            }
        )
            
      }
  }

})