package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author fanyudong
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
    implements OrderDetailService{

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Override
    public List<OrderDetail> getOrderDetailsByOrderIdAndUserId(Long orderId, Long userId) {

        return orderDetailMapper.getOrderDetailsByOrderIdAndUserId(orderId,userId);
    }
}




