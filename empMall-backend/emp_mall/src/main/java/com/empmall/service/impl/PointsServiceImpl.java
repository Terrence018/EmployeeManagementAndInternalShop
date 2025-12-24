package com.empmall.service.impl;

import com.empmall.mapper.EmpMapper;
import com.empmall.mapper.PointsLogMapper;
import com.empmall.pojo.Emp;
import com.empmall.pojo.PageResult;
import com.empmall.pojo.PointsLog;
import com.empmall.service.PointsService;
import com.empmall.utils.CurrentHolder;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PointsServiceImpl implements PointsService {

    @Autowired
    private EmpMapper empMapper;

    @Autowired
    private PointsLogMapper pointsLogMapper;

    @Override
    @Transactional // 事務控制：兩步操作要嘛全成功，要嘛全失敗
    public void grantPoints(Integer empId, Integer amount, String description) {
        // 1. 查詢該員工目前點數
        Emp emp = empMapper.getById(empId);
        if (emp == null) return; // 員工不存在

        // 2. 計算新餘額
        int currentPoints = (emp.getPoints() == null) ? 0 : emp.getPoints();
        int newBalance = currentPoints + amount;

        // 3. 更新員工表 (EmpMapper 裡原本就有 updatePoints 方法)
        empMapper.updatePoints(empId, newBalance);

        // 4. 寫入流水帳
        PointsLog log = new PointsLog();
        log.setEmpId(empId);
        log.setType(1); // 1 代表增加
        log.setPoints(amount);
        log.setBalance(newBalance);
        log.setInfo(description);
        log.setCreateTime(LocalDateTime.now());

        pointsLogMapper.insert(log);
    }

    @Override
    public List<PointsLog> listAll() {
        return pointsLogMapper.listAll();
    }

    @Override
    public List<PointsLog> listByEmpId(Integer empId) {
        return pointsLogMapper.listByEmpId(empId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void issuePoints(PointsLog pointsLog) {
        // 1. 補全基本屬性 (時間)
        pointsLog.setCreateTime(LocalDateTime.now());

        // 2. 獲取當前操作人(管理員)的 ID
        Integer operatorId = CurrentHolder.getCurrentId();
        if (operatorId == null) {
            throw new RuntimeException("無法獲取操作人資訊，請重新登入");
        }
        pointsLog.setOperatorId(operatorId);

        // 3. ✅ [關鍵修改] 計算變動後的「結餘」 (Balance)
        // 3.1 先查詢該員工目前的狀態
        Emp emp = empMapper.getById(pointsLog.getEmpId());
        if (emp == null) {
            throw new RuntimeException("找不到該員工 ID: " + pointsLog.getEmpId());
        }

        // 3.2 取得目前點數 (防呆：如果是 null 視為 0)
        int currentPoints = (emp.getPoints() == null) ? 0 : emp.getPoints();

        // 3.3 計算新餘額 = 目前點數 + 變動點數 (變動點數可能是負的)
        int newBalance = currentPoints + pointsLog.getPoints();

        // 3.4 將算出來的結餘設定到 Log 物件中，準備寫入資料庫
        pointsLog.setBalance(newBalance);

        // 4. 寫入紀錄表 (此時 pointsLog 已經包含 empId, points, balance, info, operatorId)
        pointsLogMapper.insert(pointsLog);

        // 5. 更新員工表中的點數餘額 (原子操作更新)
        empMapper.updatePointsBalance(pointsLog.getEmpId(), pointsLog.getPoints());
    }

    // ✅ 實作 list 方法
    @Override
    public PageResult<PointsLog> list(Integer page, Integer pageSize, String name) {
        // 1. 設定分頁參數
        PageHelper.startPage(page, pageSize);

        // 2. 執行查詢 (Mapper 需要有對應的 list 方法)
        List<PointsLog> list = pointsLogMapper.list(name);

        // 3. 強制轉型為 Page 物件以獲取總筆數
        Page<PointsLog> p = (Page<PointsLog>) list;

        // 4. 封裝回傳
        return new PageResult<>(p.getTotal(), p.getResult());
    }



}