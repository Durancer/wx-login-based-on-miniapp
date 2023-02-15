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
	public static String addId;

	/**
	 * 小程序密钥
	 */
	public static String secret;

	public static void setAddId(String addId) {
		WxLoginContant.addId = addId;
	}

	public static void setSecret(String secret) {
		WxLoginContant.secret = secret;
	}

}
