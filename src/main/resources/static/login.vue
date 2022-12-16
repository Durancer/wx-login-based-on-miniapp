<template>
  <div class="index">
    <div class="daa">
      <!--      下面三种登录状态进行选择-->

      <!--      正在登录-->
      <div v-if=" loginStatus === 'ing'" class="status commen_bac" style="position: relative">
        <img :src="qrcodeUrl" alt="" style="width: 150px;height: 150px;margin-top: 20px;">
        <!--      认证成功绿勾-->
        <img src="@/assets/scan.png" alt=""
             style="width: 130px;height: 130px; opacity: .9;
            position: absolute;
            left: 60px;
            top: 30px;"
            v-if= "scanning"
>
        <p style="margin-top: 10px;" v-if="scanStatus === 'wait'">请扫码，完成登录</p>
        <p style="margin-top: 10px;" v-if="scanStatus === 'ing'">已扫码,等待认证</p>
        <p style="margin-top: 10px;" v-if="scanStatus === 'success'">用户认证成功</p>
        <p style="margin-top: 10px;" v-if="scanStatus === 'cancel'">用户取消认证</p>
      </div>

      <!--      未登录-->
      <div v-else-if="loginStatus === 'pre'" class="status commen_bac">
        <img src="@/assets/default_avatar.png" alt="" style="width: 100px;height: 100px;margin-top: 45px;">
        <p style="margin-top: 10px;">你的状态,
          <el-link type="primary" @click="login">未登录</el-link>
        </p>
      </div>
      <!--      已登录-->
      <div v-else-if="loginStatus === 'done'" class="status commen_bac">
        <img :src="userInfo.avatar" alt="" style="width: 100px;height: 100px;margin-top: 45px; border-radius: 50%">
        <p style="margin-top: 10px;">你好，<span style="font-size: 22px; ">{{ userInfo.username }}</span></p>
      </div>
    </div>
  </div>

</template>

<script>
import {getHomeInfo} from "@/api/home";
import {getUserDetail} from "@/api/user";
import {getBaseUrl} from "@/main";
import {createLogin} from "@/api/login";
import {checkLoginStatus} from "@/api/login";
import {deleteLoginData} from "@/api/login";
import bus from "@/bus/eventbus"
export default {
  name: "IndexPage",
  data() {
    return {
      // api 根路径
      baseUrl: '',
      // 用户信息
      userInfo: {
        avatar: '',
        username: '',
      },
      // 登录状态 ing 正在登录 done 完成登录 pre 未登录
      loginStatus: 'pre',
      // 扫码状态 wait 等待扫码 | ing 已扫码 | success 已认证 cancel 取消认证
      scanStatus: 'wait',
      // 小程序码url
      qrcodeUrl: '',
      // 扫码状态
      scanning: false

    }

  },
  methods: {
    login() {
      // 获取二维码
       let form = new FormData()
       form.append('password','buta422')
       createLogin(form).then(res =>{
         console.log(res)
         let scene = res.data
         //  拼接 小程序码url
         this.$data.qrcodeUrl = this.$data.baseUrl + 'qrcode/' + scene + '.jpg'
         // 修改登录状态为 正在扫码
         this.$data.loginStatus = 'ing'
         // 轮询扫码状态
         let timer = setInterval(() => {
           checkLoginStatus(scene).then(res => {
             if (res.data.status === 'wait') { // 等待扫码
               this.$data.scanStatus = 'wait'
             } else if (res.data.status === 'ing') { // 用户进入小程序（已扫码）
               this.$data.scanStatus = 'ing'
               // 显示 已扫码图片
               this.$data.scanning = 'true'
             } else if (res.data.status === 'success') { // 用户点击确认认证,处理业务结束轮询
               this.$data.scanStatus = "success"
               this.$data.loginStatus = "done"
               clearInterval(timer)
               // 认证成功根据 用户id 获取用户信息
               getUserDetail(res.data.userid).then(resp => {
                 // 赋值 并将用户信息存入本地缓存
                 this.$data.userInfo = resp.data
                 localStorage.setItem("userInfo", JSON.stringify(this.$data.userInfo));
                 // 发送认证事件
                 bus.$emit('auth',this.$data.userInfo)
               })
               // 发送请求删除资源
               this.deleteLoginInfo(scene)
             } else if (res.data.status === "cancel") { // 用户点击取消认证,处理业务结束轮询
               this.$data.scanStatus = res.data.status
               // 发送请求删除资源
               this.deleteLoginInfo(scene)
               clearInterval(timer)
               setTimeout(() => {
                 this.$data.loginStatus = 'pre'
               },3000)

             }
           })
         }, 2000);
       })
    },
  },
  created() {
    // layout退出登录
    bus.$on('layout',(val) => {
      console.log('val =>',val)
      this.$data.scanStatus = 'wait'
      this.$data.loginStatus = 'pre'
      this.$data.scanning = false
    })
  },
  mounted() {
    // 赋值全局路径
    this.$data.baseUrl = getBaseUrl()
    //获取用户信息
    let info = JSON.parse(localStorage.getItem('userInfo'))
    if( info !== null){
      this.$data.loginStatus = 'done'
      this.$data.userInfo = info
      // 发送认证事件
      bus.$emit('auth',this.$data.userInfo)
    }
  }

}
</script>

