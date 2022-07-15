package com.atguigu.gmall.order.test;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author fanyudong
 * @date 2022/7/11 18:10
 */

@SpringBootTest
public class MQTest {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void testSend(){
        rabbitTemplate.convertAndSend("testExchange","route-test-key","hello,rabbit");
        



    }
}
