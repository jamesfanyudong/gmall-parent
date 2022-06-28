package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.vo.CategoryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface CategoryBizMapper extends BaseMapper<CategoryVo> {
    List<CategoryVo> getCategorys();

}
