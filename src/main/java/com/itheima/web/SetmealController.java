package com.itheima.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.bean.Setmeal;
import com.itheima.common.PageParam;
import com.itheima.common.R;
import com.itheima.dto.SetmealDto;
import com.itheima.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/setmeal")
@RestController
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 添加套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R add(@RequestBody SetmealDto setmealDto){

        int row = setmealService.add(setmealDto);

        if(row > 0){
            return R.success("添加套餐成功！");
        }
        return R.error("添加套餐失败！");

    }

    /**
     * 套餐分页
     * @param pageParam
     * @return
     */
    @GetMapping("/page")
    public R findPage(PageParam pageParam){

        IPage<SetmealDto> page = setmealService.findPage(pageParam);
        return R.success(page);

    }


    /**
     * 删除套餐： 不管是删除单个还是批量删除，都访问这个方法！
     * @param ids
     * @return
     */
    @DeleteMapping
    public R delete(@RequestParam List<Long> ids){

        int row = setmealService.delete(ids);

        if(row > 0){
            return R.success("删除套餐成功！");
        }
        return R.error("删除套餐失败！");

    }

    /**
     * 根据类型来查询套餐，并且只查询处于启售状态的套餐。
     * @param categoryId
     * @param status
     * @return
     */
    @GetMapping("/list")
    public R findByCategoryId(Long categoryId , Integer status){
        List<Setmeal> setmealList = setmealService.findByCategoryId(categoryId, status);
        return R.success(setmealList);

    }
}
