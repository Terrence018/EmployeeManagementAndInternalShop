package com.empmall.service;

import com.empmall.pojo.PageBean;
import com.empmall.pojo.ProductQueryParam;
import com.empmall.pojo.Products;
import java.util.List;

public interface ProductsService {

    // 原有的查詢方法
    PageBean<Products> list(ProductQueryParam param);
    Products getById(Integer id);

    /**
     * 新增商品
     */
    void save(Products products);

    /**
     * 修改商品
     */
    void update(Products products);

    /**
     * 刪除商品
     */
    void delete(List<Integer> ids);
}