package com.empmall.service.impl;

import com.empmall.mapper.LogMapper;
import com.empmall.pojo.EmpLog;
import com.empmall.pojo.OperateLog;
import com.empmall.pojo.PageResult;
import com.empmall.service.LogService;
import com.empmall.utils.CurrentHolder;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {
    @Autowired
    private LogMapper logMapper;

    @Override
    public PageResult<OperateLog> pageOperate(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<OperateLog> list = logMapper.listOperateLogs();
        Page<OperateLog> p = (Page<OperateLog>) list;
        return new PageResult<>(p.getTotal(), p.getResult());
    }

    @Override
    public PageResult<EmpLog> pageEmp(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<EmpLog> list = logMapper.listEmpLogs();
        Page<EmpLog> p = (Page<EmpLog>) list;
        return new PageResult<>(p.getTotal(), p.getResult());
    }

    @Override
    public void insertLog(EmpLog empLog) {
        // 1. 補全操作時間
        empLog.setOperateTime(LocalDateTime.now());

        // 2. 補全操作人 ID (從 Token 解析出的 CurrentHolder 拿)
        Integer currentId = CurrentHolder.getCurrentId();
        empLog.setEmpId(currentId != null ? currentId : 0); // 若無 ID 則設為 0 或處理例外

        // 3. 呼叫 Mapper 寫入
        // ⚠️ 注意：你的 LogMapper 內必須要有 insert 方法 (原本的代碼沒貼 LogMapper，請確認有)
        // 這裡假設是針對 emp_log 表操作
        logMapper.insertEmpLog(empLog);
    }
}