package com.atguigu.gmall.search;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author fanyudong
 */
@FeignClient("service-search")
@RequestMapping("/rpc/inner/search")
public interface SearchFeignClient {

    /**
     *  es检索商品
     * @param searchParm
     * @return
     */

    @PostMapping("/goods")
    public Result<Map<String,Object>> search(@RequestBody SearchParm searchParm);
//
//    @PostMapping("/goods")
//    public Result<SearchResponseVo> search(@RequestBody SearchParm searchParm);

    /**
     * 上线商品
     * @param goods
     * @return
     */
    @PostMapping("/up")
    public Result up(@RequestBody Goods goods);

    /**
     * 下线商品
     * @param skuId
     * @return
     */
    @GetMapping("/down/{skuId}")
    public Result down(@PathVariable("skuId") Long skuId);




}
