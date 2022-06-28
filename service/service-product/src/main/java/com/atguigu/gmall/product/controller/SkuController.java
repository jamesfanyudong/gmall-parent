package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product")
public class SkuController {

    @Autowired
    SkuInfoService skuInfoService;



    // list/{page}/{limit}

    /**
     * 分页查询sku信息
     *
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/list/{page}/{limit}")
    public Result list(@PathVariable("page") Long page,
                       @PathVariable("limit") Long limit){

        Page<SkuInfo> page1 = new Page<>(page, limit);

        Page<SkuInfo> result = skuInfoService.page(page1);
        return Result.ok(result);


    }

    /**
     * 保存sku信息
     * @param skuInfo
     * @return
     */
    // /saveSkuInfo
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
            skuInfoService.saveSkuInfo(skuInfo);
            return Result.ok();
    }


    /**
     * 上架商品
     * @param skuId
     * @return
     */
    // /onSale/{skuId}
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId){
            skuInfoService.onSale(skuId);
            return Result.ok();
    }

    // /cancelSale/45

    /**
     * 下架sku
     *
     * @param skuId
     * @return
     */
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId){
        skuInfoService.cancelSale(skuId);
        return Result.ok();
    }







}
