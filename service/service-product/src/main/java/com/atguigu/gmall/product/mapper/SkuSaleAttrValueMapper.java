package com.atguigu.gmall.product.mapper;


import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.model.vo.ValuesSkuVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Entity com.atguigu.gmall.product.domin.SkuSaleAttrValue
 */
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {

    List<ValuesSkuVo> getSpudeAllSkuSaleAttrAndValue(Long spuId);
}




