package com.itheima.service.impl;

import com.itheima.bean.SetmealDish;
import com.itheima.dao.SetmealDishDao;
import com.itheima.service.SetmealDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SetmealDishServiceImpl implements SetmealDishService {

    @Autowired
    private SetmealDishDao setmealDishDao;

    /**
     * 添加套餐菜品数据
     *
     * @param setmealDish
     * @return
     */
    @Override
    public int add(SetmealDish setmealDish) {
        return setmealDishDao.insert(setmealDish);
    }
}
