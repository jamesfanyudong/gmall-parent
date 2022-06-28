package com.atguigu.gmall.common.annation;


import com.atguigu.gmall.common.config.RedissonConfig;
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
