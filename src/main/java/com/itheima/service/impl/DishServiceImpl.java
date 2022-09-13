package com.itheima.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.bean.Category;
import com.itheima.bean.Dish;
import com.itheima.bean.DishFlavor;
import com.itheima.common.PageParam;
import com.itheima.dao.DishDao;
import com.itheima.dto.DishDto;
import com.itheima.service.CategoryService;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishDao dishDao;

    //缓存菜品数据 第一步：注入RedisTemplate对象
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类的id来查询菜品数据
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<DishDto> findByCategoryId(long categoryId , Integer status) {
        //缓存菜品数据 第二步a.查询redis看缓存中是否有此菜品分类的缓存数据
        //注意：将集合的数据转为string存入redis的
        String key = "DISH_"+categoryId+status;
        String dishListStr = (String)redisTemplate.opsForValue().get(key);
        // 缓存菜品数据 第三步 b.假设缓存中有数据，直接返回
        if(StringUtils.isNotEmpty(dishListStr)){
            log.debug("****************查询redis数据****************");
            //注意：将string转为List
            return JSON.parseObject(dishListStr,List.class);
        }

        //1. 构建条件对象
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();

        //1.1 设置条件
        lqw.eq(Dish::getCategoryId , categoryId);
        lqw.eq(status != null , Dish::getStatus , status);

        // 缓存菜品数据 第四步 c.假设缓存中没有数据，则查询数据库表
        //2. 执行查询
        List<Dish> dishList = dishDao.selectList(lqw);

        //3. 把List<Dish> ----> List<DishDto>
        List<DishDto> dishDtoList = dishList.stream().map(dish -> {
            //3.1 构建DishDto
            DishDto dto = new DishDto();

            //3.2 把菜品的数据拷贝到dto来
            BeanUtils.copyProperties(dish, dto);

            //3.2 给dto设置口味数据
            List<DishFlavor> flavorList = dishFlavorService.findByDishId(dish.getId());
            dto.setFlavors(flavorList);

            return dto;
        }).collect(Collectors.toList());

        // 缓存菜品数据 第五步 d.将数据库表的最新数据，存入缓存中
        //注意：将list转为string
        redisTemplate.opsForValue().set(key,JSON.toJSONString(dishDtoList));
        log.debug("****************查询mysql数据****************");
        return dishDtoList;
    }

    /**
     * 添加菜品
     *   1. dishDto包含 菜品数据和口味数据
     *   2. 菜品数据是放在菜品表【dish】 , 口味数据是放在口味表 【dish_flavor】
     *   3. 添加菜品其实就是把数据分别放到这两张表里面去。
     *      3.1 先添加 菜品 表
     *
     *      3.2 再添加 口味 表
     *
     * @param dishDto
     * @return
     */
    @Override
    public int add(DishDto dishDto) {
        //1. 添加到菜品表
        int row1 = dishDao.insert(dishDto);

        //2. 添加到口味表
        //2.1 获取菜品的口味数据
        List<DishFlavor> flavorList = dishDto.getFlavors();

        int row2 = 0 ;
        //确保有口味数据，才去执行口味表的添加操作。
        if(flavorList.size() > 0){

            //2.2 遍历集合，得到每一个口味数据
            for (DishFlavor dishFlavor : flavorList) {

                //2.3 设置口味属于哪一个菜品
                dishFlavor.setDishId(dishDto.getId());

                //2.4 添加到口味表里面
                row2 += dishFlavorService.add(dishFlavor);

            }
        }
        //当保存某个菜品分类的下菜品数据时候，需要删除此菜品分类的数据
        String key = "DISH_"+dishDto.getCategoryId()+dishDto.getStatus();
        redisTemplate.delete(key);
        return row1 > 0 && row2 ==flavorList.size() ? 1 : 0 ;
    }

    /**
     * 菜品分页
     *  1. 由于在页面上展示菜品分页数据的时候，除了要展示菜品数据，还需要展示菜品所属的分类数据。
     *  2. 但是直接查询菜品表，是不会得到分类的名字，只得到分类的id值。
     *  3. 应该拿着分类的id去查询分类表，得到分类的数据。
     *  4. 那么现在面临一个问题：
     *      4.1 分页要查询两张表： 菜品表，分类表
     *      4.2 查询菜品表会得到菜品数据，使用Dish对象来封装
     *      4.3 查询分类表会得到分类数据，使用Category对象来封装。
     *      4.4 需要从Category对象里面拿出来分类的名字 +  Dish对象，就可以给页面展示出来一条完整的数据。
     *      4.5 此时需要用DishDto来封装数据返回了。也就是说： 一会Service的返回值不能是 IPage<Dish>
     *          而是变成IPage<DishDto>
     *
     * @param pageParam
     * @return
     */
    @Override
    public IPage<DishDto> findPage(PageParam pageParam) {

        //1. 定义分页对象
        IPage<Dish> page = new Page<>(pageParam.getPage() , pageParam.getPageSize());

        //2. 构建条件对象
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();

        //2.1 设置条件
        lqw.like(pageParam.getName() != null , Dish::getName , pageParam.getName());

        //3. 执行查询 :: 不能直接返回这个对象，因为它里面没有分类的名称！
        IPage<Dish> dishPage = dishDao.selectPage(page, lqw);

        //IPage<Dish>
        //IPage :: total   :: long----> 总记录数 :20
        //IPage :: records  :: List<Dish>----> 当前页的集合数据

        //IPage<DishDto>
        //IPage :: total   :: long----> 总记录数 :20
        //IPage :: records  :: List<DishDto>----> 当前页的集合数据

        //4. 构建IPage<DishDto>
        IPage<DishDto> dishDtoPage = new Page<>();

        //4.1 设置总记录数
        dishDtoPage.setTotal(dishPage.getTotal());

        //4.2 构建出来List<DishDto> ， 才可以装到dishDtoPage里面
        List<DishDto> dtoList = dishPage.getRecords().stream().map(dish -> {

            //4.2.1 每一个Dish就要映射成一个DishDto
            DishDto dto = new DishDto();

            //4.2.2 把dish的数据拷贝到DishDto里面去
            BeanUtils.copyProperties(dish, dto);

            //4.2.3 设置分类的名称
            Category category = categoryService.findById(dish.getCategoryId());
            dto.setCategoryName(category.getName());

            //4.2.4 返回映射的对象
            return dto;
        }).collect(Collectors.toList());

        //设置集合数据
        dishDtoPage.setRecords(dtoList);

        return dishDtoPage;
    }

    /**
     * 根据id查询菜品数据
     *
     * @param id
     * @return
     */
    @Override
    public DishDto findById(long id) {

        //1. 先查询dish数据
        Dish dish = dishDao.selectById(id);

        //2. 再查询分类数据
        Category category = categoryService.findById(dish.getCategoryId());

        //3. 最后查询口味数据
        List<DishFlavor> flavorList = dishFlavorService.findByDishId(dish.getId());

        //4. 构建dishDto
        DishDto dishDto = new DishDto();

        //4.1 封装dish数据到dto里面去
        BeanUtils.copyProperties(dish , dishDto);

        //4.2 封装分类数据到dto里面去
        dishDto.setCategoryName(category.getName());

        //4.3 封装口味数据到dto里面去
        dishDto.setFlavors(flavorList);

        return dishDto;
    }

    /**
     * 更新菜品
     *  1. 更新菜品和以前的更新不一样，因为它要操作两张表： 菜品表（dish） 和 口味表(dish_flavor)
     *  2. 先更新菜品表
     *      2.1 很简单，就按照原来的更新操作来执行即可
     *
     *  3. 再更新口味表
     *      3.1 由于口味的更新的组合太多了，需要去计较的种类太多，太复杂。
     *      3.2 所以可以把口味表里面的原始口味数据删除掉 ，然后再把页面传递过来的口味数据添加进去。
     *
     *
     * @param dishDto
     * @return
     */
    @Override
    public int update(DishDto dishDto) {
        //1. 先更新菜品表
        int row1 = dishDao.updateById(dishDto);

        //2. 再更新口味表
        //2.1 先把原来的口味删除掉
        dishFlavorService.deleteByDishId(dishDto.getId());

        //2.2 再把页面传递过来的口味数据添加到表里面
        //2.2.1 从DishDto里面拿出来口味数据
        List<DishFlavor> flavorList = dishDto.getFlavors();

        //2.2.2 判断是否有口味数据，如果有，就遍历，然后添加到口味表
        int row2 = 0 ;
        if(flavorList.size() > 0){
            for (DishFlavor dishFlavor : flavorList) {

                //设置口味数据属于哪个菜品
                dishFlavor.setDishId(dishDto.getId());

                row2 += dishFlavorService.add(dishFlavor);

            }
        }
        return row1 > 0 && row2 == flavorList.size() ? 1 : 0;
    }
}