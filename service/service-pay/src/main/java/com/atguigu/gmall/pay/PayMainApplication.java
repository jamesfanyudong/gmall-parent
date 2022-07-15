package com.atguigu.gmall.pay;

import com.atguigu.gmall.common.annotation.EnableFeignInterceptor;
import com.atguigu.gmall.common.annotation.EnableThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author fanyudong
 * @date 2022/7/12 18:50
 */

@EnableFeignInterceptor
@EnableFeignClients(basePackages = "com.atguigu.gmall.order")
@EnableThreadPool
@SpringCloudApplication
public class PayMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayMainApplication.class,args);
    }
}
