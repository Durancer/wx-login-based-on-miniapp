package com.xueyu.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author durance
 */
@Configuration
@ConfigurationProperties(prefix = "login")
public class WxLoginContant {

	/**
	 * 小程序appId
	 */
	public static String appId;

	/**
	 * 小程序密钥
	 */
	public static String secret;

	/**
	 * 小程序授权登录页面
	 */
	public static String authpage;

	public void setAppId(String appId) {
		WxLoginContant.appId = appId;
	}

	public void setSecret(String secret) {
		WxLoginContant.secret = secret;
	}

	public void setAuthpage(String authpage) {
		WxLoginContant.authpage = authpage;
	}

}
