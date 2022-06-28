package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/admin/product")
public class SaleAttrController {
    @Autowired
    SpuSaleAttrService spuSaleAttrService;


    @Autowired
    BaseSaleAttrService baseSaleAttrService;
    /**
     * 根据spuid 获取所有销售属性列表
     * @param spuId
     * @return
     */
    // /spuSaleAttrList/{spuId}
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable("spuId") Long spuId){
        List<SpuSaleAttr> list = spuSaleAttrService.getSpuSaleAttrList(spuId);

        return Result.ok(list);

    }


    /**
     * 获取基本销售属性列表
     * @return
     */
    // /baseSaleAttrList
    @GetMapping("/baseSaleAttrList")
    public Result getBaseSaleAttrList(){
        List<BaseSaleAttr> list = baseSaleAttrService.list();
        return Result.ok(list);
    }
}