<style scoped>
.status {
  height: 220px;
  width: 250px;
  text-align: center;
}

.record {
  margin-bottom: 50px;
  border: 1px solid #ddd;
  padding: 0 20px 20px;
  border-radius: 10px;
}

.record h4 {
  line-height: 60px;
  border-bottom: 1px solid #ddd;
}

.record p {
  line-height: 40px;
}

.nav_item p {
  font-weight: bold;
  margin: 20px 0;

}

.nav_item img {
  width: 200px;
  height: 200px;
}

.el-carousel__item .nav_item {
  width: 350px;
  height: 350px;
  margin: 0 auto;
  text-align: center;
}

/deep/ .el-carousel__mask {
  background: none !important;
}

.el-carousel__item:nth-child(2n) {
  background: none !important;
}

.el-carousel__item:nth-child(2n+1) {
  background: none !important;
}

.line_left {
  height: 4px;
  width: 70px;
  border-radius: 2px;
  background: -webkit-linear-gradient(right, #986BF3, rgb(239, 11, 255));
}

.line_right {
  height: 4px;
  width: 70px;
  border-radius: 2px;
  background: -webkit-linear-gradient(left, #986BF3, rgb(239, 11, 255));
}

.rec_item img {
  width: 220px;
  height: 300px;
  border-radius: 20px;
}

.inter_imgs {
  margin-top: 20px;
}

.inter_imgs img {
  width: 220px;
  height: 140px;
}

.el-card {
  border-radius: 20px;
}

.inter_left {
  width: 700px;
}

.inter_right {
  width: 300px;
}

.text_inter {
  width: 500px;
  margin: 0 auto;
}

.interduce_text {
  margin-top: 20px;
  color: #727272;
  font-size: 15px;
  line-height: 20px;
}

.inter_title {
  color: #986BF3;
  font-size: 28px;
  background-image: -webkit-linear-gradient(right, rgb(40, 32, 243), #986BF3, rgb(239, 11, 255));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;

}

.type_list {
  display: flex;
  flex-flow: row wrap;
  justify-content: space-between;
}

.product {
  margin: 50px 0;
  padding-top: 20px;

}

.shop_button {
  width: 100px;
  height: 40px;
  background-color: #986BF3;
  font-size: 14px;
  border-radius: 20px;
  color: #fff;
  text-align: center;
  line-height: 40px;
  margin-top: 20px;
  transition: .4s;
}

.shop_button:hover {
  background-color: #fff;
  color: #986BF3;
  cursor: pointer;
}

.el-carousel__item h3 {
  color: #475669;
  font-size: 14px;
  opacity: 0.75;
  line-height: 150px;
  margin: 0;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n+1) {
  background-color: #d3dce6;
}

.banner_imgs {
  position: relative;
}

.imgs {
  width: 700px;
  border-radius: 20px;
  overflow: hidden;
}

.imgs img {
  width: 100%;
  height: 100%;
}

.imgs_text {
  position: absolute;
  left: 50px;
  top: 50px;
  z-index: 10;
  margin-top: 10px;
}


</style>