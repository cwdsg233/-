package com.itheima.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.bean.Category;
import com.itheima.common.PageParam;

import java.util.List;

public interface CategoryService {

    /**
     * 添加分类
     * @param category
     * @return
     */
    int add(Category category);

    /**
     * 分页查询
     * @param pageParam
     * @return
     */
    IPage<Category> findPage(PageParam pageParam);

    /**
     * 删除分类
     * @param id
     * @return
     */
    int delete(long id);

    /**
     * 更新分类
     * @param category
     * @return
     */
    int update(Category category);

    /**
     * 根据类型来查询分类数据
     * @param type
     * @return
     */
    List<Category> findByType(Integer type);

    /**
     * 根据主键找到分类数据
     * @param id
     * @return
     */
    Category findById(long id);
}
