package com.atguigu.gmall.product.mapper;


import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Entity com.atguigu.gmall.product.domin.SkuInfo
 */
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    void updateStatus(@Param("skuId") Long skuId, @Param("i") int i);
}




