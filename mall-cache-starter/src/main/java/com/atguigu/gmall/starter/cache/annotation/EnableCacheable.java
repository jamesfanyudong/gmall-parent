package com.atguigu.gmall.starter.cache.annotation;

import com.atguigu.gmall.starter.cache.MallCacheAutoCofiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author fanyudong
 */

@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@EnableAspectJAutoProxy
@Import(MallCacheAutoCofiguration.class)
public @interface EnableCacheable {
}
