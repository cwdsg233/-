package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.bean.Category;
import com.itheima.bean.Dish;
import com.itheima.bean.Setmeal;
import com.itheima.common.PageParam;
import com.itheima.dao.CategoryDao;
import com.itheima.dto.DishDto;
import com.itheima.exception.CustomException;
import com.itheima.service.CategoryService;
import com.itheima.service.DishService;
import com.itheima.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryDao categoryDao;

    /**
     * 添加分类
     *
     * @param category
     * @return
     */
    @Override
    public int add(Category category) {
        return categoryDao.insert(category);
    }

    /**
     * 分页查询
     *
     * @param pageParam
     * @return
     */
    @Override
    public IPage<Category> findPage(PageParam pageParam) {

        //1. 构建分页条件对象
        IPage<Category> page  = new Page<>(pageParam.getPage() , pageParam.getPageSize());

        //2. 执行分页查询
        return categoryDao.selectPage(page , null);
    }

    /**
     * 删除分类
     *  1. 不能直接去删除分类表的数据，要考虑分类和菜品 、 套餐 关联的情况。
     *  2. 先拿着id去查询菜品表，看看是否有关联的数据，如果有，就禁止删除。
     *  3. 再拿着id去查询套餐表，看看是否有关联的数据，如果有，就禁止删除。
     *  4. 如果都没有关联的数据，那么就可以删除该分类了。
     * @param id
     * @return 1:表示正常删除， 0： 删除失败， -1 ： 有关联的菜品数据， -2：有关联的套餐数据
     */
    @Override
    public int delete(long id) {

        //1. 查询菜品表，看看是否有关联数据
        List<DishDto> dishList = dishService.findByCategoryId(id , null);
        if(dishList != null && dishList.size() > 0){
            //有关联的菜品数据，禁止删除！
            throw new CustomException("该分类有关联的菜品数据，禁止删除！");
        }

        //2. 查询套餐表，看看是否有关联数据
        List<Setmeal> setmealList = setmealService.findByCategoryId(id);
        if(setmealList != null && setmealList.size() > 0){
            // 有关联的套餐数据，禁止删除！
            throw new CustomException("该分类有关联的套餐数据，禁止删除！");
        }

        //3. 如果都没有，那么就直接删除即可。
        return categoryDao.deleteById(id);  // 1 :  0
    }

    /**
     * 更新分类
     *
     * @param category
     * @return
     */
    @Override
    public int update(Category category) {
        return categoryDao.updateById(category);
    }

    /**
     * 根据类型来查询分类数据
     *
     * @param type
     * @return
     */
    @Override
    public List<Category> findByType(Integer type) {

        //1. 构建条件对象
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();

        //1.1 设置条件
        lqw.eq(type != null , Category::getType , type);

        //2. 执行查询，返回结果
        return categoryDao.selectList(lqw);
    }

    /**
     * 根据主键找到分类数据
     *
     * @param id
     * @return
     */
    @Override
    public Category findById(long id) {
        return categoryDao.selectById(id);
    }
}
