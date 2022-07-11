package com.atguigu.gmall.order.config;


import com.atguigu.gmall.common.constant.MQConst;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fanyudong
 * @date 2022/7/11 18:17
 */
@Configuration
public class MQConfiguration {

    //只需要把队列、交换机、绑定关系等放在容器中。MQ中没有就会自动创建

    /**
     * 订单事件交换机
     * @return
     */
    @Bean
    Exchange orderEventExchange(){
        //String name,
        //boolean durable, boolean autoDelete, Map<String, Object> arguments
        return new TopicExchange(
                MQConst.EXCHANGE_ORDER_EVENT,
                true,
                false
        );
    }

    /**
     * 订单的延迟队列，利用队列进行超时计时
     * @return
     */
    @Bean
    Queue orderDelayQueue(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-message-ttl",MQConst.ORDER_TIMEOUT);
        arguments.put("x-dead-letter-exchange",MQConst.EXCHANGE_ORDER_EVENT);
        arguments.put("x-dead-letter-routing-key",MQConst.ROUTE_KEY_ORDER_TIMEOUT);


        return new Queue(MQConst.QUEUE_ORDER_DELAY,
                true,
                false,
                false,
                arguments
        );
    }

    /**
     * 订单交换机和延迟队列进行绑定
     * 订单一创建好，消息就先抵达延迟队列
     * @return
     */
    @Bean
    Binding orderDelayQueueBinding(){
        return new Binding(
                MQConst.QUEUE_ORDER_DELAY,
                Binding.DestinationType.QUEUE,
                MQConst.EXCHANGE_ORDER_EVENT,
                MQConst.ROUTE_KEY_ORDER_CREATE,
                null
        );
    }
    /**
     * 订单死信队列。
     * 关单服务消费这个队列就能拿到所有待关闭的超时订单
     * @return
     */
    @Bean
    Queue orderDeadQueue(){
        return new Queue(
                MQConst.QUEUE_ORDER_DEAD,
                true,
                false,
                false
        );
    }

    /**
     * 死信队列和交换机利用超时路由键进行绑定
     * @return
     */
    @Bean
    Binding orderDeadQueueBinding(){
        return new Binding(
                MQConst.QUEUE_ORDER_DEAD,
                Binding.DestinationType.QUEUE,
                MQConst.EXCHANGE_ORDER_EVENT,
                MQConst.ROUTE_KEY_ORDER_TIMEOUT,
                null
        );
    }
}
