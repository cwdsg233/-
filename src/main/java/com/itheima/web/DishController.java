package com.itheima.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.bean.Dish;
import com.itheima.common.PageParam;
import com.itheima.common.R;
import com.itheima.dao.DishDao;
import com.itheima.dto.DishDto;
import com.itheima.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/dish")
@RestController
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R add(@RequestBody DishDto dishDto){

        //1. 调用service
        int row = dishService.add(dishDto);

        //2. 返回结果
        if(row > 0){
            return R.success("添加菜品成功！");
        }

        return R.error("添加菜品失败！");
    }

    /**
     * 菜品分页展示
     * @param pageParam
     * @return
     */
    @GetMapping("/page")
    public R findPage(PageParam pageParam){

        //1. 调用service
        IPage<DishDto> page = dishService.findPage(pageParam);

        //2. 返回结果
        return R.success(page);
    }


    /**
     * 根据id查询菜品数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R findById(@PathVariable long id){

        DishDto dishDto = dishService.findById(id);
        return R.success(dishDto);

    }

    /**
     * 更新菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R update(@RequestBody DishDto dishDto){

        int row = dishService.update(dishDto);
        if(row > 0){
            return R.success("更新成功！");
        }
        return R.error("更新失败！");

    }

    /**
     * 根据分类id查询菜品数据
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public R findByCategoryId(long categoryId , Integer status ){
        List<DishDto> dishList = dishService.findByCategoryId(categoryId , status);
        return R.success(dishList);
    }
}
