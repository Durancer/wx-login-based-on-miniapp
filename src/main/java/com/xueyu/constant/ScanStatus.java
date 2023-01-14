package com.xueyu.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录扫码状态
 *
 * @author durance
 */
@AllArgsConstructor
@Getter
public enum ScanStatus {

	/**
	 * 等待扫码
	 */
	WAIT("wait"),

	/**
	 * 已扫码
	 */
	ING("ing"),

	/**
	 * 认证成功
	 */
	SUCCESS("success"),

	/**
	 * 取消认证
	 */
	CANCEL("cancel");

	/**
	 * 扫码状态
	 */
	private String status;

}
