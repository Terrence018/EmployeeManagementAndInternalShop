package com.empmall.mapper;

import com.empmall.pojo.EmpReportVO;
import com.empmall.pojo.Orders;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrdersMapper {



    // 1. 新增訂單
    // 把資料庫生成的 ID 塞回 Orders 物件的 id 屬性中
    void insert(Orders order);

    // 2. 查詢指定員工的訂單 (員工看自己的)
    // 查詢單人訂單 (巢狀查詢)
    List<Orders> findByEmpId(Integer empId);

    // 3. 查詢所有訂單 (管理員看全部)
    List<Orders> findAll();

    // 4. 更新訂單狀態 (例如：發貨、結案)
    @Update("update orders set status = #{status}, update_time = #{updateTime} where id = #{id}")
    void updateStatus(Integer id, Integer status, java.time.LocalDateTime updateTime);

    // 5. 根據ID查詢訂單
    @Select("select * from orders where id = #{id}")
    Orders getById(Integer id);

    //-----------------------
    /**
     * 1. 熱銷商品 Top 10 (統計每個 product_id 的出現次數 (或 sum quantity)，並關聯 products 表拿名稱)
     */
    @Select("SELECT p.name AS categoryName, IFNULL(SUM(oi.quantity), 0) AS count " +
            "FROM order_item oi " +
            "LEFT JOIN products p ON oi.product_id = p.id " +
            "GROUP BY oi.product_id, p.name " +
            "ORDER BY count DESC " +
            "LIMIT 10")
    List<EmpReportVO> getTop10Products();


    /**
     * 商品分類銷售佔比(關聯 products 表，根據 category 分組統計)
     */
    List<EmpReportVO> getCategorySales();

    /**
     * 近 7 日交易趨勢(按日期分組 (DATE_FORMAT))
     */
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m-%d') AS categoryName, COUNT(*) AS count " +
            "FROM orders " +
            "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d') " +
            "ORDER BY categoryName ASC")
    List<EmpReportVO> getSalesTrend();
}