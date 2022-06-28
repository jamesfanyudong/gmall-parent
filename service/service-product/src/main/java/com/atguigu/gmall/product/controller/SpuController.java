package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
public class SpuController {

    @Autowired
    SpuInfoService spuInfoService;

    @Autowired
    SpuImageService spuImageService;



    /**
     *
     * 根据spuid 获取图片列表
     * @param spuId
     * @return
     */
    @GetMapping("/spuImageList/{spuId}")
    public Result  getSpuImageList(@PathVariable("spuId") Long spuId){
        List<SpuImage> spuImageList =  spuImageService.getSpuImageList(spuId);
        return Result.ok(spuImageList);
    }
    // /1/10?category3Id=61

    /**
     *
     * 查询三级分类的 spu信息
     * @param category3Id
     * @param page 第几页
     * @param limit 每页的记录数
     * @return
     */
    @GetMapping("/{page}/{limit}")
    public Result getSpuList(@RequestParam("category3Id") Long category3Id,
                             @PathVariable("page") Long page,
                             @PathVariable("limit") Long limit){
        Page<SpuInfo> page1 = new Page<>(page, limit);
        QueryWrapper<SpuInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id",category3Id);
        Page<SpuInfo> result = spuInfoService.page(page1, wrapper);
                return Result.ok(result);


    }

    // /spuSaleAttrList/{spuId}





    /**
     * 保存spu 信息
     * @param spuInfo
     * @return
     */
    // /saveSpuInfo
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        spuInfoService.saveSpuInfo(spuInfo);
        return Result.ok();

    }


}
