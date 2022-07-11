package com.atguigu.gmall.common.constant;

/**
 * @author fanyudong
 * @date 2022/7/11 18:20
 */
public class MQConst {

    public static final String EXCHANGE_ORDER_EVENT = "exchange-order-event";
    public static final String QUEUE_ORDER_DELAY = "queue-order-delay";
    //订单超时
    public static final long ORDER_TIMEOUT = 60000*30;


    //订单超时路由键    微服务名-事件
    public static final String ROUTE_KEY_ORDER_TIMEOUT = "order.timeout" ;
    public static final String ROUTE_KEY_ORDER_CREATE = "oreder.create";
    public static final String QUEUE_ORDER_DEAD = "queue-order-dead";
}
