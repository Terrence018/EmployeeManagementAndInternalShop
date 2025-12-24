package com.empmall.controller;

import com.empmall.pojo.GoodsSalesVO;
import com.empmall.pojo.Result;
import com.empmall.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 查詢商品銷量統計報表
     * GET /goods/stats?orderBy=1
     */
    @GetMapping("/stats")
    public Result getSalesStats(@RequestParam(value = "orderBy", defaultValue = "1") Integer orderBy) {
        log.info("查詢商品銷量統計, 排序方式: {}", orderBy);
        List<GoodsSalesVO> list = goodsService.getSalesStats(orderBy);
        return Result.success(list);
    }
}