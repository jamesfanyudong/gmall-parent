package com.atguigu.gmall.product.rpc;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.biz.CategoryBizService;
import com.atguigu.gmall.model.vo.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/rpc/inner/product")
@RestController
public class CategoryRpcController {

    @Autowired
    CategoryBizService categoryBizService;
    /**
     * 获取系统所有三级分类并组装成树形结构
     * 把对象自动序列化成json数据。
     * 序列化：   把内存中的 JavaBean 表示成一个实际的文本或者二进制文件方式。 为了传输或者持久化
     * 反序列化：  把之前传输过来或者从磁盘中读取到的文本或文件，转为内存中的JavaBean。 为了方便编码使用。
     *
     * RPC远程调用？
     *
     * @return
     */
    @GetMapping("/categorys/all")
    public Result<List<CategoryVo>> getCategorys(){
       List<CategoryVo> vos =  categoryBizService.getCategorys();
       return Result.ok(vos);
    }






}
