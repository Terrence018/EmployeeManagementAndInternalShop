package com.empmall.mapper;

import com.empmall.pojo.EmpLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmpLogMapper {

    // 員工日誌
    @Insert("insert into emp_log (emp_id, operate_time, info) values (#{empId}, #{operateTime}, #{info})")
    void insert(EmpLog empLog);

}
