package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
    implements BaseAttrInfoService {
    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    BaseAttrValueService baseAttrValueService;
    /**
     * 查询 各级分类 属性信息
     * @param c1Id
     * @param c2Id
     * @param c3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> getAttrInfoList(Long c1Id, Long c2Id, Long c3Id) {


        return baseAttrInfoMapper.getAttrInfoList(c1Id,c2Id,c3Id);
    }

    /**
     *
     * 更改属性信息
     * @param baseAttrInfo
     */
    @Override
    public void updateAttrInfo(BaseAttrInfo baseAttrInfo) {

        //1、修改属性名(名，分类，层级)
        baseAttrInfoMapper.updateById(baseAttrInfo);

        //2、修改属性值
        ArrayList<Long> ids = new ArrayList<>();
        List<BaseAttrValue> valueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue attrValue : valueList) {

            // 新增·11
            if (attrValue.getId() == null){
                //新增
                attrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueService.save(attrValue);

            }



            if (attrValue.getId() != null){
                // 修改
                baseAttrValueService.updateById(attrValue);
                ids.add(attrValue.getId());

            }
        }

        //2.3、删除(前端没带的值id，就是删除)
        //1、查出12原来是 59,60,61
        //2、前端带的id   60,61
        //3、计算差集：  59
        // delete * from base_attr_value
        // where attr_id=12 and id not in(60,61)
        if (ids.size() > 0){ // 删除部分
            QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id",baseAttrInfo.getId());
            wrapper.notIn("id",ids);
            baseAttrValueService.remove(wrapper);
        }else {
            QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id",baseAttrInfo.getId());
            baseAttrValueService.remove(wrapper);
        }


    }

    /**
     * 保存属性信息
     * @param baseAttrInfo
     */

    @Transactional
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        // 保存属性名
        baseAttrInfoMapper.insert(baseAttrInfo);
        Long att_id = baseAttrInfo.getId();

        //2、属性值信息保存
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue attrValue : attrValueList) {
            // 保存att_id
            attrValue.setAttrId(att_id);
        }
        // 批量保存
        baseAttrValueService.saveBatch(attrValueList);



    }
}




