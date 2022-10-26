package com.xueyu.pojo.dto;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author durance
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class User {

    /**
     * 用户id
     */
    private Integer id;

    /**
     * 用户名
     */
    private String nickname;

    /**
     * 用户唯一标识
     */
    private String openid;

    /**
     * 用户头像
     */
    private String avatar;

}
