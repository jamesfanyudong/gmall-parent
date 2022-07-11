package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.order.OrderStatusLog;
import com.atguigu.gmall.model.vo.order.OrderSubmitDetailVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.service.OrderStatusLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author fanyudong
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
    implements OrderInfoService{
    @Autowired
    OrderDetailService orderDetailService;
    @Autowired
    OrderInfoMapper orderInfoMapper;
    @Autowired
    OrderStatusLogService orderStatusLogService;

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


}




