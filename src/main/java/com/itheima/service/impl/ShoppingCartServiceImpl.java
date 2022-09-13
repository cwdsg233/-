package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.bean.ShoppingCart;
import com.itheima.dao.ShoppingCartDao;
import com.itheima.service.ShoppingCartService;
import com.itheima.utils.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartDao shoppingCartDao;

    /**
     * 加入购物车
     * 1. 加入购物车，不能直接认为就是简单的往表里面添加一条记录即可。
     * 2. 还需要判定，现在要添加的这个菜品 | 套餐，在购物车里面有还是没有。如果没有直接添加，如果有，就改变数量。
     * 2.1 先查询购物车表里面有没有这个菜|套餐
     * 2.2 处理使用菜品id 或者套餐id来查询之外，还要附加上 用户id来查询。
     * 3. 如果查询出来有数据 ， 把数量 修改一下： 执行更新操作。
     * 4. 如果查询出来没有数据，执行添加操作即可。
     *
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {

        //1. 先执行查询，看看购物车了里面：这个用户有没有添加过这件菜品|套餐
        LambdaUpdateWrapper<ShoppingCart> lqw = new LambdaUpdateWrapper<>();

        //1.1 添加条件：用户id
        lqw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        //1.2 添加条件：菜品id或者是套餐id
        // 因为不知道用户要添加的是菜品还是套餐，所以这里要判断！
        if (shoppingCart.getDishId() != null) {
            lqw.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            lqw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        //2. 执行查询操作
        ShoppingCart cartInDB = shoppingCartDao.selectOne(lqw);

        //3. 判断是否存在数据
        if (cartInDB != null) {
            //表明数据库里面有这件菜品 | 套餐
            cartInDB.setNumber(cartInDB.getNumber() + 1);

            //更新到数据库
            shoppingCartDao.updateById(cartInDB);

            return cartInDB;
        }

        //表明数据库里面没有这件菜品 | 套餐
        // 填充数据
        // 设置用户id
        shoppingCart.setUserId(BaseContext.getCurrentId());
        // 设置添加时间
        shoppingCart.setCreateTime(LocalDateTime.now());
        // 设置数量
        shoppingCart.setNumber(1);

        // 添加到数据库
        shoppingCartDao.insert(shoppingCart);
        return shoppingCart;


    }

    /**
     * 根据用户的id来查询该用户的购物车数据
     *
     * @return
     */
    @Override
    public List<ShoppingCart> list() {

        //1. 构建条件对象
        LambdaUpdateWrapper<ShoppingCart> lqw = new LambdaUpdateWrapper<>();

        //2. 设置条件
        lqw.eq(ShoppingCart::getUserId , BaseContext.getCurrentId());


        return shoppingCartDao.selectList(lqw);
    }

    /**
     * 根据用户的id来删除该用户的购物车数据
     *
     * @return
     */
    @Override
    public int clean() {
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId , BaseContext.getCurrentId());
        return shoppingCartDao.delete(lqw);
    }
}
