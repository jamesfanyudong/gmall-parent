package com.atguigu.gmall.cache.component;


import com.fasterxml.jackson.core.type.TypeReference;

import java.util.concurrent.TimeUnit;

public interface CacheService {


    <T> T getData(String cacheKey, Class<T> t);
    <T> T getData(String cacheKey, TypeReference<T> t);

    <T> void saveData(String cacheKey, T detail, Long time, TimeUnit unit);
    <T> void saveData(String cacheKey, T detail);
}
