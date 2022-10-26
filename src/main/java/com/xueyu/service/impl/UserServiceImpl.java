package com.xueyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xueyu.dao.UserMapper;
import com.xueyu.pojo.dto.User;
import com.xueyu.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author durance
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
