package com.empmall.mapper;

import com.empmall.pojo.ProductQueryParam;
import com.empmall.pojo.Products;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductsMapper {

    // 1. 查詢所有商品
    List<Products> list(ProductQueryParam param);

    // 2. 新增商品
    void insert(Products products);

    // 3. 修改商品
    void update(Products products);

    // 4. 批量刪除
    void deleteBatchIds(@Param("ids") List<Integer> ids);

    // 5. 批量查詢
    List<Products> selectBatchIds(@Param("ids") List<Integer> ids);

    // --- 以下保持註解 (簡單 SQL) ---

    // 根據ID查詢
    @Select("select * from products where id = #{id}")
    Products getById(Integer id);

    // 刪除單個
    @Delete("delete from products where id = #{id}")
    void delete(Integer id);

    // 扣庫存 (原子操作，保持註解)
    @Update("update products set stock = stock - #{num} where id = #{id} and stock >= #{num}")
    int reduceStock(@Param("id") Integer id, @Param("num") Integer num);

    // 回補庫存 (原子操作，保持註解)
    @Update("update products set stock = stock + #{num} where id = #{id}")
    void increaseStock(@Param("id") Integer id, @Param("num") Integer num);
}