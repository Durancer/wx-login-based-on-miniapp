package com.xueyu.pojo.dto;


import com.baomidou.mybatisplus.annotation.TableId;
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
    @TableId
    Integer id;

    /**
     * 用户名
     */
    String nickname;

    /**
     * 用户唯一标识
     */
    String openid;

    /**
     * 用户头像
     */
    String avatar;

}
