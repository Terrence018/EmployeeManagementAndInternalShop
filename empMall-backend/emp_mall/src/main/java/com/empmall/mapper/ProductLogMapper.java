package com.empmall.mapper;

import com.empmall.pojo.ProductLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductLogMapper {

    /**
     * 新增日誌
     */
    @Insert("INSERT INTO product_log(operate_user, operate_time, content) " +
            "VALUES(#{operateUser}, #{operateTime}, #{content})")
    void insert(ProductLog log);

    /**
     * 查詢日誌列表 (關聯 emp 表查詢操作人姓名)
     */
    @Select("SELECT pl.*, e.name AS operateName " +
            "FROM product_log pl " +
            "LEFT JOIN emp e ON pl.operate_user = e.id " +
            "ORDER BY pl.operate_time DESC")
    List<ProductLog> list();
}