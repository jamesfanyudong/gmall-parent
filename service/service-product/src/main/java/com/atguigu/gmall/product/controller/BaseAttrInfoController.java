package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/product")
@RestController
@Slf4j
public class BaseAttrInfoController {

    @Autowired
    BaseAttrInfoService baseAttrInfoService;
    @Autowired
    BaseAttrValueService baseAttrValueService;

    /**
     * 查询 各级分类 属性信息
     * @param c1Id
     * @param c2Id
     * @param c3Id
     * @return
     */
    // admin/product/attrInfoList/3/0/0
    @RequestMapping("/attrInfoList/{c1id}/{c2id}/{c3id}")
    public Result attrInfoList(@PathVariable("c1id") Long c1Id,
                               @PathVariable("c2id") Long c2Id,
                               @PathVariable("c3id") Long c3Id
                               ){
        List<BaseAttrInfo> attrInfos =  baseAttrInfoService.getAttrInfoList(c1Id,c2Id,c3Id);
        return Result.ok(attrInfos);
    }

    /**
     * 保存属性信息（属性名称和属性值）
     * @param baseAttrInfo
     * @return
     */
    //saveAttrInfo
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody  BaseAttrInfo baseAttrInfo){
        log.info("保存/修改属性: {}",baseAttrInfo);
        if (baseAttrInfo.getId() != null){
            // 修改
            baseAttrInfoService.updateAttrInfo(baseAttrInfo);
        } else{
            // 保存

            baseAttrInfoService.saveAttrInfo(baseAttrInfo);
        }


        return Result.ok();

    }

    /**
     * 回显属性信息
     * @param id
     * @return
     */

    ///getAttrValueList/1
    @GetMapping("/getAttrValueList/{id}")
    public Result getAttrValueList(@PathVariable Long id){
        QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_id",id);

        List<BaseAttrValue> values = baseAttrValueService.list(wrapper);
        return Result.ok(values);

    }



}
