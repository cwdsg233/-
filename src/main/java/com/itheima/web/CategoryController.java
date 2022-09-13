package com.itheima.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.bean.Category;
import com.itheima.common.PageParam;
import com.itheima.common.R;
import com.itheima.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/category")
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加分类
     * @param category
     * @return
     */
    @PostMapping
    public R add(@RequestBody Category category){

        //1. 调用service
        int row = categoryService.add(category);

        //2. 判断返回结果
        if(row > 0 ){
            return R.success("添加成功！");
        }
        return R.error("添加失败！");
    }

    /**
     * 分页查询
     * @param pageParam
     * @return
     */
    @GetMapping("/page")
    public R findPage( PageParam pageParam){
        IPage<Category> page = categoryService.findPage(pageParam);
        return R.success(page);
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public R delete(long id){

        //1. 调用service
        int row = categoryService.delete(id);

        //2. 判断返回结果
        if(row > 0){
            return R.success("删除分类成功！");
        }
        return R.error("删除分类失败！");

    }

    /**
     * 更新分类
     * @param category
     * @return
     */
    @PutMapping
    public R update(@RequestBody Category category){

        //1. 调用service
        int row = categoryService.update(category);

        //2. 返回结果
        if(row > 0){
            return R.success("更新分类成功！");
        }

        return R.error("更新分类失败！");
    }


    /**
     * 根据类型查询分类数据
     * @param type
     * @return
     */
    @GetMapping("/list")
    public R findByType(Integer type){
        List<Category> list = categoryService.findByType(type);
        return R.success(list);
    }
}
