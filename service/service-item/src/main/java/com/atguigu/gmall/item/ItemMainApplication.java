package com.atguigu.gmall.item;


import com.atguigu.gmall.cache.MallCacheAutoCofiguration;
import com.atguigu.gmall.common.annation.EnableRedisson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;


//调谁扫谁。
//每一个微服务不用扫自己的controller暴露的feignclient，微服务暴露的feignclient给别人用的，不是给自己用的。

/**
 * @author fanyudong
 */

@Import(MallCacheAutoCofiguration.class)
@EnableRedisson
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.atguigu.gmall.product"})
public class ItemMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemMainApplication.class,args);
    }
}
