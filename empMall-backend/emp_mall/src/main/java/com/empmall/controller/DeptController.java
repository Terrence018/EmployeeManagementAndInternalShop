package com.empmall.controller;

import com.empmall.anno.Log;
import com.empmall.pojo.Dept;
import com.empmall.pojo.Result;
import com.empmall.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/dept")
@RestController
public class DeptController {

    //使用@Slf4j註解取代...
//    private static final Logger log = LoggerFactory.getLogger(DeptController.class);   //固定的代碼，紀錄日誌都會用到

    @Autowired
    private DeptService deptService;

    /**
     * 查詢部門列表
     */
    // @RequestMapping(value = "/depts",method = RequestMethod.GET)
    @GetMapping
    public Result list() {
        log.info("查詢全部的部門數據");
        List<Dept> deptList = deptService.findAll();
        return Result.success(deptList);
    }


    /**
     * 刪除部門(根據id)
     */
    @Log
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
//        System.out.println("根據ID刪除部門： "+ id);
        log.info("根據ID刪除部門： {}", id);
        deptService.deleteById(id);
        return Result.success();
    }

    /**
     * 新增部門
     */
    @Log
    @PostMapping
    public Result add(@RequestBody Dept dept){
//        System.out.println("新增部門： "+ dept);
        log.info("新增部門: {}",dept);
        deptService.add(dept);
        return Result.success();
    }

    /**
     * 查詢部門(根據id)
     */
    @GetMapping("{id}")
    public Result getInfo(@PathVariable Integer id){
//        System.out.println("根據ID查詢部門： " + id);
        log.info("根據ID查詢部門： {}", id);
        Dept dept = deptService.getById(id);
        return Result.success(dept);
    }

    /**
     * 修改部門
     */
    @Log
    @PutMapping
    public Result update(@RequestBody Dept dept){
//        System.out.println("修改部門： " + dept);
        log.info("修改部門: {}",dept);
        deptService.update(dept);
        return Result.success();
    }

}
