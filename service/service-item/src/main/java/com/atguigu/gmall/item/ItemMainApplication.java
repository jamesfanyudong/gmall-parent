package com.atguigu.gmall.item;


import com.atguigu.gmall.common.annotation.EnableThreadPool;
import com.atguigu.gmall.starter.cache.annotation.EnableRedisson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


//调谁扫谁。
//每一个微服务不用扫自己的controller暴露的feignclient，微服务暴露的feignclient给别人用的，不是给自己用的。

/**
 * @author fanyudong
 */


@EnableThreadPool
@EnableRedisson
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.atguigu.gmall.product","com.atguigu.gmall.search"})
public class ItemMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemMainApplication.class,args);
    }
}
