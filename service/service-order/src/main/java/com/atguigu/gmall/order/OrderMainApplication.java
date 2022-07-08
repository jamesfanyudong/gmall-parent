package com.atguigu.gmall.order;

import com.atguigu.gmall.common.annotation.EnableFeignInterceptor;
import com.atguigu.gmall.common.annotation.EnableThreadPool;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author fanyudong
 * @date 2022/7/8 12:45
 */

@EnableFeignInterceptor
@MapperScan("com.atguigu.gmall.order.mapper")
@EnableFeignClients({"com.atguigu.gmall.user","com.atguigu.gmall.cart","com.atguigu.gmall.product"})
@EnableThreadPool
@SpringCloudApplication
public class OrderMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderMainApplication.class,args);
    }
}
