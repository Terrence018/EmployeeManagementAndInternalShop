package com.empmall.service;

import com.empmall.pojo.*;
import com.empmall.pojo.EmpReportVO;

import java.util.List;

public interface EmpService {
    //分頁查詢
    PageResult<Emp> page(EmpQueryParam empQueryParam);

    //新增員工
    void save(Emp emp) throws Exception;

    //批量刪除員工
    void delete(List<Integer> ids);

    //根據id查詢員工
    Emp getInfo(Integer id);

    //修改員工
    void update(Emp emp);

    /**
     * 員工登錄
     */
    LoginInfo login(Emp emp);


    //分頁查詢。page頁碼、pageSize每頁紀錄數
    //PageResult<Emp> page(Integer page, Integer pageSize,String name, Integer gender, LocalDate begin, LocalDate end);

    List<EmpReportVO> getGenderStats();
    List<EmpReportVO> getDeptStats();


    //修改個人資料
    void updatePersonal(Emp emp);

    /**
     * 獲取薪資區間統計
     */
    List<EmpReportVO> getSalaryStats();

    /**
     * 獲取點數排行榜
     */
    List<EmpReportVO> getPointsRankStats();

    /**
     * 獲取入職年度趨勢
     */
    List<EmpReportVO> getEntryTrendStats();
}
