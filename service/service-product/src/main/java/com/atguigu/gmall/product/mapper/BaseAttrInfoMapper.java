package com.atguigu.gmall.product.mapper;


import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

/**
 * @Entity com.atguigu.gmall.product.domin.BaseAttrInfo
 */
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {

    List<BaseAttrInfo> getAttrInfoList(@Param("c1Id") Long c1Id,
                                       @Param("c2Id") Long c2Id,
                                       @Param("c3Id") Long c3Id);

}




