package com.atguigu.gmall.order.service.impl;
import com.atguigu.gmall.common.constant.MQConst;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.order.OrderStatusLog;
import com.atguigu.gmall.model.to.mq.WareStockDetail;
import com.atguigu.gmall.model.to.mq.WareStockMsg;
import com.atguigu.gmall.model.vo.order.OrderSubmitDetailVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.service.OrderStatusLogService;
import com.atguigu.gmall.order.service.PaymentInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author fanyudong
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
    implements OrderInfoService{
    @Autowired
    PaymentInfoService paymentInfoService;
    @Autowired
    OrderDetailService orderDetailService;
    @Autowired
    OrderInfoMapper orderInfoMapper;
    @Autowired
    OrderStatusLogService orderStatusLogService;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void saveDetail(OrderInfo orderInfo, OrderSubmitVo order) {
        // 1.拿到前端提交的需要购买的商品]
        List<OrderSubmitDetailVo> orderDetailList = order.getOrderDetailList();
        Long userId = AuthContextHolder.getUserAuth().getUserId();
        //2、得到订单详情
        // 订单：
        // 小米8  799-3      100
        // 华为10  7999-1    200
        //
        List<OrderDetail> orderDetails = orderDetailList.stream()
                .map(detail -> {
                    OrderDetail result = new OrderDetail();
                    result.setOrderId(orderInfo.getId());
                    result.setUserId(userId);
                    result.setSkuId(detail.getSkuId());
                    result.setSkuName(detail.getSkuName());
                    result.setImgUrl(detail.getImgUrl());
                    result.setOrderPrice(detail.getOrderPrice());
                    result.setSkuNum(detail.getSkuNum());
                    result.setHasStock(detail.getStock());
                    result.setCreateTime(new Date());
                    // 设置当前商品的总价
                    result.setSplitTotalAmount(
                            detail.getOrderPrice().multiply(new BigDecimal(detail.getSkuNum()))
                    );
                    return result;


                }).collect(Collectors.toList());

        //3.保存详情
        orderDetailService.saveBatch(orderDetails);

    }

    @Override
    public void updateOrderStatus(Long orderId, Long userId, String orderStatus, String processStatus, String expectedStatus) {
        long l = orderInfoMapper.updateOrderStatus(orderId, userId, orderStatus, processStatus, expectedStatus);
        if (l > 0){
            //需要判断上次是否数据库发生了变化
            //2、记录订单状态日志
            OrderStatusLog log = new OrderStatusLog();
            log.setOrderId(orderId);
            log.setUserId(userId);
            log.setOrderStatus(orderStatus);
            log.setOperateTime(new Date());
            orderStatusLogService.save(log);



        }
    }

    @Override
    public OrderInfo getOrderInfoByIdAndUserId(Long id) {

        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);
        Long userId = AuthContextHolder.getUserAuth().getUserId();
        wrapper.eq("user_id",userId);
        List<OrderInfo> infos = orderInfoMapper.selectList(wrapper);
        if (infos == null || infos.size() == 0){
            return null;
        }
        return infos.get(0);

    }


    @Override
    public OrderInfo getOrderInfoAndDetails(long orderId, long userId) {
        return orderInfoMapper.getOrderInfoAndDetails(orderId,userId);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void orderPayedStatusChange(Map<String, String> map) {
        //1、拿到订单信息（交易号、用户id）
        String outTradeNo = map.get("out_trade_no");
        String[] split = outTradeNo.split("_");
        long userId = Long.parseLong(split[split.length - 1]);
        //2.拿到订单信息
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        wrapper.eq("out_trade_no",outTradeNo);
        OrderInfo orderInfo = orderInfoMapper.selectOne(wrapper);
        // 3.修改订单状态
        ProcessStatus paid = ProcessStatus.PAID;
        // 订单状态流转。状态机：
        // 未支付 -- 支付 -- xx  --关闭
        // 未支付 -- 关闭 -- 支付 -- xx -- 关闭
        // 未支付 -- 已支付 -- 已发货 -- xxx -- 已支付
       long status =  orderInfoMapper.updateOrderStatusInExpects(
                orderInfo.getId(),
                userId,
                paid.getOrderStatus().name(),
                paid.name(),
                Arrays.asList(ProcessStatus.UNPAID.name(),
                        ProcessStatus.CLOSED.name())
        );
       if (status > 0){
           // 4.推进日志
           OrderStatusLog log = new OrderStatusLog();
           log.setOrderId(orderInfo.getId());
           log.setUserId(userId);
           log.setOrderStatus(paid.getOrderStatus().name());
           log.setOperateTime(new Date());
           orderStatusLogService.save(log);
           // 5.此次支付宝返回的信息也保存
           paymentInfoService.savePayment(map,orderInfo);

           // 6.给库存系统发送扣库存消息
           WareStockMsg msg = prepareWareMsg(orderInfo);
           rabbitTemplate.convertAndSend(
                   MQConst.EXCHANGE_WARE_SYS,
                   MQConst.ROUTE_KEY_WARE_STOCK, Jsons.toStr(msg)
           );



       }



    }

    private WareStockMsg prepareWareMsg(OrderInfo orderInfo) {

        WareStockMsg msg = new WareStockMsg();
        msg.setOrderId(orderInfo.getId());
        msg.setUserId(orderInfo.getUserId());
        msg.setConsignee(orderInfo.getConsignee());
        msg.setConsigneeTel(orderInfo.getConsigneeTel());
        msg.setOrderComment(orderInfo.getOrderComment());
        msg.setOrderBody(orderInfo.getTradeBody());
        msg.setDeliveryAddress(orderInfo.getDeliveryAddress());
        msg.setPaymentWay("2");

        // 订单详情
        List<OrderDetail> details = orderDetailService.getOrderDetailsByOrderIdAndUserId(orderInfo.getId(), orderInfo.getUserId());

        List<WareStockDetail> stockDetails = details.stream()
                .map(item -> new WareStockDetail(item.getSkuId(),
                        item.getSkuNum(), item.getSkuName()))
                .collect(Collectors.toList());
        msg.setDetails(stockDetails);

        return msg;
    }


}




