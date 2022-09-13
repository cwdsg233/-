package com.itheima.filter;

import com.itheima.bean.Employee;
import com.itheima.bean.User;
import com.itheima.utils.BaseContext;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//过滤所有的请求
@WebFilter("/*")
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {


        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        //1. 获取客户端访问的地址
        String uri = req.getRequestURI();

        //2. 判断地址是否属于免登录的地址，如果是，那么直接放行。
        if(uri.contains("login") || uri.endsWith(".js") || uri.endsWith(".css") || uri.contains("images")
            || uri.endsWith(".ico") || uri.contains("styles") || uri.contains("plugins")||uri.contains("user/sms")){
            filterChain.doFilter(req,resp);
            return ;
        }

        //3. 如果不是免登录，那么就要查看是否已经登录了。

        //3.1 从session作用域里面取出来 employee对象
        Employee employee = (Employee) req.getSession().getAttribute("employee");

        if(employee != null){

            //在这里，把员工的id，保存到ThreadLocal里面去！...
            BaseContext.setCurrentId(employee.getId());


            //3.2 如果取出来的对象不是 null, 就表明已经登录了。那么直接放行！
            filterChain.doFilter(req,resp);
            return ;
        }

        //4. 在这里补充前端的过滤操作
        User user = (User) req.getSession().getAttribute("user");
        if(user != null){


            //在这里，把用户的id，保存到ThreadLocal里面去！...
            BaseContext.setCurrentId(user.getId());

            //表示用户已经登录了。
            filterChain.doFilter(req,resp);
            return ;
        }

        //3.3 如果取出来的对象是 null, 就表明还没有登录，那么就要跳转到登录页面去!
        if(uri.contains("front")){
            resp.sendRedirect("/front/page/login.html");
        }else{
            resp.sendRedirect("/backend/page/login/login.html");
        }
    }
}
