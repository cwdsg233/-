package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.bean.Employee;
import com.itheima.common.PageParam;
import com.itheima.dao.EmployeeDao;
import com.itheima.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.beans.PropertyDescriptor;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeDao employeeDao;
    /**
     * 登录
     *
     * @param employee
     * @return
     */
    @Override
    public Employee login(Employee employee) {

        //1. 构建查询条件对象
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();

        //2. 封装条件
        lqw.eq(Employee::getUsername, employee.getUsername());

        //处理密码加密
        String md5Pwd = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());

        lqw.eq(Employee::getPassword , md5Pwd);

        //3. 执行查询
        return employeeDao.selectOne(lqw);
    }

    /**
     * 添加员工
     *
     * @param employee
     * @return
     */
    @Override
    public int add(Employee employee) {
        return employeeDao.insert(employee);
    }

    /**
     * 员工分页
     *
     * @param pageParam
     * @return
     */
    @Override
    public IPage<Employee> findPage(PageParam pageParam) {

        //1. 构建分页对象 设置查询第几页，每页多少条
        IPage<Employee> page = new Page<>(pageParam.getPage() , pageParam.getPageSize());

        //2. 构建条件对象
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();

        //2.1 设置条件
        lqw.like( pageParam.getName() != null,Employee::getName , pageParam.getName());

        //3. 执行查询，返回结果
        return employeeDao.selectPage(page ,lqw);
    }

    /**
     * 更新员工
     *
     * @param employee
     * @return
     */
    @Override
    public int update(Employee employee) {
        return employeeDao.updateById(employee);
    }
}
