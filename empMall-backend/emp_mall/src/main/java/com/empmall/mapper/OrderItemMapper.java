package com.empmall.mapper;

import com.empmall.pojo.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    /**
     * 新增訂單明細
     */
    @Insert("INSERT INTO order_item(order_id, product_id, product_name, image, quantity, points_per_item) " +
            "VALUES(#{orderId}, #{productId}, #{productName}, #{image}, #{quantity}, #{pointsPerItem})")
    void insert(OrderItem item);

    /**
     * 根據訂單ID查詢訂單明細
     */
    @Select("select * from order_item where order_id = #{orderId}")
    List<OrderItem> getByOrderId(Integer orderId);
}