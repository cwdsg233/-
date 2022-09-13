package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.bean.User;
import com.itheima.dao.UserDao;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    /**
     * 根据手机号码来查询用户
     *
     * @param phone
     * @return
     */
    @Override
    public User findUser(String phone) {
        LambdaUpdateWrapper<User> lqw = new LambdaUpdateWrapper<>();
        lqw.eq(User::getPhone , phone);
        return userDao.selectOne(lqw);
    }

    /**
     * 添加用户
     *
     * @param user
     * @return
     */
    @Override
    public int add(User user) {
        return userDao.insert(user);
    }
}
