package com.atguigu.gmall.front.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.ItemFeginClient;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import com.atguigu.gmall.product.SkuFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;


/**
 * 商品详情控制器
 */
@Controller
public class ItemController {
    @Autowired
    ItemFeginClient itemFeginClient;
    @Autowired
    SkuFeignClient skuFeignClient;


    /**
     * 查询sku的详情
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String item(@PathVariable("skuId") Long skuId, Model model){
        //商品详情服务 - 查询商品的详情。
        //        //1、sku的基本信息【名字、售价...】
        //        //2、sku所在的完整分类
        //        //3、sku所属的spu的名字
        //        //4、sku的所有图片
        //        //5、sku的销售属性
        //        //6、大数据推荐商品（大数据算出喜好，这个商品的周边，保存到一张表中）
        //        //7、sku的平台属性
        //        //8、库存
        //        //9、【评价不用立即查。点击以后发送请求，获取评价数据】
        //        //10、商品介绍
        Result<SkuDetailVo> skuDetail = itemFeginClient.getSkuDetail(skuId);
        SkuDetailVo vo = skuDetail.getData();
        SkuDetailVo data = skuDetail.getData();
        if (data != null){
            model.addAttribute("categoryView",vo.getCategoryView());
            model.addAttribute("skuInfo",vo.getSkuInfo());
            // 实时获取商品价格
            Result<BigDecimal> price = skuFeignClient.getSkuPrice(skuId);

            model.addAttribute("price",price.getData());
            model.addAttribute("spuSaleAttrList",vo.getSpuSaleAttrList());
            model.addAttribute("valuesSkuJson",vo.getValuesSkuJson());
        }else{
            return "item/error";
        }
       return "item/index";

    }

}
