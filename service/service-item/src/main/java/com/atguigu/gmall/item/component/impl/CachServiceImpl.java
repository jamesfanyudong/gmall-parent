package com.atguigu.gmall.item.component.impl;

import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.component.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author fanyudong
 */
@Service
public class CachServiceImpl implements CacheService {

    @Autowired
    StringRedisTemplate redisTemplate;

    // 从缓存中获取一个数据
    @Override
    public <T> T getData(String cacheKey, Class<T> t) {

        String json = redisTemplate.opsForValue().get(cacheKey);
        return Jsons.toObj(json,t);
    }

    //给缓存中保存一个数据
    @Override
    public <T> void saveData(String cacheKey, T detail, Long time, TimeUnit unit) {
       redisTemplate.opsForValue().set(cacheKey,Jsons.toStr(detail),time,unit);
    }

    @Override
    public <T> void saveData(String cacheKey, T detail) {
        redisTemplate.opsForValue().set(cacheKey,Jsons.toStr(detail));
    }


}
