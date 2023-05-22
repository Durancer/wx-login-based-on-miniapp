package com.xueyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xueyu.pojo.dto.Login;

/**
 * @author durance
 */
public interface LoginService extends IService<Login> {

	/**
	 * 请求创建图片文件并返回文件名
	 * 有一定侵入性，需要有对应的sql数据库表
	 *
	 * @return 返回scene值，作为网站做剩余操作的依据
	 */
	String saveQrcodeFileAndData();

	/**
	 * 创建扫码图片，无侵入式，保存图片后返回图片文件名
	 * 无额外进行任何业务逻辑
	 *
	 * @return 返回扫码图片名称
	 */
	String saveQrcodeFile();

}
