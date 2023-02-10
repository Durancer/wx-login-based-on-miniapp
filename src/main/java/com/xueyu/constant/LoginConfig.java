package com.xueyu.constant;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author durancec
 */
@Configuration
@ConfigurationProperties(prefix = "login")
@Data
@NoArgsConstructor
public class LoginConfig {

	/**
	 * 小程序appId
	 */
	String appId;

	/**
	 * 小程序密钥
	 */
	String secret;

}
