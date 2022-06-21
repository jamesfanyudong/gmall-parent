package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin/product")
@RestController
public class AttrInfoController {

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


        return Result.ok();
    }

}
