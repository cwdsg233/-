package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.bean.Category;
import com.itheima.bean.Setmeal;
import com.itheima.bean.SetmealDish;
import com.itheima.common.PageParam;
import com.itheima.dao.SetmealDao;
import com.itheima.dto.SetmealDto;
import com.itheima.exception.CustomException;
import com.itheima.service.CategoryService;
import com.itheima.service.SetmealDishService;
import com.itheima.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDao setmealDao;

    /**
     * 根据分类的id来查询套餐数据
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Setmeal> findByCategoryId(long categoryId) {

        //1. 构建条件对象
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();

        //1.1 设置条件
        lqw.eq(Setmeal::getCategoryId , categoryId);

        //2. 执行查询，返回结果
        return setmealDao.selectList(lqw);

    }

    /**
     * 新增套餐
     *  1. 套餐数据包含两部分： 套餐本身的数据 和 套餐使用的菜品数据
     *  2. 需要往两张表里面添加数据： 套餐数据就保存到套餐表（setmeal） ，套餐菜品数据就放到 套餐菜品表 (setmeal_dish)
     *  3. 先添加套餐表
     *
     *  4. 再添加套餐菜品表
     * @param setmealDto
     * @return
     */
    @Override
    //@CacheEvict:清除环境
    //value = "setmeal" 跟存入缓存value要一致
    // allEntries = true 将setmeal下所有缓存数据清除
    @CacheEvict(value = "setmeal",allEntries = true)
    public int add(SetmealDto setmealDto) {
        log.debug("************新增套餐了************");
        //1. 添加套餐表
        int row1 = setmealDao.insert(setmealDto);

        //2. 添加套餐菜品表

        //2.1 获取该套餐都有什么菜
        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();

        int row2 = 0 ;

        //2.2 遍历，把每一种菜的数据，都添加到套餐菜品表里面去
        if(setmealDishList.size() > 0 ){
            for (SetmealDish setmealDish : setmealDishList) {

                //2.2.1 设置菜品属于哪一种套餐
                setmealDish.setSetmealId(setmealDto.getId());

                //2.2.2 添加菜品数据
                row2 += setmealDishService.add(setmealDish);
            }
        }


        return row1 >0 && row2 == setmealDishList.size() ? 1 : 0;
    }

    /**
     * 套餐分页
     *
     * @param pageParam
     * @return
     */
    @Override
    public IPage<SetmealDto> findPage(PageParam pageParam) {

        //1. 构建分页对象
        IPage<Setmeal> setmealIPage = new Page<>(pageParam.getPage() , pageParam.getPageSize());

        //2. 构建条件对象
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();

        //2.1 追加条件
        lqw.like(pageParam.getName() != null , Setmeal::getName , pageParam.getName());

        //3. 执行分页查询
        IPage<Setmeal> setmealPage = setmealDao.selectPage(setmealIPage, lqw);

        /*
            1. 不能直接返回setmealPage对象，因为它里面只有setmeal套餐的数据，没有分类的数据。
            2. 需要去查询分类的数据，和setmeal套餐的数据 绑定到一起，形成一个DTO对象，返回才行。

            IPage<Setmeal> setmealPage :
                    total:  总记录数 ---- long
                    records : 当前页的集合数据 ---- List<Steaml>

            IPage<SetmealDto> setmealDtoPage:
                    total:  总记录数 ---- long
                    records : 当前页的集合数据 ---- List<SteamlDto>

         */

        //4. 构建IPage<SetmealDto>
        IPage<SetmealDto> dtoPage = new Page<>();

        //4.1 组装total值
        dtoPage.setTotal(setmealPage.getTotal());

        //4.2 组装records值

        //4.2.1 得到原始的records ，然后遍历，映射成dto对象，最后组装成新的List集合
        List<SetmealDto> dtoList = setmealPage.getRecords().stream().map(setmeal -> {

            //a. 构建新的Dto对象
            SetmealDto dto = new SetmealDto();

            //b. 把setmeal的数据拷贝到dto来
            BeanUtils.copyProperties(setmeal, dto);

            //c. 把分类的数据设置到dto里面来
            Category category = categoryService.findById(setmeal.getCategoryId());
            dto.setCategoryName(category.getName());

            return dto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dtoList);

        return dtoPage;

    }

    /**
     * 删除套餐：不管是删除单个还是批量删除，都执行这个方法
     *  1. 删除套餐，有要求：
     *      1.1 如果套餐处于启售状态，那么禁止删除。
     *      1.2 只有套餐处于停售状态，才允许删除。
     *
     * @param ids
     * @return
     */
    @Override
    @CacheEvict(value = "setmeal",allEntries = true)
    public int delete(List<Long> ids) {  // 1, 3, 5
        log.debug("************删除套餐了************");

        //1. 先检查有没有哪个套餐处于启售状态，如果有，禁止删除
        //1.1 构建条件对象 : select count(*) from setmeal where status = 1 and id in(1,3,5);
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();

        //1.2 设置状态的条件 select count(*) from setmeal where status = 1
        lqw.eq(Setmeal::getStatus , 1);

        // select count(*) from setmeal where status = 1 and id in(1,3,5);
        lqw.in(Setmeal::getId, ids);

        //1.3 执行查询。
        Long total = setmealDao.selectCount(lqw);

        //1.4 判定结果
        if(total > 0){
            throw new CustomException("有套餐处于启售状态，禁止删除！");
        }
        //2. 如果都处于停售状态，那么就直接删除即可！
        int row = setmealDao.deleteBatchIds(ids);
        return row == ids.size() ? 1 : 0 ;
    }

    /**
     * 根据分类id来查询套餐数据，并且只查询处于启售状态的套餐
     *
     * @param categoryId
     * @param status
     * @return
     */
    @Override
    //使用@Cacheable缓存数据 key=通过@Cacheable注解定义的 value=返回值List<Setmeal>
    //value = "setmeal":当前缓存的数据类型
    //key=分类id_1
    //key语法：#参数名称获取参数值作为key(例如：#categoryId)
    //key语法：#对象.对象中属性名称 获取参数值作为key （例如：#pageParam.page）
    @Cacheable(value = "setmeal",key = "#categoryId +'_'+#status ")
    public List<Setmeal> findByCategoryId(Long categoryId, Integer status) {
        log.debug("************从mysql查询套餐数据了************");
        LambdaUpdateWrapper<Setmeal> lqw = new LambdaUpdateWrapper<>();
        lqw.eq(Setmeal::getCategoryId , categoryId);
        lqw.eq(Setmeal::getStatus , status);
        return setmealDao.selectList(lqw);
    }
}
