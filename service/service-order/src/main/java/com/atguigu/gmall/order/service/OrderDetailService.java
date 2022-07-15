package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 * @author fanyudong
 */
public interface OrderDetailService extends IService<OrderDetail> {

    List<OrderDetail> getOrderDetailsByOrderIdAndUserId(Long id, Long userId);

}
