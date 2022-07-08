package com.atguigu.gmall.front;

import com.atguigu.gmall.common.annotation.EnableFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author fanyudong
 */
@EnableFeignInterceptor
@EnableFeignClients(basePackages = {"com.atguigu.gmall"})
@SpringCloudApplication
public class WebFrontMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebFrontMainApplication.class,args);
    }
}
