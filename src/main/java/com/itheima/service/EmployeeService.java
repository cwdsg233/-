package com.itheima.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.bean.Employee;
import com.itheima.common.PageParam;

public interface EmployeeService {

    /**
     * 登录
     * @param employee
     * @return
     */
    Employee login(Employee employee);

    /**
     * 添加员工
     * @param employee
     * @return
     */
    int add(Employee employee);

    /**
     * 员工分页
     * @param pageParam
     * @return
     */
    IPage<Employee> findPage(PageParam pageParam);

    /**
     * 更新员工
     * @param employee
     * @return
     */
    int update(Employee employee);
}
