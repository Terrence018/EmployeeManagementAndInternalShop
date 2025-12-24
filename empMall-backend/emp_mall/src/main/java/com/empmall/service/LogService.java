package com.empmall.service;

import com.empmall.pojo.EmpLog;
import com.empmall.pojo.OperateLog;
import com.empmall.pojo.PageResult;

public interface LogService {
    PageResult<OperateLog> pageOperate(int page, int pageSize);
    PageResult<EmpLog> pageEmp(int page, int pageSize);

    void insertLog(EmpLog empLog);
}