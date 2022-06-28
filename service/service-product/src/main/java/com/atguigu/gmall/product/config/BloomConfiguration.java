package com.atguigu.gmall.product.config;


import com.atguigu.gmall.product.service.BloomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fanyudong
 */
@Configuration
@Slf4j
public class BloomConfiguration {

    @Autowired
    BloomService bloomService;

    @Bean
    public ApplicationRunner applicationRunner(){
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                log.info("应用启动完成正在初始化布隆。。。");
                bloomService.initBloom();
            }
        };
    }






}
