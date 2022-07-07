package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author fanyudong
 */
public interface SkuInfoService extends IService<SkuInfo> {

    void saveSkuInfo(SkuInfo skuInfo);

    void onSale(Long skuId);

    void cancelSale(Long skuId);

    List<Long> getSkuIds();

    BigDecimal getSkuPrice(Long skuId);

    public void updateSkuInfo(SkuInfo skuInfo);

    Goods getGoodsInfoBySkuId(Long skuId);

    CartInfo getCartInfoBySkuId(Long skuId);

    BigDecimal get1010Price(Long skuId);
}
