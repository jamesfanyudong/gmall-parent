package com.atguigu.gmall.model.vo.ware;

import lombok.Data;

/**
 * @author fanyudong
 * @date 2022/7/13 18:29
 */
@Data
public class OrderSplitVo {
    private String orderId;
    //用户id
    private String userId;
    //json串
    private String wareSkuMap;
}
