package com.atguigu.gmall.model.vo.ware;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author fanyudong
 * @date 2022/7/13 18:30
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDeduceStatusVo {


    private Long orderId;
    private Long userId;
    private String status;



}
