package com.atguigu.gmall.product.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.dto.CategoryViewDo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.vo.CategoryView;
import com.atguigu.gmall.product.service.CategoryViewService;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.biz.SpudeSkuSaleAttrBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;


@RequestMapping("/rpc/inner/product")
@RestController
public class SkuRpcController {

    
    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    CategoryViewService categoryViewService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;


    @Autowired
    SpudeSkuSaleAttrBizService spudeSkuSaleAttrBizService;


    /**
     * 查询skuInfo信息
     * @param skuId
     * @return
     */

    @GetMapping("/skuinfo/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable("skuId") Long skuId){
        SkuInfo info = skuInfoService.getById(skuId);
        return Result.ok(info);
    }

    /**
     * 根据c3Id查询完整路径
     */

    @GetMapping("/categoryview/{c3Id}")
    public Result<CategoryView> getCategoryView(@PathVariable("c3Id") Long c3Id){
        CategoryViewDo viewDo =  categoryViewService.getViewByC3Id(c3Id);

        //把do转成页面需要的vo；
        CategoryView categoryView = new CategoryView();
        categoryView.setCategory1Id(viewDo.getId());
        categoryView.setCategory1Name(viewDo.getName());
        categoryView.setCategory2Id(viewDo.getC2id());
        categoryView.setCategory2Name(viewDo.getC2name());
        categoryView.setCategory3Id(viewDo.getC3id());
        categoryView.setCategory3Name(viewDo.getC3name());
        return Result.ok(categoryView);

    }

    /**
     * 根据skuId和spuId查询出当前商品spu定义的所有销售属性名和值以及标记出当前sku是哪一对组合
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/sku/saleattr/{skuId}/{spuId}")
    public Result<List<SpuSaleAttr>> getSaleAttr(@PathVariable("skuId") Long skuId,
                                                 @PathVariable("spuId") Long spuId){
        List<SpuSaleAttr> list =  spuSaleAttrService.getSpuSaleAttrAndMarkSkuSaleValue(skuId,spuId);
        return Result.ok(list);
    }


    /**
     * 查出这个sku对应的spu到底有多少个sku组合，以及每个sku销售属性值组合封装成Map（"值1|值2|值N":skuId）
     */

    @GetMapping("/spu/skus/saleattrvalue/json/{spuId}")
    public Result<String> getSpudeAllSkuSaleAttrAndValue(@PathVariable("spuId") Long spuId){
        String json = spudeSkuSaleAttrBizService.getSpudeAllSkuSaleAttrAndValue(spuId);
        return Result.ok(json);
    }


    @GetMapping("/skuinfo/skuPrice/{skuId}")
    Result<BigDecimal> getSkuPrice(Long skuId){

      BigDecimal price =  skuInfoService.getSkuPrice(skuId);
      return Result.ok(price);

    }









}
