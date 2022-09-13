package com.itheima.exception;

import com.itheima.common.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 处理数据库 唯一性约束的异常
     * @param e
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e){

        //把异常的信息，打印到控制台上。
        e.printStackTrace();

        //获取异常的消息
        String msg = e.getMessage();

        //重复存在的问题
        if(msg.contains("Duplicate entry")){
            //Duplicate entry 'admin' for key 'idx_username'
            String value = msg.split(" ")[2];
            return R.error(value + " , 已存在！");
        }
        return R.error("未知错误！");
    }


    /**
     * 处理我们自己跑出来的异常
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R handleCustomException(CustomException e){
        e.printStackTrace();
        return R.error(e.getMessage());
    }

}
