package com.atguigu.gmall.starter.cache.annotation;


import com.atguigu.gmall.starter.cache.RedissonConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author fanyudong
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(RedissonConfig.class)
public @interface EnableRedisson {

}
