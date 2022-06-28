package com.atguigu.gmall.common.config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fanyudong
 */
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Configuration
public class RedissonConfig {
    @Autowired
    RedisProperties redisProperties;


    @Bean
    public RedissonClient getRedissonClient(){
        // 1. 代表redisson的配置
        Config config = new Config();
        String host = redisProperties.getHost();
        int port = redisProperties.getPort();
        String password = redisProperties.getPassword();


        config.useSingleServer()
                .setAddress("redis://"+host+":"+port)
                .setPassword(password);

        // 2. 创建客户端
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;

    }

}
