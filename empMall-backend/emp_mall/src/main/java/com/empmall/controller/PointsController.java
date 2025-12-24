package com.empmall.controller;

import com.empmall.pojo.PageResult; // 假設你有這個分頁結果類
import com.empmall.pojo.PointsLog;
import com.empmall.pojo.Result;
import com.empmall.service.PointsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/points")
public class PointsController {

    @Autowired
    private PointsService pointsService;

    /**
     * 發放/扣除點數
     * 前端路徑: POST /points
     * 前端參數: { "empId": 1, "points": 10, "info": "備註" }
     */
    @PostMapping
    public Result issuePoints(@RequestBody PointsLog pointsLog) {

        log.info("發放點數請求: {}", pointsLog);

        pointsService.issuePoints(pointsLog); // 呼叫 Service
        return Result.success();
    }

    /**
     * 查詢所有紀錄 (支援分頁與搜尋)
     * 前端路徑: GET /points/log
     * 前端參數: ?page=1&pageSize=10&name=xxx
     */
    @GetMapping("/log")
    public Result list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String name // 接收搜尋條件
    ) {
        log.info("查詢點數紀錄: page={}, pageSize={}, name={}", page, pageSize, name);

        // 呼叫 Service 進行分頁查詢
        PageResult<PointsLog> pageResult = pointsService.list(page, pageSize, name);

        return Result.success(pageResult);
    }

    /**
     * 查詢個人紀錄 (員工用)
     * 保持不變，這看起來沒問題
     */
    @GetMapping("/log/my/{empId}")
    public Result listMyLog(@PathVariable Integer empId) {
        List<PointsLog> list = pointsService.listByEmpId(empId);
        return Result.success(list);
    }
}