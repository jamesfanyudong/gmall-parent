package com.atguigu.gmall.product;

import com.atguigu.gmall.common.config.Swagger2Config;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author fanyudong
 */

@EnableScheduling
@EnableTransactionManagement   //开启基于注解的自动事务管理
@Import(Swagger2Config.class)
@SpringCloudApplication
@MapperScan(basePackages = "com.atguigu.gmall.product.mapper")
@EnableFeignClients(basePackages = "com.atguigu.gmall.search")
public class ProductMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductMainApplication.class,args);
    }
}
