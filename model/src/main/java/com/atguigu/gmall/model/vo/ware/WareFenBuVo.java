package com.atguigu.gmall.model.vo.ware;

import lombok.Data;

import java.util.List;


/**
 * @author fanyudong
 */
@Data
public class WareFenBuVo {

    private String wareId;
    private List<String> skuIds;
}