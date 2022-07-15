package com.atguigu.gmall.order;

import com.atguigu.gmall.common.annotation.EnableFeignInterceptor;
import com.atguigu.gmall.common.annotation.EnableThreadPool;
import com.atguigu.gmall.common.config.MybatisPlusConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author fanyudong
 * @date 2022/7/8 12:45
 */
@EnableTransactionManagement
@Import(MybatisPlusConfig.class)
@EnableFeignInterceptor
@MapperScan("com.atguigu.gmall.order.mapper")
@EnableFeignClients({"com.atguigu.gmall.user","com.atguigu.gmall.cart","com.atguigu.gmall.product","com.atguigu.gmall.ware"})
@EnableThreadPool
@SpringCloudApplication
@EnableRabbit
public class OrderMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderMainApplication.class,args);
    }
}
