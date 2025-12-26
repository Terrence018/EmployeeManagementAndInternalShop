package com.empmall.service.impl;

import com.empmall.mapper.EmpExprMapper;
import com.empmall.mapper.EmpMapper;
import com.empmall.pojo.*;
import com.empmall.service.EmpLogService;
import com.empmall.service.EmpService;
import com.empmall.utils.CurrentHolder;
import com.empmall.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmpServiceImpl implements EmpService {

    @Autowired
    private EmpMapper empMapper;

    @Autowired
    private EmpExprMapper empExprMapper;

    @Autowired
    private EmpLogService empLogService;

    // 統計相關方法
    @Override
    public List<EmpReportVO> getGenderStats() {
        return empMapper.countByGender();
    }

    @Override
    public List<EmpReportVO> getDeptStats() {
        return empMapper.countByDept();
    }

    // 分頁查詢
    @Override
    public PageResult<Emp> page(EmpQueryParam empQueryParam) {
        // 1. 設置分頁參數
        com.github.pagehelper.PageHelper.startPage(empQueryParam.getPage(), empQueryParam.getPageSize());

        // 2. 執行查詢
        List<Emp> empList = empMapper.list(empQueryParam);

        // 3. 解析查詢結果
        com.github.pagehelper.Page<Emp> p = (com.github.pagehelper.Page<Emp>) empList;

        return new PageResult<>(p.getTotal(), p.getResult());
    }

    // 新增員工
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void save(Emp emp) throws Exception {
        // 1. 補全基礎信息
        emp.setCreateTime(LocalDateTime.now());
        emp.setUpdateTime(LocalDateTime.now());
        emp.setStatus(1); // 預設狀態為在職

        // 設定預設 role、積分
        if (emp.getRole() == null) emp.setRole(2);
        if (emp.getPoints() == null) emp.setPoints(0);

        // 2. 插入員工表
        empMapper.insert(emp);

        // 3. 保存員工經歷信息
        List<EmpExpr> exprList = emp.getExprList();
        if (!CollectionUtils.isEmpty(exprList)) {
            exprList.forEach(empExpr -> {
                empExpr.setEmpId(emp.getId());
            });
            empExprMapper.insertBatch(exprList);
        }

        // 4. 紀錄日誌
        recordEmpLog("新增員工：" + emp.getName());
    }

    // 批量刪除
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void delete(List<Integer> ids) {

        // 安全檢查：禁止刪除 ID 為 1 的金牌管理員
        if (ids.contains(1)) {
            throw new RuntimeException("操作失敗：無法刪除最高權限管理員！");
        }

        // 1：在刪除前，先查出這些 ID 對應的員工名字 (用於日誌)
        List<Emp> emps = empMapper.selectBatchIds(ids);
        String empNames = emps.stream()
                .map(Emp::getName)
                .collect(Collectors.joining(", "));

        // 2. 刪除員工基本信息
        //邏輯刪除(底層 SQL 執行的是 UPDATE status=0)
        empMapper.deleteByIds(ids);

        // 3. 刪除員工的工作經歷信息
        //先保留員工的工作經歷紀錄，不進行物理刪除
        // empExprMapper.deleteByEmpIds(ids);

        // 4. 根據刪除數量，動態寫入日誌
        String logContent;
        if (ids.size() == 1) {
            logContent = "刪除員工：" + empNames;
        } else {
            logContent = "批量刪除員工：" + empNames;
        }

        // 執行記錄
        recordEmpLog(logContent);
    }

    // 獲取詳情
    @Override
    public Emp getInfo(Integer id) {
        return empMapper.getById(id);
    }

    // 編輯員工
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Emp emp) {
        Emp oldEmp = empMapper.getById(emp.getId());

        // 1. 根據 ID 修改員工信息
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.updateById(emp);

        // 2. 修改工作經歷 (先刪後加)
        empExprMapper.deleteByEmpIds(Arrays.asList(emp.getId()));

        List<EmpExpr> exprList = emp.getExprList();
        if (!CollectionUtils.isEmpty(exprList)) {
            exprList.forEach(empExpr -> {
                empExpr.setEmpId(emp.getId());
            });
            empExprMapper.insertBatch(exprList);
        }

        // 3. 紀錄日誌 (會自動調用 generateDiffLog 來比對圖片)
        String diff = generateDiffLog(oldEmp, emp);
        if (diff.length() > 0) {
            recordEmpLog("管理員修改員工 [" + oldEmp.getName() + "] 資料：" + diff);
        } else {
            recordEmpLog("管理員修改員工 [" + oldEmp.getName() + "] (無基本資料變更)");
        }
    }

    /**
     * 登入
     */
    @Override
    public LoginInfo login(Emp emp) {
        // 1. 查詢員工
        Emp e = empMapper.selectByUsernameAndPassword(emp);

        // 2. 判斷是否存在
        if (e != null) {
            log.info("登陸成功, 員工信息: {}", e);

            // JWT 加入權限標識
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", e.getId());
            claims.put("username", e.getUsername());
            claims.put("name", e.getName());
            claims.put("role", e.getRole());

            String jwt = JwtUtils.generateToken(claims);

            return new LoginInfo(e.getId(), e.getUsername(), e.getName(), jwt, e.getRole(), e.getPoints());
        }
        return null;
    }

    /**
     * 通用日誌紀錄方法s
     */
    private void recordEmpLog(String info) {
        try {
            // 1. 獲取當前操作人 ID
            Integer currentId = CurrentHolder.getCurrentId();
            if (currentId == null) {
                currentId = 1; // 預設值，防止系統操作時報錯
            }

            // 2. 構建日誌物件
            EmpLog empLog = new EmpLog();
            empLog.setEmpId(currentId);
            empLog.setOperateTime(LocalDateTime.now());
            empLog.setInfo(info);

            // 3. 寫入資料庫
            empLogService.insertLog(empLog);
        } catch (Exception e) {
            log.error("紀錄業務日誌失敗: {}", info, e);
        }
    }

    /**
     * 員工修改資料
     */
    @Override
    public void updatePersonal(Emp emp) {
        Emp oldEmp = empMapper.getById(emp.getId());

        // 呼叫 Mapper 只更新特定欄位
        empMapper.updatePersonal(emp);

        String diff = generateDiffLog(oldEmp, emp);

        if (diff.length() > 0) {
            recordEmpLog("員工自行修改資料：" + diff);
        } else {
            recordEmpLog("員工自行修改資料 (無內容變更)");
        }
    }

    @Override
    public List<EmpReportVO> getSalaryStats() {
        return empMapper.countBySalary();
    }

    @Override
    public List<EmpReportVO> getPointsRankStats() {
        return empMapper.getPointsRank();
    }

    @Override
    public List<EmpReportVO> getEntryTrendStats() {
        return empMapper.countByEntryYear();
    }

    /**
     * 輔助方法：比對新舊員工資料，產生變更明細字串
     */
    private String generateDiffLog(Emp oldEmp, Emp newEmp) {
        if (oldEmp == null) return "無法比對：舊資料為空"; // 加這行防呆
        StringBuilder changes = new StringBuilder();
        
        // 1. 比對姓名
        if (newEmp.getName() != null && !newEmp.getName().equals(oldEmp.getName())) {
            changes.append("姓名:[").append(oldEmp.getName()).append("→").append(newEmp.getName()).append("] ");
        }

        // 2. 比對手機
        if (newEmp.getPhone() != null && !newEmp.getPhone().equals(oldEmp.getPhone())) {
            changes.append("手機:[").append(oldEmp.getPhone()).append("→").append(newEmp.getPhone()).append("] ");
        }

        // 3. 比對 Email
        if (newEmp.getEmail() != null && !newEmp.getEmail().equals(oldEmp.getEmail())) {
            changes.append("信箱:[").append(oldEmp.getEmail()).append("→").append(newEmp.getEmail()).append("] ");
        }

        // 4. 比對薪資 (只有管理員會改)
        if (newEmp.getSalary() != null && !newEmp.getSalary().equals(oldEmp.getSalary())) {
            changes.append("薪資:[").append(oldEmp.getSalary()).append("→").append(newEmp.getSalary()).append("] ");
        }

        // 5. 比對部門
        if (newEmp.getDeptId() != null && !newEmp.getDeptId().equals(oldEmp.getDeptId())) {
            changes.append("部門ID:[").append(oldEmp.getDeptId()).append("→").append(newEmp.getDeptId()).append("] ");
        }

        // 6. 比對職位
        if (newEmp.getJob() != null && !newEmp.getJob().equals(oldEmp.getJob())) {
            changes.append("職位:[").append(oldEmp.getJob()).append("→").append(newEmp.getJob()).append("] ");
        }

        // 7. 比對密碼
        if (newEmp.getPassword() != null && !newEmp.getPassword().isEmpty() && !newEmp.getPassword().equals(oldEmp.getPassword())) {
            changes.append("[密碼已變更] ");
        }

        // 8. 比對圖片 (頭像)
        if (newEmp.getImage() != null && !newEmp.getImage().equals(oldEmp.getImage())) {
            changes.append("[頭像已更新] ");
        }

        return changes.toString();
    }
}