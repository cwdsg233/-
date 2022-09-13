package com.itheima.service;

import com.itheima.bean.Orders;
import com.itheima.bean.User;

public interface OrderService {

    /**
     * 添加订单
     * @param orders
     * @param user
     * @return
     */
    int add(Orders orders , User user );
}
