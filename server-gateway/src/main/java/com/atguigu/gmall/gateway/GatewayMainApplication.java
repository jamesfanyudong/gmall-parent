package com.atguigu.gmall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * @author fanyudong
 */
@SpringCloudApplication
public class GatewayMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayMainApplication.class,args);
    }
}
