package com.itheima.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.bean.Dish;
import com.itheima.common.PageParam;
import com.itheima.dto.DishDto;

import java.util.List;

public interface DishService {


    /**
     * 根据分类的id来查询菜品数据
     * @param categoryId
     * @return
     */
    List<DishDto> findByCategoryId(long categoryId , Integer status );

    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    int add(DishDto dishDto);

    /**
     * 菜品分页
     * @param pageParam
     * @return
     */
    IPage<DishDto> findPage(PageParam pageParam);


    /**
     * 根据id查询菜品数据
     * @param id
     * @return
     */
    DishDto findById(long id);

    /**
     * 更新菜品
     * @param dishDto
     * @return
     */
    int update(DishDto dishDto);

}
