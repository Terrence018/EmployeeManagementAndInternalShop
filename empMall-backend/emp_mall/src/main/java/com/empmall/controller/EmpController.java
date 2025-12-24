package com.empmall.controller;

import com.empmall.mapper.EmpMapper;
import com.empmall.pojo.*;
import com.empmall.pojo.EmpReportVO;
import com.empmall.service.EmpService;
import com.empmall.utils.CurrentHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

//員工管理Controller
@Slf4j
@RequestMapping("/emps")
@RestController
public class EmpController {

    @Autowired
    private EmpService empService;

    @Autowired
    private EmpMapper empMapper; // 為了方便直接調用 mapper，正規建議走 Service


    //分頁查詢
    @GetMapping
    public Result page(EmpQueryParam empQueryParam){
        log.info("分頁查詢：{}",empQueryParam);
        PageResult<Emp> pageResult = empService.page(empQueryParam);
        return Result.success(pageResult);
    }

    //新增員工
    @PostMapping
    public Result save(@RequestBody Emp emp) throws Exception {
        log.info("新增員工: {}", emp);
        empService.save(emp);
        return Result.success();
    }

    //刪除員工-使用集合List
    @DeleteMapping
    public Result delete (@RequestParam List<Integer> ids){
        log.info("刪除員工： {}", ids);
        empService.delete(ids);
        return Result.success();
    }

    //根據員工id查詢基本信息
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id ){
        log.info("根據員工查詢基本信息： {}", id);
        Emp emp = empService.getInfo(id);
        return Result.success(emp);
    }

    /**
     * 修改員工
     */
    @PutMapping
    public Result update(@RequestBody Emp emp){
        log.info("修改員工： {}", emp);
        empService.update(emp);
        return Result.success();
    }


    /**
     * 統計-性別 API
     * GET /emps/report/gender
     */
    @GetMapping("/report/gender")
    public Result reportGender() {
        log.info("獲取員工性別統計數據");
        List<EmpReportVO> list = empService.getGenderStats();
        return Result.success(list);
    }

    /**
     * 統計-部門 API
     * GET /emps/report/dept
     */
    @GetMapping("/report/dept")
    public Result reportDept() {
        log.info("獲取員工部門統計數據");
        List<EmpReportVO> list = empService.getDeptStats();
        return Result.success(list);
    }

    // 員工自行修改個人資料(部分)
    @PutMapping("/updatePersonal")
    public Result updatePersonal(@RequestBody Emp emp) {

        // 1. 檢查驗證碼 (使用者信箱)

        if (emp.getCode() == null || emp.getCode().isEmpty()) {
            return Result.error("請輸入驗證碼");
        }

        boolean isValid = EmailController.checkCode(emp.getEmail(), emp.getCode());
        if (!isValid) {
            return Result.error("驗證碼錯誤或已過期");
        }

        // 2. 執行更新
        emp.setUpdateTime(LocalDateTime.now());
        empService.updatePersonal(emp);

        // 3. 清除驗證碼 (避免重複使用)
        EmailController.removeCode(emp.getEmail());

        return Result.success("資料修改成功");
    }


    // 忘記密碼/重置密碼
    @PostMapping("/reset-password")
    public Result resetPassword(@RequestBody ResetPasswordRequest req) {

        // 1. 檢查驗證碼是否正確 (呼叫 EmailController 的靜態方法)
        boolean isCodeValid = EmailController.checkCode(req.getEmail(), req.getCode());
        if (!isCodeValid) {
            return Result.error("驗證碼錯誤或已過期");
        }

        // 2. 檢查 Email 是否存在
        Emp emp = empMapper.getByEmail(req.getEmail());
        if (emp == null) {
            return Result.error("該 Email 尚未註冊員工帳號");
        }

        // 3. 執行密碼更新
        empMapper.updatePasswordByEmail(req.getEmail(), req.getNewPassword());

        // 4. 清除驗證碼
        EmailController.removeCode(req.getEmail());

        return Result.success("密碼重置成功，請使用新密碼登入");
    }

    /**
     * 統計-薪資分布 API
     * GET /emps/report/salary
     */
    @GetMapping("/report/salary")
    public Result reportSalary() {
        log.info("獲取員工薪資統計數據");
        List<EmpReportVO> list = empService.getSalaryStats();
        return Result.success(list);
    }

    /**
     * 統計-點數排行 API
     * GET /emps/report/pointsRank
     */
    @GetMapping("/report/pointsRank")
    public Result reportPointsRank() {
        log.info("獲取員工點數排行數據");
        List<EmpReportVO> list = empService.getPointsRankStats();
        return Result.success(list);
    }
    
    /**
     * 統計-入職趨勢 API
     * GET /emps/report/entryTrend
     */
    @GetMapping("/report/entryTrend")
    public Result reportEntryTrend() {
        log.info("獲取員工入職趨勢數據");
        List<EmpReportVO> list = empService.getEntryTrendStats();
        return Result.success(list);
    }



}
