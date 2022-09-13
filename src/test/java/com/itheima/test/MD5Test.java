package com.itheima.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

@SpringBootTest
public class MD5Test {

    @Test
    public void test01(){

        String str = DigestUtils.md5DigestAsHex("123456".getBytes());
        System.out.println("str = " + str);


    }
}
