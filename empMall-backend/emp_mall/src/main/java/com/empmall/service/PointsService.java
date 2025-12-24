package com.empmall.service;

import com.empmall.pojo.PageResult;
import com.empmall.pojo.PointsLog;
import java.util.List;

public interface PointsService {
    // 發放點數 (給誰, 給多少, 理由)
    void grantPoints(Integer empId, Integer amount, String description);

    //(暫留)
    List<PointsLog> listAll();

    //查詢紀錄
    List<PointsLog> listByEmpId(Integer empId);

    // 對應 issuePoints
    void issuePoints(PointsLog pointsLog);

    /**
     * 查詢點數紀錄 (分頁 + 搜尋)
     * @param page 當前頁碼
     * @param pageSize 每頁筆數
     * @param name 員工姓名 (搜尋條件)
     * @return 分頁結果
     */
    PageResult<PointsLog> list(Integer page, Integer pageSize, String name);

}