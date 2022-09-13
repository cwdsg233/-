package com.itheima.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 搭建redis环境 第五步 配置类改变redis序列化方式
 * 注意：RedisAutoConfiguration自动创建RedisTemplate对象 改变默认创建的RedisTemplate对象的key value序列化方式
 */
@Configuration
public class RedisConfig {
    /**
     * 设置key value序列化方式
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        //设置key序列化方式为string value没有设置使用的是默认的是JDK序列化方式
        template.setKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
