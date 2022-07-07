package com.atguigu.gmall.product.service;


/**
 * @author fanyudong
 */
public interface BloomService {

    /**
     * 初始化布隆过滤器
     */
     void initBloom();

    /**
     * 重建布隆
     */
    void rebuildSkuBloom();
}
