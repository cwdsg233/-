package com.itheima.web;

import com.itheima.bean.User;
import com.itheima.common.R;
import com.itheima.service.UserService;
import com.itheima.utils.SMSUtils;
import com.itheima.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    //#搭建redis环境 第三步 注入RedisTemplate
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 发送短信验证码
     * @param phone
     * @return
     */
    @GetMapping("/sms")
    public R sendValidateCode(String phone ){

        //1. 构建验证码
        String code = ValidateCodeUtils.generateValidateCode4String(4);
        System.out.println("验证码：" + code);
        //2. 调用短信发送的代码，发送验证码
        //SMSUtils.sendMessage("签名" , "模板" , phone , code);

        //改造验证码 第一步：将验证码存入redis
        //3. 保存验证码 设置5分钟过期
        //key=固定的前缀+唯一标识
        //为什么加固定的前缀：确认验证码是登录使用的   例如：手机号获取验证码进行登录 、手机号获取验证码进行重置密码
        String key = "VALIDATE_CODE_"+phone;
        //String key = "FORGOT_PASS_CODE_"+phone;
        redisTemplate.opsForValue().set(key,code,5, TimeUnit.MINUTES);
        return R.success(code);
    }
    /*@GetMapping("/sms")
    public R sendValidateCode(String phone , HttpSession session){

        //1. 构建验证码
        String code = ValidateCodeUtils.generateValidateCode4String(4);
        System.out.println("验证码：" + code);

        //2. 调用短信发送的代码，发送验证码
        //SMSUtils.sendMessage("签名" , "模板" , phone , code);

        //3. 保存验证码...
        session.setAttribute(phone , code);

        return R.success(code);
    }*/


    /**
     * 登录
     *  1. 登录不仅仅是判定验证码正确就表示完成所有工作了。
     *  2. 还需要做两件事情：
     *      2.1 把用户的信息保存到数据库 【可以做一个统计、留存的工作，以后搞活动，可以通知用户。】
     *          2.1.1 只有新用户【第一次访问】才保存到数据库。
     *
     *          2.1.2 先使用手机号码去数据库执行查询操作。
     *
     *              a. 如果查询出来了用户信息，那么表示该用户是老用户，就不要执行添加操作了。
     *                  把查询出来的用户信息，保存到session作用域
     *
     *              b. 如果没有查询出来用户信息，那么表示该用户是新用户，第一次来。就要执行添加操作。
     *                  把生成的User对象保存到session作用域。
     *
     *      2.2 把用户的信息保存到session作用域 【以便一会下单，服务器能够知道是谁下单。】
     * @param map
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody Map<String, String> map , HttpSession session){

        //1. 获取手机号 和 验证码
        String phone = map.get("phone");
        String code = map.get("code");

        //2.  获取原来的生成的验证码
        //String validateCode = (String) session.getAttribute(phone);

        //改造验证码 第二步：获取redis中的验证码
        String key = "VALIDATE_CODE_"+phone;
        String validateCode = (String)redisTemplate.opsForValue().get(key);

        //3. 校验验证码
        if(validateCode != null && validateCode.equalsIgnoreCase(code)){
            redisTemplate.delete(key);//验证码已经校验过了，则直接删除
            //3.1 查询数据库，看看是否是新用户。
            User user = userService.findUser(phone);

            //3.2 判定结果：
            if(user == null){
                //新用户:: 补充user的数据，添加到数据库，保存到session作用域
                user  = new User();

                user.setPhone(phone);
                user.setStatus(1);
                user.setName(phone);

                //添加到数据库
                int row = userService.add(user);
                if(row > 0 ){
                    //保存到session作用域
                    session.setAttribute("user", user);

                    //登录成功：
                    return R.success("登录成功！");
                }
                return R.error("登录失败！");
            }else{
                //保存到session作用域
                session.setAttribute("user", user);
                return R.success("登录成功！");
            }
        }
        return R.error("登录失败！");
    }
}
