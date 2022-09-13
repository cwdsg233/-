package com.itheima.service;

import com.itheima.bean.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 加入购物车
     * @param shoppingCart
     * @return
     */
    ShoppingCart add(ShoppingCart shoppingCart);

    /**
     * 根据用户的id来查询该用户的购物车数据
     * @return
     */
    List<ShoppingCart> list();

    /**
     * 根据用户的id来删除该用户的购物车数据
     * @return
     */
    int clean();
}
