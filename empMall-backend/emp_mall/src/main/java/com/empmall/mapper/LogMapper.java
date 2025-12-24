package com.empmall.mapper;

import com.empmall.pojo.EmpLog;
import com.empmall.pojo.OperateLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface LogMapper {

    /**
     * 查詢系統操作日誌 (operate_log)
     */
    // 注意：資料庫欄位是 operate_emp_id，POJO 是 operateEmpId，MyBatis 開啟駝峰映射會自動對應
    // 我們額外 JOIN emp 表來拿 name，並別名為 operateEmpName
    @Select("select o.*, e.name as operateEmpName " +
            "from operate_log o " +
            "left join emp e on o.operate_emp_id = e.id " +
            "order by o.operate_time desc")
    List<OperateLog> listOperateLogs();

    /**
     * 查詢員工業務日誌 (emp_log)
     */
    @Select("select el.*, e.name as empName " +
            "from emp_log el " +
            "left join emp e on el.emp_id = e.id " +
            "order by el.operate_time desc")
    List<EmpLog> listEmpLogs();

    /**
     * 插入員工日誌
     * @param empLog
     */
    @Insert("insert into emp_log (emp_id, operate_time, info) " +
            "values (#{empId}, #{operateTime}, #{info})")
    void insertEmpLog(EmpLog empLog);
}