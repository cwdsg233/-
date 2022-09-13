package com.itheima.bean;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    //@JsonSerialize(using = ToStringSerializer.class)
    private Long id;  //{"id":1287381638476826348, "username":"zhangsan" ,"status":1}

    private String username;


    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    //下面4个属性属于公共字段。也就是很多表都有这4个列，很多类里面都有这4个属性
    // 我们希望mybatisplus 在执行表的添加或者更新操作的时候，自动的给这4个属性赋值或者给其中的某些属性赋值。

    @TableField(fill = FieldFill.INSERT )
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT )
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
