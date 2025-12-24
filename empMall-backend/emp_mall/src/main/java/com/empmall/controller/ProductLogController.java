package com.empmall.controller;

import com.empmall.pojo.PageResult;
import com.empmall.pojo.ProductLog;
import com.empmall.pojo.Result;
import com.empmall.service.ProductLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductLogController {

    @Autowired
    private ProductLogService productLogService;

    /**
     * 查詢商品日誌
     */
    @GetMapping("/productLog")
    public Result page(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<ProductLog> pageResult = productLogService.page(page, pageSize);
        return Result.success(pageResult);
    }
}