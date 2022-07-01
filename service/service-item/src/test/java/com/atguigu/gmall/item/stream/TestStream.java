package com.atguigu.gmall.item.stream;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;


@SpringBootTest
public class TestStream {


    @Test
    public void test01(){

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        Integer integer = list.stream()
                .filter(val ->
                        // 过滤偶数
                        val % 2 == 0
                )
                .map(val -> val * 2)
                .filter(val -> val < 10)
                .reduce((v1, v2) -> v1 + v2)
                .get();

        System.out.println("integer = " + integer);


    }



}
