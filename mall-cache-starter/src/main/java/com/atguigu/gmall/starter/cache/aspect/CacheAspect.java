package com.atguigu.gmall.starter.cache.aspect;


import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import com.atguigu.gmall.starter.cache.annotation.Cache;
import com.atguigu.gmall.starter.cache.component.CacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

/**
 * @author fanyudong
 */
@Aspect // 标注此类是一个通知
 // 将通知类放入容器中
public class CacheAspect {

    @Autowired
    CacheService cacheService;
    @Autowired
    RedissonClient redissonClient;

    SpelExpressionParser parser = new SpelExpressionParser();
    /**
     *
     * @param pjp 可推进的连接点
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.atguigu.gmall.starter.cache.annotation.Cache)")
    public Object cacheAspectAround(ProceedingJoinPoint pjp)  {
        Object retVal = null;
        // 参数
        Object[] args = pjp.getArgs();
        try {
            // 前置通知
            // 1. 先查缓存
            // 计算key
            Object cacheKey = calculateKey(pjp);

            Object data = cacheService.getData(cacheKey.toString(), new TypeReference<Object>() {
                @Override
                public Type getType() {
                    MethodSignature signature = (MethodSignature) pjp.getSignature();
                    // 当前方法带泛型的返回值类型
                    return signature.getMethod().getGenericReturnType();
                }
            });
            if (data != null){
                // 缓存命中,直接返回
                return data;
            }
            // 缓存不命中，先查布隆
            Cache cache = getCacheAnnotation(pjp, Cache.class);
            if (StringUtils.isEmpty(cache.bloomName())){
                // 不适用布隆
                return getDataWithLock(pjp, args, cacheKey.toString());
            }else{
                // 使用布隆
                //4.1、拿到布隆，问有没有
                RBloomFilter<Object> filter = redissonClient.getBloomFilter(cache.bloomName());
                //4.2、获取布隆判定用的值。
                Object bloomIfValue = getBloomIfValue(pjp);
                if (filter.contains(bloomIfValue)){
                    //4.2、布隆说有
                    //4.2.1、防止击穿，加锁
                    return getDataWithLock(pjp, args, cacheKey.toString());
                } else{
                    //4.3、布隆说没有
                    return null;
                }
                // 布隆说没有 ，就没有
            }
           // 返回通知
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new RuntimeException(throwable);
            // 异常通知
        } finally {
            // 后置通知
        }



    }

    private Object getBloomIfValue(ProceedingJoinPoint pjp) {
        Cache cacheAnnotation = getCacheAnnotation(pjp, Cache.class);
        // 得到布隆判定表达式
        String bloomIf = cacheAnnotation.bloomIf();
        // 得到最终的值
        return calculateExpression(bloomIf, pjp);
    }


    private Object getDataWithLock(ProceedingJoinPoint pjp, Object[] args, String cacheKey) throws Throwable {
        Object retVal;
        String lockKey = RedisConst.LOCK_PREFIX + cacheKey;
        //cacheKey: lock:sku:info:49   lock:categorys
        RLock lock = redissonClient.getLock(lockKey);
        //4.2.2、 加锁
        //有自动解锁逻辑
        boolean tryLock = lock.tryLock();
        if(tryLock){
            //4.2.3、加锁成功，回源查数据
            //执行目标方法，还能自动续期
            retVal = pjp.proceed(args);
            //4.2.4、放缓存
            Cache cache = getCacheAnnotation(pjp, Cache.class);
            cacheService.saveData(cacheKey,retVal,cache.ttl(),TimeUnit.MILLISECONDS);
            //4.2.5、解锁&返回
            lock.unlock();
            return retVal;
        }

        //4.2.6 没得到锁
        TimeUnit.MILLISECONDS.sleep(500);
        //4.2.7 直接查缓存结束即可
        return cacheService.getData(cacheKey, SkuDetailVo.class);
    }



    private Object calculateKey(ProceedingJoinPoint pjp) {
        return getKey(pjp);
    }

    private Object getKey(ProceedingJoinPoint pjp) {
        // 拿到目标方法上的@cache
        Cache cache = getCacheAnnotation(pjp, Cache.class);

        // 得到key值
        String key = cache.key();
        // 5.计算表达式，得到最终的值
        return calculateExpression(key,pjp);
    }

    /**
     * 计算spEl
     * @param key
     * @param pjp
     * @return
     */
    private Object calculateExpression(String key, ProceedingJoinPoint pjp) {

        // 1。得到一个表达式

        Expression expression = parser.parseExpression(key, new TemplateParserContext());
        // 2.准备一个计算上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        //支持的所有语法【动态扩展所有支持的属性】
        context.setVariable("params",pjp.getArgs());
        // 所有参数列表
        //指向一个组件，可以无限调方法
        context.setVariable("redisson",redissonClient);


        return expression.getValue(context, Object.class);
    }

    // 获取一个注解
    public <T extends Annotation> T getCacheAnnotation(ProceedingJoinPoint pjp, Class<T> tClass){
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        // 2.拿到目标方法
        Method method = signature.getMethod();
        // 3.获取方法上的标注的注解
        T cache = method.getDeclaredAnnotation(tClass);
        return cache;
    }


}
