package com.atguigu.gmall.product;


import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;



@SpringBootTest
public class TestSharding {

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Test
    void testRead(){
        for (int i = 0; i < 2; i++) {

            BigDecimal skuPrice =
                    skuInfoMapper.getSkuPrice(52L);
            System.out.println("skuPrice = " + skuPrice);
        }
    }



}
