package com.empmall.controller;

import com.empmall.pojo.PageBean;
import com.empmall.pojo.ProductQueryParam;
import com.empmall.pojo.Products;
import com.empmall.pojo.Result;
import com.empmall.service.ProductsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductsService productsService;

    /**
     * 查詢商品列表
     */
    @GetMapping
    public Result list(ProductQueryParam param) {
        log.info("查詢商品列表參數: {}", param);
        // 呼叫 Service
        PageBean<Products> pageBean = productsService.list(param);

        return Result.success(pageBean);
    }

    /**
     * 查詢商品詳情(根據單一id)
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        log.info("查詢商品ID: {}", id);
        Products product = productsService.getById(id);
        return Result.success(product);
    }

    /**
     * 新增商品
     */
    @PostMapping
    public Result add(@RequestBody Products products) {
        log.info("新增商品: {}", products);
        productsService.save(products); // 請去 Service 補上 save 方法
        return Result.success();
    }

    /**
     * 修改商品 (含上下架)
     */
    @PutMapping
    public Result update(@RequestBody Products products) {
        log.info("修改商品: {}", products);
        productsService.update(products); // 請去 Service 補上 update 方法
        return Result.success();
    }

    /**
     * 刪除商品 (支援單刪與批量刪除)
     * 前端傳遞格式: /products/1,2,3
     */
    @DeleteMapping("/{ids}")
    public Result delete(@PathVariable List<Integer> ids) {
        log.info("執行刪除商品操作，IDs: {}", ids);

        productsService.delete(ids);
        return Result.success();
    }
}