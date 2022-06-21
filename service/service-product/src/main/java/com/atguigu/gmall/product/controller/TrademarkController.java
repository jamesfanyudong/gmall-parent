package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product")
public class TrademarkController {


    @Autowired
    BaseTrademarkService baseTrademarkService;

    // admin/product/baseTrademark/1/10

    /**
     * 分页查询
     * @param page 第几页
     * @param limit 每页记录数数
     * @return
     */
    @GetMapping("/baseTrademark/{page}/{limit}")
    public Result queryBaseTrademarkPage(@PathVariable("page") Long page,
                                         @PathVariable("limit") Long limit){
        Page<BaseTrademark> page1 = new Page<>(page,limit);

        Page<BaseTrademark> result = baseTrademarkService.page(page1);

        return Result.ok(result);

    }

    /**
     *
     * 根据id 查询 baseTrademark
     * @param id
     * @return
     */
    //baseTrademark/get/2
    @GetMapping("/baseTrademark/get/{id}")
    public Result  getBaseTrademark(@PathVariable Long id){
        BaseTrademark trademark = baseTrademarkService.getById(id);
        return Result.ok(trademark);
    }

    /**
     * 根据id 删除 baseTrademark
     * @param id
     * @return
     */
    // baseTrademark/remove/2
    @DeleteMapping("/baseTrademark/remove/{id}")
    public Result removeBaseTrademark(@PathVariable Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }

    //baseTrademark/save

    /**
     * 保存 品牌
     * @param baseTrademark
     * @return
     */
    @PostMapping("/baseTrademark/save")
    public Result saveBaseTrademark(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }



    @PutMapping("/baseTrademark/update")
    public Result updateBaseTrademark(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }




}
