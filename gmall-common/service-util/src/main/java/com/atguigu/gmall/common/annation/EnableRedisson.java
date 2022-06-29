package com.atguigu.gmall.common.annation;


import com.atguigu.gmall.cache.RedissonConfig;
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
