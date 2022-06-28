package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class BaseCategory3ServiceImpl extends ServiceImpl<BaseCategory3Mapper, BaseCategory3>
    implements BaseCategory3Service {

    /**
     * 查询二级分类下的三级分类列表
     * @param category2Id
     * @return
     */
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {

            QueryWrapper<BaseCategory3> wrapper = new QueryWrapper<>();
            wrapper.eq("category2_id",category2Id);
            List<BaseCategory3> category3s = this.list(wrapper);


        return category3s;
    }
}




