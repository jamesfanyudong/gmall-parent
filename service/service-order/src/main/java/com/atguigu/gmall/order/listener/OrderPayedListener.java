package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.MQConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.order.service.OrderBizService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * @author fanyudong
 * @date 2022/7/14 9:40
 */
@Slf4j
@Service
public class OrderPayedListener {
    @Autowired
    OrderBizService orderBizService;

    @Autowired
    OrderInfoService orderInfoService;
    @RabbitListener(queues = MQConst.QUEUE_ORDER_PAYED)
    public void orderPayed(Message message, Channel channel) throws InterruptedException, IOException {
        MessageProperties properties = message.getMessageProperties();
        Map<String, String> map = null;
        try {
        byte[] body = message.getBody();
        //1、支付成功,支付宝给我们的所有数据json
        map = Jsons.toObj(new String(body), new TypeReference<Map<String, String>>(){});


        //2、修改订单为已支付状态
        orderInfoService.orderPayedStatusChange(map);
        //无论是否批量拿消息，都要一个个回复
        channel.basicAck(properties.getDeliveryTag(),false);
    } catch (Exception e) {
        log.error("消息消费失败，返回队列：{}",map);
        Thread.sleep(1000);
        channel.basicNack(properties.getDeliveryTag(),false,true);
    }


    }

}
