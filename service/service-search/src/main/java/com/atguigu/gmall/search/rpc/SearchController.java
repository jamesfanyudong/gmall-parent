package com.atguigu.gmall.search.rpc;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParam;
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
    public Result<SearchResponseVo> search(@RequestBody SearchParam searchParm){
       SearchResponseVo vo =  goodsSearchService.search(searchParm);
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

    /**
     * 下架商品
     * @param skuId
     * @return
     */

    @GetMapping("/down/{skuId}")
    public Result down(@PathVariable("skuId") Long skuId){
        goodsSearchService.downGoods(skuId);
        return Result.ok();

    }

    /**
     * 增加热度分
     * @return
     */
    @GetMapping("/incr/hotscore/{skuId}")
    public Result incrHotScore(@PathVariable("skuId") Long skuId,
                               @RequestParam("score") Long score){
        goodsSearchService.incrHotScore(skuId, score);
        return Result.ok();

    }


}
