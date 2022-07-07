package com.atguigu.gmall.cart;

import com.atguigu.gmall.common.annotation.EnableFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author fanyudong
 */
@EnableFeignInterceptor
@SpringCloudApplication
@EnableFeignClients(basePackages = "com.atguigu.gmall.product")
public class CartMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartMainApplication.class,args);
    }
}
