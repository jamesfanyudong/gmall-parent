package com.atguigu.gmall.cache.annotation;


import java.lang.annotation.*;

/**
 * @author fanyudong
 */

@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Cache {

    String key() default "";

    //布隆判定时用的值
    String bloomIf() default "";
    //指定布隆过滤器的名字。  默认不用
    String bloomName() default "";

    long ttl() default 1000*60*30L; //默认不说，数据就缓存30min

}
