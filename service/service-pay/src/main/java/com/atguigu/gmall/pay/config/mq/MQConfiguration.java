package com.atguigu.gmall.pay.config.mq;


import com.atguigu.gmall.common.constant.MQConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fanyudong
 * @date 2022/7/11 18:17
 */
@Configuration
@Slf4j
public class MQConfiguration {

    //只需要把队列、交换机、绑定关系等放在容器中。MQ中没有就会自动创建

    @Bean
    RabbitTemplate rabbitTemplate(RabbitTemplateConfigurer configurer, ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate();
        configurer.configure(template, connectionFactory);


        /**
         * message – the returned message.
         * replyCode – the reply code.
         * replyText – the reply text.
         * exchange – the exchange.
         * routingKey – the routing key.
         * 消息如果发出去了，就能调这个回调
         */
        template.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            //消息发生错误调用【服务器没收到的】。持久化不成功；1、路由键错误，消息没给队列  2、队列的磁盘挂了。
            log.info("ReturnCallback: message:[{}]",message);
            //日志记录；（消息持久化失败； 1、路由键错误，消息没给队列  2、队列的磁盘挂了。）
            //数据库专门建立一张消息表，失败消息存到数据库，写一个定时任务，重试发送。
        });

        //mq确认消息了。会调用这个回调
        template.setConfirmCallback((correlationData, ack, cause) -> {
            //消息发生错误调用【服务器没回复（消息不能正确投递给队列）】
            log.info("ConfirmCallback: ack:[{}]， 原因：{}",ack,cause);
            //日志，ack：false不用记录；  ack: true；服务器确认收到了也有这个交换机，也有这个路由键。
        });

        return template;
    }

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
