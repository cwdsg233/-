package com.itheima.service;

import com.itheima.bean.DishFlavor;

import java.util.List;

public interface DishFlavorService {

    /**
     * 添加口味
     * @param dishFlavor
     * @return
     */
    int add(DishFlavor dishFlavor);

    /**
     * 根据菜品id，查询菜品的口味数据
     * @param dishId
     * @return
     */
    List<DishFlavor>  findByDishId(long dishId);

    /**
     * 根据菜品id，删除该菜品的口味数据
     * @param dishId
     * @return
     */
    int deleteByDishId(long dishId);
}
