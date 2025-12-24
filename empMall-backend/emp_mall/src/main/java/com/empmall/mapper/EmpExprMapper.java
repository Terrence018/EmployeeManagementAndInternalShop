package com.empmall.mapper;

import com.empmall.pojo.EmpExpr;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

//員工工作經歷
@Mapper
public interface EmpExprMapper {


    /**
     * 批量保存員工的工作經歷信息
     * @param exprList
     */
    void insertBatch(List<EmpExpr> exprList);

    /**
     * 根據員工ID批量刪除員工的工作經歷
     * @param empIds
     */
    void deleteByEmpIds(List<Integer> empIds);


}
