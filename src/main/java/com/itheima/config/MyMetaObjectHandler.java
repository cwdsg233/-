package com.itheima.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.itheima.bean.Employee;
import com.itheima.utils.BaseContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    //@Autowired
    //private HttpSession session;


    /**
     * 插入元对象字段填充（用于插入时对公共字段的填充）
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        System.out.println("来调用insertFill 填充数据了~！");
        System.out.println("MyMetaObjectHandler.insertFill=======" + Thread.currentThread().getId());

        //告诉mybatisplus 什么字段，赋什么值。
        //  参数一：具体的字段名字，名字不能乱写，必须是属性的名字
        //  参数二： 具体赋的值
        metaObject.setValue("createTime" , LocalDateTime.now());
        metaObject.setValue("updateTime" , LocalDateTime.now());

        //从session对象里面取曾经登录的时候，保存的员工对象
        //Employee e = (Employee) session.getAttribute("employee");

        //metaObject.setValue("createUser" , e.getId());
        //metaObject.setValue("updateUser" , e.getId());

        metaObject.setValue("createUser" , BaseContext.getCurrentId());
        metaObject.setValue("updateUser" , BaseContext.getCurrentId());

    }

    /**
     * 更新元对象字段填充（用于更新时对公共字段的填充）
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        System.out.println("来调用updateFill 填充数据了~！");

        //从session对象里面取曾经登录的时候，保存的员工对象
        //Employee e = (Employee) session.getAttribute("employee");

        //更新的时候，填充值。
        metaObject.setValue("updateTime" , LocalDateTime.now());
        //metaObject.setValue("updateUser" , e.getId());

        metaObject.setValue("updateUser" , BaseContext.getCurrentId());
    }
}
