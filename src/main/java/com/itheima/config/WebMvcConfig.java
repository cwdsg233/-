package com.itheima.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    // 设置地址和资源位置的映射
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 配置资源的位置
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");

    }

    //设置扩展的消息转换器。设置Jackson 在转化 JSON 和 对象之间的规则
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        //1. 构建消息转化器
        MappingJackson2HttpMessageConverter mc = new MappingJackson2HttpMessageConverter();

        //2. 把我们的映射器装到里面去
        mc.setObjectMapper(new JacksonObjectMapper());

        //3. 把构建的消息转化器，装到参数集合中。
        // 由于集合里面已经存在了和我们现在放进去的mc一样种类的转化器，所以需要把我们的mc放在集合的前面。
        // 只要超过原来的那个转化器的位置即可（ 7 ） ，可以放在最前面，也可以放在7数字往前都可以。
        converters.add(0 , mc);
    }
}
