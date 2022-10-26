package com.xueyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xueyu.constant.WxLoginContant;
import com.xueyu.pojo.dto.Login;
import com.xueyu.pojo.dto.User;
import com.xueyu.service.LoginService;
import com.xueyu.service.UserService;
import com.xueyu.utils.FileManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * @author durance
 */
@RestController
@RequestMapping("/login")
public class LoginController {

	@Autowired
	private LoginService loginService;

	@Autowired
	private UserService userService;

	@Value("${file.qrcode}")
	String codePath;

	/**
	 * 创建 login 数据
	 */
	@PostMapping("create")
	public String createUserLogin(){
		return loginService.createAndGetQrcodeFile();
	}

	/**
	 *  修改扫码状态为 已扫码
	 */
	@PostMapping("scan")
	public void userScanQrcode(Login login){
		login.setStatus(WxLoginContant.STATUS_ING);
		QueryWrapper<Login> wrapper = new QueryWrapper<>();
		wrapper.eq("scene",login.getScene());
		loginService.update(login,wrapper);
	}

	/**
	 * 用户点击确认
	 * @param user 执行的用户
	 * @param login 登录信息
	 */
	@PostMapping("auth")
	public void userAuthWebPro(User user, Login login){
		// 更新/添加 用户信息
		getUserInfo(user);
		QueryWrapper<User> wrapper = new QueryWrapper<>();
		wrapper.eq("openid", user.getOpenid());
		User one = userService.getOne(wrapper);
		// 更改 扫码状态
		login.setStatus(WxLoginContant.STATUS_SUCCESS);
		login.setUserid(one.getId());
		QueryWrapper<Login> wrapper1 = new QueryWrapper<>();
		wrapper1.eq("scene",login.getScene());
		loginService.update(login,wrapper1);
	}

	/**
	 * 用户取消认证
	 * @param scene 场景值
	 */
	@PostMapping("cancel")
	public void userCancelAuth(String scene){
		// 修改状态为 cancel
		Login login = new Login();
		login.setStatus(WxLoginContant.STATUS_CANCEL);
		QueryWrapper<Login> wrapper = new QueryWrapper<>();
		wrapper.eq("scene",scene);
		loginService.update(login,wrapper);
	}

	/**
	 * 用户点击同意后 或 超时后，浏览器发送请求删除该项数据
	 */
	@PostMapping("delete/scene")
	public void delete(String scene){
		// 删除数据库信息
		QueryWrapper<Login> wrapper = new QueryWrapper<>();
		wrapper.eq("scene",scene);
		loginService.remove(wrapper);
		// 删除图片文件
		FileManage.delFileByPath(codePath+scene+".jpg");
	}

	/**
	 * 浏览器查询 当前二维码状态
	 */
	@GetMapping("login/status")
	public Login getLoginStatus(String scene){
		QueryWrapper<Login> wrapper = new QueryWrapper<>();
		wrapper.eq("scene",scene);
		return loginService.getOne(wrapper);
	}

	/**
	 * 添加用户
	 * @param user 用户信息
	 */
	private void getUserInfo(User user){
		QueryWrapper<User> wrapper = new QueryWrapper<>();
		wrapper.eq("openid",user.getOpenid());
		User user1 = userService.getOne(wrapper);
		// 如果数据库已存在这个用户，则更新用户信息
		if(user1 == null){
			userService.save(user);
		}
		userService.update(user,wrapper);
	}

}
