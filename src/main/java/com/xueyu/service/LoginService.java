package com.xueyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xueyu.pojo.dto.Login;

/**
 * @author durance
 */
public interface LoginService extends IService<Login> {

	/**
	 * 请求创建文件并返回文件名
	 *
	 * @return 返回scene值，作为网站做剩余操作的依据
	 */
	String createAndGetQrcodeFile();
}
