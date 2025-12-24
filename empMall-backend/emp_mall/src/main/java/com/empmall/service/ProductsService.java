package com.empmall.service;

import com.empmall.pojo.PageBean;
import com.empmall.pojo.ProductQueryParam;
import com.empmall.pojo.Products;
import java.util.List;

public interface ProductsService {

    /**
     * 商品列表
     */
    PageBean<Products> list(ProductQueryParam param);

    /**
     * 根據id查詢商品
     */
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