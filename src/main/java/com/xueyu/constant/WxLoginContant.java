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

	public void setAppId(String APPID) {
		appId = APPID;
	}

	public void setSecret(String SECRET) {
		secret = SECRET;
	}

}
