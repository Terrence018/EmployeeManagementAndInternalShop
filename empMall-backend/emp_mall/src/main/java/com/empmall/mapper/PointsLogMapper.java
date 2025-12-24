package com.empmall.mapper;

import com.empmall.pojo.PointsLog;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PointsLogMapper {

    /**
     * 1. 新增紀錄
     */
    void insert(PointsLog pointsLog);

    /**
     * 2. 查詢所有紀錄 (管理員用)
     */
    List<PointsLog> listAll();

    /**
     * 3. 查詢個人的紀錄 (員工自己看)
     */
    List<PointsLog> listByEmpId(Integer empId);

    /**
     * 4. 模糊查詢
     */
    List<PointsLog> list(String name);
}