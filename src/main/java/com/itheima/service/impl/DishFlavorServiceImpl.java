package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.bean.DishFlavor;
import com.itheima.dao.DishFlavorDao;
import com.itheima.service.DishFlavorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DishFlavorServiceImpl implements DishFlavorService {

    @Autowired
    private DishFlavorDao dishFlavorDao;

    /**
     * 添加口味
     *
     * @param dishFlavor
     * @return
     */
    @Override
    public int add(DishFlavor dishFlavor) {
        return dishFlavorDao.insert(dishFlavor);
    }

    /**
     * 根据菜品id，查询菜品的口味数据
     *
     * @param dishId
     * @return
     */
    @Override
    public List<DishFlavor> findByDishId(long dishId) {

        //1. 构建条件对象
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();

        //1.1 设置条件
        lqw.eq(DishFlavor::getDishId , dishId);

        //2. 执行查询
        return dishFlavorDao.selectList(lqw);
    }

    /**
     * 根据菜品id，删除该菜品的口味数据
     *
     * @param dishId
     * @return
     */
    @Override
    public int deleteByDishId(long dishId) {

        //1. 构建条件对象
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();

        //1.1 设置条件
        lqw.eq(DishFlavor::getDishId , dishId);

        //2. 执行删除
        return dishFlavorDao.delete(lqw);
    }
}
