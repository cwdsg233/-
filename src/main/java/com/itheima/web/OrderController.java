package com.itheima.web;

import com.itheima.bean.Orders;
import com.itheima.bean.User;
import com.itheima.common.R;
import com.itheima.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 提交订单
     * @param orders
     * @return
     */
    @PostMapping("/order/submit")
    public R add(@RequestBody Orders orders , HttpSession session){

        //获取用户对象
        User user = (User) session.getAttribute("user");

        int row = orderService.add(orders , user);
        if(row > 0 ){
            return R.success("提交订单成功！");
        }
        return R.error("提交订单失败！");
    }
}
