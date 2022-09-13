package com.itheima.web;

import com.itheima.bean.ShoppingCart;
import com.itheima.common.R;
import com.itheima.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/shoppingCart")
@RestController
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R add(@RequestBody ShoppingCart shoppingCart){
        ShoppingCart cart = shoppingCartService.add(shoppingCart);
        return R.success(cart);
    }

    /**
     * 查询购物车
     * @return
     */
    @GetMapping("/list")
    public R list(){
        List<ShoppingCart> list = shoppingCartService.list();
        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R clean(){
        int row = shoppingCartService.clean();
        if(row > 0 ){
            return R .success("清空购物车成功！");
        }
        return R .error("清空购物车事变！");
    }
}
