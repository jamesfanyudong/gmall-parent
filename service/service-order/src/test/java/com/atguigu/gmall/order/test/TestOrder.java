package com.atguigu.gmall.order.test;

import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;

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


    public static void main(String[] args) throws Exception {
        System.out.println("开始查询商品：");
//        CompletableFuture.runAsync(()->{
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("小米手机：");
//
//        });
//        System.out.println("结束：");

        String ex = CompletableFuture.supplyAsync(() -> {

            System.out.println("小米手机：");
            int a = 10 / 0;
            return "xiaomi1";
            // 异常兜底
        }).exceptionally(throwable -> {
                    System.out.println("有异常");
                    return "补货了异常";
        }).get();
        System.out.println("异常为："+ex);


        // 感知异常 whencomplete（x,y)






    }






















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
