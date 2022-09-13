package com.itheima.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.bean.Employee;
import com.itheima.common.PageParam;
import com.itheima.common.R;
import com.itheima.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@RequestMapping("/employee")
@RestController
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param employee
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody Employee employee, HttpSession session) {

        log.debug("********************开始登录啦********************");
        //1. 调用service
        Employee loginEmployee = employeeService.login(employee);

        //2. 返回结果
        if (loginEmployee != null) {

            //2.1 登录成功！

            //2.1.1 把登录查询出来的员工对象保存到session作用域，以便后面在会话里面使用
            session.setAttribute("employee", loginEmployee);

            //2.1.2 给客户端响应 ,把 登录查询的对象，返回给前端。
            return R.success(loginEmployee);
        }

        //如果来到这里，即表明登录失败！
        return R.error("用户名或者密码错误！");
    }

    /**
     * 退出登录
     *
     * @return
     */
    @PostMapping("/logout")
    public R logout(HttpSession session) {

        //1. 让session失效：
        session.invalidate();

        return R.success("退出成功！");
    }


    /**
     * 添加员工
     *
     * @param employee
     * @return
     */
    @PostMapping
    public R add(@RequestBody Employee employee) {

        System.out.println("EmployeeController.add()=======" + Thread.currentThread().getId());

        //0. 补充数据

        //设置初始密码是123456
        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);

        //设置创建时间和更新时间都是现在
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //设置是哪个用户完成的这个创建和更新的工作。谁登录就是谁负责的。
        //Employee loginEmployee = (Employee) session.getAttribute("employee");

        //employee.setCreateUser(loginEmployee.getId());
        //employee.setUpdateUser(loginEmployee.getId());
        int row = 0;


        //1. 调用service
        row = employeeService.add(employee);


        //2. 判定结果
        if (row > 0) {
            return R.success("添加成功！");
        }

        return R.error("添加失败！");


    }


    /**
     * 员工分页
     * @param pageParam
     * @return
     */
    @GetMapping("/page")
    public R findPage(PageParam pageParam){

        //1. 调用service
        IPage<Employee> page = employeeService.findPage(pageParam);

        //2. 返回结果
        return R.success(page);
    }


    /**
     * 更新员工
     * @param employee
     * @return
     */
    @PutMapping
    public R update(@RequestBody Employee employee ){

        //1. 补充数据
        //Employee e = (Employee) session.getAttribute("employee");
        //employee.setUpdateUser(e.getId());
        //employee.setUpdateTime(LocalDateTime.now());

        //2. 调用service
        int row = employeeService.update(employee);

        //3. 判断返回结果
        if(row > 0 ) {
            return R.success("更新成功！");
        }

        return R.error("更新失败！");

    }
}
