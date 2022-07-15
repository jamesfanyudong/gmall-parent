package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author fanyudong
 * @Entity com.atguigu.gmall.order.domin.OrderInfo
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {


      long updateOrderStatus(@Param("orderId") Long orderId, @Param("userId") Long userId, @Param("orderStatus") String orderStatus, @Param("processStatus") String processStatus, @Param("expectedStatus") String expectedStatus);

    OrderInfo getOrderInfoAndDetails(@Param("orderId") long orderId, @Param("userId") long userId);

    long updateOrderStatusInExpects(@Param("orderId") Long orderId, @Param("userId") long userId, @Param("orderStatus") String orderStatus, @Param("processStatus") String processStatus, @Param("expectedStatus") List<String> expectedStatus);
}




