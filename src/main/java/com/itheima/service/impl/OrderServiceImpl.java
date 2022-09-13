package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.itheima.bean.*;
import com.itheima.dao.OrderDao;
import com.itheima.dao.OrderDetailDao;
import com.itheima.service.AddressBookService;
import com.itheima.service.OrderService;
import com.itheima.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加订单
     *  1. 添加订单是要往两张表里面添加数据： 订单表 和 订单详情表
     *
     *  2. 先往订单表添加数据
     *
     *  3. 再往订单详情表添加数据
     *
     *      3.1 要记得设置订单详情属于哪一个订单。
     *
     * @param orders
     * @return
     */
    @Override
    public int add(Orders orders , User user) {

        //1. 准备订单数据
        orders = prepareOrder(orders , user);

        //2. 添加到订单表
        int row1 = orderDao.insert(orders);

        //3. 添加到订单详情表:: 订单详情的数据是来自于购物车的数据
        List<ShoppingCart> cartList = shoppingCartService.list();

        int row2 = 0 ;

        //3.1 遍历购物车数据，取出每一件菜品 | 套餐
        for (ShoppingCart shoppingCart : cartList) {

            //3.2 一件购物车对象 转化成 一个 订单详情对象
            OrderDetail orderDetail = new OrderDetail();

            //3.3 设置数据
            BeanUtils.copyProperties(shoppingCart,orderDetail);

            //3.4 设置订单详情属于哪个订单的。
            orderDetail.setOrderId(orders.getId());

            //3.5 添加到订单详情表
            row2 += orderDetailDao.insert(orderDetail);
        }

        if(row1 > 0 && row2 == cartList.size()){
            //添加成功, 就清空购物车数据
            shoppingCartService.clean();
            return 1 ;
        }

        return  0;
    }

    public Orders prepareOrder(Orders orders , User user){
        //orders.setId(IdWorker.getId());
        //1. 补充数据
        //orders.setNumber(orders.getId()+"");
        //1.1 订单编号
        orders.setNumber(UUID.randomUUID().toString().replace("-" , ""));
        //1.2 订单状态
        orders.setStatus(2); // 1. 未付款，2已付款，3.已派送 ，4. 已完成，5. 已取消
        //1.3  下单用户的用户id
        orders.setUserId(user.getId());
        //1.4 地址簿id:: 不需要设置，页面已经提供了
        //1.5 订单时间
        orders.setOrderTime(LocalDateTime.now());
        //1.6 支付时间
        orders.setCheckoutTime(LocalDateTime.now());
        //1.7 支付方式 : 1. 微信支付， 2. 支付宝支付
        orders.setPayMethod(1);
        //1.8 总价:: 不需要设置，页面已经提供了。
        //1.9 备注:: 不需要设置，页面已经提供了。


        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());

        //1.10 收货电话
        orders.setPhone(addressBook.getPhone());

        //1.11 收货地址
        String address = (addressBook.getProvinceName() != null ? addressBook.getProvinceName() : "") +
                (addressBook.getCityName() != null ? addressBook.getCityName() : "") +
                (addressBook.getDistrictName() !=null ? addressBook.getDistrictName() : "") +
                (addressBook.getDetail() != null  ? addressBook.getDetail()  : "");
        orders.setAddress(address);
        //1.12 下单用户的用户名
        orders.setUserName(user.getName());
        //1.13 收货人
        orders.setConsignee(addressBook.getConsignee());

        return orders;
    }
}
