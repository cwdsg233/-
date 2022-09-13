package com.itheima.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.bean.Setmeal;
import com.itheima.common.PageParam;
import com.itheima.dto.SetmealDto;

import java.util.List;

public interface SetmealService {


    /**
     * 根据分类的id来查询套餐数据
     * @param categoryId
     * @return
     */
    List<Setmeal> findByCategoryId(long categoryId);

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    int add(SetmealDto setmealDto);

    /**
     * 套餐分页
     * @param pageParam
     * @return
     */
    IPage<SetmealDto> findPage(PageParam pageParam);


    /**
     * 删除套餐：不管是删除单个还是批量删除，都执行这个方法
     * @param ids
     * @return
     */
    int delete(List<Long> ids);

    /**
     * 根据分类id来查询套餐数据，并且只查询处于启售状态的套餐
     * @param categoryId
     * @param status
     * @return
     */
    List<Setmeal> findByCategoryId(Long categoryId , Integer status);
}
