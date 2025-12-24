package com.empmall.controller;

import com.empmall.pojo.EmpLog;
import com.empmall.pojo.Result;
import com.empmall.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    private LogService logService;

    /**
     * 查詢操作日誌
     */
    @GetMapping("/operate")
    public Result pageOperate(@RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(logService.pageOperate(page, pageSize));
    }

    /**
     * 查詢員工日誌
     */
    @GetMapping("/emp")
    public Result pageEmp(@RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(logService.pageEmp(page, pageSize));
    }

    /**
     * 新增員工日誌
     */
    @PostMapping("/add")
    public Result addLog(@RequestBody EmpLog empLog) {
        log.info("接收前端日誌請求: {}", empLog.getInfo());
        logService.insertLog(empLog);
        return Result.success();
    }



}