package com.atguigu.gmall.search.rpc;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParm;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import com.atguigu.gmall.search.service.GoodsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author fanyudong
 */
@RestController
@RequestMapping("/rpc/inner/search")
public class SearchController {
    @Autowired
    GoodsSearchService goodsSearchService;




    @PostMapping("/goods")
    public Result<SearchResponseVo> search(@RequestBody SearchParm searchParm){
       SearchResponseVo vo=  goodsSearchService.search(searchParm);
       return Result.ok(vo);

    }

    /**
     * 下线
     * @param goods
     * @return
     */

    @PostMapping("/up")
    public Result up(@RequestBody Goods goods){
        goodsSearchService.upGoods(goods);
        return Result.ok();

    }

    @GetMapping("/down/{skuId}")
    public Result down(@PathVariable("skuId") Long skuId){
        goodsSearchService.downGoods(skuId);
        return Result.ok();

    }


}
