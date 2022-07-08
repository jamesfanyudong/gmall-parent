package com.atguigu.gmall.order.test;

import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author fanyudong
 * @date 2022/7/8 18:12
 */
@SpringBootTest
public class TestOrder {
    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    OrderDetailService orderDetailService;

    @Test
    void testSaveInfo(){
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(16L);
        orderInfo.setConsignee("fjsdalfjaskflasdfjkasldfjl");

        orderInfoService.save(orderInfo);

    }

    @Test
    void testSaveDetail() {
        OrderDetail orderDetail = new OrderDetail();

        orderDetail.setUserId(16L);
        orderDetail.setImgUrl("456465fdsaf4as65f465fsa4d65");
        orderDetailService.save(orderDetail);
    }

}
