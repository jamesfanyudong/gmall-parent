package com.atguigu.gmall.item.stream;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
public class CFTest {

    @Test
    public void cfTest(){
        System.out.println("开始");


        CompletableFuture<Integer> basefuture = CompletableFuture.supplyAsync(()->{
            System.out.println("fafsfad");
            return 11;
        });
        System.out.println("结束");

        CompletableFuture<BigDecimal> future = basefuture.thenApply((val)->{
            System.out.println(val + "：商品的id=" + 1);
            System.out.println("正在查询1号商品价格");
            return new BigDecimal("9999");
        });



    }
}
