package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.MQConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.vo.ware.OrderDeduceStatusVo;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author fanyudong
 * @date 2022/7/11 18:49
 */
/**
 * 感知订单的库存扣减状态
 */
@Slf4j
@Service
public class OrderWareStockListener {
    @Autowired
    OrderInfoService orderInfoService;

    //MQ能极大程度保证业务一致性
    //幂等性问题: 怎么解决？ 业务自己保证=(关单期望状态+影响行数日志)；

    @RabbitListener(queues = MQConst.QUEUE_WARE_ORDER)
    void orderStock(Message message, Channel channel) throws IOException {


        MessageProperties properties = message.getMessageProperties();
        try {
            // 获取消息体
            byte[] body = message.getBody();
            OrderDeduceStatusVo statusVo = Jsons.toObj(new String(body), OrderDeduceStatusVo.class);

            String status = statusVo.getStatus();
            ProcessStatus changeStatus = null;
            switch (status){
                case "DEDUCTED":   changeStatus = ProcessStatus.WAITING_DELEVER ;break;
                case "OUT_OF_STOCK": changeStatus = ProcessStatus.STOCK_EXCEPTION; break;
                default:break;
            }
            //更新订单的状态
            orderInfoService.updateOrderStatus(
                    statusVo.getOrderId(),
                    statusVo.getUserId(),
                    changeStatus.getOrderStatus().name(),
                    changeStatus.name(),
                    ProcessStatus.PAID.name()
            );


            channel.basicAck(properties.getDeliveryTag(),false);
        } catch (Exception e) {
            log.error("异常：{}",e);
           channel.basicNack(properties.getDeliveryTag(),false,true);
        }


    }

}
