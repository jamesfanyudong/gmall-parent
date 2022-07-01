package com.atguigu.gmall.starter.cache;


import com.atguigu.gmall.starter.cache.aspect.CacheAspect;
import com.atguigu.gmall.starter.cache.component.CacheService;
import com.atguigu.gmall.starter.cache.component.impl.CachServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author fanyudong
 */
@Configuration
@Import(RedissonConfig.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class MallCacheAutoCofiguration {


    // 缓存切面
    @Bean
    public CacheAspect cacheAspect(){
        return new CacheAspect();
    }
    //操做缓存的组件
    @Bean
    public CacheService cacheService(){
        return new CachServiceImpl();
    }

}
