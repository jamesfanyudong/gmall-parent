package com.atguigu.gmall.product;

import com.atguigu.gmall.common.annation.EnableRedisson;
import com.atguigu.gmall.common.config.Swagger2Config;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author fanyudong
 */

@EnableScheduling
@EnableRedisson
@EnableTransactionManagement   //开启基于注解的自动事务管理
@Import(Swagger2Config.class)
@SpringCloudApplication
@MapperScan(basePackages = "com.atguigu.gmall.product.mapper")
public class ProductMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductMainApplication.class,args);
    }
}
