package com.itheima.utils;

// 这是ThreadLocal的包装类
public class BaseContext {


    //1. 创建ThreadLocal的对象
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    //2. 定义存数据的方法
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    //3. 定义取数据的方法
    public static Long  getCurrentId(){
        return threadLocal.get();
    }
}
