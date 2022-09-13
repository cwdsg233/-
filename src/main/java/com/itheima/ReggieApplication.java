package com.itheima;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;

@ServletComponentScan // 加上这个注解之后，SpringBoot就认识了以前我们写的Filter和Servlet的注解了
@MapperScan("com.itheima.dao")
@SpringBootApplication
//spring cache使用环境搭建 第三步 开启spring cache缓存功能
@EnableCaching
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class , args);
    }
}
