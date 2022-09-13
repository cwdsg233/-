package com.itheima.service;

import com.itheima.bean.User;

public interface UserService {

    /**
     * 根据手机号码来查询用户
     * @param phone
     * @return
     */
    User findUser(String phone);

    /**
     * 添加用户
     * @param user
     * @return
     */
    int add(User user);
}
