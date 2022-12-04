package com.xueyu.pojo.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author durance
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("login")
public class Login {

	/**
	 * 自增id
	 */
	@TableId
	Integer id;

	/**
	 * 场景随机值
	 */
	String scene;

	/**
	 * 扫码状态
	 */
	String status;

	/**
	 * 用户id
	 */
	Integer userid;

}
