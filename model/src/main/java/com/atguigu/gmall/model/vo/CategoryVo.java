package com.atguigu.gmall.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 返回给首页的分类id和name
 * @author fanyudong
 */
@Data
public class CategoryVo {
    private Long categoryId;
    private String categoryName;

    private List<CategoryVo> categoryChild;
}
