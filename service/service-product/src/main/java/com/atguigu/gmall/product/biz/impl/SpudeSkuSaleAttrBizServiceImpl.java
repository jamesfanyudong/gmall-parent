package com.atguigu.gmall.product.biz.impl;

import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.vo.ValuesSkuVo;
import com.atguigu.gmall.product.biz.SpudeSkuSaleAttrBizService;
import com.atguigu.gmall.product.mapper.SkuSaleAttrValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpudeSkuSaleAttrBizServiceImpl implements SpudeSkuSaleAttrBizService {

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Override
    public String getSpudeAllSkuSaleAttrAndValue(Long spuId) {
        List<ValuesSkuVo> skuVos = skuSaleAttrValueMapper.getSpudeAllSkuSaleAttrAndValue(spuId);
        Map<String,String> jsonMap = new HashMap<>();
        for (ValuesSkuVo vo : skuVos) {
            String values = vo.getSku_values();
            String skuId = vo.getSku_id();
            jsonMap.put(values,skuId);
        }
        return Jsons.toStr(jsonMap);
    }
}
