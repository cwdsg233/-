package com.itheima.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mpi(){
        //1. 构建mybatisplus拦截器集合对象
        MybatisPlusInterceptor mpi = new MybatisPlusInterceptor();

        //2. 设置分页拦截器
        mpi.addInnerInterceptor(new PaginationInnerInterceptor());

        //3. 返回集合对象
        return mpi;
    }
}
